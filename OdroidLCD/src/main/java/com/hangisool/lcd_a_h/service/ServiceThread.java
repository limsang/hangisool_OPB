package com.hangisool.lcd_a_h.service;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import com.hangisool.lcd_a_h.LcdActivity;
import com.hangisool.lcd_a_h.VideoFragment;
import com.hangisool.lcd_a_h.filepath.ImgPath;
import com.hangisool.lcd_a_h.ftpmanager.FtpFileManager;
import com.hangisool.lcd_a_h.util.Util;
import org.apache.log4j.Logger;
import org.ini4j.Wini;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

public class ServiceThread extends Thread{
    Handler handler;
    Context lcdServiceContext;
    boolean isRun = true;
    private String carName="";
    private ImgPath path;

    public ServiceThread(Handler handler,Context serviceContext){
        this.handler = handler;
        this.lcdServiceContext = serviceContext;
        path = new ImgPath();
        Log.d("capture", "signal");
        Wini wini = null;
        try {
            wini = new Wini(new File(path.getImgPath(), "carName.ini"));
            carName = wini.get("carName","carName");
            Log.d("carName", carName);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stopForever(){
        synchronized (this) {
            this.isRun = false;
        }
    }

    private static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void run(){

        byte[] data = new byte[1500];
        DatagramSocket socket = null;
        DatagramPacket packet = new DatagramPacket(data, data.length);

        String message;
        try {
            socket = new DatagramSocket(9877);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while(true){
            try{
                if(socket != null) {
                    socket.receive(packet);
                    message = new String(packet.getData(), 0, packet.getLength());
                    Log.e("ServiceThreadMassage",message);
                    if (message.equals("reboot_" + carName)) {
                        //삼일용 프로토콜이 들어가고 광시야각으로 화면잔상이 발생할 수 있으면 재부팅전에 스크린 세이브기능을 실행한다.
                        if (LcdActivity.screenMode.equals("3PART_H_BAR_VIDEO_POLICE")||LcdActivity.screenMode.equals("3PART_H_LOBBY_VIDEO_POLICE")) {
                            LcdActivity.screenSaveFlag = true;
                        }else {
                            Log.d("!!!SOCKET!!!", " !!!!SOCKET RECEVIE!!!!");
                            Log.d("!!!REBOOT START!!!", "!!!!!!!!!!");
                            Process sh = Runtime.getRuntime().exec("su", null, null);
                            Runtime.getRuntime().exec("su -c reboot");
                        }
                    }
                    else if (message.equals("start")) {
                        Log.e("message", "start");
                        LcdActivity.restartFlag = true;
                        LcdActivity.finishFlag = true;
                    }
                    else if(message.equals("remoteImg")){
                        Log.e("remoteImg", "remoteImg");
                        try {

                                if (LcdActivity.playList.get(LcdActivity.playListIndex).split(",")[0].contains("video") && LcdActivity.playStatus) {
                                    VideoFragment.captureSignal = true;
                                    while (!VideoFragment.captureStartFalg) {
                                        Thread.sleep(1);
                                    }
                                }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                                try {
                                    Process sh = Runtime.getRuntime().exec("su", null, null);
                                    OutputStream os = sh.getOutputStream();
                                    Log.d("IMG PATH:", path.getImgPath() + "/" + carName + ".jpg");
                                    os.write(("/system/bin/screencap -p " + path.getImgPath() + "/" + carName + "_" + Util.getIpAdress() + ".jpg").getBytes("ASCII"));
                                    os.flush();

                                    os.close();
                                    sh.waitFor();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                        }
                        VideoFragment.captureSignal = false;

                        FtpFileManager ftpFileManager = new FtpFileManager("/LcdImg",path.getImgPath() + "/"+ carName + "_" + Util.getIpAdress() + ".jpg");
                        if(ftpFileManager.connect()) {
                            ftpFileManager.imagFileUpload();
                        }
                    }
                    //비디오와 사진이 표현되는 main_view에 자료를 늘려서 전체화면으로 표현할것인지 자료의 비율에 맞게 메인뷰에 표현할 것인지 선택
                    else if (message.equals("FULL_MAINVIEW")) {
                        Log.e("message", "FULL_MAINVIEW");
                        SharedPreferences pref = lcdServiceContext.getSharedPreferences(LcdActivity.screenSizeFile, 0);
                        SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                        editor.putString("screenSizeFile","FULL_MAINVIEW"); // 입력할 값
                        editor.commit();

                        LcdActivity.restartFlag = true;
                        LcdActivity.finishFlag = true;
                    }
                    else if (message.equals("RESIZE_MAINVIEW")) {
                        Log.e("message", "RESIZE_MAINVIEW");
                        SharedPreferences pref = lcdServiceContext.getSharedPreferences(LcdActivity.screenSizeFile, 0);
                        SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                        editor.putString("screenSizeFile","RESIZE_MAINVIEW"); // 입력할 값
                        editor.commit();

                        LcdActivity.restartFlag = true;
                        LcdActivity.finishFlag = true;
                    }
                    //스크린 모드에 대한 패킷을 받으면 SharedPreferences로 그 값을 저장한후 앱을 재시작하는 로직들.
                    else if (message.equals("FULL_H_MODE")) {
                        Log.e("message", "FULL_H_MODE");
                        SharedPreferences pref = lcdServiceContext.getSharedPreferences(LcdActivity.fileName, 0);
                        SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                        editor.putString("ScreenMode","FULL_H_MODE"); // 입력할 값
                        editor.commit();

                        LcdActivity.restartFlag = true;
                        LcdActivity.finishFlag = true;
                    }
                    else if (message.equals("FULL_V_MODE")) {
                        Log.e("message", "FULL_V_MODE");
                        SharedPreferences pref = lcdServiceContext.getSharedPreferences(LcdActivity.fileName, 0);
                        SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                        editor.putString("ScreenMode","FULL_V_MODE"); // 입력할 값
                        editor.commit();

                        LcdActivity.restartFlag = true;
                        LcdActivity.finishFlag = true;
                    }
                    else if (message.equals("3PART_H_MODE")) {
                        Log.e("message", "3PART_H_MODE");
                        SharedPreferences pref = lcdServiceContext.getSharedPreferences(LcdActivity.fileName, 0);
                        SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                        editor.putString("ScreenMode","3PART_H_MODE"); // 입력할 값
                        editor.commit();

                        LcdActivity.restartFlag = true;
                        LcdActivity.finishFlag = true;
                    }

                    else if (message.equals("2PART_V_MODE")) {
                        Log.e("message", "2PART_V_MODE");
                        SharedPreferences pref = lcdServiceContext.getSharedPreferences(LcdActivity.fileName, 0);
                        SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                        editor.putString("ScreenMode","2PART_V_MODE"); // 입력할 값
                        editor.commit();

                        LcdActivity.restartFlag = true;
                        LcdActivity.finishFlag = true;
                    }

                    else if (message.equals("3PART_V_MODE")) {
                        Log.e("message", "3PART_V_MODE");
                        SharedPreferences pref = lcdServiceContext.getSharedPreferences(LcdActivity.fileName, 0);
                        SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                        editor.putString("ScreenMode","3PART_V_MODE"); // 입력할 값
                        editor.commit();

                        LcdActivity.restartFlag = true;
                        LcdActivity.finishFlag = true;
                    }

                    else if (message.equals("3PART_V_MODE_FULLHD")) {
                        Log.e("message", "3PART_V_MODE_FULLHD");
                        SharedPreferences pref = lcdServiceContext.getSharedPreferences(LcdActivity.fileName, 0);
                        SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                        editor.putString("ScreenMode","3PART_V_MODE_FULLHD"); // 입력할 값
                        editor.commit();

                        LcdActivity.restartFlag = true;
                        LcdActivity.finishFlag = true;
                    }

                    else if (message.equals("3PART_V_KMEC")) {
                        Log.e("message", "3PART_V_KMEC");
                        SharedPreferences pref = lcdServiceContext.getSharedPreferences(LcdActivity.fileName, 0);
                        SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                        editor.putString("ScreenMode","3PART_V_KMEC"); // 입력할 값
                        editor.commit();

                        LcdActivity.restartFlag = true;
                        LcdActivity.finishFlag = true;
                    }

                    else if (message.equals("3PART_H_BAR")) {
                        Log.e("message", "3PART_H_BAR");
                        SharedPreferences pref = lcdServiceContext.getSharedPreferences(LcdActivity.fileName, 0);
                        SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                        editor.putString("ScreenMode","3PART_H_BAR"); // 입력할 값
                        editor.commit();

                        LcdActivity.restartFlag = true;
                        LcdActivity.finishFlag = true;
                    }

                    else if (message.equals("3PART_H_BAR_VIDEO")) {
                        Log.e("message", "3PART_H_BAR_VIDEO");
                        SharedPreferences pref = lcdServiceContext.getSharedPreferences(LcdActivity.fileName, 0);
                        SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                        editor.putString("ScreenMode","3PART_H_BAR_VIDEO"); // 입력할 값
                        editor.commit();

                        LcdActivity.restartFlag = true;
                        LcdActivity.finishFlag = true;
                    }

                    else if (message.equals("3PART_H_BAR_VIDEO_POLICE")) {
                        Log.e("message", "3PART_H_BAR_VIDEO_POLICE");
                        SharedPreferences pref = lcdServiceContext.getSharedPreferences(LcdActivity.fileName, 0);
                        SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                        editor.putString("ScreenMode","3PART_H_BAR_VIDEO_POLICE"); // 입력할 값
                        editor.commit();

                        LcdActivity.restartFlag = true;
                        LcdActivity.finishFlag = true;
                    }

                    else if (message.equals("3PART_H_LOBBY_VIDEO_POLICE")) {
                        Log.e("message", "3PART_H_LOBBY_VIDEO_POLICE");
                        SharedPreferences pref = lcdServiceContext.getSharedPreferences(LcdActivity.fileName, 0);
                        SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                        editor.putString("ScreenMode","3PART_H_LOBBY_VIDEO_POLICE"); // 입력할 값
                        editor.commit();

                        LcdActivity.restartFlag = true;
                        LcdActivity.finishFlag = true;
                    }

                    //컨텐츠 스크롤 시간설정 패킷
                    else if (message.contains("SCROLLCYCLE/")){
                        Log.e("message", message);
                        SharedPreferences pref = lcdServiceContext.getSharedPreferences(LcdActivity.scrollCycleFile, 0);
                        SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                        editor.putString("scrollCycle",message); // 입력할 값
                        editor.commit();
                        //서버에서 스크롤 시간을 변경하면 프로그램이 재시작되면서 적용된다.
                        LcdActivity.restartFlag = true;
                        LcdActivity.finishFlag = true;
                    }

                    //뉴스를 표시할 것인지 공지를 표시할 것인지 선택
                    else if (message.contains("NEWS_OR_NOTICE/")){
                        Log.e("message", message);
                        SharedPreferences pref = lcdServiceContext.getSharedPreferences(LcdActivity.nOrNFile, 0);
                        SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                        editor.putString("newsOrNotice",message); // 입력할 값
                        editor.commit();

                        LcdActivity.restartFlag = true;
                        LcdActivity.finishFlag = true;
                    }
                }
            }catch (Exception e){
                socket.close();
                Logger logger = Logger.getLogger("");
                logger.error(e);
                e.printStackTrace();
            }
        }
    }


}
