package com.hangisool.lcd_a_h.cleaner;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.VideoView;
import com.hangisool.lcd_a_h.R;

public class DpCleanerActivity extends Activity {
    private VideoView mVideoView;
    private TextView mNoticeTextview;
    private View decorView;
    private int uiOption;
    int cntExitTime = 300;//시스템 재시작까지시간
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_dp_cleaner);
        Log.e("activity_bar_dp_cleaner","activity_bar_dp_cleaner");

        //전체화면으로 변환을 위해
        decorView = getWindow().getDecorView();
        uiOption = getWindow().getDecorView().getSystemUiVisibility();
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH )
            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOption);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//화면을 항상 켜지게 설정

        mNoticeTextview = (TextView)findViewById(R.id.txtNoticeCleaner);
        mVideoView = (DpVideoView) findViewById(R.id.video_view_bar_cleaner);



        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoView.setVideoPath(Environment.getExternalStorageDirectory()+ "/img/Nebula - 6044.mp4");
                mVideoView.requestFocus();
                mVideoView.start();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
            while (true) {
                try {
                    handler.sendEmptyMessage(0);
                    Thread.sleep(1000);
                    cntExitTime-=1;
                    if(cntExitTime == 0){
                        Log.d("!!!REBOOT START!!!", "!!!!!!!!!!");
                        Process sh = Runtime.getRuntime().exec("su", null,null);
                        Runtime.getRuntime().exec("su -c reboot");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            }
        }).start();
        try {
            mVideoView.setVideoPath(Environment.getExternalStorageDirectory()+ "/img/Nebula - 6044.mp4");
            mVideoView.requestFocus();
            mVideoView.start();
        }catch(Exception e){
            e.printStackTrace();
            Log.e("dpcleaner",String.valueOf(e));
        }
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                mNoticeTextview.setText("화면 조정작업 중 입니다. "+cntExitTime+"초 후에 시스템 재시작");
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.news_anim);
                mNoticeTextview.startAnimation(animation);
            }
        }
    };
}
