package com.hangisool.lcd_a_h;

import org.ini4j.Wini;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hangisool.lcd_a_h.filepath.ImgPath;
import com.hangisool.lcd_a_h.backend.emercall.BackendService;
import com.hangisool.lcd_a_h.backend.emercall.Data;

import java.io.File;
import java.io.IOException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hangisool.lcd_a_h.LcdActivity.nOrNFile;
import static com.hangisool.lcd_a_h.backend.emercall.Data.getUnsafeOkHttpClient;

public class SettingActivity extends AppCompatActivity {
    //각각의 현장에 맞는 각각의 세팅을 할 수 있는 액티비티
    private LinearLayout group_EMERKEY;
    private RadioButton set_NOTICE, set_NEWS, set_DISP_EXPEND, set_DISP_RATIO, set_ENABLE_EMERCALL, set_DISENABLE_EMERCALL;
    private RadioGroup rg_TEXTLINE, rg_DISP, rg_EMERCALL;
    private EditText set_CARNAME, set_name, set_location, set_field, set_phonenumber,set_IPCAM_STREAM;
    private Button set_SAVE, btn_setting_close, save_emercall, btn_modeSelect;
    String carName, textLine, dispScreen, flagEmercall;
    String emer_name, emer_location, emer_field;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init_UI();
        autoSet_UI();

