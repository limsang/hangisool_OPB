package com.hangisool.lcd_a_h;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;

public class VideoFragment extends Fragment implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private static SurfaceView sv;
    private ImageView svImgView;
    private SurfaceHolder mSh;
    private MediaPlayer mPlayer;
    private int playTime;
    private static View view;
    private long playStartTime;
    private long playEndTime;
    private static long playingTime;
    public boolean isPlaying = false;
    public static boolean captureSignal = false;
    public static boolean captureStartFalg = false;
    private Bitmap bitmap;

    public VideoFragment() {
        // Required empty public constructor
    }
    public Bitmap viewToBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        return bitmap;
    }



    private void logWrite(String logText){
        String dir = Environment.getExternalStorageDirectory()+"/log.txt";
        try {
            File file = new File(dir);
            FileOutputStream fOut = new FileOutputStream(file, true);
            fOut.write((logText+"\r\n").getBytes());
            fOut.close();
        } catch (Exception e) {
            Log.e("Video Error", e.getMessage());
        }
    }

    private int getPlayTime(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInmillisec = Long.parseLong(time);

        return (int)timeInmillisec;
    }
    public static VideoFragment newInstance() {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isPlaying = false;
        captureStartFalg = false;
        captureSignal = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_video, null);
        //logWrite("onCreate Video");
        sv = (SurfaceView) view.findViewById(R.id.svVideo);
        svImgView = (ImageView) view.findViewById(R.id.svImgView);
        mSh = sv.getHolder();
        mSh.addCallback(this);
        return view;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mPlayer = new MediaPlayer();
        Log.e("VideoFragment","surfaceCreated");
        try {
            //logWrite("surfaceCreated Video");
            mPlayer.setDataSource(LcdActivity.playPath.getPlayPath() + "/" + LcdActivity.playList.get(LcdActivity.playListIndex).split(",")[1]);
            mPlayer.setDisplay(mSh);
            mPlayer.setOnPreparedListener(this);
            mPlayer.prepareAsync();
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.e("VideoFragment","onError"+what+"  "+extra);
                    mPlayer.stop();
                    mPlayer.release();
                    return false;
                }
            });
            playTime = getPlayTime(LcdActivity.playPath.getPlayPath() + "/" + LcdActivity.playList.get(LcdActivity.playListIndex).split(",")[1]);
        } catch (Exception e) {
            //예외가 발생할경우(동영상이 깨지던가 다운로드중 비정상동작할때)
            //LcdActivity.updateFlag = true;//파일을 다시 다운로드한다.
            //동영상재생 재시작을 위한 플래그
            isPlaying = false;
            mPlayer.release();
            LcdActivity.playStatus = false;
            //**
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("VideoFragment","surfaceDestroyed");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("VideoFragment","onCompletion");
        //동영상재생이 완료되면 영상을 종료하고 다음 동영상을 재생한다.
        Log.e("setOnComplet","true");
        isPlaying = false;
        mPlayer.release();

        LcdActivity.playListIndex++;
        if (LcdActivity.playListIndex >= LcdActivity.playListLength) {
            LcdActivity.playListIndex = 0;
        }
        LcdActivity.playStatus = false;
        //logWrite("playStatus false - Video");
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //Log.e("VideoHeight", String.valueOf(mPlayer.getVideoHeight()));
        //Log.e("VideoWidth", String.valueOf(mPlayer.getVideoWidth()));
        //mSh.setSizeFromLayout();
        Log.e("VideoFragment","onPrepared");

        playStartTime = System.currentTimeMillis();
        isPlaying = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while(isPlaying){
                                try {
                                    Thread.sleep(1);
                                    playEndTime = System.currentTimeMillis();
                                    playingTime = (playEndTime - playStartTime)/1000;
                                    //Log.d("playTime", playingTime + "");
                                    if(captureSignal){


                                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                                        mmr.setDataSource(LcdActivity.playPath.getPlayPath() + "/" + LcdActivity.playList.get(LcdActivity.playListIndex).split(",")[1]);
                                        Log.d("playing Time" , playingTime+"sec");
                                        bitmap = mmr.getFrameAtTime(playingTime*1000000);

                                        handler.sendEmptyMessage(1);
                                        Thread.sleep(1);
                                        while (true){
                                            if(!captureSignal || !isPlaying){
                                                handler.sendEmptyMessage(2);
                                                break;
                                            }
                                            Thread.sleep(1);
                                        }
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();

                    //Thread.sleep(playTime);

                    //logWrite("onCompletion Video");
                    //Log.d("=======VIDEO======", "============Comple===========");
                }catch (Exception e){
                    e.printStackTrace();
                    Logger logger = Logger.getLogger("");
                    logger.error(e);
                    Log.d("=======VIDEO======", "============FAIL===========");
                }

            }
        }).start();

        if(LcdActivity.screenSizeMode == 1) {//mainview비율에 맞춰 표현하면
            // Surfaceview화면에 표출되는 동영상을 화면 비율에 맞게 변경해주는 로직
            int videoWidth = mPlayer.getVideoWidth();
            int videoHeight = mPlayer.getVideoHeight();
            float videoProportion = (float) videoWidth / (float) videoHeight;
            WindowManager wm = (WindowManager) LcdActivity.mContext.getSystemService(Context.WINDOW_SERVICE);
            int screenWidth = LcdActivity.mainViewWidth;//비디오가 그려지는 mainView의 가로길이 - 비디오는 mainView안에서 재생되는데 그것보다 큰 크기인 스크린크기를 넣게되면 영상이 깨짐
            int screenHeight = LcdActivity.mainViewHeight;//비디오가 그려지는 mainView의 세로길이
            float screenProportion = (float) screenWidth / (float) screenHeight;
            android.view.ViewGroup.LayoutParams lp = sv.getLayoutParams();

            if (videoProportion > screenProportion) {
                lp.width = screenWidth;
                lp.height = (int) ((float) screenWidth / videoProportion);
            } else {
                lp.width = (int) (videoProportion * (float) screenHeight);
                lp.height = screenHeight;
            }
            sv.setLayoutParams(lp);
            //캡처시에 surface view 위에 image view가 잠시 올라갔다가 내려가는데
            //그 image view의 비율을 맞춰주기위해서
            //true이면 비율에 맞게 늘어나거나 줄어들고 false이면 무조건 화면에 맞게 늘어남
            svImgView.setAdjustViewBounds(true);
        }else if(LcdActivity.screenSizeMode == 0){//mainview에 꽉채워서 표현하면
        }
        mPlayer.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                //mPlayer.pause();
                //sv.setVisibility(View.INVISIBLE);
                svImgView.setVisibility(View.VISIBLE);
                svImgView.setImageBitmap(bitmap);
                captureStartFalg = true;
            }
            if (msg.what == 2) {
                //sv.setVisibility(View.VISIBLE);
                svImgView.setVisibility(View.INVISIBLE);
                captureStartFalg = false;
                //mPlayer.start();
            }
        }
    };
}
