package com.hangisool.lcd_a_h;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hangisool.lcd_a_h.backend.arealistapi.AreaListAPI;
import com.hangisool.lcd_a_h.backend.arealistapi.AreaListData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hangisool.lcd_a_h.backend.emercall.Data.getUnsafeOkHttpClient;
import java.util.*;

public class AreaSettingActivity extends Activity {
    Button btn_weather_area, btn_tour_area, btn_close, btn_air_area, btn_save_air_region;
    TextView txt_areaname, txt_air_region, txt_weather_region;
    EditText edit_air_region;
    ArrayList<String> tourAreaName;
    ArrayList<Integer> tourAreaCode;
    public static String SP_tourAreaFile = "TOURAREA";
    public static String SP_tourAreaName = "NAME_KEY";
    public static String SP_tourAreaCode = "CODE_KEY";

    public static String SP_airRegionFile = "AIRREGION";
    public static String SP_airRegionName = "NAME_KEY";
    public static String SP_airRegionNameDetail = "NAME_DETAIL_KEY";

    public static String SP_weatherRegionFile = "WEATHERREGION";
    public static String SP_weatherRegionName = "NAME_KEY";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_setting);

        initComponent();
        initTouchListener();
    }

    private View.OnTouchListener btnTouchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                switch(v.getId()){
                    case R.id.btn_weatherArea:
                        toastMessage("날씨 지역설정");
                        String[] listWeather = {"gongju", "chungju","daejeon", "seoul", "asan", "junju", "gyeongju",
                                "gangneung", "suwon", "anyang", "andong", "sokcho", "ilsan", "changwon",
                                "ulsan", "sungnam", "geoje", "cheongju", "jecheon", "yongin", "jeju"
                                , "gimpo", "busan", "daegu", "inchun", "sungnam", "gumi", "pyeongtaek"
                                , "dongtan", "taebaek", "gwangmyeong", "gwangju", "pohang", "wonju"
                                , "gwacheon"};
                        ArrayList<String> weatherRegionList = new ArrayList<>();
                        for(int i = 0; i<listWeather.length; i++) weatherRegionList.add(listWeather[i]);
                        showWeatherDialog(weatherRegionList);
                        break;
                    case R.id.btn_tourArea:
                        toastMessage("여행정보 지역설정");
                        showTourList();
                        break;
                    case R.id.btn_airArea:
                        toastMessage("대기오염 지역설정");
                        String[] listAir = {"서울", "부산","대구", "인천", "광주", "대전", "울산", "경기",
                                "강원", "충북", "충남", "전북", "전남", "경북", "경남", "제주", "세종"};
                        ArrayList<String> regionList = new ArrayList<>();
                        for(int i = 0; i<listAir.length; i++) regionList.add(listAir[i]);
                        showAirRegionListDialog(regionList);
                        break;
                    case R.id.btn_save_air_region:
                        SharedPreferences pref = getApplicationContext().getSharedPreferences(SP_airRegionFile, 0);
                        SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                        //Save the selected Air Resion Detail name to SP
                        editor.putString(SP_airRegionNameDetail,edit_air_region.getText().toString()); // 입력할 값
                        Log.e("TEST!",edit_air_region.getText().toString());
                        editor.commit();
                        toastMessage("save complete");
                        break;
                    case R.id.btn_setting_area_close:
                        Intent intent = new Intent(getApplicationContext(), LcdActivity.class);
                        startActivity(intent);
                        System.runFinalizersOnExit(true);
                        System.exit(0);
                        finish();
                        break;
                }
            }
            return false;
        }
    };

    void showTourList(){
        searchTourAreaList();
        Log.e("showTourList","finish");
    }

    void toastMessage(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    void initTouchListener(){
        btn_weather_area.setOnTouchListener(btnTouchListener);
        btn_tour_area.setOnTouchListener(btnTouchListener);
        btn_close.setOnTouchListener(btnTouchListener);
        btn_air_area.setOnTouchListener(btnTouchListener);
        btn_save_air_region.setOnTouchListener(btnTouchListener);
    }

    void initComponent(){
        btn_tour_area = (Button) findViewById(R.id.btn_tourArea);
        btn_weather_area = (Button) findViewById(R.id.btn_weatherArea);
        btn_close = (Button) findViewById(R.id.btn_setting_area_close);
        btn_air_area = (Button) findViewById(R.id.btn_airArea);
        btn_save_air_region = (Button) findViewById(R.id.btn_save_air_region);

        txt_areaname = (TextView) findViewById(R.id.txt_areaname);
        txt_air_region = (TextView) findViewById(R.id.txt_air_region);
        txt_weather_region = (TextView) findViewById(R.id.txt_weather_region);

        edit_air_region = (EditText) findViewById(R.id.edit_air_region);

        //Set textviwe value
        txt_areaname.setText(getAreaName() + " " + String.valueOf(getAreaCode()));
        txt_air_region.setText(getAirRegionName());
        txt_weather_region.setText(getWeatherRegionName());

        //Set EditText value
        edit_air_region.setText(getAirRegionNameDetail());
    }

    void showWeatherDialog(ArrayList<String> airRegion){
        String[] items = new String[airRegion.size()];
        for(int i=0; i< airRegion.size(); i++){
            items[i] = airRegion.get(i);
        }

        int[] selectedIndex = {0};

        AlertDialog.Builder builder = new AlertDialog.Builder(AreaSettingActivity.this);

        builder.setTitle("설정할 지역을 선택해주세요.") .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedIndex[0] = which;
            } }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toastMessage(items[selectedIndex[0]]+" "+"설정완료");
                //Create SP
                SharedPreferences pref = getApplicationContext().getSharedPreferences(SP_weatherRegionFile, 0);
                SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                //Save the selected AreaName to SP
                editor.putString(SP_weatherRegionName,items[selectedIndex[0]]); // 입력할 값
                Log.e("TEST!",String.valueOf(selectedIndex[0]));
                editor.commit();
                //Set textviwe value
                txt_weather_region.setText(getWeatherRegionName());
            } })
                .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        //모드선택 다이얼로그의 위치,크기를 임의로 변경한다.(스크린이 반쪽만 나오는경우 고려...(ex바타입))
        AlertDialog aDialog = builder.create();
        WindowManager.LayoutParams wmlp = aDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
        aDialog.getWindow().setAttributes(wmlp);
        aDialog.show();
    }

    void showAirRegionListDialog(ArrayList<String> airRegion){
        String[] items = new String[airRegion.size()];
        for(int i=0; i< airRegion.size(); i++){
            items[i] = airRegion.get(i);
        }

        int[] selectedIndex = {0};

        AlertDialog.Builder builder = new AlertDialog.Builder(AreaSettingActivity.this);

        builder.setTitle("설정할 모드를 선택해주세요.") .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedIndex[0] = which;
            } }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toastMessage(items[selectedIndex[0]]+" "+"설정완료");
                //Create SP
                SharedPreferences pref = getApplicationContext().getSharedPreferences(SP_airRegionFile, 0);
                SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                //Save the selected AreaName to SP
                editor.putString(SP_airRegionName,items[selectedIndex[0]]); // 입력할 값
                Log.e("TEST!",String.valueOf(selectedIndex[0]));
                editor.commit();
                //Set textviwe value
                txt_air_region.setText(getAirRegionName());
            } })
                .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        //모드선택 다이얼로그의 위치,크기를 임의로 변경한다.(스크린이 반쪽만 나오는경우 고려...(ex바타입))
        AlertDialog aDialog = builder.create();
        WindowManager.LayoutParams wmlp = aDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
        aDialog.getWindow().setAttributes(wmlp);
        aDialog.show();
    }

    void searchTourAreaList(){
        /*한국관광공사 제공 API 지역명 조회*/
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AreaListAPI.API_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getUnsafeOkHttpClient().build())
                .build();

        tourAreaName = new ArrayList<>();
        tourAreaCode = new ArrayList<>();

        AreaListAPI areaListAPI =retrofit.create(AreaListAPI.class);
        areaListAPI.getAreaList(AreaListAPI.AUTH_KEY,100,AreaListAPI.MobileApp,AreaListAPI.MobileOS,AreaListAPI._type)
                .enqueue(new Callback<AreaListData>() {
                    @Override
                    public void onResponse(Call<AreaListData> call, Response<AreaListData> response) {
                        Log.e("AreaList","dataRCV SUCCEED");
                        AreaListData data = response.body();
                        if(data != null) {
                            Log.e("areaListAPI","body not null");
                            if (data.getResponse().getHeader().getResultCode().equals("0000")) {
                                for (int i = 0; i < data.getResponse().getBody().getItems().getItem().size(); i++) {
                                    //각지역별 이름과 코드명 리스트에저장
                                    tourAreaName.add(data.getResponse().getBody().getItems().getItem().get(i).getName());
                                    Log.e("getName", data.getResponse().getBody().getItems().getItem().get(i).getName());
                                    tourAreaCode.add(data.getResponse().getBody().getItems().getItem().get(i).getCode());
                                    Log.e("getCode", data.getResponse().getBody().getItems().getItem().get(i).getCode().toString());
                                }
                                showAreaListDialog(tourAreaName, tourAreaCode);
                            }else{
                                Log.e("response[MSG]",data.getResponse().getHeader().getResultMsg());
                                Log.e("response[CODE]",data.getResponse().getHeader().getResultCode());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AreaListData> call, Throwable t) {
                        Log.e("AreaList","dataRCV FAILED");
                        Log.e("Failed_AreList",t.getMessage());
                        Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
    void showAreaListDialog(ArrayList<String> areaName, ArrayList<Integer> areaCode){
        String[] items = new String[areaName.size()];
        for(int i=0; i< areaName.size(); i++){
            items[i] = areaName.get(i);
        }

        int[] selectedIndex = {0};

        AlertDialog.Builder builder = new AlertDialog.Builder(AreaSettingActivity.this);

        builder.setTitle("설정할 모드를 선택해주세요.") .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedIndex[0] = which;
            } }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toastMessage(items[selectedIndex[0]]+" "+"설정완료");
                //Create SP
                SharedPreferences pref = getApplicationContext().getSharedPreferences(SP_tourAreaFile, 0);
                SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                //Save the selected AreaName to SP
                editor.putString(SP_tourAreaName,items[selectedIndex[0]]); // 입력할 값
                Log.e("TEST!",String.valueOf(selectedIndex[0]));
                editor.commit();
                //Save the selected AreaName's AreaCode to SP
                editor.putInt(SP_tourAreaCode,areaCode.get(selectedIndex[0]));
                editor.commit();
                //Set textviwe value
                txt_areaname.setText(getAreaName() + " " + String.valueOf(getAreaCode()));
            } })
                .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        //모드선택 다이얼로그의 위치,크기를 임의로 변경한다.(스크린이 반쪽만 나오는경우 고려...(ex바타입))
        AlertDialog aDialog = builder.create();
        WindowManager.LayoutParams wmlp = aDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
        aDialog.getWindow().setAttributes(wmlp);
        aDialog.show();
    }
    public int getAreaCode(){
        SharedPreferences pref = (SharedPreferences) getSharedPreferences(SP_tourAreaFile, 0);
        int area_code = pref.getInt(SP_tourAreaCode, 0);
        return area_code;
    }

    public String getAreaName(){
        SharedPreferences pref = getSharedPreferences(SP_tourAreaFile, 0);
        String area_name = pref.getString(SP_tourAreaName, "");
        return area_name;
    }

    public String getAirRegionName(){
        SharedPreferences pref = getSharedPreferences(SP_airRegionFile, 0);
        String air_region = pref.getString(SP_airRegionName, "지역설정필요");
        return air_region;
    }

    public String getWeatherRegionName(){
        SharedPreferences pref = getSharedPreferences(SP_weatherRegionFile, 0);
        String weather_region = pref.getString(SP_weatherRegionName, "seoul");
        return weather_region;
    }

    public String getAirRegionNameDetail(){
        SharedPreferences pref = getSharedPreferences(SP_airRegionFile, 0);
        String air_region = pref.getString(SP_airRegionNameDetail, "");
        return air_region;
    }
}
