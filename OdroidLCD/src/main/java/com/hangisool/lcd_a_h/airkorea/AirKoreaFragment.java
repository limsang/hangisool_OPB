package com.hangisool.lcd_a_h.airkorea;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hangisool.lcd_a_h.LcdActivity;
import com.hangisool.lcd_a_h.R;
import com.hangisool.lcd_a_h.backend.airkoreaapi.AirKoreaAPI;
import com.hangisool.lcd_a_h.backend.airkoreaapi.AirKoreaData;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hangisool.lcd_a_h.AreaSettingActivity.SP_airRegionFile;
import static com.hangisool.lcd_a_h.AreaSettingActivity.SP_airRegionName;
import static com.hangisool.lcd_a_h.AreaSettingActivity.SP_airRegionNameDetail;
import static com.hangisool.lcd_a_h.backend.emercall.Data.getUnsafeOkHttpClient;

public class AirKoreaFragment extends Fragment {
    public static AirKoreaData airKoreaData;
    ImageView img_coGrade, img_o3Grade, img_no2Grade, img_pm25Grade1h, img_pm10Grade1h, img_khaiGrade;
    TextView txt_air_region_info, txt_khaiGrade, txt_khaiValue, txt_pm10Grade1h, txt_pm10Value, txt_pm25Grade1h,
            txt_pm25Value, txt_no2Grade, txt_no2Value, txt_o3Grade, txt_o3Value, txt_coGrade, txt_coValue;
    LinearLayout air_backcolor1, air_backcolor2;
    ViewGroup view;
    boolean flagThread = true;

