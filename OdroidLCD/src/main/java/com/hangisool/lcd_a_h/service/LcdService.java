package com.hangisool.lcd_a_h.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import com.hangisool.lcd_a_h.bootstart.StartReceiver;
import java.io.IOException;

public class LcdService extends Service {
  NotificationManager Notifi_M;
  ServiceThread thread;
  boolean threadFlag = true;

  public LcdService() {
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    myServiceHandler handler = new myServiceHandler();
    Log.e("LcdService","onStartCommand");
    //Toast.makeText(getApplicationContext(),"LcdService Started",Toast.LENGTH_LONG).show();
    thread = new ServiceThread(handler,getApplicationContext());
    thread.start();
    new Thread(){
      @Override
      public void run() {
        super.run();
        while(threadFlag) {
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          //Log.d("LcdService", "alive");
        }
      }
    }.start();
    return START_STICKY;
  }

  class myServiceHandler extends Handler {
    @Override
    public void handleMessage(android.os.Message msg) {
      if(msg.what == 1){
        try {
          Log.e("!!!REBOOT!!!", " !!!!REBOOT START!!!!");
          try{
            Log.d("!!!REBOOT START!!!", "!!!!!!!!!!");
            Process sh = Runtime.getRuntime().exec("su", null,null);
            Runtime.getRuntime().exec("su -c reboot");
          } catch (IOException e) {
            e.printStackTrace();
          }
          //rebootAlarm();
        } catch (Exception e) {
          Log.e("!!!REBOOT!!!", " !!!!REBOOT ERROR!!!!");
          e.printStackTrace();
        }
      }
    }
  };

  @Override
  public void onCreate() {
    super.onCreate();
    Log.e("!!!SERVICE!!!", " !!!!SERVICE START!!!!");
    unregisterRestartAlarm();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.e("!!!SERVICE!!!", " !!!!SERVICE CLOSE!!!!");
    registerRestartAlarm();
    threadFlag = false;
    //thread.stopForever();
  }

  void rebootAlarm(){
    Log.e("REBOOT ALARM", "Android Reboot Alarm");
    Intent intent = new Intent("ACTION_REBOOT_BROAD_CAST");
    sendBroadcast(intent);
  }

  void registerRestartAlarm(){
    Log.d("RESTART ALARM", "register Restart Alarm");

    Intent intent = new Intent(this, StartReceiver.class);
    intent.setAction("ACTION_RESTART_PERSISTENTSERVICE");
    PendingIntent sender = PendingIntent.getBroadcast(this,0,intent,0);
    long firstTime = SystemClock.elapsedRealtime();
    firstTime+=1*1000;
    AlarmManager am =(AlarmManager)getSystemService(ALARM_SERVICE);
    am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 10 * 1000, sender);
  }

  void unregisterRestartAlarm(){
    Log.d("UNRESTART ALARM", "unregister Restart Alarm");
    Intent intent = new Intent(this,StartReceiver.class);
    intent.setAction("ACTION_RESTART_PERSISTENTSERVICE");
    PendingIntent sender = PendingIntent.getBroadcast(this,0,intent,0);
    AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
    am.cancel(sender);
  }
}
