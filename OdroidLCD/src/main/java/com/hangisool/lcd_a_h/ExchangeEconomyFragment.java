package com.hangisool.lcd_a_h;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hangisool.lcd_a_h.collections.Contents;

public class ExchangeEconomyFragment extends Fragment {

    private TextView usaBuyText;
    private TextView usaSellText;
    private TextView japanBuyText;
    private TextView japanSellText;
    private TextView eurBuyText;
    private TextView eurSellText;
    private TextView chinaBuyText;
    private TextView chinaSellText;
    private TextView fragmentKospiValue;
    private TextView fragmentKosdaqValue;
    private TextView fragmentKospiArrow;
    private TextView fragmentKosdaqArrow;

    public ExchangeEconomyFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ExchangeEconomyFragment newInstance() {
        ExchangeEconomyFragment fragment = new ExchangeEconomyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    //teset

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exchange_economy, null);

        usaBuyText = (TextView) view.findViewById(R.id.usaBuyText);
        usaSellText = (TextView) view.findViewById(R.id.usaSellText);
        japanBuyText = (TextView) view.findViewById(R.id.japanBuyText);
        japanSellText = (TextView) view.findViewById(R.id.japanSellText);
        eurBuyText = (TextView) view.findViewById(R.id.eurBuyText);
        eurSellText = (TextView) view.findViewById(R.id.eurSellText);
        chinaBuyText = (TextView) view.findViewById(R.id.chinaBuyText);
        chinaSellText = (TextView) view.findViewById(R.id.chinaSellText);
        fragmentKospiValue = (TextView) view.findViewById(R.id.fragmentKospi);
        fragmentKosdaqValue = (TextView) view.findViewById(R.id.fragmentKosdaq);
        fragmentKospiArrow = (TextView) view.findViewById(R.id.fragmentKospiArrow);
        fragmentKosdaqArrow = (TextView) view.findViewById(R.id.fragmentKosdaqArrow);

        try {
            usaBuyText.setText(Contents.exchangeMap.get("usa_buy"));
            usaSellText.setText(Contents.exchangeMap.get("usa_sell"));
            japanBuyText.setText(Contents.exchangeMap.get("japan_buy"));
            japanSellText.setText(Contents.exchangeMap.get("japan_sell"));
            eurBuyText.setText(Contents.exchangeMap.get("eur_buy"));
            eurSellText.setText(Contents.exchangeMap.get("eur_sell"));
            chinaBuyText.setText(Contents.exchangeMap.get("china_buy"));
            chinaSellText.setText(Contents.exchangeMap.get("china_sell"));

            fragmentKospiValue.setText(Contents.stockPriceMap.get("kospi_price"));
            if (Contents.stockPriceMap.get("kospi_contrast") != null) {
                if (Double.parseDouble(Contents.stockPriceMap.get("kospi_contrast")) >= 0) {
                    fragmentKospiArrow.setTextColor(Color.parseColor("#FF5A5A"));
                    fragmentKospiArrow.setText("▲" + Contents.stockPriceMap.get("kospi_contrast"));
                } else {

                    fragmentKospiArrow.setTextColor(Color.parseColor("#489CFF"));
                    fragmentKospiArrow.setText("▼" + Contents.stockPriceMap.get("kospi_contrast"));
                }
            }

            fragmentKosdaqValue.setText(Contents.stockPriceMap.get("kosdaq_price"));
            if (Contents.stockPriceMap.get("kosdaq_contrast") != null) {
                if (Double.parseDouble(Contents.stockPriceMap.get("kosdaq_contrast")) >= 0) {
                    fragmentKosdaqArrow.setTextColor(Color.parseColor("#FF5A5A"));
                    fragmentKosdaqArrow.setText("▲" + Contents.stockPriceMap.get("kosdaq_contrast"));
                } else {
                    fragmentKosdaqArrow.setTextColor(Color.parseColor("#489CFF"));
                    fragmentKosdaqArrow.setText("▼" + Contents.stockPriceMap.get("kosdaq_contrast"));
                }
            }

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
