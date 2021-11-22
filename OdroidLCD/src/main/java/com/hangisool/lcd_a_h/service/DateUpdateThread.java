package com.hangisool.lcd_a_h.service;

import android.os.Build;
import android.util.Log;
import com.hangisool.lcd_a_h.LcdActivity;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class DateUpdateThread extends Thread{

  @Override
  public void run() {
    byte[] data = new byte[1500];
    DatagramSocket socket = null;
    DatagramPacket packet = new DatagramPacket(data, data.length);
    try {
      socket = new DatagramSocket(9878);
    } catch (SocketException e) {
      e.printStackTrace();
    }
    Runtime runtime = Runtime.getRuntime();
    while(true){
      if(LcdActivity.ThreadStopFlag == true){
        break;
      }
      try{
        if(socket != null) {
          socket.receive(packet);
          String message = new String(packet.getData(), 0, packet.getLength());
          Log.d("DATE_LOAD", message);
          if(message.contains("date")){
            Log.d("DATE_LOAD", message);
            //시스템시간 변경로직추가 kmw
            //마쉬멜로우이상일경우
            if(Build.VERSION.SDK_INT >=23) {
              Process su = Runtime.getRuntime().exec("su" );
              String cmd = "date "+
                      message.split("_")[2] +
                      message.split("_")[3] +
                      message.split("_")[5].split(":")[0] +
                      message.split("_")[5].split(":")[1] +
                      message.split("_")[1] +
                      "." + "00" + "\n";//시간조합(ex.date 112310512017.00)월일시분년.초
              Log.e("dateString", cmd);
              DataOutputStream os = new DataOutputStream( su.getOutputStream() );
              os.writeBytes(cmd);
              os.flush();
              os.writeBytes("exit\n");
              os.flush();

              su.getErrorStream().close();
              su.getInputStream().close();
              su.getOutputStream().close();
              su.waitFor();
            }
            //마쉬멜로우이하일경우
            else{
              Process su = Runtime.getRuntime().exec("su" );
              String cmd = "date -s " +
                      message.split("_")[1] +
                      message.split("_")[2] +
                      message.split("_")[3] +
                      "." +
                      message.split("_")[5].split(":")[0] +
                      message.split("_")[5].split(":")[1] +
                      "00" + "\n";//시간조합(ex.date -s 20171123.105100)년월일.시분초
              Log.e("dateString",cmd);
              DataOutputStream os = new DataOutputStream( su.getOutputStream() );
              os.writeBytes(cmd);
              os.flush();
              os.writeBytes("exit\n");
              os.flush();

              su.getErrorStream().close();
              su.getInputStream().close();
              su.getOutputStream().close();
              su.waitFor();
            }


          }
        }
      }catch (Exception e){
        socket.close();
        e.printStackTrace();
      }
    }
  }
}
