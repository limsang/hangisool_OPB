package com.hangisool.lcd_a_h.webrtc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hangisool.lcd_a_h.LcdActivity;
import com.hangisool.lcd_a_h.R;
import com.hangisool.lcd_a_h.backend.emercall.BackendService;
import com.hangisool.lcd_a_h.backend.emercall.Data;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hangisool.lcd_a_h.backend.emercall.Data.getUnsafeOkHttpClient;

public class WebRTCActivity_test_211119 extends AppCompatActivity implements View.OnClickListener, SignallingClient.SignalingInterface {
    PeerConnectionFactory peerConnectionFactory;
    MediaConstraints audioConstraints;
    MediaConstraints videoConstraints;
    MediaConstraints sdpConstraints;
    VideoSource videoSource;
    VideoTrack localVideoTrack;
    AudioSource audioSource;
    AudioTrack localAudioTrack;

    private static final String MAX_VIDEO_WIDTH_CONSTRAINT = "maxWidth";
    private static final String MIN_VIDEO_WIDTH_CONSTRAINT = "minWidth";
    private static final String MAX_VIDEO_HEIGHT_CONSTRAINT = "maxHeight";
    private static final String MIN_VIDEO_HEIGHT_CONSTRAINT = "minHeight";
    private static final String MAX_VIDEO_FPS_CONSTRAINT = "maxFrameRate";
    private static final String MIN_VIDEO_FPS_CONSTRAINT = "minFrameRate";

    SurfaceViewRenderer localVideoView;
    SurfaceViewRenderer remoteVideoView;

    TextView btnCancel;
    PeerConnection localPeer;
    EglBase rootEglBase;

    boolean gotUserMedia;
    List<PeerConnection.IceServer> peerIceServers = new ArrayList<>();

    private static final String TAG = "WebRTCAcitity";
    public static String hash = "";
    private boolean webRTCQuitThread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_rtc_bar);
