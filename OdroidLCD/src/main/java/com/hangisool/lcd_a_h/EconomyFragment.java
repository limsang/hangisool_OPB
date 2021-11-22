package com.hangisool.lcd_a_h;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EconomyFragment extends Fragment {
    private static Typeface typeface;


    public EconomyFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static EconomyFragment newInstance() {
        EconomyFragment fragment = new EconomyFragment();
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
        View view = inflater.inflate(R.layout.economy_point, null);

        return view;
    }

    public void onDestroy() {
        //System.gc();
        Log.d("EconomyFragment", "die");
        super.onDestroy();
    }
    public void onStop(){
        super.onStop();
    }

    public void onResume(){
        super.onResume();
    }
}


