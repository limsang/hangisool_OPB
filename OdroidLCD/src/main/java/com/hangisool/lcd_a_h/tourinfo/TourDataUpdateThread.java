package com.hangisool.lcd_a_h.tourinfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hangisool.lcd_a_h.LcdActivity;
import com.hangisool.lcd_a_h.backend.arealistapi.AreaListAPI;
import com.hangisool.lcd_a_h.backend.areatourapi.AreaTourAPI;
import com.hangisool.lcd_a_h.backend.areatourapi.AreaTourData;
import com.hangisool.lcd_a_h.backend.festivalapi.FestivalAPI;
import com.hangisool.lcd_a_h.backend.festivalapi.FestivalData;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hangisool.lcd_a_h.AreaSettingActivity.SP_tourAreaCode;
import static com.hangisool.lcd_a_h.AreaSettingActivity.SP_tourAreaFile;
import static com.hangisool.lcd_a_h.backend.emercall.Data.getUnsafeOkHttpClient;

public class TourDataUpdateThread extends Thread{
    public static AreaTourData areaTourData_kor;
    public static AreaTourData areaTourData_eng;
    public static AreaTourData areaTourData_chn;
    public static AreaTourData areaTourData_jpn;
    //AREA TOUR INFORMATION
    public static String KOR_TOUR_KEY = "KOR_TOUR_KEY";
    public static String ENG_TOUR_KEY = "ENG_TOUR_KEY";
    public static String CHN_TOUR_KEY = "CHN_TOUR_KEY";
    public static String JPN_TOUR_KEY = "JPN_TOUR_KEY";
    //CURTURAL FECILLIY
    public static String KOR_CUR_KEY = "KOR_CUR_KEY";
    public static String ENG_CUR_KEY = "ENG_CUR_KEY";
    public static String CHN_CUR_KEY = "CHN_CUR_KEY";
    public static String JPN_CUR_KEY = "JPN_CUR_KEY";
    //FESTIVAL
    public static String KOR_FEST_KEY = "KOR_FEST_KEY";
    public static String ENG_FEST_KEY = "ENG_FEST_KEY";
    public static String CHN_FEST_KEY = "CHN_FEST_KEY";
    public static String JPN_FEST_KEY = "JPN_FEST_KEY";

