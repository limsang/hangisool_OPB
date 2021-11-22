package com.hangisool.lcd_a_h.ftpmanager;

import android.content.SharedPreferences;
import android.util.Log;
import com.hangisool.lcd_a_h.LcdActivity;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UpdateThread extends Thread{

    @Override
    public void run() {
        byte[] data = new byte[1500];
        DatagramSocket socket = null;
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
            socket = new DatagramSocket(9876);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while(true){
            if(LcdActivity.ThreadStopFlag == true){
                break;
            }
            try {
                if(socket != null) {
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    if (message.equals("File Update")) {
                        //ftp에서 파일을 다운받는 도중 앱이 꺼지면 파일을 전부 다운 받지 못하는 현상으로 완전히 다운받았는지 여부를
                        // SharedPreferences에 저장하고 시작할때마다 꺼내서 다시 ftp다운로드 할지를 판단한다.
                        Log.e("FileDownload","Ready");
                        SharedPreferences pref = LcdActivity.mContext.getSharedPreferences(LcdActivity.updateFlagFile, 0);
                        SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                        editor.putString("updateFlag","true"); // 입력할 값
                        editor.commit();
                        LcdActivity.updateFlag = true;
                        //LcdActivity.playList = null;
                    }
                    else if (message.equals("Restart LcdActivity")) {
                        LcdActivity.restartFlag = true;
                    }
                    else if (message.equals("APK DOWNLOAD")){
                        LcdActivity.apkUpdateFlag = true;
                    }
                }
            }catch (Exception e){
                socket.close();
                e.printStackTrace();
            }
        }
    }
}