        set_SAVE.setOnTouchListener(touchListener);
        btn_setting_close.setOnTouchListener(touchListener);
        save_emercall.setOnTouchListener(touchListener);
        btn_modeSelect.setOnTouchListener(touchListener);

    }



    public void init_UI(){
        set_SAVE = (Button) findViewById(R.id.set_SAVE);
        btn_setting_close = (Button) findViewById(R.id.btn_setting_close);
        set_NOTICE = (RadioButton) findViewById(R.id.set_NOTICE);
        set_NEWS = (RadioButton) findViewById(R.id.set_NEWS);
        set_DISP_EXPEND = (RadioButton) findViewById(R.id.set_DISP_EXPEND);
        set_DISP_RATIO = (RadioButton) findViewById(R.id.set_DISP_RATIO);
        set_ENABLE_EMERCALL = (RadioButton) findViewById(R.id.set_ENABLE_EMERCALL);
        set_DISENABLE_EMERCALL = (RadioButton) findViewById(R.id.set_DISENABLE_EMERCALL);
        rg_TEXTLINE = (RadioGroup) findViewById(R.id.rg_TEXTLINE);
        rg_DISP = (RadioGroup) findViewById(R.id.rg_DISP);
        rg_EMERCALL = (RadioGroup) findViewById(R.id.rg_EMERCALL);
        set_CARNAME = (EditText) findViewById(R.id.set_CARNAME);
        set_name = (EditText) findViewById(R.id.set_name);
        set_location = (EditText) findViewById(R.id.set_location);
        set_field = (EditText) findViewById(R.id.set_field);
        set_phonenumber = (EditText) findViewById(R.id.set_phonenumber);
        group_EMERKEY = (LinearLayout) findViewById(R.id.group_EMERKEY);
        save_emercall = (Button) findViewById(R.id.save_emercall);
        set_IPCAM_STREAM = (EditText) findViewById(R.id.set_IPCAM_STREAM);
        btn_modeSelect = (Button)findViewById(R.id.btn_modeselect);
    }
    public void autoSet_UI(){
        SharedPreferences pref;
        //뉴스를 표시할지 공지사항 표시할지 기존값불러와 표시
        pref = getSharedPreferences(nOrNFile, 0);
        String choiceTxtContents[] = (pref.getString("newsOrNotice", "")).split("/");
        if(choiceTxtContents.length>1) {
            Log.e("newsOrNotice", choiceTxtContents[1]);
            if (choiceTxtContents[1].equals("0")) {
                set_NEWS.setChecked(true);
            } else if (choiceTxtContents[1].equals("1")) {
                set_NOTICE.setChecked(true);
            }
        }
        //뷰사이즈 조절을 판별한다.
        pref = getSharedPreferences("screenSizeFile", 0);
        String screenSize = pref.getString("screenSizeFile", "");
        switch (screenSize) {
            case "FULL_MAINVIEW":
                set_DISP_EXPEND.setChecked(true);
                break;
            case "RESIZE_MAINVIEW":
                set_DISP_RATIO.setChecked(true);
                break;
            default://기본 구성은 FULL_MAINVIEW
                set_DISP_EXPEND.setChecked(true);
                break;
        }
        //비상영상통화장치 사용여부 기존값 불러와 표시한다.
        pref = getSharedPreferences("useEmerFile",0);
        String userEmerFile = pref.getString("useEmerFile","");
        switch (userEmerFile) {
            case "EMERCALL_ENABLE":
                set_ENABLE_EMERCALL.setChecked(true);
                break;
            case "EMERCALL_DISENABLE":
                set_DISENABLE_EMERCALL.setChecked(true);
                break;
            default://기본 구성은 FULL_MAINVIEW
                set_DISENABLE_EMERCALL.setChecked(true);
                break;
        }
        //카이름을 가져와 표시한다.
        if(getCarNameString() != null) {
            set_CARNAME.setText(getCarNameString());
        }
        //비상영상통화 name을 가져와 표시한다.
        pref = getSharedPreferences("emer_name",0);
        String emer_name = pref.getString("emer_name","");
        if(emer_name != null){
            set_name.setText(emer_name);
        }
        //비상영상통화 location을 가져와 표시한다.
        pref = getSharedPreferences("emer_location",0);
        String emer_location = pref.getString("emer_location","");
        if(emer_location != null){
            set_location.setText(emer_location);
        }
        //비상영상통화 emer_field 가져와 표시한다.
        pref = getSharedPreferences("emer_field",0);
        String emer_field = pref.getString("emer_field","");
        if(emer_field != null){
            set_location.setText(emer_field);
        }
        //비상영상통화 emer_field 가져와 표시한다.
        pref = getSharedPreferences("emer_phonenumber",0);
        String emer_phonenumber = pref.getString("emer_phonenumber","");
        if(emer_field != null){
            set_phonenumber.setText(emer_phonenumber);
        }
        //IP캠 스트림링크를 가져와서 표시한다.
        pref = getSharedPreferences("ipcamStreamLinkFile",0);
        String ipcamLink = pref.getString("ipcamStreamLinkFile","");
        set_IPCAM_STREAM.setText(ipcamLink);

        rg_EMERCALL.setOnCheckedChangeListener(rg_EMERCALL_ChangeListener);
        group_EMERKEY.setVisibility(View.INVISIBLE);
    }

    Button.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    if(v.getId() == R.id.set_SAVE) {
                        SharedPreferences pref;
                        SharedPreferences.Editor editor;
                        //뉴스 or 공지사항 라디오 버튼
                        if (rg_TEXTLINE.getCheckedRadioButtonId() == R.id.set_NOTICE) {//공지사항
                            textLine = "newsOrNotice/1";
                            //Toast.makeText(getApplicationContext(), "newsOrNotice/1",Toast.LENGTH_SHORT).show();
                        } else if (rg_TEXTLINE.getCheckedRadioButtonId() == R.id.set_NEWS) {//뉴스
                            textLine = "newsOrNotice/0";
                            //Toast.makeText(getApplicationContext(), "newsOrNotice/0",Toast.LENGTH_SHORT).show();
                        }
                        //sharedpreferences에 저장
                        pref = getSharedPreferences("newsOrNotice", 0);
                        editor = pref.edit();//저장하려면 editor가 필요
                        editor.putString("newsOrNotice", textLine); // 입력할 값
                        editor.commit();

                        //영상화면 비율유지,늘리기 라디오 버튼
                        if (rg_DISP.getCheckedRadioButtonId() == R.id.set_DISP_EXPEND) {//영상화면 늘리기
                            dispScreen = "FULL_MAINVIEW";
                        } else if (rg_DISP.getCheckedRadioButtonId() == R.id.set_DISP_RATIO) {//영상 비율유지
                            dispScreen = "RESIZE_MAINVIEW";
                        }
                        //sharedpreferences에 저장
                        pref = getSharedPreferences("screenSizeFile", 0);
                        editor = pref.edit();//저장하려면 editor가 필요
                        editor.putString("screenSizeFile", dispScreen); // 입력할 값
                        editor.commit();

                        //비상영상통화기능 사용여부 라디오버튼
                        if (rg_EMERCALL.getCheckedRadioButtonId() == R.id.set_ENABLE_EMERCALL) {//비상영상통화기능 사용
                            flagEmercall = "EMERCALL_ENABLE";
                        } else if (rg_EMERCALL.getCheckedRadioButtonId() == R.id.set_DISENABLE_EMERCALL) {//비상영상통화기능 사용안함
                            flagEmercall = "EMERCALL_DISENABLE";
                        }
                        //sharedpreferences에 저장
                        pref = getSharedPreferences("useEmerFile", 0);
                        editor = pref.edit();//저장하려면 editor가 필요
                        editor.putString("useEmerFile", flagEmercall); // 입력할 값
                        editor.commit();

                        //기기명 변경
                        carName = set_CARNAME.getText().toString();
                        Log.e("CARNAME",carName);
                        putCarNameString(carName);

                        //ipcam stream link 주소 저장
                        String ipcamLink = set_IPCAM_STREAM.getText().toString();
                        pref = getSharedPreferences("ipcamStreamLinkFile",0);
                        editor = pref.edit();
                        editor.putString("ipcamStreamLinkFile",ipcamLink);
                        editor.commit();

                        Toast.makeText(getApplicationContext(), "저장완료",Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(), textLine+" "+dispScreen+" "+flagEmercall,Toast.LENGTH_SHORT).show();
                    }
                    else if(v.getId() == R.id.btn_setting_close){
                        Intent intent = new Intent(getApplicationContext(), LcdActivity.class);
                        startActivity(intent);
                        System.runFinalizersOnExit(true);
                        System.exit(0);
                        finish();
                    }
                    else if(v.getId() == R.id.save_emercall){
                        //에디트 텍스트의 비상영상통화 name 가져오기
                        emer_name = set_name.getText().toString();
                        //에디트 텍스트의 비상영상통화 location 가져오기
                        emer_location = set_location.getText().toString();
                        //에디트 텍스트의 비상영상통화 location 가져오기
                        emer_field = set_field.getText().toString();
                        //하나라도 공백이라면
                        if(emer_name.equals("") || emer_location.equals("") || emer_field.equals("")){
                            Toast.makeText(getApplicationContext(),"비상영상통화정보를 정확히 입력해주세요.",Toast.LENGTH_SHORT).show();
                        }else{
                            String name = set_name.getText().toString();
                            String location = set_location.getText().toString();
                            String field = set_field.getText().toString();
                            String phoneNumber = set_phonenumber.getText().toString();

                            //서버 DB테이블값과 비교
                            check_device_info_available(name, location, field, phoneNumber);
                        }
                    }
                    else if(v.getId() == R.id.btn_modeselect){
                        final String[] items = new String[]{
                                "FULL_H_MODE","3PART_V_MODE_FULLHD", "FULL_V_MODE", "3PART_H_MODE", "2PART_V_MODE", "3PART_V_MODE", "3PART_V_KMEC",
                                "3PART_H_BAR", "3PART_H_BAR_VIDEO", "3PART_H_BAR_VIDEO_POLICE","3PART_H_LOBBY_VIDEO_POLICE",
                                "3PART_H_BAR_IPCAM","HOP","COP","HOP_2","COP_2","VMD","COP_SCROLL","DSIDS","COP_SCROLL3",
                                "COP_SCROLL_DEMO","WEB_GAME_DEMO","COP_SCROLL_MOVIE_INFO","COP_AIR_KOREA","COP_SCROLL_CRYPTO","COP_SCROLL_TOUR_INFO"};
                        final int[] selectedIndex = {0};

                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);

                        builder.setTitle("설정할 모드를 선택해주세요.") .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedIndex[0] = which;
                            } }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(SettingActivity.this, items[selectedIndex[0]], Toast.LENGTH_SHORT).show();
                                    SharedPreferences pref = getApplicationContext().getSharedPreferences(LcdActivity.fileName, 0);
                                    SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                                    editor.putString("ScreenMode",items[selectedIndex[0]]); // 입력할 값
                                    editor.commit();
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
                        /*//device 화면 크기 구하기
                        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
                        int width = dm.widthPixels;
                        int height = dm.heightPixels;*/
                        /*SharedPreferences pref = getSharedPreferences("ScreenMode", 0);
                        String screenMode = pref.getString("ScreenMode", "");
                        if((screenMode.equals("HOP")) || (screenMode.equals("COP")) || (screenMode.equals("COP_2")) || (screenMode.equals("HOP_2"))) {
                            Log.e("testttttt","testeasdf");
                            wmlp.horizontalMargin = 540;
                            wmlp.verticalMargin = 540;
                            wmlp.x = 540;
                            wmlp.y = 0;
                        }*/
                        aDialog.getWindow().setAttributes(wmlp);
                        aDialog.show();
                    }
                    break;
            }
            return false;
        }
    };

    private void check_device_info_available(final String name, final String location, final String field, final String phonenumber){
        new Thread(){
            public void run() {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BackendService.URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(getUnsafeOkHttpClient().build())
                        .build();

                BackendService service =retrofit.create(BackendService.class);

                service.postAddCar(name,location,field,phonenumber).enqueue(new retrofit2.Callback<Data>() {
                    @Override
                    public void onResponse(retrofit2.Call<Data> call, retrofit2.Response<Data> response) {
                        Log.e("RetrofitSucced",response.message());
                        if (response.isSuccessful()) {
                            Data body = response.body();
                            Log.e("data.result",body.getResult());
                            Log.e("data.message",body.getMessage());

                            if(body.getResult().equals("success")){
                                Toast.makeText(getApplicationContext(),"성공적으로 저장되었습니다.",Toast.LENGTH_SHORT).show();
                                saveEmerCarData(name,location,field,phonenumber,body.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Data> call, Throwable t) {
                        Log.e("RetrofitFailed- this",t.getMessage());
                        Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();
    }

    RadioGroup.OnCheckedChangeListener rg_EMERCALL_ChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if(checkedId == R.id.set_ENABLE_EMERCALL){//비상영상통화기능 사용
                flagEmercall = "EMERCALL_ENABLE";
                group_EMERKEY.setVisibility(View.VISIBLE);
            }else if(checkedId == R.id.set_DISENABLE_EMERCALL) {//비상영상통화기능 사용안함
                flagEmercall = "EMERCALL_DISENABLE";
                group_EMERKEY.setVisibility(View.INVISIBLE);
            }
        }
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.e("onKeyDown", String.valueOf(keyCode));

        //키보드의 숫자1 입력이 들어오면 액티비티 전환됨
        if (keyCode == KeyEvent.KEYCODE_S) {
            Intent intent = new Intent(this, LcdActivity.class);
            startActivity(intent);
            System.runFinalizersOnExit(true);
            System.exit(0);
            finish();
        }else if(keyCode == KeyEvent.KEYCODE_V){
            Toast.makeText(this, "Odroid-Version1.3", Toast.LENGTH_LONG).show();
        }
        return super.onKeyDown(keyCode, event);
    }

    public String getCarNameString() {
        String carName = "";
        ImgPath path;
        path = new ImgPath();
        Wini wini = null;
        try {
            wini = new Wini(new File(path.getImgPath(), "carName.ini"));
            carName = wini.get("carName", "carName");
            Log.d("carName", carName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return carName;
    }
    public void putCarNameString(String carName){
        ImgPath path;
        path = new ImgPath();
        Wini wini = null;
        try {
            wini = new Wini(new File(path.getImgPath(), "carName.ini"));
            wini.put("carName", "carName",carName);
            wini.store();
            Log.d("carName", carName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void saveEmerCarData(String name, String location, String field, String phonenumber, String hash){
        SharedPreferences pref;
        SharedPreferences.Editor editor;

        pref = getSharedPreferences("emer_name",0);
        editor = pref.edit();//저장하려면 editor가 필요
        editor.putString("emer_name", name); // 입력할 값
        editor.commit();

        pref = getSharedPreferences("emer_location",0);
        editor = pref.edit();//저장하려면 editor가 필요
        editor.putString("emer_location", location); // 입력할 값
        editor.commit();

        pref = getSharedPreferences("emer_field",0);
        editor = pref.edit();//저장하려면 editor가 필요
        editor.putString("emer_field", field); // 입력할 값
        editor.commit();

        pref = getSharedPreferences("emer_phonenumber",0);
        editor = pref.edit();//저장하려면 editor가 필요
        editor.putString("emer_phonenumber", phonenumber); // 입력할 값
        editor.commit();

        pref = getSharedPreferences("hash",0);
        editor = pref.edit();//저장하려면 editor가 필요
        editor.putString("hash", hash); // 입력할 값
        editor.commit();
    }
}
