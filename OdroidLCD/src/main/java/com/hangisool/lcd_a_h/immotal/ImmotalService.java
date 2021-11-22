package com.hangisool.lcd_a_h.immotal;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import com.hangisool.lcd_a_h.LcdActivity;
import java.util.List;
import java.util.Iterator;

public class ImmotalService extends Service {
    //for ImmotalService
    public static String ImmotalFlagFile = "ImmotalFlagFile";

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        Log.d("Test", "Immotal Service Start");


        TestThread thread = new TestThread();
        thread.start();
    }

    class TestThread extends Thread {
        public void run() {
            try{
                Thread.sleep(5*1000); // 5초마다
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                // TODO: handle exception
            }
            mHandler.sendMessage(Message.obtain(mHandler, 1)); // handler로 메시지를 보냄
        }

        public Handler mHandler = new Handler(){ // 핸들러 처리부분
            public void handleMessage(Message msg){
                mHandler.sendEmptyMessageDelayed(0, 1000);    // 5초마다 반복함  cf) 1000=1초
                if(!getRunActivity()){
                    SharedPreferences pref = getSharedPreferences(ImmotalFlagFile, 0);
                    String flagImmotalMode = pref.getString("flag","true");
                    //Log.e("flagImmotalMode",flagImmotalMode);
                    if(flagImmotalMode.equals("true")) {//앱꺼짐방지 플래그가 ON일경우에만 LcdActivity를 자동으로 다시시작시킴
                        Intent intent = new Intent(getApplicationContext(), LcdActivity.class);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    }
                }
            };
        };
    }

    public boolean getRunActivity()	{

        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(getApplicationContext().ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info;
        info = activityManager.getRunningTasks(1);

        for (Iterator iterator = info.iterator(); iterator.hasNext();)  {
            ActivityManager.RunningTaskInfo runningTaskInfo = (ActivityManager.RunningTaskInfo) iterator.next();
            Log.d("topActivity",runningTaskInfo.topActivity.getClassName());
            if(runningTaskInfo.topActivity.getClassName().equals("com.hangisool.lcd_a_h.LcdActivity")) {

                return true;
            }
        }
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        super.onStartCommand(intent, flags, startId);

        startForeground(1, new Notification());


        return START_REDELIVER_INTENT;

    }


    @Override
    public void onDestroy(){

        super.onDestroy();
    }

}