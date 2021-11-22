package com.hangisool.lcd_a_h;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

//FloorList정보 fragment 이미지를 표시
public class FloorListImageFragment extends Fragment {
    private static Typeface typeface;
    private ImageView floor_list_image;
    int page;

    public FloorListImageFragment() {
        // Required empty public constructor
        page = LcdActivity.cnt_floorlst_page;
    }

    // TODO: Rename and change types and number of parameters
    public static FloorListImageFragment newInstance() {
        FloorListImageFragment fragment = new FloorListImageFragment();
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
        View view = inflater.inflate(R.layout.fragment_floor_list_image, null);
        floor_list_image = (ImageView)view.findViewById(R.id.floor_list_image);
        File file = new File(Environment.getExternalStoragePublicDirectory("floorPicture"), String.valueOf(page)+".png");

        Log.e("floorPicture",String.valueOf(page));
        try {
            Glide.with(LcdActivity.mContext)
                    .load(file)
                    .into(floor_list_image);
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    public void onDestroy() {
        //System.gc();
        Log.d("FloorListImageFragment", "die");
        super.onDestroy();
    }

    public void onStop() {
        super.onStop();
    }

    public void onResume() {
        super.onResume();
    }
}