    @Override
    public void run() {
        while(true){
            TEST_TOURDATA();
            try {
                Thread.sleep(60000*60*24);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void TEST_TOURDATA(){
        // getting Area code
        SharedPreferences pref = (SharedPreferences) LcdActivity.mContext.getSharedPreferences(SP_tourAreaFile, 0);
        int area_code = pref.getInt(SP_tourAreaCode, 0);
        Log.e("area_code", String.valueOf(area_code));
        // getting Date time
        String date = getDate();
        int NumOfRows = 20;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AreaTourAPI.API_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getUnsafeOkHttpClient().build())
                .build();

        /*관광지정보 Load*/
        AreaTourAPI tourAPI =retrofit.create(AreaTourAPI.class);

        tourAPI.getKOR_Data(AreaTourAPI.AUTH_KEY, 1,NumOfRows, AreaTourAPI.MobileApp, AreaTourAPI.MobileOS,
                "B",12,"Y", AreaTourAPI._type)
                .enqueue(new Callback<AreaTourData>() {
                    @Override
                    public void onResponse(Call<AreaTourData> call, Response<AreaTourData> response) {
                        Log.e("Succed_KOR","test");

                            Gson gson = new Gson();
                        areaTourData_kor = response.body();
                        String df = gson.toJson(response.body());
                        if(areaTourData_kor.getResponse().getHeader().getResultCode().equals("0000")) {
                            Log.e("asdasd", "SDfsdfsdf");
                            setAreaTourData(response.body(),KOR_TOUR_KEY);
                        }
                    }

                    @Override
                    public void onFailure(Call<AreaTourData> call, Throwable t) {
                        Log.e("Failed_KOR",t.getMessage());
                        Toast.makeText(LcdActivity.mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        tourAPI.getENG_Data(AreaTourAPI.AUTH_KEY, 1,NumOfRows, AreaTourAPI.MobileApp, AreaTourAPI.MobileOS,
                "B",12,"Y", AreaTourAPI._type)
                .enqueue(new Callback<AreaTourData>() {
                    @Override
                    public void onResponse(Call<AreaTourData> call, Response<AreaTourData> response) {

                        if (response.isSuccessful()) {
                            AreaTourData data = response.body();
                            if(data.getResponse().getHeader().getResultCode().equals("0000"))  {
                                Log.e("Succed_ENG",response.message());
                                setAreaTourData(response.body(),ENG_TOUR_KEY);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AreaTourData> call, Throwable t) {
                        Log.e("Failed_ENG",t.getMessage());
                        Toast.makeText(LcdActivity.mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        tourAPI.getCHN_Data(AreaTourAPI.AUTH_KEY, 1,NumOfRows, AreaTourAPI.MobileApp, AreaTourAPI.MobileOS,
                "B",12,"Y", AreaTourAPI._type)
                .enqueue(new Callback<AreaTourData>() {
                    @Override
                    public void onResponse(Call<AreaTourData> call, Response<AreaTourData> response) {

                        if (response.isSuccessful()) {
                            AreaTourData data = response.body();
                            if(data.getResponse().getHeader().getResultCode().equals("0000")) {
                                Log.e("Succed_CHN",response.message());
                                setAreaTourData(response.body(),CHN_TOUR_KEY);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AreaTourData> call, Throwable t) {
                        Log.e("Failed_CHN",t.getMessage());
                        Toast.makeText(LcdActivity.mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        tourAPI.getJPN_Data(AreaTourAPI.AUTH_KEY, 1,NumOfRows, AreaTourAPI.MobileApp, AreaTourAPI.MobileOS,
                "B",12,"Y", AreaTourAPI._type)
                .enqueue(new Callback<AreaTourData>() {
                    @Override
                    public void onResponse(Call<AreaTourData> call, Response<AreaTourData> response) {
                        if (response.isSuccessful()) {
                            AreaTourData data = response.body();
                            if(data.getResponse().getHeader().getResultCode().equals("0000")) {
                                Log.e("Succed_JPN",response.message());
                                setAreaTourData(response.body(),JPN_TOUR_KEY);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AreaTourData> call, Throwable t) {
                        Log.e("Failed_JPN",t.getMessage());
                        Toast.makeText(LcdActivity.mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        /*문화시설 Load*/
        tourAPI.getKOR_Data(AreaTourAPI.AUTH_KEY, 1,NumOfRows, AreaTourAPI.MobileApp, AreaTourAPI.MobileOS,
                "B",14,"Y", AreaTourAPI._type)
                .enqueue(new Callback<AreaTourData>() {
                    @Override
                    public void onResponse(Call<AreaTourData> call, Response<AreaTourData> response) {
                        if (response.isSuccessful()) {
                            AreaTourData data = response.body();
                            if(data.getResponse().getHeader().getResultCode().equals("0000")) {
                                Log.e("Succed_KOR_CUR",response.message());
                                setAreaTourData(response.body(),KOR_CUR_KEY);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AreaTourData> call, Throwable t) {
                        Log.e("FAIL_KOR_CUR",t.getMessage());
                        Toast.makeText(LcdActivity.mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        tourAPI.getENG_Data(AreaTourAPI.AUTH_KEY, 1,NumOfRows, AreaTourAPI.MobileApp, AreaTourAPI.MobileOS,
                "B",14,"Y", AreaTourAPI._type)
                .enqueue(new Callback<AreaTourData>() {
                    @Override
                    public void onResponse(Call<AreaTourData> call, Response<AreaTourData> response) {
                        if (response.isSuccessful()) {
                            AreaTourData data = response.body();
                            if(data.getResponse().getHeader().getResultCode().equals("0000")) {
                                Log.e("Succed_ENG_CUR",response.message());
                                setAreaTourData(response.body(),ENG_CUR_KEY);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AreaTourData> call, Throwable t) {
                        Log.e("FAIL_ENG_CUR",t.getMessage());
                        Toast.makeText(LcdActivity.mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        tourAPI.getCHN_Data(AreaTourAPI.AUTH_KEY, 1,NumOfRows, AreaTourAPI.MobileApp, AreaTourAPI.MobileOS,
                "B",14,"Y", AreaTourAPI._type)
                .enqueue(new Callback<AreaTourData>() {
                    @Override
                    public void onResponse(Call<AreaTourData> call, Response<AreaTourData> response) {
                        if (response.isSuccessful()) {
                            AreaTourData data = response.body();
                            if(data.getResponse().getHeader().getResultCode().equals("0000")) {
                                Log.e("Succed_CHN_CUR",response.message());
                                setAreaTourData(response.body(),CHN_CUR_KEY);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AreaTourData> call, Throwable t) {
                        Log.e("FAIL_CHN_CUR",t.getMessage());
                        Toast.makeText(LcdActivity.mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        tourAPI.getJPN_Data(AreaTourAPI.AUTH_KEY, 1,NumOfRows, AreaTourAPI.MobileApp, AreaTourAPI.MobileOS,
                "B",14,"Y", AreaTourAPI._type)
                .enqueue(new Callback<AreaTourData>() {
                    @Override
                    public void onResponse(Call<AreaTourData> call, Response<AreaTourData> response) {
                        if (response.isSuccessful()) {
                            AreaTourData data = response.body();
                            if(data.getResponse().getHeader().getResultCode().equals("0000")) {
                                Log.e("Succed_JPN_CUR",response.message());
                                setAreaTourData(response.body(),JPN_CUR_KEY);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AreaTourData> call, Throwable t) {
                        Log.e("FAIL_JPN_CUR",t.getMessage());
                        Toast.makeText(LcdActivity.mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        /*날짜별축제행사 Load*/
        retrofit = new Retrofit.Builder()
                .baseUrl(FestivalAPI.API_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getUnsafeOkHttpClient().build())
                .build();

        FestivalAPI festivalAPI =retrofit.create(FestivalAPI.class);

        festivalAPI.getKOR_Data(FestivalAPI.AUTH_KEY, 1,NumOfRows,FestivalAPI.MobileApp, FestivalAPI.MobileOS,
                "B",15,date,FestivalAPI._type)
                .enqueue(new Callback<FestivalData>() {
                    @Override
                    public void onResponse(Call<FestivalData> call, Response<FestivalData> response) {
                        FestivalData data = response.body();
                        if(data.getResponse().getHeader().getResultCode().equals("0000")) {
                            Log.e("KOR_FESTI",response.message());
                            setFestiData(response.body(),KOR_FEST_KEY);
                                /*for (int i = 0; i < data.getResponse().getBody().getItems().getItem().size(); i++) {
                                    Log.e("title-" + i, data.getResponse().getBody().getItems().getItem().get(i).getTitle());
                                    Log.e("title-" + i, data.getResponse().getBody().getItems().getItem().get(i).getCreatedtime().toString());
                                }*/
                        }
                    }

                    @Override
                    public void onFailure(Call<FestivalData> call, Throwable t) {
                        Log.e("KOR_FESTI",t.getMessage());
                        Toast.makeText(LcdActivity.mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        festivalAPI.getENG_Data(FestivalAPI.AUTH_KEY, 1,NumOfRows,FestivalAPI.MobileApp, FestivalAPI.MobileOS,
                "B",15,date,FestivalAPI._type)
                .enqueue(new Callback<FestivalData>() {
                    @Override
                    public void onResponse(Call<FestivalData> call, Response<FestivalData> response) {
                        FestivalData data = response.body();
                        if(data.getResponse().getHeader().getResultCode().equals("0000")) {
                            Log.e("ENG_FESTI",response.message());
                            setFestiData(response.body(),ENG_FEST_KEY);
                                /*for (int i = 0; i < data.getResponse().getBody().getItems().getItem().size(); i++) {
                                    Log.e("title-" + i, data.getResponse().getBody().getItems().getItem().get(i).getTitle());
                                    Log.e("title-" + i, data.getResponse().getBody().getItems().getItem().get(i).getCreatedtime().toString());
                                }*/
                        }
                    }

                    @Override
                    public void onFailure(Call<FestivalData> call, Throwable t) {
                        Log.e("ENG_FESTI",t.getMessage());
                        Toast.makeText(LcdActivity.mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        festivalAPI.getCHN_Data(FestivalAPI.AUTH_KEY, 1,NumOfRows,FestivalAPI.MobileApp, FestivalAPI.MobileOS,
                "B",15,date,FestivalAPI._type)
                .enqueue(new Callback<FestivalData>() {
                    @Override
                    public void onResponse(Call<FestivalData> call, Response<FestivalData> response) {
                        //Log.e("Festival","dataRCV SUCCEED");
                        FestivalData data = response.body();
                        if(data.getResponse().getHeader().getResultCode().equals("0000")) {
                            Log.e("CHN_FESTI",response.message());
                            setFestiData(response.body(),CHN_FEST_KEY);
                                /*for (int i = 0; i < data.getResponse().getBody().getItems().getItem().size(); i++) {
                                    Log.e("title-" + i, data.getResponse().getBody().getItems().getItem().get(i).getTitle());
                                    Log.e("title-" + i, data.getResponse().getBody().getItems().getItem().get(i).getCreatedtime().toString());
                                }*/
                        }
                    }

                    @Override
                    public void onFailure(Call<FestivalData> call, Throwable t) {
                        Log.e("CHN_FESTI",t.getMessage());
                        Toast.makeText(LcdActivity.mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        festivalAPI.getJPN_Data(FestivalAPI.AUTH_KEY, 1,NumOfRows,FestivalAPI.MobileApp, FestivalAPI.MobileOS,
                "B",15,date,FestivalAPI._type)
                .enqueue(new Callback<FestivalData>() {
                    @Override
                    public void onResponse(Call<FestivalData> call, Response<FestivalData> response) {
                        FestivalData data = response.body();
                        if(data.getResponse().getHeader().getResultCode().equals("0000")) {
                            Log.e("JPN_FESTI",response.message());
                            setFestiData(response.body(),JPN_FEST_KEY);
                                /*for (int i = 0; i < data.getResponse().getBody().getItems().getItem().size(); i++) {
                                    Log.e("title-" + i, data.getResponse().getBody().getItems().getItem().get(i).getTitle());
                                    Log.e("title-" + i, data.getResponse().getBody().getItems().getItem().get(i).getCreatedtime().toString());
                                }*/
                        }
                    }

                    @Override
                    public void onFailure(Call<FestivalData> call, Throwable t) {
                        Log.e("JPN_FESTI",t.getMessage());
                        Toast.makeText(LcdActivity.mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String format_time = formatter.format(System.currentTimeMillis());
        Log.e("getDate",format_time);
        return format_time;
    }

    public static void setAreaTourData(AreaTourData data, String key){
        Gson gson = new Gson();
        String favData = gson.toJson(data);
        SharedPreferences pref = LcdActivity.mContext.getSharedPreferences(key, 0);
        SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
        editor.putString(key,favData); // 입력할 값
        editor.commit();
    }

    public static AreaTourData getAreaTourData(String key) {
        SharedPreferences pref = (SharedPreferences) LcdActivity.mContext.getSharedPreferences(key, 0);
        String data = pref.getString(key, "");
        Log.e("gson key", key);
        Gson gson = new Gson();

        Type type = new TypeToken<AreaTourData>() {
        }.getType();


        return gson.fromJson(data, type);
    }

    //retrofit gson data to string data and then save at shared preference
    public static void setFestiData(FestivalData data, String key){
        Gson gson = new Gson();
        String favData = gson.toJson(data);
        SharedPreferences pref = LcdActivity.mContext.getSharedPreferences(key, 0);
        SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
        editor.putString(key,favData); // 입력할 값
        editor.commit();
    }

    //get gson data format from shared preference string data
    public static FestivalData getFestiTourData(String key) {
        SharedPreferences pref = (SharedPreferences) LcdActivity.mContext.getSharedPreferences(key, 0);
        String data = pref.getString(key, "");
        Gson gson = new Gson();

        Type type = new TypeToken<FestivalData>() {
        }.getType();
        return gson.fromJson(data, type);
    }
}