//        choose_contentsview();
//        getHash();
//        check_flag_calling(hash);

        setFullScreen();

        initViews();
        initVideos();
        getIceServers();

        start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //키보드의 숫자1 입력이 들어오면 액티비티 전환됨
        if (keyCode == KeyEvent.KEYCODE_1) {
            end_emergency();
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * Closing up - normal hangup and app destroye
     */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel: {
                end_emergency();
                break;
            }
        }
    }

    public void end_emergency(){
        try {
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            //LAN이 연결 되어있을 경우에만 서버에 데이터 요청
            if (activeNetwork != null) {
                SharedPreferences pref;
                pref = getSharedPreferences("hash",0);
                String hash = pref.getString("hash","");
                Log.e("HASH",hash);

                if(hash == null || hash.equals("")) Toast.makeText(getApplicationContext(), "Can not found hash for emergency call...",Toast.LENGTH_SHORT).show();
                else {
                    SignallingClient.getInstance().emitLeave();
                    set_emergency_flag(hash, 0);//set emergency_flag to 0 & set calling flag to 0
                }
            }
        } catch (Exception e) {
        }
    }
    public void choose_contentsview(){
        SharedPreferences pref = getSharedPreferences("ScreenMode", 0);
        String screenMode = pref.getString("ScreenMode", "");
        /*if(screenMode.equals("COP") || screenMode.equals("COP_2")){//COP모드일 경우
            setContentView(R.layout.activity_web_rtc_bar);
        }else if(screenMode.equals("HOP") || screenMode.equals("HOP_2")){//HOP모드일 경우
            setContentView(R.layout.activity_web_rtc_bar);
        }else if(screenMode.equals("COP_SCROLL") || screenMode.equals("COP_SCROLL3")){
            setContentView(R.layout.activity_web_rtc_bar);
        }
        else {
            setContentView(R.layout.activity_web_rtc);
        }*/
        setContentView(R.layout.activity_web_rtc_bar);
    }

    public void set_emergency_flag(final String hash, final int emerFlag){
        new Thread(){
            public void run() {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BackendService.URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(getUnsafeOkHttpClient().build())
                        .build();

                BackendService service =retrofit.create(BackendService.class);

                service.postSetCarEmer(hash,emerFlag,0).enqueue(new retrofit2.Callback<Data>() {
                    @Override
                    public void onResponse(retrofit2.Call<Data> call, retrofit2.Response<Data> response) {
                        Log.e("RetrofitSucced",response.message());
                        if (response.isSuccessful()) {
                            Data body = response.body();
                            if(body.getResult().equals("success")){
                                Log.e("data.result",body.getResult());
                                Log.e("data.message",body.getMessage());
                            }else{
                                Toast.makeText(getApplicationContext(),"getCarCall-"+body.getMessage(),Toast.LENGTH_SHORT).show();
                                Log.e("data.result",body.getResult());
                                Log.e("data.message",body.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Data> call, Throwable t) {
                        Log.e("RetrofitFailed",t.getMessage());
                        Toast.makeText(getApplicationContext(),"postSetCarEmer-"+t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();
    }

    private void check_flag_calling(String hash){

        new Thread(){
            public void run() {
                while(!webRTCQuitThread) {
                    check_calling_flag(hash);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
    private void check_calling_flag(final String hash){
        new Thread(){
            public void run() {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BackendService.URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(getUnsafeOkHttpClient().build())
                        .build();

                BackendService service =retrofit.create(BackendService.class);

                service.getCarCall(hash).enqueue(new retrofit2.Callback<Data>() {
                    @Override
                    public void onResponse(retrofit2.Call<Data> call, retrofit2.Response<Data> response) {
                        Log.e("RetrofitSucced",response.message());
                        if (response.isSuccessful()) {
                            Data body = response.body();
                            if(body.getResult().equals("success")){
                                try {
                                    JSONObject obj = new JSONObject(body.getMessage());

                                    //Log.e("emergency","=> "+obj.getInt("emergency"));
                                    //Log.e("calling","=> "+obj.getInt("calling"));
                                    if(obj.getInt("calling") == 0){//if calling Flag turn to 0
                                        webRTCQuitThread = true;
//                                        Intent intent = new Intent(WebRTCActivity.this, LcdActivity.class);
//                                        startActivity(intent);
//                                        System.runFinalizersOnExit(true);
//                                        System.exit(0);
//                                        finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.e("test",e.getMessage());
                                }
                            }else{
                                Toast.makeText(getApplicationContext(),"getCarCall-"+body.getMessage(),Toast.LENGTH_SHORT).show();
                                Log.e("data.result",body.getResult());
                                Log.e("data.message",body.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Data> call, Throwable t) {
                        Log.e("RetrofitFailed",t.getMessage());
                        Toast.makeText(getApplicationContext(),"getCarCall-"+t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();
    }

    public void getHash(){
        //set the room name here
        SharedPreferences pref;
        pref = getSharedPreferences("hash",0);
        String hash = pref.getString("hash","");
        Log.e("HASH",hash);
        this.hash = hash;
    }

    public void setFullScreen(){
        View decorView;
        int uiOption;
        decorView = getWindow().getDecorView();
        uiOption = getWindow().getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOption);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//화면을 항상 켜지게 설정
    }

    private void initViews() {
        localVideoView = findViewById(R.id.local_gl_surface_view);
        remoteVideoView = findViewById(R.id.remote_gl_surface_view);
        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
    }

    private void initVideos() {
        rootEglBase = EglBase.create();
        //localVideoView.setRotation(90);
        localVideoView.init(rootEglBase.getEglBaseContext(), null);
        remoteVideoView.init(rootEglBase.getEglBaseContext(), null);
        localVideoView.setZOrderMediaOverlay(true);
        remoteVideoView.setZOrderMediaOverlay(true);
    }

    private void getIceServers() {
        PeerConnection.IceServer peerIceServer;
        peerIceServer = PeerConnection.IceServer.builder(BackendService.STUN_SERVER_URI).createIceServer();
        peerIceServers.add(peerIceServer);
        peerIceServer = PeerConnection.IceServer.builder(BackendService.TURN_SERVER_URI).
                setUsername(BackendService.TURN_SERVER_ID).
                setPassword(BackendService.TURN_SERVER_PW).
                createIceServer();
        if(peerIceServer == null){
            Log.e("getIceServers","peerIceServer is NULL!");
        }
        peerIceServers.add(peerIceServer);
        Log.e("getIceServers", "IceServers");
    }


    public void start() {
        Log.e("RTC_TEST","TEST-5");
        //Initialize PeerConnectionFactory globals.
        PeerConnectionFactory.InitializationOptions initializationOptions =
                PeerConnectionFactory.InitializationOptions.builder(this)
                        .setEnableVideoHwAcceleration(true)
                        .createInitializationOptions();
        PeerConnectionFactory.initialize(initializationOptions);

        //Create a new PeerConnectionFactory instance - using Hardware encoder and decoder.
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        DefaultVideoEncoderFactory defaultVideoEncoderFactory = new DefaultVideoEncoderFactory(
                rootEglBase.getEglBaseContext(),  /* enableIntelVp8Encoder */true,  /* enableH264HighProfile */true);
        DefaultVideoDecoderFactory defaultVideoDecoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());
        peerConnectionFactory = new PeerConnectionFactory(options, defaultVideoEncoderFactory, defaultVideoDecoderFactory);
        Log.e("RTC_TEST","TEST-4");

        //Now create a VideoCapturer instance.
        VideoCapturer videoCapturerAndroid;
        //videoCapturerAndroid = createCameraCapturer(new Camera1Enumerator(false));
        videoCapturerAndroid = createCameraCapturer(new Camera1Enumerator(false));

        //Create MediaConstraints - Will be useful for specifying video and audio constraints.
        audioConstraints = new MediaConstraints();
        videoConstraints = new MediaConstraints();

        //Create a VideoSource instance
        if (videoCapturerAndroid != null) {
            Log.e("videoCapturerAndroid"," not null");
            videoSource = peerConnectionFactory.createVideoSource(videoCapturerAndroid);
            localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource);
        }else{
            Log.e("videoCapturerAndroid","null");
            showToast("Can not found Camera!");
        }

        //create an AudioSource instance
        audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
        localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource);


        if (videoCapturerAndroid != null) {
            videoCapturerAndroid.startCapture(640, 480, 20);
        }
        localVideoView.setVisibility(View.VISIBLE);


        // And finally, with our VideoRenderer ready, we
        // can add our renderer to the VideoTrack.
        localVideoTrack.addRenderer(new VideoRenderer(localVideoView));

        localVideoTrack.addSink(localVideoView);

        localVideoView.setMirror(true);
        remoteVideoView.setMirror(true);

        gotUserMedia = true;

//        SignallingClient.getInstance().init(this);

        if (SignallingClient.getInstance().isInitiator) {

            onTryToStart();
        }
        Log.e("RTC_TEST","TEST-3");
    }


    /**
     * This method will be called directly by the app when it is the initiator and has got the local media
     * or when the remote peer sends a message through socket that it is ready to transmit AV data
     */
    @Override
    public void onTryToStart() {
        runOnUiThread(() -> {
            if (!SignallingClient.getInstance().isStarted && localVideoTrack != null && SignallingClient.getInstance().isChannelReady) {
                createPeerConnection();
                SignallingClient.getInstance().isStarted = true;
                if (SignallingClient.getInstance().isInitiator) {
                    Log.e("onTryToStart","doCall()");
                    doCall();
                }
            }
        });
    }


    /**
     * Creating the local peerconnection instance
     */
    private void createPeerConnection() {
        PeerConnection.RTCConfiguration rtcConfig =
                new PeerConnection.RTCConfiguration(peerIceServers);
        // TCP candidates are only useful when connecting to a server that supports
        // ICE-TCP.
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;

        // Use ECDSA encryption.
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA;
        localPeer = peerConnectionFactory.createPeerConnection(rtcConfig, new CustomPeerConnectionObserver("localPeerCreation") {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                onIceCandidateReceived(iceCandidate);
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                showToast("Received Remote stream");
                super.onAddStream(mediaStream);
                gotRemoteStream(mediaStream);
            }
        });
        Log.e("RTC_TEST","TEST-2");
        addStreamToLocalPeer();
    }

    /**
     * Adding the stream to the localpeer
     */
    private void addStreamToLocalPeer() {
        //creating local mediastream
        MediaStream stream = peerConnectionFactory.createLocalMediaStream("102");
        stream.addTrack(localAudioTrack);
        stream.addTrack(localVideoTrack);
        localPeer.addStream(stream);
        Log.e("RTC_TEST","TEST-1");
    }

    /**
     * This method is called when the app is initiator - We generate the offer and send it over through socket
     * to remote peer
     */
    private void doCall() {
        sdpConstraints = new MediaConstraints();
        sdpConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveVideo", "false"));
        //sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair(MAX_VIDEO_FPS_CONSTRAINT, "10"));
        //sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair(MIN_VIDEO_FPS_CONSTRAINT, "10"));
        //sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair(MAX_VIDEO_HEIGHT_CONSTRAINT, "600"));
        //sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair(MAX_VIDEO_WIDTH_CONSTRAINT, "800"));
        //sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair("H264", "true"));
        localPeer.createOffer(new CustomSdpObserver("localCreateOffer") {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                super.onCreateSuccess(sessionDescription);
                localPeer.setLocalDescription(new CustomSdpObserver("localSetLocalDesc"), sessionDescription);
                Log.d("onCreateSuccess", "SignallingClient emit ");
                SignallingClient.getInstance().emitMessage(sessionDescription);
            }
        }, sdpConstraints);
    }

    /**
     * Received remote peer's media stream. we will get the first video track and render it
     */
    private void gotRemoteStream(MediaStream stream) {
        //we have remote video stream. add to the renderer.
        final VideoTrack videoTrack = stream.videoTracks.get(0);
        //final AudioTrack audioTrack = stream.audioTracks.get(0);
        runOnUiThread(() -> {
            try {
                remoteVideoView.setVisibility(View.VISIBLE);

                //videoTrack.addRenderer(new VideoRenderer(remoteVideoView));
                videoTrack.addSink(remoteVideoView);

                //audioTrack.setVolume(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }


    /**
     * Received local ice candidate. Send it to remote peer through signalling for negotiation
     */
    public void onIceCandidateReceived(IceCandidate iceCandidate) {
        //we have received ice candidate. We can set it to the other peer.
        SignallingClient.getInstance().emitIceCandidate(iceCandidate);
    }

    /**
     * SignallingCallback - called when the room is created - i.e. you are the initiator
     */
    @Override
    public void onCreatedRoom() {
        showToast("You created the room " + gotUserMedia);
        if (gotUserMedia) {
            //SignallingClient.getInstance().emitMessage("got user media");
            SignallingClient.getInstance().emitMessage("gotUserMedia");
        }
    }

    /**
     * SignallingCallback - called when you join the room - you are a participant
     */
    @Override
    public void onJoinedRoom() {
        Log.e("onJoinedRoom","You joined the room " + gotUserMedia);
    }

    public void onReadyRoom(){
        Log.e("onReadyRoom","You Ready the room " + gotUserMedia);
        if (gotUserMedia) {
            SignallingClient.getInstance().emitMessage("gotUserMedia");
        }
    }

    @Override
    public void onNewPeerJoined() {
        if (gotUserMedia) {
            showToast("Remote Peer Joined");
            Log.e("Remote Peer Joined", "gotUserMedia");
            SignallingClient.getInstance().emitMessage("gotUserMedia");
        }else{
            Log.e("Remote Peer Joined", "NO gotUserMedia");
        }
    }



    /**
     * SignallingCallback - Called when remote peer sends offer
     */
    @Override
    public void onOfferReceived(final JSONObject data) {
        showToast("Received Offer");
        runOnUiThread(() -> {
            if (!SignallingClient.getInstance().isInitiator && !SignallingClient.getInstance().isStarted) {
                onTryToStart();
            }

            try {
                localPeer.setRemoteDescription(new CustomSdpObserver("localSetRemote"), new SessionDescription(SessionDescription.Type.OFFER, data.getString("sdp")));
                doAnswer();
                updateVideoViews(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void doAnswer() {
        localPeer.createAnswer(new CustomSdpObserver("localCreateAns") {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                super.onCreateSuccess(sessionDescription);
                Log.e("doAnswer","onCreateSuccess");
                localPeer.setLocalDescription(new CustomSdpObserver("localSetLocal"), sessionDescription);
                SignallingClient.getInstance().emitMessage(sessionDescription);
            }
        }, new MediaConstraints());
    }

    /**
     * SignallingCallback - Called when remote peer sends answer to your offer
     */

    @Override
    public void onAnswerReceived(JSONObject data) {
        showToast("Received Answer");
        try {
            localPeer.setRemoteDescription(new CustomSdpObserver("localSetRemote"), new SessionDescription(SessionDescription.Type.fromCanonicalForm(data.getString("type").toLowerCase()), data.getString("sdp")));
            updateVideoViews(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remote IceCandidate received
     */
    @Override
    public void onIceCandidateReceived(JSONObject data) {
        try {
            localPeer.addIceCandidate(new IceCandidate(data.getString("id"), data.getInt("label"), data.getString("candidate")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void updateVideoViews(final boolean remoteVisible) {
        runOnUiThread(() -> {
            ViewGroup.LayoutParams params = localVideoView.getLayoutParams();
            SharedPreferences pref = getSharedPreferences("ScreenMode", 0);
            String screenMode = pref.getString("ScreenMode", "");

            if (remoteVisible) {
                /*if(screenMode.equals("COP") || screenMode.equals("COP_2")){//COP모드일 경우
                }else if(screenMode.equals("HOP") || screenMode.equals("HOP_2")){//HOP모드일 경우
                }else if(screenMode.equals("COP_SCROLL") || screenMode.equals("COP_SCROLL3")){
                }else{
                    params.height = dpToPx(400);
                    params.width = dpToPx(400);
                }*/
            } else {
                params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
            localVideoView.setLayoutParams(params);
        });

    }

    @Override
    protected void onDestroy() {
        SignallingClient.getInstance().close();
        super.onDestroy();
    }

    /**
     * Util Methods
     */
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public void showToast(final String msg) {
        runOnUiThread(() -> Toast.makeText(WebRTCActivity_test_211119.this, msg, Toast.LENGTH_SHORT).show());
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();
        for(String deviceName : deviceNames){
            Log.e("deviceNames",deviceName);
        }

        // First, try to find front facing camera
        Logging.e(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.e(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.e(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.e(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        Log.e("createCameraCapturer","null");
        return null;
    }


}