    public static AirKoreaFragment newInstance() {


        Bundle args = new Bundle();

        AirKoreaFragment fragment = new AirKoreaFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.fragment_airkorea, container, false);
        initComponent();
        airAPIThread();
        return view;
    }

    void airAPIThread() {
        new Thread(new Runnable() {
            String time;

            @Override
            public void run() {
                //처음시작시 API요청
                requestAirInfo();
                while (flagThread) {
                    try {
                        time = new SimpleDateFormat("mm").format(new Date());
                        if (time.equals("01")) {//분침이 1분일경우는 한시간에 한번뿐
                            requestAirInfo();//한시간에 한번 대기질정보요청
                        }
                        Thread.sleep(60 * 1000);//1분에 한번씩 체크
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    void requestAirInfo() {
        AirKoreaAsyncTask airKoreaAsyncTask = new AirKoreaAsyncTask();
        airKoreaAsyncTask.execute(getRegion());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("AirKoreaFragment", "onDestroyView");
        flagThread = false;
    }

    void initComponent() {
        txt_air_region_info = view.findViewById(R.id.txt_air_region_info);//위치표시 텍스트
        img_khaiGrade = view.findViewById(R.id.img_khaiGrade);//통합대기환경 등급 이미지
        txt_khaiGrade = view.findViewById(R.id.txt_khaiGrade);//통합대기환경 등급 텍스트
        txt_khaiValue = view.findViewById(R.id.txt_khaiValue);//통합대기환경지수값 텍스트
        img_pm10Grade1h = view.findViewById(R.id.img_pm10Grade1h);//미세먼지 등급 이미지
        txt_pm10Grade1h = view.findViewById(R.id.txt_pm10Grade1h);//미세먼지 등급 텍스트
        txt_pm10Value = view.findViewById(R.id.txt_pm10Value);//미세먼지지수값 텍스트
        img_pm25Grade1h = view.findViewById(R.id.img_pm25Grade1h);//초미세먼지 등급 이미지
        txt_pm25Grade1h = view.findViewById(R.id.txt_pm25Grade1h);//초미세먼지 등급 텍스트
        txt_pm25Value = view.findViewById(R.id.txt_pm25Value);//초미세먼지지수값 텍스트
        img_no2Grade = view.findViewById(R.id.img_no2Grade);//이산화질소 등급 이미지
        txt_no2Grade = view.findViewById(R.id.txt_no2Grade);//이산화질소 등급 텍스트
        txt_no2Value = view.findViewById(R.id.txt_no2Value);//이산화질소지수값 텍스트
        img_o3Grade = view.findViewById(R.id.img_o3Grade);//오존 등급 이미지
        txt_o3Grade = view.findViewById(R.id.txt_o3Grade);//오존 등급 텍스트
        txt_o3Value = view.findViewById(R.id.txt_o3Value);//오존 지수값 텍스트
        img_coGrade = view.findViewById(R.id.img_coGrade);//일산화탄소 등급 이미지
        txt_coGrade = view.findViewById(R.id.txt_coGrade);//일산화질소 등급 텍스트
        txt_coValue = view.findViewById(R.id.txt_coValue);//일산화질소 지수값 텍스트
        air_backcolor2 = view.findViewById(R.id.air_backcolor2);//하단 배경화면
        air_backcolor1 = view.findViewById(R.id.air_backcolor1);//상단 배경화면

        txt_air_region_info.setText(getRegionDetail() + " " + "실시간 대기오염정보");
    }

    public String getRegion() {
        SharedPreferences pref = getActivity().getSharedPreferences(SP_airRegionFile, 0);
        String air_region = pref.getString(SP_airRegionName, "지역설정필요");
        return air_region;
    }

    public String getRegionDetail() {
        SharedPreferences pref = getActivity().getSharedPreferences(SP_airRegionFile, 0);
        String air_region = pref.getString(SP_airRegionNameDetail, "");
        return air_region;
    }

    public void setAirInfo(AirKoreaData data, int index) {
        String khaiGrade = data.getList().get(index).getKhaiGrade();
        String khaiValue = data.getList().get(index).getKhaiValue();
        txt_khaiValue.setText("통합대기환경수치 : " + khaiValue);
        switch (khaiGrade) {
            case "1"://좋음
                img_khaiGrade.setImageResource(R.drawable.ic_dust_good);
                txt_khaiGrade.setText("좋음");
                air_backcolor1.setBackgroundResource(R.color.air_good_top);
                air_backcolor2.setBackgroundResource(R.color.air_good_bottom);
                break;
            case "2":
                img_khaiGrade.setImageResource(R.drawable.ic_dust_soso);
                txt_khaiGrade.setText("보통");
                air_backcolor1.setBackgroundResource(R.color.air_soso_top);
                air_backcolor2.setBackgroundResource(R.color.air_soso_bottom);
                break;
            case "3":
                img_khaiGrade.setImageResource(R.drawable.ic_dust_bad);
                txt_khaiGrade.setText("나쁨");
                air_backcolor1.setBackgroundResource(R.color.air_bad_top);
                air_backcolor2.setBackgroundResource(R.color.air_bad_bottom);
                break;
            case "4":
                img_khaiGrade.setImageResource(R.drawable.ic_dust_verybad);
                txt_khaiGrade.setText("매우 나쁨");
                air_backcolor1.setBackgroundResource(R.color.air_verybad_top);
                air_backcolor2.setBackgroundResource(R.color.air_verybad_bottom);
                break;
            default:
                img_khaiGrade.setImageResource(R.drawable.ic_dust_verybad);
                txt_khaiGrade.setText("매우 나쁨");
                air_backcolor1.setBackgroundResource(R.color.air_verybad_top);
                air_backcolor2.setBackgroundResource(R.color.air_verybad_bottom);
                break;
        }
        String pm10Grade1h = data.getList().get(index).getPm10Grade1h();
        String pm10Value = data.getList().get(index).getPm10Value();
        txt_pm10Value.setText(pm10Value + " " + "㎍/m³");
        switch (pm10Grade1h) {
            case "1"://좋음
                img_pm10Grade1h.setImageResource(R.drawable.ic_dust_good);
                txt_pm10Grade1h.setText("좋음");
                break;
            case "2":
                img_pm10Grade1h.setImageResource(R.drawable.ic_dust_soso);
                txt_pm10Grade1h.setText("보통");
                break;
            case "3":
                img_pm10Grade1h.setImageResource(R.drawable.ic_dust_bad);
                txt_pm10Grade1h.setText("나쁨");
                break;
            case "4":
                img_pm10Grade1h.setImageResource(R.drawable.ic_dust_verybad);
                txt_pm10Grade1h.setText("매우 나쁨");
                break;
            default:
                img_pm10Grade1h.setImageResource(R.drawable.ic_dust_verybad);
                txt_pm10Grade1h.setText("매우 나쁨");
                break;
        }
        String pm25Grade1h = data.getList().get(index).getPm25Grade1h();
        String pm25Value = data.getList().get(index).getPm25Value();
        txt_pm25Value.setText(pm25Value + " " + "㎍/m³");
        switch (pm25Grade1h) {
            case "1"://좋음
                img_pm25Grade1h.setImageResource(R.drawable.ic_dust_good);
                txt_pm25Grade1h.setText("좋음");
                break;
            case "2":
                img_pm25Grade1h.setImageResource(R.drawable.ic_dust_soso);
                txt_pm25Grade1h.setText("보통");
                break;
            case "3":
                img_pm25Grade1h.setImageResource(R.drawable.ic_dust_bad);
                txt_pm25Grade1h.setText("나쁨");
                break;
            case "4":
                img_pm25Grade1h.setImageResource(R.drawable.ic_dust_verybad);
                txt_pm25Grade1h.setText("매우 나쁨");
                break;
            default:
                img_pm25Grade1h.setImageResource(R.drawable.ic_dust_verybad);
                txt_pm25Grade1h.setText("매우 나쁨");
                break;
        }
        String no2Grade = data.getList().get(index).getNo2Grade();
        String no2Value = data.getList().get(index).getNo2Value();
        txt_no2Value.setText(no2Value + " " + "ppm");
        switch (no2Grade) {
            case "1"://좋음
                img_no2Grade.setImageResource(R.drawable.ic_dust_good);
                txt_no2Grade.setText("좋음");
                break;
            case "2":
                img_no2Grade.setImageResource(R.drawable.ic_dust_soso);
                txt_no2Grade.setText("보통");
                break;
            case "3":
                img_no2Grade.setImageResource(R.drawable.ic_dust_bad);
                txt_no2Grade.setText("나쁨");
                break;
            case "4":
                img_no2Grade.setImageResource(R.drawable.ic_dust_verybad);
                txt_no2Grade.setText("매우 나쁨");
                break;
            default:
                img_no2Grade.setImageResource(R.drawable.ic_dust_verybad);
                txt_no2Grade.setText("매우 나쁨");
                break;
        }
        String o3Grade = data.getList().get(index).getO3Grade();
        String o3Value = data.getList().get(index).getO3Value();
        txt_o3Value.setText(o3Value + " " + "ppm");
        switch (o3Grade) {
            case "1"://좋음
                img_o3Grade.setImageResource(R.drawable.ic_dust_good);
                txt_o3Grade.setText("좋음");
                break;
            case "2":
                img_o3Grade.setImageResource(R.drawable.ic_dust_soso);
                txt_o3Grade.setText("보통");
                break;
            case "3":
                img_o3Grade.setImageResource(R.drawable.ic_dust_bad);
                txt_o3Grade.setText("나쁨");
                break;
            case "4":
                img_o3Grade.setImageResource(R.drawable.ic_dust_verybad);
                txt_o3Grade.setText("매우 나쁨");
                break;
            default:
                img_o3Grade.setImageResource(R.drawable.ic_dust_verybad);
                txt_o3Grade.setText("매우 나쁨");
                break;
        }
        String coGrade = data.getList().get(index).getCoGrade();
        String coValue = data.getList().get(index).getCoValue();
        txt_coValue.setText(coValue + " " + "ppm");
        switch (coGrade) {
            case "1"://좋음
                img_coGrade.setImageResource(R.drawable.ic_dust_good);
                txt_coGrade.setText("좋음");
                break;
            case "2":
                img_coGrade.setImageResource(R.drawable.ic_dust_soso);
                txt_coGrade.setText("보통");
                break;
            case "3":
                img_coGrade.setImageResource(R.drawable.ic_dust_bad);
                txt_coGrade.setText("나쁨");
                break;
            case "4":
                img_coGrade.setImageResource(R.drawable.ic_dust_verybad);
                txt_coGrade.setText("매우 나쁨");
                break;
            default:
                img_coGrade.setImageResource(R.drawable.ic_dust_verybad);
                txt_coGrade.setText("매우 나쁨");
                break;
        }
    }

    public class AirKoreaAsyncTask extends AsyncTask<String, Void, Void> {

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
            String regionName = strings[0];
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AirKoreaAPI.API_SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getUnsafeOkHttpClient().build())
                    .build();
            AirKoreaAPI airKoreaAPI = retrofit.create(AirKoreaAPI.class);

            airKoreaAPI.getAirData(AirKoreaAPI.AUTH_KEY, 1000, regionName, "1.3", "json")
                    .enqueue(new Callback<AirKoreaData>() {
                        @Override
                        public void onResponse(Call<AirKoreaData> call, Response<AirKoreaData> response) {
                            if (response.isSuccessful()) {
                                Gson gson = new Gson();
                                if (response.body() != null) {
                                    airKoreaData = response.body();
                                    int dataSize = airKoreaData.getList().size();
                                    //가져온 미세먼지 데이터중 사용자가 선택한 지역을 찾는다.
                                    for (int i = 0; i < dataSize; i++) {
                                        if (airKoreaData.getList().get(i).getStationName().equals(getRegionDetail())) {
                                            //사용자가 선택한 지역 데이터를 찾으면
                                            setAirInfo(airKoreaData, i);
                                            break;
                                        }
                                        if (i == dataSize - 1) {//선탠한 지역데이터를 못찾았을때
                                            toastMessage("지역정보(동,구) 재설정 필요");
                                        }
                                    }
                                } else {
                                    toastMessage("대기오염정보API에서 에러발생 관리자에게 문의");
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<AirKoreaData> call, Throwable t) {
                            Log.e("FailedAir", t.getMessage());
                            Toast.makeText(LcdActivity.mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            return null;
        }

        void toastMessage(String message) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }
}