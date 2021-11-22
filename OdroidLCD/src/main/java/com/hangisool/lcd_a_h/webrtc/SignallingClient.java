package com.hangisool.lcd_a_h.webrtc;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Socket;

class SignallingClient {
    private static SignallingClient instance;
    private String roomName = null;
    private Socket socket;
    boolean isChannelReady = false;
    boolean isInitiator = false;
    boolean isStarted = false;
    private SignalingInterface callback;

    //This piece of code should not go into production!!
    //This will help in cases where the node server is running in non-https server and you want to ignore the warnings
    @SuppressLint("TrustAllX509TrustManager")
    private final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }

        public void checkClientTrusted(X509Certificate[] chain,
                                       String authType) {
        }

        public void checkServerTrusted(X509Certificate[] chain,
                                       String authType) {
        }
    }};

    public static SignallingClient getInstance() {
        if (instance == null) {
            instance = new SignallingClient();
        }
        if (instance.roomName == null) {
            String hash = WebRTCActivity.hash;

            if(hash == null || hash.equals("")) Log.e("SignallingClient","Can not found hash");
            else instance.roomName = hash;
        }
        return instance;
    }


    public void init(SignalingInterface signalingInterface) {
        this.callback = signalingInterface;
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, trustAllCerts, null);
            IO.setDefaultHostnameVerifier((hostname, session) -> true);
            IO.setDefaultSSLContext(sslcontext);
            //set the socket.io url here
            //socket = IO.socket("https://hgstest.b.lue.blue:4143");
            socket = IO.socket("https://192.168.0.120:4143");
            socket.connect();
            Log.e("SignallingClient", "init() called");

            if (!roomName.isEmpty()) {
                emitInitStatement(roomName);
            }


            //room created event.
            //socket.on("created", args -> {//roomCreated
            socket.on("roomCreated", args -> {//roomCreated
                Log.e("SignallingClient", "created call() called with: args = [" + Arrays.toString(args) + "]");
                isInitiator = true;
                callback.onCreatedRoom();
            });

            //room is full event
            //socket.on("full", args -> Log.d("SignallingClient", "full call() called with: args = [" + Arrays.toString(args) + "]"));
            socket.on("roomFull", args -> Log.d("SignallingClient", "full call() called with: args = [" + Arrays.toString(args) + "]"));

            //peer joined event
            //socket.on("join", args -> {
            socket.on("roomSomeoneJoined", args -> {
                Log.e("SignallingClient", "join call() called with: args = [" + Arrays.toString(args) + "]");
                isChannelReady = true;
                callback.onNewPeerJoined();
            });

            //when you joined a chat room successfully
            //socket.on("joined", args -> {
            socket.on("roomJoined", args -> {
                Log.e("SignallingClient", "joined call() called with: args = [" + Arrays.toString(args) + "]");
                isChannelReady = true;
                callback.onJoinedRoom();
            });

            socket.on("roomReady", args -> {
                Log.e("SignallingClient", "roomReady call() called with: args = [" + Arrays.toString(args) + "]");
                callback.onReadyRoom();
            });

            //log event
            socket.on("log", args -> Log.e("SignallingClient", "log call() called with: args = [" + Arrays.toString(args) + "]"));

            //roomSomeoneLeft 이벤트 상대방이 통화방에서 나갔을경우
            socket.on("roomSomeoneLeft", args -> {
                Log.e("roomSomeoneLeft","상대방에서통화종료");
                //callback.onRemoteHangUp((String) args[0]);
            });

            //messages - SDP and ICE candidates are transferred through this
            socket.on("roomMessageRelayed", args -> {
                Log.e("SignallingClient", "message call() called with: args = [" + Arrays.toString(args) + "]");
                if (args[1] instanceof String) {
                    Log.e("SignallingClient", "String received :: " + args[1]);
                    String data = (String) args[1];
                    //if (data.equalsIgnoreCase("got user media")) {
                    if (data.equalsIgnoreCase("gotUserMedia")) {
                        Log.e("RECV","gotUsermedia");
                        callback.onTryToStart();
                    }
                    /*if (data.equalsIgnoreCase("bye")) {
                        callback.onRemoteHangUp(data);
                    }*/
                } else if (args[1] instanceof JSONObject) {
                    try {
                        JSONObject data = (JSONObject) args[1];
                        Log.e("SignallingClient", "Json Received :: " + data.toString());
                        String type = data.getString("type");
                        if (type.equalsIgnoreCase("offer")) {
                            Log.e("RECV","offer");
                            callback.onOfferReceived(data);
                        } else if (type.equalsIgnoreCase("answer") && isStarted) {
                            Log.e("RECV","answer");
                            callback.onAnswerReceived(data);
                        } else if (type.equalsIgnoreCase("candidate") && isStarted) {
                            Log.e("RECV","candidate");
                            callback.onIceCandidateReceived(data);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    private void emitInitStatement(String message) {
        Log.e("SignallingClient", "emitInitStatement() called with: event = [" + "create or join" + "], message = [" + message + "]");
        //socket.emit("create or join", message);
        socket.emit("join", message);
    }

    public void emitMessage(String message) {
        Log.e("SignallingClient", "emitMessage() called with: message = [" + message + "]");
        //socket.emit("message", message);
        socket.emit("roomMessage", roomName,  message);
    }

    public void emitLeave() {
        Log.e("SignallingClient", "emitLeave() called ");
        //socket.emit("message", message);
        socket.emit("leave", roomName);
    }

    public void emitMessage(SessionDescription message) {
        try {
            Log.e("SignallingClient", "emitMessage() called with: message = [" + message + "]");
            JSONObject obj = new JSONObject();
            obj.put("type", message.type.canonicalForm());
            obj.put("sdp", message.description);
            Log.d("emitMessage", obj.toString());
            socket.emit("roomMessage",roomName, obj);
            Log.d("vivek1794", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void emitIceCandidate(IceCandidate iceCandidate) {
        try {
            JSONObject object = new JSONObject();
            object.put("type", "candidate");
            object.put("label", iceCandidate.sdpMLineIndex);
            object.put("id", iceCandidate.sdpMid);
            object.put("candidate", iceCandidate.sdp);
            socket.emit("roomMessage",roomName, object);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public void close() {
        socket.emit("bye", roomName);
        socket.disconnect();
        socket.close();
    }


    interface SignalingInterface {

        void onOfferReceived(JSONObject data);

        void onAnswerReceived(JSONObject data);

        void onIceCandidateReceived(JSONObject data);

        void onTryToStart();

        void onCreatedRoom();

        void onJoinedRoom();

        void onReadyRoom();

        void onNewPeerJoined();
    }
}
