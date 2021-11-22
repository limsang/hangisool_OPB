package com.hangisool.lcd_a_h;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hangisool.lcd_a_h.playmanager.SetPlayList;

import static com.hangisool.lcd_a_h.LcdActivity.playPath;
import static com.hangisool.lcd_a_h.LcdActivity.playList;

public class ImageFragment extends Fragment implements Runnable{

    Thread t;
    private ImageView mainImgView;

    public ImageFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ImageFragment newInstance() {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, null);

        mainImgView = (ImageView) view.findViewById(R.id.mainImageView);
        if(LcdActivity.playList == null){
            playList = SetPlayList.getPlayList(playPath.getPlayPath() + "/playList.txt");
        }
        try{
            Bitmap selectedImage = BitmapFactory.decodeFile(playPath.getPlayPath() + "/" + playList.get(LcdActivity.playListIndex).split(",")[1]);
            //다운로드가 정상적으로 완료되지 않아 해당 경로에 이미지를 load했을때 null을 반환하면 재 다운로드
            if(selectedImage == null){
                LcdActivity.updateFlag = true;
            }
            mainImgView.setImageBitmap(selectedImage);
        }catch (Exception e){
            e.printStackTrace();
        }


        t = new Thread(this);
        t.start();
        if(LcdActivity.screenSizeMode == 1) {//mainview비율에 맞춰 표현하면
            mainImgView.setAdjustViewBounds(true);
        }else if(LcdActivity.screenSizeMode == 0){//mainview에 꽉채워서 표현하면
        }

        return view;
    }

    public void onStop(){
        super.onStop();
        t.interrupt();
    }

    public void onResume(){
        super.onResume();
    }

    @Override
    public void run() {
     //   while(true){
            try {
                Thread.sleep(Integer.parseInt(LcdActivity.playList.get(LcdActivity.playListIndex).split(",")[2]));

                LcdActivity.playListIndex++;
                if(LcdActivity.playListIndex >= LcdActivity.playListLength) {
                    LcdActivity.playListIndex = 0;
                }
                LcdActivity.playStatus = false;
          //      break;
            }catch (Exception e){
                e.printStackTrace();
            }
      //  }
    }
}
