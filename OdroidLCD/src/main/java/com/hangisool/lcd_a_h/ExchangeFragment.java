package com.hangisool.lcd_a_h;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hangisool.lcd_a_h.collections.Contents;


public class ExchangeFragment extends Fragment {
    private static Typeface typeface;

    private TextView usaBuyText;
    private TextView usaSellText;
    private TextView japanBuyText;
    private TextView japanSellText;
    private TextView eurBuyText;
    private TextView eurSellText;
    private TextView chinaBuyText;
    private TextView chinaSellText;
    private TextView exchageText;



    public ExchangeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ExchangeFragment newInstance() {
        ExchangeFragment fragment = new ExchangeFragment();
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
            view = inflater.inflate(R.layout.fragment_exchange_v_fullhd, null);
        }else{
            view = inflater.inflate(R.layout.fragment_exchange, null);
        }


        exchageText = (TextView) view.findViewById(R.id.exchageText);
        usaBuyText = (TextView) view.findViewById(R.id.usaBuyText);
        usaSellText = (TextView) view.findViewById(R.id.usaSellText);
        japanBuyText = (TextView) view.findViewById(R.id.japanBuyText);
        japanSellText = (TextView) view.findViewById(R.id.japanSellText);
        eurBuyText = (TextView) view.findViewById(R.id.eurBuyText);
        eurSellText = (TextView) view.findViewById(R.id.eurSellText);
        chinaBuyText = (TextView) view.findViewById(R.id.chinaBuyText);
        chinaSellText = (TextView) view.findViewById(R.id.chinaSellText);

        try {
            usaBuyText.setText(Contents.exchangeMap.get("usa_buy"));
            usaSellText.setText(Contents.exchangeMap.get("usa_sell"));
            japanBuyText.setText(Contents.exchangeMap.get("japan_buy"));
            japanSellText.setText(Contents.exchangeMap.get("japan_sell"));
            eurBuyText.setText(Contents.exchangeMap.get("eur_buy"));
            eurSellText.setText(Contents.exchangeMap.get("eur_sell"));
            chinaBuyText.setText(Contents.exchangeMap.get("china_buy"));
            chinaSellText.setText(Contents.exchangeMap.get("china_sell"));
        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

    public void onStop(){
        super.onStop();
    }
    public void onDestroy() {
        //System.gc();
        Log.d("StockPrice", "die");
        super.onDestroy();
    }

    public void onResume(){
        super.onResume();
    }
}
