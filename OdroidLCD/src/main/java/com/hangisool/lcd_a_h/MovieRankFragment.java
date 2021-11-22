package com.hangisool.lcd_a_h;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hangisool.lcd_a_h.collections.Contents;

import static com.hangisool.lcd_a_h.LcdActivity.fileName;

public class MovieRankFragment extends Fragment {
    private static Typeface typeface;
    private TextView movieText;
    private TextView movieRankText1;
    private TextView movieRankText2;
    private TextView movieRankText3;
    private TextView movieRankText4;
    private TextView movieRankText5;
    private TextView moviePerCentText1;
    private TextView moviePerCentText2;
    private TextView moviePerCentText3;
    private TextView moviePerCentText4;
    private TextView moviePerCentText5;

    public MovieRankFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MovieRankFragment newInstance() {
        MovieRankFragment fragment = new MovieRankFragment();
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
        String screenMode = LcdActivity.screenMode;
        View view;
        if(screenMode.contains("V_MODE_FULLHD")){//세로화면에 FULLHD 1920x1080이면
            view = inflater.inflate(R.layout.fragment_movie_rank_v_fullhd, null);
        }else{
            view = inflater.inflate(R.layout.fragment_movie_rank, null);
        }

        movieText = (TextView) view.findViewById(R.id.movieRankText1);
        movieRankText1 = (TextView) view.findViewById(R.id.movieRankText1);
        movieRankText2 = (TextView) view.findViewById(R.id.movieRankText2);
        movieRankText3 = (TextView) view.findViewById(R.id.movieRankText3);
        movieRankText4 = (TextView) view.findViewById(R.id.movieRankText4);
        movieRankText5 = (TextView) view.findViewById(R.id.movieRankText5);
        moviePerCentText1 = (TextView) view.findViewById(R.id.moviePerCentText1);
        moviePerCentText2 = (TextView) view.findViewById(R.id.moviePerCentText2);
        moviePerCentText3 = (TextView) view.findViewById(R.id.moviePerCentText3);
        moviePerCentText4 = (TextView) view.findViewById(R.id.moviePerCentText4);
        moviePerCentText5 = (TextView) view.findViewById(R.id.moviePerCentText5);

        if (Contents.movieRankList != null) {
            if (Contents.movieRankList.get(0) != null && Contents.movieRankList.get(1) != null && Contents.movieRankList.get(2) != null && Contents.movieRankList.get(3) != null && Contents.movieRankList.get(4) != null) {
                movieRankText1.setText(Contents.movieRankList.get(0).split(" / ")[0]);
                movieRankText2.setText(Contents.movieRankList.get(1).split(" / ")[0]);
                movieRankText3.setText(Contents.movieRankList.get(2).split(" / ")[0]);
                movieRankText4.setText(Contents.movieRankList.get(3).split(" / ")[0]);
                movieRankText5.setText(Contents.movieRankList.get(4).split(" / ")[0]);

                moviePerCentText1.setText(Contents.movieRankList.get(0).split(" / ")[1]);
                moviePerCentText2.setText(Contents.movieRankList.get(1).split(" / ")[1]);
                moviePerCentText3.setText(Contents.movieRankList.get(2).split(" / ")[1]);
                moviePerCentText4.setText(Contents.movieRankList.get(3).split(" / ")[1]);
                moviePerCentText5.setText(Contents.movieRankList.get(4).split(" / ")[1]);
            }
        }

        return view;
    }

    public void onDestroy() {
        //System.gc();
        Log.d("MoviRank", "die");
        super.onDestroy();
    }

    public void onStop() {
        super.onStop();
    }

    public void onResume() {
        super.onResume();
    }
}
