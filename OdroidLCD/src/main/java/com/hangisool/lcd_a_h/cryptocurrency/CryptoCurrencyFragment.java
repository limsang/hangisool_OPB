package com.hangisool.lcd_a_h.cryptocurrency;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hangisool.lcd_a_h.LcdActivity;
import com.hangisool.lcd_a_h.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.*;

public class CryptoCurrencyFragment extends Fragment {
    TextView txt_crypto_name1,txt_crypto_name2,txt_crypto_name3,txt_crypto_name4,txt_crypto_name5,txt_crypto_name6;
    TextView txt_crypto_price1,txt_crypto_price2,txt_crypto_price3,txt_crypto_price4,txt_crypto_price5,txt_crypto_price6;
    TextView txt_crypto_percent1,txt_crypto_percent2,txt_crypto_percent3,txt_crypto_percent4,txt_crypto_percent5,txt_crypto_percent6;
    TextView txt_crypto_change_price1,txt_crypto_change_price2,txt_crypto_change_price3,txt_crypto_change_price4,txt_crypto_change_price5,txt_crypto_change_price6;
    ImageView img_crypto_arrow1,img_crypto_arrow2,img_crypto_arrow3,img_crypto_arrow4,img_crypto_arrow5,img_crypto_arrow6;
    View view;
    boolean flagThread = true;
    public static CryptoCurrencyFragment newInstance() {

        Bundle args = new Bundle();

        CryptoCurrencyFragment fragment = new CryptoCurrencyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_crypto_info,container,false);
        init_component();
        cryptoCurrencyAPIThread();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        flagThread = false;
    }

    void init_component(){
        txt_crypto_name1 = view.findViewById(R.id.txt_crypto_name1);
        txt_crypto_name2 = view.findViewById(R.id.txt_crypto_name2);
        txt_crypto_name3 = view.findViewById(R.id.txt_crypto_name3);
        txt_crypto_name4 = view.findViewById(R.id.txt_crypto_name4);
        txt_crypto_name5 = view.findViewById(R.id.txt_crypto_name5);
        txt_crypto_name6 = view.findViewById(R.id.txt_crypto_name6);

        txt_crypto_price1 = view.findViewById(R.id.txt_crypto_price1);
        txt_crypto_price2 = view.findViewById(R.id.txt_crypto_price2);
        txt_crypto_price3 = view.findViewById(R.id.txt_crypto_price3);
        txt_crypto_price4 = view.findViewById(R.id.txt_crypto_price4);
        txt_crypto_price5 = view.findViewById(R.id.txt_crypto_price5);
        txt_crypto_price6 = view.findViewById(R.id.txt_crypto_price6);

        txt_crypto_percent1 = view.findViewById(R.id.txt_crypto_percent1);
        txt_crypto_percent2 = view.findViewById(R.id.txt_crypto_percent2);
        txt_crypto_percent3 = view.findViewById(R.id.txt_crypto_percent3);
        txt_crypto_percent4 = view.findViewById(R.id.txt_crypto_percent4);
        txt_crypto_percent5 = view.findViewById(R.id.txt_crypto_percent5);
        txt_crypto_percent6 = view.findViewById(R.id.txt_crypto_percent6);

        txt_crypto_change_price1 = view.findViewById(R.id.txt_crypto_change_price1);
        txt_crypto_change_price2 = view.findViewById(R.id.txt_crypto_change_price2);
        txt_crypto_change_price3 = view.findViewById(R.id.txt_crypto_change_price3);
        txt_crypto_change_price4 = view.findViewById(R.id.txt_crypto_change_price4);
        txt_crypto_change_price5 = view.findViewById(R.id.txt_crypto_change_price5);
        txt_crypto_change_price6 = view.findViewById(R.id.txt_crypto_change_price6);

        img_crypto_arrow1 = view.findViewById(R.id.img_crypto_arrow1);
        img_crypto_arrow2 = view.findViewById(R.id.img_crypto_arrow2);
        img_crypto_arrow3 = view.findViewById(R.id.img_crypto_arrow3);
        img_crypto_arrow4 = view.findViewById(R.id.img_crypto_arrow4);
        img_crypto_arrow5 = view.findViewById(R.id.img_crypto_arrow5);
        img_crypto_arrow6 = view.findViewById(R.id.img_crypto_arrow6);
    }

    void cryptoCurrencyAPIThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (flagThread) {
                    try {
                        //10 초에 한번씩 새로고침
                        requestCryptoInfo();
                        Thread.sleep(1000 * 10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    void requestCryptoInfo(){
        CryptoInfoAsyncTask cryptoInfoAsyncTask = new CryptoInfoAsyncTask();
        cryptoInfoAsyncTask.execute("KRW-BTC,KRW-ETH,KRW-XRP,KRW-LTC,KRW-EOS,KRW-TRX");
    }
    public class CryptoInfoAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

        @Override
        protected Void doInBackground(String... strings) {
            String market_name = strings[0];
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(CryptoPriceAPI.API_SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            CryptoPriceAPI cryptoPriceAPI = retrofit.create(CryptoPriceAPI.class);

            cryptoPriceAPI.getCurrencyData(market_name)
                    .enqueue(new Callback<List<CryptoCurrencyData>>() {
                        @Override
                        public void onResponse(Call<List<CryptoCurrencyData>> call, Response<List<CryptoCurrencyData>> response) {
                            if (response.isSuccessful()) {
                                Gson gson = new Gson();
                                if (response.body() != null) {
                                    setCryptoInfos(response.body());
                                } else {
                                    toastMessage("upbit API에서 에러발생 관리자에게 문의");
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<CryptoCurrencyData>> call, Throwable t) {
                            Log.e("FailedCrypto", t.getMessage());
                            Toast.makeText(LcdActivity.mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            return null;
        }

        void toastMessage(String message) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }
    void setCryptoInfos(List<CryptoCurrencyData> data){
        txt_crypto_name1.setText("비트코인");
        txt_crypto_name2.setText("이더리움");
        txt_crypto_name3.setText("리플");
        txt_crypto_name4.setText("라이트코인");
        txt_crypto_name5.setText("이오스");
        txt_crypto_name6.setText("트론");

        txt_crypto_price1.setText(String.format("%.1f",data.get(0).getTradePrice()));
        txt_crypto_price2.setText(String.format("%.1f",data.get(1).getTradePrice()));
        txt_crypto_price3.setText(String.format("%.1f",data.get(2).getTradePrice()));
        txt_crypto_price4.setText(String.format("%.1f",data.get(3).getTradePrice()));
        txt_crypto_price5.setText(String.format("%.1f",data.get(4).getTradePrice()));
        txt_crypto_price6.setText(String.format("%.1f",data.get(5).getTradePrice()));

        txt_crypto_change_price1.setText(String.format("%.1f",data.get(0).getChangePrice()));
        txt_crypto_change_price2.setText(String.format("%.1f",data.get(1).getChangePrice()));
        txt_crypto_change_price3.setText(String.format("%.1f",data.get(2).getChangePrice()));
        txt_crypto_change_price4.setText(String.format("%.1f",data.get(3).getChangePrice()));
        txt_crypto_change_price5.setText(String.format("%.1f",data.get(4).getChangePrice()));
        txt_crypto_change_price6.setText(String.format("%.1f",data.get(5).getChangePrice()));

        txt_crypto_percent1.setText(String.format("%.2f",data.get(0).getChangeRate()*100) + "%");
        txt_crypto_percent2.setText(String.format("%.2f",data.get(1).getChangeRate()*100) + "%");
        txt_crypto_percent3.setText(String.format("%.2f",data.get(2).getChangeRate()*100) + "%");
        txt_crypto_percent4.setText(String.format("%.2f",data.get(3).getChangeRate()*100) + "%");
        txt_crypto_percent5.setText(String.format("%.2f",data.get(4).getChangeRate()*100) + "%");
        txt_crypto_percent6.setText(String.format("%.2f",data.get(5).getChangeRate()*100) + "%");

        String change = data.get(0).getChange();
        if(change.equals("FALL")){//하락
            img_crypto_arrow1.setImageResource(R.drawable.ic_arrow_drop_down);
        }else if(change.equals("RISE")){//상승
            img_crypto_arrow1.setImageResource(R.drawable.ic_arrow_drop_up);
        }else if(change.equals("EVEN")){//보합
            img_crypto_arrow1.setImageResource(R.drawable.none);
        }
        change = data.get(1).getChange();
        if(change.equals("FALL")){//하락
            img_crypto_arrow2.setImageResource(R.drawable.ic_arrow_drop_down);
        }else if(change.equals("RISE")){//상승
            img_crypto_arrow2.setImageResource(R.drawable.ic_arrow_drop_up);
        }else if(change.equals("EVEN")){//보합
            img_crypto_arrow2.setImageResource(R.drawable.none);
        }
        change = data.get(2).getChange();
        if(change.equals("FALL")){//하락
            img_crypto_arrow3.setImageResource(R.drawable.ic_arrow_drop_down);
        }else if(change.equals("RISE")){//상승
            img_crypto_arrow3.setImageResource(R.drawable.ic_arrow_drop_up);
        }else if(change.equals("EVEN")){//보합
            img_crypto_arrow3.setImageResource(R.drawable.none);
        }
        change = data.get(3).getChange();
        if(change.equals("FALL")){//하락
            img_crypto_arrow4.setImageResource(R.drawable.ic_arrow_drop_down);
        }else if(change.equals("RISE")){//상승
            img_crypto_arrow4.setImageResource(R.drawable.ic_arrow_drop_up);
        }else if(change.equals("EVEN")){//보합
            img_crypto_arrow4.setImageResource(R.drawable.none);
        }
        change = data.get(4).getChange();
        if(change.equals("FALL")){//하락
            img_crypto_arrow5.setImageResource(R.drawable.ic_arrow_drop_down);
        }else if(change.equals("RISE")){//상승
            img_crypto_arrow5.setImageResource(R.drawable.ic_arrow_drop_up);
        }else if(change.equals("EVEN")){//보합
            img_crypto_arrow5.setImageResource(R.drawable.none);
        }
        change = data.get(5).getChange();
        if(change.equals("FALL")){//하락
            img_crypto_arrow6.setImageResource(R.drawable.ic_arrow_drop_down);
        }else if(change.equals("RISE")){//상승
            img_crypto_arrow6.setImageResource(R.drawable.ic_arrow_drop_up);
        }else if(change.equals("EVEN")){//보합
            img_crypto_arrow6.setImageResource(R.drawable.none);
        }

        /*for(int i = 0; i<6; i++) {
            Log.e("TEST", data.get(i).getMarket());
            Log.e("TEST", data.get(i).getTradePrice().toString());
            Log.e("TEST", data.get(i).getChangePrice().toString());
            Log.e("TEST", data.get(i).getChangeRate().toString());
            Log.e("TEST", data.get(i).getChange());
        }*/

    }
}
