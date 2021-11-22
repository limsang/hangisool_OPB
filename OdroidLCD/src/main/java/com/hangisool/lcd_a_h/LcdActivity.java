package com.hangisool.lcd_a_h;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.serialport.SerialPort;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.hangisool.lcd_a_h.adapter.ListViewAdapterDemo;
import com.hangisool.lcd_a_h.adapter.ListViewAdapter_KMEC;
import com.hangisool.lcd_a_h.airkorea.AirKoreaFragment;
import com.hangisool.lcd_a_h.cleaner.DpCleanerActivity;
import com.hangisool.lcd_a_h.collections.Contents;
import com.hangisool.lcd_a_h.contents.ExchangeParser;
import com.hangisool.lcd_a_h.contents.MovieRankParser;
import com.hangisool.lcd_a_h.contents.NewsParser;
import com.hangisool.lcd_a_h.contents.NoticeParser;
import com.hangisool.lcd_a_h.contents.StockPriceParser;
import com.hangisool.lcd_a_h.contents.WeatherParser;
import com.hangisool.lcd_a_h.cryptocurrency.CryptoCurrencyFragment;
import com.hangisool.lcd_a_h.filepath.ApkPath;
import com.hangisool.lcd_a_h.filepath.ContentsPath;
import com.hangisool.lcd_a_h.filepath.ImgPath;
import com.hangisool.lcd_a_h.filepath.PlayPath;
import com.hangisool.lcd_a_h.ftpmanager.FtpContentsManager;
import com.hangisool.lcd_a_h.ftpmanager.FtpFileManager;
import com.hangisool.lcd_a_h.ftpmanager.UpdateThread;
import com.hangisool.lcd_a_h.backend.emercall.BackendService;
import com.hangisool.lcd_a_h.backend.emercall.Data;
import com.hangisool.lcd_a_h.immotal.ImmotalService;
import com.hangisool.lcd_a_h.movieinfo.MovieInfoFragment;
import com.hangisool.lcd_a_h.playmanager.SetPlayList;
import com.hangisool.lcd_a_h.service.DateUpdateThread;
import com.hangisool.lcd_a_h.service.LcdService;
import com.hangisool.lcd_a_h.tourinfo.TourDataUpdateThread;
import com.hangisool.lcd_a_h.tourinfo.TourInfoFragment;
import com.hangisool.lcd_a_h.util.UsbSerialControl;
import com.hangisool.lcd_a_h.webgame.WebGameFragment;
import com.hangisool.lcd_a_h.webrtc.WebRTCActivity;
import com.hangisool.lcd_a_h.webrtc.WebRTCActivity_test_211119;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.ini4j.Wini;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hangisool.lcd_a_h.AreaSettingActivity.SP_weatherRegionFile;
import static com.hangisool.lcd_a_h.AreaSettingActivity.SP_weatherRegionName;
import static com.hangisool.lcd_a_h.backend.emercall.Data.getUnsafeOkHttpClient;

public class LcdActivity extends AppCompatActivity implements Runnable {
    public ArrayList<String> floor_AL = new ArrayList<>();

    private static final int VID = 1027;
    private static final int PID = 24577;
    private static final int LVDS_VID = 8746;
    private static final int LVDS_PID = 1;
    private UsbDevice usbDevice = null;
    private UsbManager manager;

    private ArrayList<String> floor_storage = new ArrayList<>();
    private String cancel_floor = "";
    private int up_Num = 0;
    private int down_Num = 0;
    private int floor_idx = 0;

    private final int FRAGMENT_EXCHANGE = 1000;
    private final int FRAGMENT_MOVIE_RANK = 1001;
    private final int FRAGMENT_EXCHANG_ECONOMY = 1002;
    private final int NEWS_UPDATE = 2000;
    private final int STOCK_PRICE_UPDATE = 2001;
    private final int WEATHER_UPDATE = 2002;
    private final int DATE_UPDATE = 2003;
    private final int CAR_STATUS_KMEC = 2004;
    private final int CAR_STATUS_SAMIL = 2005;
    private final int CAR_STATUS_DSIDS = 2006;
    private final int NOTICE_UPDATE = 2007;
    public static final int IMAGE_FRAGMENT_DISPLAY = 4000;
    public static final int VIDEO_FRAGMENT_DISPLAY = 4001;
    public static final int LISTVIEW_CHANGE_BACKGROUND = 5001;

    public static Context mContext;

    //층, 방향표시를 위해
    private static String carDirection;
    private static String carPosition;
    private static String lampTitle;
    private static int lampNumber;
    private ImageView arrowImage;
    private TextView floorName;
    private TextView lampName;

    private static Typeface typeface;
    private TextView todayWeatherText;
    private TextView weatherText;
    private TextView celsiusText;
    private TextView todayText;
    private TextView todayTimeText;
    private TextView stockPriceText;
    private ImageView weatherImage;
    private TextView tmnText1;
    private TextView tmnText2;
    private TextView tmxText1;
    private TextView tmxText2;
    private TextView popText1;
    private TextView popText2;
    private TextView humidityText1;
    private TextView humidityText2;
    private TextView kospiText;
    private TextView kospiPriceText;
    private TextView kospiContastText;
    private TextView newsText;
    private RelativeLayout.LayoutParams mTodayTextParams;
    private RelativeLayout.LayoutParams mTodayTimeTextParams;
    private RelativeLayout.LayoutParams mImageArrowParams;
    private RelativeLayout.LayoutParams mTxtFloorParams;
    private VideoView videoView_ipcam;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private static FtpContentsManager ftpContentsManager;
    private Thread updateThread;

    public static boolean updateFlag = false;

    private ContentsPath contentsPath;

    private int newsCount = 0;
    private int noticeCount = 0;
    private int demoCount = 0;

    private final int KOSPI = 0;
    private final int KOSDAQ = 1;
    private static int stockPriceFlag = 0;

    public static ArrayList<String> playList;
    public static PlayPath playPath;
    public static ApkPath apkPath;
    public static boolean playStatus;
    public static int playListIndex;
    public static int playListLength;
    public static String textValue = "";

    public static String date1 = "";
    public static String date2 = "";

    public static boolean restartFlag;
    public static boolean finishFlag;
    public static boolean ThreadStopFlag;
    public static boolean apkUpdateFlag;
    public static boolean screenSaveFlag;

    Thread t;
    //mainview에 나오는 비디오,사진의 크기를 full로 할것인지 비율에 맞출것인지 선택하는 변수와 shared preference파일명
    public static String screenSizeFile = "screenSizeFile";
    public static String screenSize = "";
    public static int screenSizeMode;
    //스크린모드를 저장하는 변수와 shared preference파일명
    public static String fileName = "ScreenMode";
    public static String screenMode = "";
    //컨텐츠(뉴스,영화등)가 스크롤 되는 시간을 설정하는 변수와 shared preference파일명
    public static String scrollCycleFile = "scrollCycle";
    public static int scrollCycle = 4000;
    //News를 표시할 것인지 공지사항을 표시할 것인지 선택하는 변수와 shared preference파일명
    public static int newsOrNotice = 0;
    public static String nOrNFile = "newsOrNotice";

    public static int lifeCount = 0;
    public static int lifeCount2 = 0;

    private static PendingIntent mPermissionIntent;

    public static String updateFlagFile = "updateFlagFile";
    public static String updateFlagString = "false";
    public static String screenSvFlagFile = "screenSvFlagFile";
    public static String screenSvFlagString = "false";
    public static int mainViewHeight;
    public static int mainViewWidth;
    private int[] floors = new int[100000];

    public static boolean fiveMinCheckFlag = false;
    public static boolean weatherOrTime = false;

    private static boolean webRTCQuitThread = false;
    public static String elevatorStateFile = "elevatorStateFile";
    private String placeName, privateName;

    //for COP //COP와 COB는 같은것을 가리킴
    private TextView floor_1, floor_2, floor_3, floor_4, floor_5, floor_6, floor_7, floor_8, floor_9, floor_10, floor_11, floor_12, floor_13, floor_14, floor_15;
    public static boolean[] btnTouched;
    public static int cntFloor_demo = 0;
    private final int REGISTER_FLOOR_UPDATE = 5004;
    public static String registerFloor = "";
    public static boolean flagTouchWaitTime = false;
    public static int cntTouchWaitTime = 0;
    private final int CHANGE_TO_WAIT_IMAGE = 5003;
    byte[] kmec_cob_buffer_packet;
    private final int COB_KMEC_UI = 2005;
    //img/carName.ini에 저장되어있는 최하층~최상층 층이름을 변수저장
    String[] kmec_cob_floorNames = new String[128];
    ListViewAdapter_KMEC adapterFloor;
    ListViewAdapterDemo adapterFloorDemo;
    public static int numFloorNames = 0;
    static boolean[] registeredFloorMap;
    static boolean[] registeredFloorMap_compare;
    public static UsbDeviceConnection conn_COP;
    public static UsbEndpoint epIN_COP = null;
    public static UsbEndpoint epOUT_COP = null;


    //for HOP
    public static int cntfloorPictureFolder;
    private TextView btn_back, btn_next, btn_menu, btn_tenkey00, btn_tenkey01, btn_tenkey02, btn_tenkey03, btn_tenkey04, btn_tenkey05, btn_tenkey06, btn_tenkey07, btn_tenkey08, btn_tenkey09, btn_tenkey10, btn_tenkey11;
    private ImageView img_wait_touch;
    private TextView txt_pushed_floor, txt_assigned_car, txt_assigned_floor;
    public TextView set_floor_TV_1, set_floor_TV_2;
    private RelativeLayout group_result;
    public static int cntBtnToggle;
    public static int cnt_floorlst_page = 1;
    public static String touchedFloor = "";
    private TouchEventListener mTouchEventListener;
    private final int FRAGMENT_FLOORLIST_IMAGE = 1004;
    private final int FRAGMENT_CONTENTS = 1005;
    public static int hogiIndex = 0;
    private final int DRAW_BUTTON_NUMBER = 5001;
    private final int DRAW_ASSIGN_RESULT = 5002;

    //for touch panel
    private FrameLayout tenkey_fl;
    private final int OVERLAY_PERMISSION_CODE = 7777;
    private final int USB_NOT_FOUND = 7778;

    private final int EMERCALL_LAMP = 0504;

    //for DEMO
    boolean Flag_simulationThread = true;

    //for ImmotalService
    public static String ImmotalFlagFile = "ImmotalFlagFile";

    TourInfoFragment tourInfoFragment;
    WebGameFragment webGameFragment;
    MovieInfoFragment movieInfoFragment;
    AirKoreaFragment airKoreaFragment;
    CryptoCurrencyFragment cryptoCurrencyFragment;

    // 485 serial connect
    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("LifeCycle", "onCreate");
        startOverlayWindowService(this);
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission();
        }
        mContext = this;
        mPermissionIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("com.android.example.USB_PERMISSION"), 0);

        settingFiles();
        weatherAreaSelect();//날씨지역 설정 가져오기
        modeSelect();//프로그램시작전 스크린모드를 판별하여 모드에 맞는 레이아웃을 선택한다.
        Intent serviceIntent;
        serviceIntent = new Intent(this.getBaseContext(), ImmotalService.class);
        this.startService(serviceIntent);
        serviceIntent = new Intent(this.getBaseContext(), LcdService.class);
        this.startService(serviceIntent);

        viewSizeSelect();//비디오와 사진이 재생되는 mainview에 레이아웃크기에 맞춰서 재생할지, 자료의 비율에 맞춰서 재생할지 선택
        checkFileReady();
        checkContentsScrollTime();
        checkNewsOrNotice();
        checkUseEmerCall();
        initCompo();
        setFullScreen();
        contentsChangeThread();
        contentsChangeCheckThread();
        clockTimer();

        new Thread(new UpdateThread()).start();
        new Thread(new DateUpdateThread()).start();

        t = new Thread(this);
        t.start();

//        mReadThread = new ReadThread();
//        mReadThread.start();

        setModeAction();//화면모드에 따른 각각 실행내용을 달리해주는 메소드
        copDemoAction();

        try {
            mSerialPort = getSerialPort();
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

            /* Create a receiving thread */
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
            DisplayError(R.string.error_security);
        } catch (IOException e) {
            DisplayError(R.string.error_unknown);
        } catch (InvalidParameterException e) {
            DisplayError(R.string.error_configuration);
        }

        //copDemoAction();//cop 데모를 위해 램프를 터치하면 화면 모드가 변경되도록 함
    }

    // 패킷을 읽어오는 쓰레드
    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[256];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private void DisplayError(int resourceId) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Error");
        b.setMessage(resourceId);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                LcdActivity.this.finish();
            }
        });
        b.show();
    }

    // 시리얼 포트 세팅
    public SerialPort getSerialPort()
            throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {

            String packageName = getPackageName();
            SharedPreferences sp = getSharedPreferences(packageName + "_preferences", MODE_PRIVATE);
            String path = sp.getString("DEVICE", "");
            Log.d("qqqq", path);
            int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));

            /* Check parameters */
//            if ((path.length() == 0) || (baudrate == -1)) {
//                Intent intent = new Intent(LcdActivity.this, MainMenu.class);
//                startActivity(intent);
//            }

            /* Open the serial port */
            //mSerialPort = new SerialPort(new File(path), baudrate, 0);

            SerialPort serialPort = SerialPort //
                    .newBuilder("/dev/ttyS1", 57600) // 디바이스, 통신 속도
                    .dataBits(8) // databits 5 ~ 8
                    .parity(0) // 0 : NONE, 1 : ODD, 2 : EVEN
                    .stopBits(1) // 1, 1.5, 2
                    .build();

            mSerialPort = serialPort;
        }
        return mSerialPort;
    }

    // 데이터 패킷 처리
    protected void onDataReceived(final byte[] buffer, final int size) {

        Serial485_Logic(buffer);

    }

    // onDataReceived 에서 읽어온 byte buffer로 처리
    public void Serial485_Logic(byte[] buffer) {
        int cnt = 0;

        floorName = (TextView) findViewById(R.id.txtFloor);
        arrowImage = (ImageView) findViewById(R.id.imageArrow);
        lampName = (TextView) findViewById(R.id.txtLamp);
        floorName = (TextView) findViewById(R.id.txtFloor);
        arrowImage = (ImageView) findViewById(R.id.imageArrow);
        lampName = (TextView) findViewById(R.id.txtLamp);

        mTodayTextParams = (RelativeLayout.LayoutParams) todayText.getLayoutParams();
        mTodayTimeTextParams = (RelativeLayout.LayoutParams) todayTimeText.getLayoutParams();
        mImageArrowParams = (RelativeLayout.LayoutParams) arrowImage.getLayoutParams();
        mTxtFloorParams = (RelativeLayout.LayoutParams) floorName.getLayoutParams();

        Log.e("Serial485_Logic", String.valueOf(buffer));
        String[] floorNames = new String[64];

        for (int i = 0; i < getFloorNameString().split(",").length; i++) {

            floorNames[i] = getFloorNameString().split(",")[i];

        }

        {
            //checksum
            int checksum = (buffer[7] & 0xff) + (buffer[8] & 0xff) + (buffer[9] & 0xff) + (buffer[10] & 0xff);

            //checksum Low,High Low,High
//            Log.d("checksum=", "" + checksum);
//            Log.d("buffer[7]trueORfalse", String.valueOf((buffer[5] & 0xff) == 0xBE));
//            Log.d("buffer[8]trueORfalse", String.valueOf((buffer[6] & 0xff) == 0x01));
//            Log.d("buffer[13]trueORfalse", String.valueOf((buffer[11] & 0xff) == (checksum & 0xff)));
//            Log.d("buffer[14]trueORfalse", String.valueOf((buffer[12] & 0xff) == checksum >> 8));
//            Log.d("buffer0x40", String.valueOf((buffer[7] & 0xC0) == 0x40));
//            Log.d("buffer0x80", String.valueOf((buffer[7] & 0xC0) == 0x80));
            if (((buffer[5] & 0xff) == 0xBE) &&
                    ((buffer[6] & 0xff) == 0x01) &&
                    (((buffer[11] & 0xff) == (checksum & 0xff)) &&
                            ((buffer[12] & 0xff) == (checksum >> 8)))) {

                cnt = 0;

                Log.e("packet", Integer.toHexString(buffer[2])
                        + " " + Integer.toHexString(buffer[3])
                        + " " + Integer.toHexString(buffer[4])
                        + " " + Integer.toHexString(buffer[5])
                        + " " + Integer.toHexString(buffer[6])
                        + " " + Integer.toHexString(buffer[7])
                        + " " + Integer.toHexString(buffer[8])
                        + " " + Integer.toHexString(buffer[9])
                        + " " + Integer.toHexString(buffer[10])
                        + " " + Integer.toHexString(buffer[11])
                        + " " + Integer.toHexString(buffer[12])
                        + " " + Integer.toHexString(buffer[13])
                        + " " + Integer.toHexString(buffer[14])
                );

                StringBuilder str = new StringBuilder();
                str.append(String.format("%c", buffer[10] & 0xff));
                str.append(String.format("%c", buffer[9] & 0xff));

                Log.d("  ", String.valueOf(buffer[9] * 0x3f));

                //층표시1 (0="00"  //1=최하층 //2~ = 최하층+1~ //63 = 최하층+62")
                int carPosition2 = buffer[7] & 0x3f;
                Log.e("carPosition2", String.valueOf(carPosition2 - 1));
                if (carPosition2 != 0) {
                    carPosition = floorNames[carPosition2 - 1];
                }

                //방향
                if ((buffer[7] & 0xC0) == 0x40) {
                    Log.e("Direction", "UP");
                    carDirection = "UP";
                } else if ((buffer[7] & 0xC0) == 0x80) {
                    Log.e("Direction", "DOWN");
                    carDirection = "DOWN";
                } else {
                    Log.e("Direction", "NONE");
                    carDirection = "NONE";
                }
                //램프표시
                if (((buffer[8] & 0xff) & 0x01) == 0x01) {//FULL
                    lampTitle = "만원 입니다.";
                    lampNumber = 1;
                } else if (((buffer[8] & 0xff) & 0x02) == 0x02) {//OVERLOAD
                    lampTitle = "인원이 초과되었습니다.";
                    lampNumber = 2;
                } else if (((buffer[8] & 0xff) & 0x08) == 0x08) {//Maintenance
                    lampTitle = "이 승강기는 점검중 입니다. 불편을 드려 죄송합니다. 다른 승강기를 이용해 주세요.";
                    lampNumber = 3;
                } else if (((buffer[8] & 0xff) & 0x10) == 0x10) {//AUTO
                    lampTitle = "NONE";
                    lampNumber = 4;
                } else if (((buffer[8] & 0xff) & 0x20) == 0x20) {//Earthquake
                    lampTitle = "이 승강기는 지진운전입니다. 불편을 드려 죄송합니다. 다른 승강기를 이용해 주세요.";
                    lampNumber = 5;
                } else if (((buffer[8] & 0xff) & 0x40) == 0x40) {//Fire
                    lampTitle = "이 승강기는 소방운전입니다. 불편을 드려 죄송합니다. 다른 승강기를 이용해 주세요.";
                    lampNumber = 6;
                } else if (((buffer[8] & 0xff) & 0x80) == 0x80) {//Parking
                    lampTitle = "이 승강기는 휴지중 입니다. 불편을 드려 죄송합니다. 다른 승강기를 이용해 주세요.";
                    lampNumber = 7;
                } else {
                    lampTitle = "NONE";
                    lampNumber = 8;
                }
            } else {
                cnt++;
                // 30초 이상 통신이 불량할 시 메세지 띄움
                if (cnt > 30) {
                    lampTitle = "통신 불량 상태입니다.";
                    lampNumber = 9;

                    // 40초 이상 통신이 불량할 시 메세지 띄운 후 재시작
                } else if (cnt > 40) {
                    lampTitle = "통신 불량으로 인해 5초 후 재부팅합니다.";
                    lampNumber = 10;

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            restartLCDActivity();
                        }
                    }, 5000);
                }

                Log.e("PacketError", "Checksum not matched");
                Log.e("packet", Integer.toHexString(buffer[2])
                        + " " + Integer.toHexString(buffer[3])
                        + " " + Integer.toHexString(buffer[4])
                        + " " + Integer.toHexString(buffer[5])
                        + " " + Integer.toHexString(buffer[6])
                        + " " + Integer.toHexString(buffer[7])
                        + " " + Integer.toHexString(buffer[8])
                        + " " + Integer.toHexString(buffer[9])
                        + " " + Integer.toHexString(buffer[10])
                        + " " + Integer.toHexString(buffer[11])
                        + " " + Integer.toHexString(buffer[12])
                        + " " + Integer.toHexString(buffer[13])
                        + " " + Integer.toHexString(buffer[14])
                );
            }
            handler.sendEmptyMessage(CAR_STATUS_SAMIL);
        }
    }

    @Override
    public void run() {
        try {
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            //LAN이 연결 되어있을 경우에만 파일을 다운로드한다.
            if (activeNetwork != null) {
                //fileDownLoad();
            } else {
                Toast.makeText(getApplicationContext(), "네트워크 연결상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
        }
        while (true) {

            try {
                if (ThreadStopFlag == true) {
                    break;
                }
                if (screenSaveFlag) {
                    Intent intent = new Intent(LcdActivity.this, DpCleanerActivity.class);
                    startActivity(intent);
                    screenSaveFlag = false;
                    SharedPreferences pref = LcdActivity.mContext.getSharedPreferences(LcdActivity.screenSvFlagFile, 0);
                    SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                    editor.putString("screenSvFlag", "true"); // 입력할 값
                    editor.commit();
                    System.exit(0);
                    finish();
                    break;
                }
                if (updateFlag == true) {
                    FtpFileManager ftpFileManager = new FtpFileManager("/playList", playPath.getPlayPath());
                    if (ftpFileManager.connect()) {
                        if (ftpFileManager.playScriptDownload()) {

                            playList = SetPlayList.getPlayList(playPath.getPlayPath() + "/playList.txt");

                            playListLength = playList.size();

                            File directory = new File(playPath.getPlayPath());
                            File[] files = directory.listFiles();

                            for (int i = 0; i < files.length; i++) {
                                if (!files[i].getName().equals("playList.txt")) {
                                    files[i].delete();
                                }
                            }
                            ArrayList<String> downList = new ArrayList<String>();
                            for (int i = 0; i < playList.size(); i++) {
                                if (playList.get(i).split(",")[0].contains("img") || playList.get(i).split(",")[0].contains("video")) {
                                    downList.add(playList.get(i).split(",")[1].trim());
                                }
                            }
                            if (ftpFileManager.connect()) {
                                ftpFileManager.fileDownload(downList);
                                playListLength = playList.size();
                                //ftp에서 파일을 다운받는 도중 앱이 꺼지면 파일을 전부 다운 받지 못하는 현상으로 완전히 다운받았는지 여부를
                                // SharedPreferences에 저장하고 시작할때마다 꺼내서 다시 ftp다운로드 할지를 판단한다.
                                Log.e("fileDownload", "Complete");
                                SharedPreferences pref = getSharedPreferences(LcdActivity.updateFlagFile, 0);
                                SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                                editor.putString("updateFlag", "false"); // 입력할 값
                                editor.commit();
                                updateFlag = false;
                                playListIndex = 0;
                            }

                        }
                    }
                }

                if (apkUpdateFlag == true) {
                    Log.e("apkDownload", "start");
                    apkUpdateFlag = false;
                    FtpFileManager ftpFileManager = new FtpFileManager("/apk", apkPath.getPlayPath());
                    Log.e("apkDownload", "mid");
                    if (ftpFileManager.connect()) {
                        Log.e("ftpFileManager", "connect");
                        if (ftpFileManager.apkDownload()) {
                            Log.e("apkDownload", "success");

                            File directory = new File(apkPath.getPlayPath());
                            File[] files = directory.listFiles();

                            for (int i = 0; i < files.length; i++) {
                                if (!files[i].getName().equals("app-debug.apk")) {
                                    files[i].delete();
                                }
                            }
                        }
                    }
                }

                if (restartFlag == true) {
                    restartFlag = false;
                    //ThreadStopFlag = true;
                    Log.e("restart", "Flag = true");
                    //startActivity(new Intent(LcdActivity.this, LcdActivity.class));
                    restartLCDActivity();
                }

                if (finishFlag) {
                    Log.e("LcdActivity", "finishFlag == true");
                    finishFlag = false;
                    restartLCDActivity();
                }

                if (playStatus == false) {
                    Log.e("playStatus", "false");
                    if (playList == null) {
                        Log.e("LcdActivity", "playList == null");
                        if (!playPath.isPlayListFile()) {
                            Log.e("LcdActivity", "!playPath.isPlayListFile()");
                            fileDownLoad();
                        }
                    } else {
                        String[] tmp = playList.get(playListIndex).split(",");
                        Log.e("프레그먼트", tmp[0] + ":" + tmp[1]);
                        if (tmp[0].contains("img")) {
                            //Log.e("fragment", tmp[0]);
                            if (playStatus == false) {
                                playStatus = true;
                                handler.sendEmptyMessage(IMAGE_FRAGMENT_DISPLAY);
                            }
                        }
                        if (tmp[0].contains("video")) {
                            //Log.e("fragment", tmp[0]);
                            if (playStatus == false) {
                                //Log.e("video","playStatus == false");
                                playStatus = true;
                                handler.sendEmptyMessage(VIDEO_FRAGMENT_DISPLAY);
                            }
                        }
                    }
                }


                Thread.sleep(300);
            } catch (Exception e) {
                Log.e("videoChangeThread", "catch", e);
            }
        }
    }

    public void copDemoAction() {
        newsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqEmerCall();
            }
        });

        floorName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(LcdActivity.fileName, 0);
                    SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                    demoCount = pref.getInt("count", 0);
                    String a = "";

                    demoCount++;

                    switch (demoCount) {
                        case 1:
                            a = "SUNGWON";
                            Log.e("SUNGWON", String.valueOf(demoCount));

                            break;
                        case 2:
                            a = "COP_2";
                            Log.e("COP_2", String.valueOf(demoCount));
                            demoCount = 0;

                            break;
                        case 3:
                            a = "COP_SCROLL_TOUR_INFO";
                            Log.e("COP_SCROLL_TOUR_INFO", String.valueOf(demoCount));

                            demoCount = 0;
                            break;
                    }
                    Log.e("demoCount", String.valueOf(demoCount));
                    Log.e("copDemoAction()", a);
                    editor.putInt("count", demoCount);
                    editor.putString("ScreenMode", a); // 입력할 값
                    editor.commit();

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(getApplicationContext(), LcdActivity.class);
                    startActivity(intent);
                    System.runFinalizersOnExit(true);
                    System.exit(0);
                    finish();
                }
                return false;
            }
        });
        //층이름을 터치하면 서버에 비상통화요청을함
//        floorName.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    reqEmerCall();
//                }
//                return false;
//            }
//        });
    }

    public void setModeAction() {
        //미쓰비시용 층정보표시되는 LCD일 경우
        Log.e("scrren,oadas", screenMode);

        if (screenMode.equals("3PART_H_BAR")) {
            KMEC_Serial_Logic();
        }
        //삼일 엘리베이터 용 층정보 표시되는 LCD일 경우
        else if (screenMode.equals("3PART_H_BAR_VIDEO_POLICE") || screenMode.equals("3PART_H_LOBBY_VIDEO_POLICE")) {
//        else if (screenMode.equals("3PART_H_BAR_VIDEO_POLICE") || screenMode.equals("3PART_H_LOBBY_VIDEO_POLICE") || screenMode.equals("SUNGWON")) {
            SharedPreferences prefSamil = getSharedPreferences(elevatorStateFile, 0);
            //Log.e("elevatorFloor",prefSamil.getString("floor", ""));
            //Log.e("elevatorDirection",prefSamil.getString("direction", ""));
            //Log.e("elevatorLamp",prefSamil.getString("lamp", ""));
            //Log.e("elevatorLampNumber",prefSamil.getString("lampNumber", ""));
            carPosition = prefSamil.getString("floor", "");
            carDirection = prefSamil.getString("direction", "");
            lampTitle = prefSamil.getString("lamp", "");
            if (!(prefSamil.getString("lampNumber", "").equals(""))) {
                lampNumber = Integer.parseInt(prefSamil.getString("lampNumber", ""));
            }
            handler.sendEmptyMessage(CAR_STATUS_SAMIL);
            SAMIL_Serial_Logic();
            //simulationThread();
        } else if (screenMode.equals("3PART_H_BAR_IPCAM")) {
            manager_IPCAM();
            simulationThread();
        } else if (screenMode.equals("COP") || screenMode.equals("COP_2")) {//COP모드일 경우
            Logic_COP();
            KMEC_COP_Serial_Logic();
            //Logic_TouchPanel();
        } else if (screenMode.equals("HOP") || screenMode.equals("HOP_2")) {//HOP모드일 경우
            // 211109 테스트 하려고 잠시 주석 처리
//            Logic_HOP();
//            KMEC_COP_Serial_Logic();

            init_TouchBar();

            //Logic_TouchPanel();
        } else if (screenMode.equals("VMD")) {
            simulationThread();
        } else if (screenMode.equals("COP_SCROLL")) {
//            setFloorInfo_cob_hob();//스크롤뷰에 들어갈 층정보 세팅
            Logic_HOP();
//            KMEC_COP_Serial_Logic();
        } else if (screenMode.equals("DSIDS")) {//if this device for DEASEONG IDS protocol indicator
            DSIDS_Serial_Logic();
        } else if (screenMode.equals("COP_SCROLL3")) {
            setFloorInfo_cob_hob_DEMO();//스크롤뷰에 들어갈 층정보 세팅
            simulationThread();
            /*TourDataUpdateThread thread = new TourDataUpdateThread();
            thread.start();
            tourInfoFragment = new TourInfoFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.tourinfo_container,tourInfoFragment).commit();*/
            movieInfoFragment = new MovieInfoFragment();
            getFragmentManager().beginTransaction().replace(R.id.tourinfo_container, movieInfoFragment).commit();
        } else if (screenMode.equals("COP_SCROLL_DEMO")) {
            setFloorInfo_cob_hob_DEMO();//스크롤뷰에 들어갈 층정보 세팅
            simulationThread();
        } else if (screenMode.equals("WEB_GAME_DEMO")) {
            setFloorInfo_cob_hob_DEMO();//스크롤뷰에 들어갈 층정보 세팅
            simulationThread();
            webGameFragment = new WebGameFragment();
            getFragmentManager().beginTransaction().replace(R.id.tourinfo_container, webGameFragment).commit();
        } else if (screenMode.equals("COP_SCROLL_MOVIE_INFO")) {
//            init_TouchBar();
            setFloorInfo_cob_hob_DEMO();//스크롤뷰에 들어갈 층정보 세팅
            simulationThread();
            movieInfoFragment = new MovieInfoFragment();
            getFragmentManager().beginTransaction().replace(R.id.tourinfo_container, movieInfoFragment).commit();
        } else if (screenMode.equals("COP_AIR_KOREA")) {
            setFloorInfo_cob_hob_DEMO();//스크롤뷰에 들어갈 층정보 세팅
            simulationThread();
            airKoreaFragment = new AirKoreaFragment();
            getFragmentManager().beginTransaction().replace(R.id.tourinfo_container, airKoreaFragment).commit();
        } else if (screenMode.equals("COP_SCROLL_CRYPTO")) {
            setFloorInfo_cob_hob_DEMO();//스크롤뷰에 들어갈 층정보 세팅
            simulationThread();
            cryptoCurrencyFragment = new CryptoCurrencyFragment();
            getFragmentManager().beginTransaction().replace(R.id.tourinfo_container, cryptoCurrencyFragment).commit();
        } else if (screenMode.equals("COP_SCROLL_TOUR_INFO")) {
            setFloorInfo_cob_hob_DEMO();//스크롤뷰에 들어갈 층정보 세팅
//            simulationThread();
            TourDataUpdateThread thread = new TourDataUpdateThread();
            thread.start();
            tourInfoFragment = new TourInfoFragment();
            getFragmentManager().beginTransaction().replace(R.id.tourinfo_container, tourInfoFragment).commit();
        } else if (screenMode.equals("SUNGWON")) {
            init_TouchBar();
            simulationThread();
            Logic_HOP();
            handler.sendEmptyMessage(FRAGMENT_EXCHANGE);

        }
    }

    public void initCompo() {
        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        set_floor_TV_1 = (TextView) findViewById(R.id.set_floor_TV_1);
        set_floor_TV_2 = (TextView) findViewById(R.id.set_floor_TV_2);
        todayWeatherText = (TextView) findViewById(R.id.todayWeatherText);
        weatherText = (TextView) findViewById(R.id.weatherText);
        celsiusText = (TextView) findViewById(R.id.celsiusText);
        todayText = (TextView) findViewById(R.id.todayText);
        todayTimeText = (TextView) findViewById(R.id.todayTimeText);
        stockPriceText = (TextView) findViewById(R.id.stockPriceText);
        weatherImage = (ImageView) findViewById(R.id.weatherImage);
        tmnText1 = (TextView) findViewById(R.id.tmnText1);
        tmnText2 = (TextView) findViewById(R.id.tmnText2);
        tmxText1 = (TextView) findViewById(R.id.tmxText1);
        tmxText2 = (TextView) findViewById(R.id.tmxText2);
        popText1 = (TextView) findViewById(R.id.popText1);
        popText2 = (TextView) findViewById(R.id.popText2);
        humidityText1 = (TextView) findViewById(R.id.humidityText1);
        humidityText2 = (TextView) findViewById(R.id.humidityText2);
        kospiText = (TextView) findViewById(R.id.kospiText);
        kospiPriceText = (TextView) findViewById(R.id.kospiPriceText);
        kospiContastText = (TextView) findViewById(R.id.kospiContrastText);
        newsText = (TextView) findViewById(R.id.newsText);
        floorName = (TextView) findViewById(R.id.txtFloor);
        arrowImage = findViewById(R.id.imageArrow);
    }

    public void checkFileReady() {
        //ftp에서 파일을 다운받는 도중 앱이 꺼지면 파일을 전부 다운 받지 못하는 현상으로 완전히 다운받았는지 여부를
        // SharedPreferences에 저장하고 시작할때마다 꺼내서 다시 ftp다운로드 할지를 판단한다.
        SharedPreferences pref = getSharedPreferences(updateFlagFile, 0);
        updateFlagString = pref.getString("updateFlag", "");
        Log.e("updateFlag", updateFlagString);
        if (updateFlagString.equals("true")) {
            updateFlag = true;
        } else {
            updateFlag = false;
        }
    }

    public void checkContentsScrollTime() {
        //컨텐츠 스크롤 시간설정파일 Shared preference 불러와 적용
        SharedPreferences pref2 = getSharedPreferences(scrollCycleFile, 0);
        String cycle[] = (pref2.getString("scrollCycle", "")).split("/");
        if (cycle[0] == "") {
            Log.e("scrollCycle", "공백");
        } else {
            scrollCycle = Integer.parseInt(cycle[1]);
            //Log.e("scrollCycle", String.valueOf(scrollCycle));
        }
    }

    public void checkNewsOrNotice() {
        //뉴스를 표시할지 공지사항을 표시할지 판단
        SharedPreferences pref3 = getSharedPreferences(nOrNFile, 0);
        String choiceTxtContents[] = (pref3.getString("newsOrNotice", "")).split("/");
        if (choiceTxtContents[0] == "") {
            Log.e("newsOrNotice", "공백");
        } else {
            if (choiceTxtContents.length > 1) {
                newsOrNotice = Integer.parseInt(choiceTxtContents[1]);
            }
            //Log.e("newsOrNotice", String.valueOf(scrollCycle));
        }
    }

    public void checkUseEmerCall() {
        SharedPreferences pref = getSharedPreferences(updateFlagFile, 0);
        //비상영상통화장치기능 사용여부 불러오기.
        pref = getSharedPreferences("useEmerFile", 0);
        String userEmerFile = pref.getString("useEmerFile", "");
        switch (userEmerFile) {
            case "EMERCALL_ENABLE"://활성화시
                //서버에서 WebRTC채널을 형성할지 말지에 대한 명령을 읽어온다.
                //비상영상통화 식별현장명을 가져온다.
                pref = getSharedPreferences("placeNameFile", 0);
                placeName = pref.getString("placeNameFile", "");
                //비상영상통화 식별이름을 가져온다.
                pref = getSharedPreferences("privateNameFile", 0);
                privateName = pref.getString("privateNameFile", "");
                checkWebRTCStart();
                break;
            case "EMERCALL_DISENABLE"://비활성화시
                break;
            default:
                break;
        }
    }


    public void Logic_TouchPanel() {//터치패널 펌웨어 업데이트로 좌표값 강제변환이 필요 없어져서 쓰지않음
        //virtual_view = (FrameLayout) findViewById(R.id.virtual_view);
        //virtual_view.setOnTouchListener((View.OnTouchListener) VirtualTouchListener) ;
    }

    private View.OnTouchListener VirtualTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            Log.e("Location-X", String.valueOf(x));
            Log.e("Location-Y", String.valueOf(y));
            float virtual_X = (540 + (x / 2));
            event.offsetLocation(-x + virtual_X, 0);
            event.offsetLocation(-x + virtual_X, 0);
            Log.e("virtual_X", String.valueOf(virtual_X));
            return false;
        }
    };

    public void Logic_HOP() {
        //층목록 사진이 몇개 있는지 카운트
        String path = "/storage/emulated/0/floorPicture";
        File folder = new File(String.valueOf(Environment.getExternalStoragePublicDirectory("floorPicture")));
        File[] files = folder.listFiles();
        cntfloorPictureFolder = files.length;
        Log.e("cntfloorPictureFolder", String.valueOf(cntfloorPictureFolder));
        Log.e("LifeCycle", "onResume");

        btn_back = (TextView) findViewById(R.id.btn_back_hop);
        btn_menu = (TextView) findViewById(R.id.btn_menu_hop);
        btn_next = (TextView) findViewById(R.id.btn_next_hop);
        btn_tenkey00 = (TextView) findViewById(R.id.btn_tenkey00);
        btn_tenkey01 = (TextView) findViewById(R.id.btn_tenkey01);
        btn_tenkey02 = (TextView) findViewById(R.id.btn_tenkey02);
        btn_tenkey03 = (TextView) findViewById(R.id.btn_tenkey03);
        btn_tenkey04 = (TextView) findViewById(R.id.btn_tenkey04);
        btn_tenkey05 = (TextView) findViewById(R.id.btn_tenkey05);
        btn_tenkey06 = (TextView) findViewById(R.id.btn_tenkey06);
        btn_tenkey07 = (TextView) findViewById(R.id.btn_tenkey07);
        btn_tenkey08 = (TextView) findViewById(R.id.btn_tenkey08);
        btn_tenkey09 = (TextView) findViewById(R.id.btn_tenkey09);
        btn_tenkey10 = (TextView) findViewById(R.id.btn_tenkey10);
        btn_tenkey11 = (TextView) findViewById(R.id.btn_tenkey11);
        img_wait_touch = (ImageView) findViewById(R.id.img_wait_touch);
        txt_pushed_floor = (TextView) findViewById(R.id.txt_pushed_floor);
        group_result = (RelativeLayout) findViewById(R.id.group_result);
        txt_assigned_car = (TextView) findViewById(R.id.txt_assigned_car);
        txt_assigned_floor = (TextView) findViewById(R.id.txt_assigned_floor);

        btn_back.setOnClickListener(btnClickListener);
        btn_back.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_next.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_next.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_menu.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_menu.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey00.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey00.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey01.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey01.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey02.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey02.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey03.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey03.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey04.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey04.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey05.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey05.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey06.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey06.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey07.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey07.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey08.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey08.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey09.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey09.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey10.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey10.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey11.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey11.setOnTouchListener((View.OnTouchListener) btnTouchListener);

//        simulationThread();
        touchCommunication_HOP();
        handler.sendEmptyMessage(FRAGMENT_CONTENTS);//초기 info_view에 층정보이미지를 띄워줌
    }

    public void Logic_COP() {
        btnTouched = new boolean[15];
        floor_1 = (TextView) findViewById(R.id.floor_1);
        floor_2 = (TextView) findViewById(R.id.floor_2);
        floor_3 = (TextView) findViewById(R.id.floor_3);
        floor_4 = (TextView) findViewById(R.id.floor_4);
        floor_5 = (TextView) findViewById(R.id.floor_5);
        floor_6 = (TextView) findViewById(R.id.floor_6);
        floor_7 = (TextView) findViewById(R.id.floor_7);
        floor_8 = (TextView) findViewById(R.id.floor_8);
        floor_9 = (TextView) findViewById(R.id.floor_9);
        floor_10 = (TextView) findViewById(R.id.floor_10);
        floor_11 = (TextView) findViewById(R.id.floor_11);
        floor_12 = (TextView) findViewById(R.id.floor_12);
        floor_13 = (TextView) findViewById(R.id.floor_13);
        floor_14 = (TextView) findViewById(R.id.floor_14);
        floor_15 = (TextView) findViewById(R.id.floor_15);

        floor_1.setOnClickListener((View.OnClickListener) btnClickListener);
        floor_1.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        floor_2.setOnClickListener((View.OnClickListener) btnClickListener);
        floor_2.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        floor_3.setOnClickListener((View.OnClickListener) btnClickListener);
        floor_3.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        floor_4.setOnClickListener((View.OnClickListener) btnClickListener);
        floor_4.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        floor_5.setOnClickListener((View.OnClickListener) btnClickListener);
        floor_5.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        floor_6.setOnClickListener((View.OnClickListener) btnClickListener);
        floor_6.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        floor_7.setOnClickListener((View.OnClickListener) btnClickListener);
        floor_7.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        floor_8.setOnClickListener((View.OnClickListener) btnClickListener);
        floor_8.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        floor_9.setOnClickListener((View.OnClickListener) btnClickListener);
        floor_9.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        floor_10.setOnClickListener((View.OnClickListener) btnClickListener);
        floor_10.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        floor_11.setOnClickListener((View.OnClickListener) btnClickListener);
        floor_11.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        floor_12.setOnClickListener((View.OnClickListener) btnClickListener);
        floor_12.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        floor_13.setOnClickListener((View.OnClickListener) btnClickListener);
        floor_13.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        floor_14.setOnClickListener((View.OnClickListener) btnClickListener);
        floor_14.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        floor_15.setOnClickListener((View.OnClickListener) btnClickListener);
        floor_15.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        simulationThread();
        touchCommunication_COP();
    }

    public void touchCommunication_COP() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //사용자가 터치를 시작하면 flagTouchWaitTime 은 true가 되어 시간을 잰다.
                    if (flagTouchWaitTime) {
                        try {
                            Thread.sleep(100);
                            cntTouchWaitTime += 100;
                            //입력을 시작한 사용자가 일정 시간이상 다음 터치가 없을 경우 터치대기화면으로 리셋한다.
                            if (cntTouchWaitTime == 2000) {
                                flagTouchWaitTime = false;
                                registerFloor = "";
                                handler.sendEmptyMessage(CHANGE_TO_WAIT_IMAGE);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        cntTouchWaitTime = 0;
                    }
                }
            }
        }).start();
    }

    private void animation_floor() {
        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(10000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = set_floor_TV_1.getWidth();
                final float translationX = width * progress;
                set_floor_TV_1.setTranslationX(translationX);
                set_floor_TV_2.setTranslationX(translationX - width);
            }
        });
        animator.start();
    }

    //2111103 arbert14@hangisool.co.kr TouchBar Init 함수
    private void init_TouchBar() {
        // 등록층 textview 스크롤링 기능
        set_floor_TV_1.setSelected(true);
        set_floor_TV_2.setSelected(true);

        adapterFloorDemo = new ListViewAdapterDemo(); // listview
        setFloorInfo_cob_hob_DEMO();//스크롤뷰에 들어갈 층정보 세팅

//        animation_floor(); // 등록 층 애니메이션
        btn_back = (TextView) findViewById(R.id.btn_back_hop);
        btn_menu = (TextView) findViewById(R.id.btn_menu_hop);
        btn_next = (TextView) findViewById(R.id.btn_next_hop);
        btn_tenkey00 = (TextView) findViewById(R.id.btn_tenkey00);
        btn_tenkey01 = (TextView) findViewById(R.id.btn_tenkey01);
        btn_tenkey02 = (TextView) findViewById(R.id.btn_tenkey02);
        btn_tenkey03 = (TextView) findViewById(R.id.btn_tenkey03);
        btn_tenkey04 = (TextView) findViewById(R.id.btn_tenkey04);
        btn_tenkey05 = (TextView) findViewById(R.id.btn_tenkey05);
        btn_tenkey06 = (TextView) findViewById(R.id.btn_tenkey06);
        btn_tenkey07 = (TextView) findViewById(R.id.btn_tenkey07);
        btn_tenkey08 = (TextView) findViewById(R.id.btn_tenkey08);
        btn_tenkey09 = (TextView) findViewById(R.id.btn_tenkey09);
        btn_tenkey10 = (TextView) findViewById(R.id.btn_tenkey10);
        btn_tenkey11 = (TextView) findViewById(R.id.btn_tenkey11);
        img_wait_touch = (ImageView) findViewById(R.id.img_wait_touch);
        txt_pushed_floor = (TextView) findViewById(R.id.txt_pushed_floor);
        group_result = (RelativeLayout) findViewById(R.id.group_result);
        txt_assigned_car = (TextView) findViewById(R.id.txt_assigned_car);
        txt_assigned_floor = (TextView) findViewById(R.id.txt_assigned_floor);

        btn_back.setOnClickListener(btnClickListener);
        btn_back.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_next.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_next.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_menu.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_menu.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey00.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey00.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey01.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey01.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey02.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey02.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey03.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey03.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey04.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey04.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey05.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey05.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey06.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey06.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey07.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey07.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey08.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey08.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey09.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey09.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey10.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey10.setOnTouchListener((View.OnTouchListener) btnTouchListener);

        btn_tenkey11.setOnClickListener((View.OnClickListener) btnClickListener);
        btn_tenkey11.setOnTouchListener((View.OnTouchListener) btnTouchListener);

//        simulationThread();
        touchCommunication_HOP();
    }

    private void send_Floor_List() {
        Log.e("층 등록 됨 " + registerFloor, "층 등록 됨" + registerFloor);
        String[] floor_str = new String[floor_idx + 2];
        String floor_tv_str = "";

        // call이 0이면 등록 취소 1 이면 등록

        // 이미 층 등록이 되어 있으면 층 삭제
        if (floor_AL.contains(registerFloor + "층")) {
            floor_AL.remove(registerFloor + "층");

            floor_str[floor_idx] = registerFloor;
            adapterFloorDemo.isClicked = true;
            adapterFloorDemo.call = 0;
            adapterFloorDemo.floor_str = floor_AL;
            adapterFloorDemo.cancel_floor = registerFloor;
            adapterFloorDemo.up_cnt = up_Num;
            adapterFloorDemo.down_cnt = down_Num;

            floor_tv_str = floor_AL.toString();
            floor_tv_str = floor_tv_str.substring(1, floor_tv_str.length() - 1);
        } else {
            floor_AL.add(0, registerFloor + "층");

            floor_str[floor_idx] = registerFloor;
            adapterFloorDemo.isClicked = true;
            adapterFloorDemo.call = 1;
            adapterFloorDemo.floor_str = floor_AL;
            adapterFloorDemo.up_cnt = up_Num;
            adapterFloorDemo.down_cnt = down_Num;

            floor_tv_str = floor_AL.toString();
            floor_tv_str = floor_tv_str.substring(1, floor_tv_str.length() - 1);
        }
        set_floor_TV_1.setText(floor_tv_str);
        set_floor_TV_2.setText(floor_tv_str);

        adapterFloorDemo.notifyDataSetChanged();
        floor_idx++;
    }

    public void touchCommunication_HOP() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //사용자가 터치를 시작하면 flagTouchWaitTime 은 true가 되어 시간을 잰다.
                    if (flagTouchWaitTime) {
                        try {
                            Thread.sleep(100);
                            cntTouchWaitTime += 100;
                            //입력을 시작한 사용자가 일정 시간이상 다음 터치가 없을 경우 터치대기화면으로 리셋한다.
                            if (cntTouchWaitTime == 2000) {
                                flagTouchWaitTime = false;
                                registerFloor = "";
                                handler.sendEmptyMessage(CHANGE_TO_WAIT_IMAGE);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        cntTouchWaitTime = 0;
                    }
                }
            }
        }).start();
        //터치된 버튼 표시하는 부분
        setOnSampleReceivedEvent(new TouchEventListener() {
            @Override
            public void onReceivedEvent() {
                if (touchedFloor.equals("ENTER")) {
                    flagTouchWaitTime = false;
                    touchedFloor = "";
                    handler.sendEmptyMessage(DRAW_ASSIGN_RESULT);
                    if (!registerFloor.equals("") && !registerFloor.equals("B") && !registerFloor.equals("0") && !registerFloor.equals("B0")) {
                        if (registerFloor.length() > 1) {
                            // 1B 2B 이런 형태로 잘못된 데이터가 오면 필터링
                            if (!registerFloor.substring(1, 2).equals("B")) {
                                Log.e("upnum", String.valueOf(up_Num));
                                Log.e("down_Num", String.valueOf(down_Num));
                                Log.e("registerFloor", String.valueOf(registerFloor));
                                if (registerFloor.substring(0, 1).equals("B")) {
                                    // 지하층 처리
                                    if (Integer.parseInt(registerFloor.substring(1, 2)) <= down_Num) {
                                        // 리스트 뷰로 층 정보값을 넘겨줌
                                        send_Floor_List();
                                    } else {
                                        // 팝업
                                        Log.e("지하층 처리 범위 초과", "초과");
                                    }
                                } else {
                                    // 지상층 처리
                                    if (up_Num >= Integer.parseInt(registerFloor)) {
                                        // 리스트 뷰로 층 정보값을 넘겨줌
                                        send_Floor_List();
                                    } else {
                                        // 팝업
                                        Log.e("지상층 처리 범위 초과", "초과");
                                    }
                                }
                            } else {
                                // 팝업
                                Log.e("정상적이지 않은 층", "정상 아님");
                            }
                        } else {
                            // 지상층 처리
                            if (up_Num >= Integer.parseInt(registerFloor)) {
                                // 리스트 뷰로 층 정보값을 넘겨줌
                                send_Floor_List();
                            } else {
                                // 팝업
                                Log.e("지상층 처리 범위 초과", "초과");
                            }
                        }
                    }
                } else {
                    flagTouchWaitTime = true;
                    cntTouchWaitTime = 0;//touch가 있을 때마다 터치 초기화 시간을 0으로 리셋해서 초기화 까지의 시간을 늘린다.
                    registerFloor += touchedFloor;
                    //2자리 이상입력했을때
                    if (registerFloor.length() > 2) {
                        //초기화 후 1자리 수부터 다시 입력받기
                        registerFloor = touchedFloor;
                    }
                    handler.sendEmptyMessage(DRAW_BUTTON_NUMBER);
                    Log.e("registerFloor", registerFloor);
                }
            }
        });
    }

    public void removeAssignResult() {
        final int removeDelayTime = 3000;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(removeDelayTime);
                    //중간에 다른 터치가 안들어와서 registerfloor가 ""일경우만 (중간에 다른 버튼을 누르면 초기화면으로 돌아가지 않고 바로 버튼 터치 표시를 해야하기 때문)
                    if (registerFloor.equals("")) {
                        handler.sendEmptyMessage(CHANGE_TO_WAIT_IMAGE);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void simulationThread() {
        floorName = (TextView) findViewById(R.id.txtFloor);
        arrowImage = (ImageView) findViewById(R.id.imageArrow);
        lampName = (TextView) findViewById(R.id.txtLamp);

        arrowImage.setImageResource(R.drawable.arrow_otis_up);//초기 방향표시를 up으로 한다.
        new Thread(new Runnable() {
            @Override
            public void run() {
                String floor[] = {"B5", "B4", "B3", "B2", "B1", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

                int upDownFlag = 1;
                int cntLamp = 0;
                try {
                    while (Flag_simulationThread) {
                        //Log.e("cntFloor", String.valueOf(cntFloor_demo));
                        //Log.e("floorLength", String.valueOf(floor.length));
                        if (upDownFlag == 1) {
                            //Log.e("Direction", "UP");
                            carDirection = "UP";
                            cntFloor_demo += 1;
                        } else if (upDownFlag == 0) {
                            // Log.e("Direction", "DOWN");
                            carDirection = "DOWN";
                            cntFloor_demo -= 1;
                        } else {
                            //Log.e("Direction", "NONE");
                            carDirection = "NONE";
                        }
                        if (cntFloor_demo == floor.length - 1) {
                            upDownFlag = 0;
                            carDirection = "NONE";
                        }
                        if (cntFloor_demo == 0) {
                            upDownFlag = 1;
                            carDirection = "NONE";
                        }

                        try {

                            String FloorName = floor[cntFloor_demo];
                            carPosition = FloorName;
                            Log.e("carPosition", carPosition);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (cntLamp == 0) {
                            lampTitle = "만원";
                        }
                        if (cntLamp == 1) {
                            lampTitle = "점검중";
                        }
                        if (cntLamp == 2) {
                            lampTitle = "NONE";
                        }
                        cntLamp += 1;

                        if (cntLamp == 3) cntLamp = 0;

                        //Log.e("lampTitle", lampTitle);
                        //carPosition = Long.parseLong(str.substring(13, 15), 16) + "";
                        //Log.e("packet", Integer.toHexString(buffer[6]));

                                          /*  for (packetCnt = 0; packetCnt < packetLength; packetCnt++) {
                                                Log.e("packet", Integer.toHexString(buffer[packetCnt]));
                                            }*/
                        //Log.e("in", buffer.toString());
                        //handler.sendEmptyMessage(0);
                        handler.sendEmptyMessage(CAR_STATUS_KMEC);
                        handler.sendEmptyMessage(REGISTER_FLOOR_UPDATE);
                        //handler.sendEmptyMessage(FRAGMENT_FLOORLIST_IMAGE);

                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void manager_IPCAM() {
        videoView_ipcam = (VideoView) findViewById(R.id.videoView_ipcam);
        //IP캠 스트림링크를 가져오기.
        SharedPreferences pref = getSharedPreferences("ipcamStreamLinkFile", 0);
        String video_url = pref.getString("ipcamStreamLinkFile", "");

        final Uri uri = Uri.parse(video_url);
        //video.setMediaController(mc);
        videoView_ipcam.setVideoURI(uri);
        videoView_ipcam.start();
        videoView_ipcam.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e("videoView_ipcam", "setOnErrorListener");
                return false;
            }
        });
        videoView_ipcam.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e("videoView_ipcam", "setOnCompletionListener");
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean restart_rtsp_flag = false;
                while (true) {
                    try {
                        Thread.sleep(2000);
                        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkdInfo = cm.getActiveNetworkInfo();
                        if (networkdInfo != null) {
                            //Log.e("NETWORK","TRUE");
                            //rtsp 재생중 인터넷이 끊겼다가 인터넷이 다시 연결됬을때
                            if (restart_rtsp_flag) {
                                //videoView_ipcam.setVideoURI(uri);
                                rtspHandler.sendEmptyMessage(1);
                                restart_rtsp_flag = false;
                            }
                        } else {
                            Log.e("NETWORK", "FALSE");
                            //rtsp 재생중 인터넷이 끊기면 인터넷이 다시 연결될때 rtsp영상을 다시 재생하는 restart_rtsp_flag에 true를 대입한다.
                            restart_rtsp_flag = true;
                            //Toast.makeText(getApplicationContext(),"네트워크 연결상태를 확인해주세요.",Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("rtspException", e.toString());
                    }
                }
            }
        }).start();
    }

    Handler rtspHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //IP캠 스트림링크를 가져오기.
                    SharedPreferences pref = getSharedPreferences("ipcamStreamLinkFile", 0);
                    String video_url = pref.getString("ipcamStreamLinkFile", "");
                    final Uri uri = Uri.parse(video_url);
                    //다시 재생시작.
                    Log.e("videoVIew_ipcam", "restart");
                    videoView_ipcam.pause();
                    videoView_ipcam.setVideoURI(uri);
                    videoView_ipcam.start();
                    break;
            }
        }
    };

    public void settingFiles() {
        playListIndex = 0;
        //playList = null;
        playStatus = false;
        playPath = new PlayPath();

        if (playPath.isPlayListFile()) {
            Log.e("LcdActivity", "playPath.isPlayListFile");
            playList = SetPlayList.getPlayList(playPath.getPlayPath() + "/playList.txt");
            playListLength = playList.size();
        }

        contentsPath = new ContentsPath();
        playPath = new PlayPath();
        apkPath = new ApkPath();
    }

    public void KMEC_COP_Serial_Logic() {
        floorName = (TextView) findViewById(R.id.txtFloor);
        arrowImage = (ImageView) findViewById(R.id.imageArrow);
        lampName = (TextView) findViewById(R.id.txtLamp);
        // 현재 연결된 모든 USB 장치를 hashmap 으로 반환
        final HashMap<String, UsbDevice> usbDevicesList = manager.getDeviceList();
        Iterator<UsbDevice> deviter = usbDevicesList.values().iterator();

        if (usbDevicesList != null) {
            // deviter 을 읽어올 요소가 있으면 true 없으면 false
            while (deviter.hasNext()) {
                UsbDevice d = deviter.next();
                String a = d.getDeviceName();
                Log.e("deviceName", a);
                Log.e("VID", String.valueOf(d.getVendorId()));
                Log.e("PID", String.valueOf(d.getProductId()));
                // VID = 1133   PID = 50479
                if (d.getVendorId() == VID && d.getProductId() == PID) {
                    Log.e("usbdevice", "matched");
                    usbDevice = d;
                    if (!manager.hasPermission(usbDevice)) {
                        manager.requestPermission(usbDevice, mPermissionIntent);
                    }
                }
            }

            if (usbDevice != null && manager.hasPermission(usbDevice)) {
                Toast.makeText(getApplicationContext(), usbDevice.getProductId() + "/" + usbDevice.getVendorId(), Toast.LENGTH_LONG).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        conn_COP = manager.openDevice(usbDevice);
                        Log.e("getinterfacecount", String.valueOf(usbDevice.getInterfaceCount()));
                        if (usbDevice.getInterfaceCount() == 0) {
                            handler.sendEmptyMessage(USB_NOT_FOUND);
                            return;
                        }

                        if (!conn_COP.claimInterface(usbDevice.getInterface(0), true)) {
                            Log.e("test", "test2");
                            return;
                        }

                        UsbSerialControl usbSerialControl = new UsbSerialControl(usbDevice, manager);
                        usbSerialControl.setSerial(57600, 8, 0, 0, 0);

                        epIN_COP = null;
                        epOUT_COP = null;
                        UsbInterface usbIf = usbDevice.getInterface(0);

                        Log.e("test", "test3");
                        for (int i = 0; i < usbIf.getEndpointCount(); i++) {
                            if (usbIf.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                                if (usbIf.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN) {
                                    Log.e("ENDPOINT", "cpIN");
                                    epIN_COP = usbIf.getEndpoint(i);
                                } else {
                                    Log.e("ENDPOINT", "cpOUT");
                                    epOUT_COP = usbIf.getEndpoint(i);
                                }
                            }
                        }

                        int cnt_buffer_packet = 0;
                        boolean flag_buffer_packet = false;
                        set_KMEC_FloorNames();
                        kmec_cob_buffer_packet = new byte[64];
                        registeredFloorMap = new boolean[128];//총 128개 층이 등록될 수 있는데 등록된 층을 구분하는 맵
                        registeredFloorMap_compare = new boolean[128];//패킷이 수신될때마다 새로운 맵을 만드는데 기존의 맵을 저장해 두는 배열, 새로들어온 맵과 다를때만 UI갱신

                        while (true) {
                            try {
                                byte[] buffer = new byte[256];
                                int packetLength = conn_COP.bulkTransfer(epIN_COP, buffer, 50, 50);
                                //Log.e("packetLength", Integer.toString(packetLength));
                                if (packetLength >= 3) {
                                    StringBuilder str = new StringBuilder();
                                    int packetCnt = 0;
                                    String packetString = "";
                                    for (packetCnt = 2; packetCnt < packetLength; packetCnt++) {
                                        //Log.e("packet", Integer.toHexString(buffer[packetCnt]));
                                        //packetString += Integer.to0HexString(buffer[packetCnt] & 0xff) + " ";
                                        str.append(String.format("%c", buffer[packetCnt] & 0xff));
                                        if ((packetCnt == 2) && (buffer[2] == 0x02) && (!flag_buffer_packet)) {//앞에 두바이트가 쓰레기값으로 들어옴 &&Start byte가 0x02이므로
                                            kmec_cob_buffer_packet[cnt_buffer_packet] = buffer[2];
                                            flag_buffer_packet = true;
                                        } else if (flag_buffer_packet) {//Start Packet으로 0x02가 들어왔을경우 패킷을 저장하기 시작한다.
                                            cnt_buffer_packet++;
                                            kmec_cob_buffer_packet[cnt_buffer_packet] = buffer[packetCnt];
                                            if (cnt_buffer_packet == 38) {//패킷 39바이트가 쌓이면
                                                for (int i = 0; i <= cnt_buffer_packet; i++) {
                                                    packetString += Integer.toHexString(kmec_cob_buffer_packet[i] & 0xff) + " ";
                                                }
                                                Log.e("packetString", packetString);

                                                cnt_buffer_packet = 0;
                                                flag_buffer_packet = false;
                                                if (kmec_cob_buffer_packet[38] == 0x03) {//End Packet으로 0x03이 들어왔을경우에만 유효한 데이터로 판단하여 패킷에 맞는 UI작업을 한다.
                                                    kmec_COB_handler.sendEmptyMessage(COB_KMEC_UI);
                                                }
                                            }
                                        }
                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        }
    }

    Handler kmec_COB_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == COB_KMEC_UI) {
                //STX 0x02
                //EL Packet[32]
                //CS2 카상스테이션제어2
                //kmec_cob_buffer_packet[1];
                //kmec_cob_buffer_packet[2];
                //kmec_cob_buffer_packet[3];
                //kmec_cob_buffer_packet[4];
                byte lightBlAddr = 0;
                byte lightBitData = 0;
                byte regiFlag = 0;

                //조작반제어1(Front 정조작반),5(Rear 정조작반)
                if ((kmec_cob_buffer_packet[5] & 0xF0) == 0xB0) {
                    lightBlAddr = (byte) (kmec_cob_buffer_packet[5] & 0x0F); //Light ON/OFF Block Addr.
                    lightBitData = (byte) (kmec_cob_buffer_packet[6]);//Light ON/OFF bit data
                    calc_RegisterFloor(lightBlAddr, lightBitData);
                    //kmec_cob_buffer_packet[7];
                    //kmec_cob_buffer_packet[8];
                }

                //카상 스테이션 제어1(AAN)
                //kmec_cob_buffer_packet[9];
                //kmec_cob_buffer_packet[10];
                //kmec_cob_buffer_packet[11];
                //kmec_cob_buffer_packet[12];

                //조작반제어2(Front 부조작반), 6(Rear 부조작반)
                if ((kmec_cob_buffer_packet[13] & 0xF0) == 0xA0) {
                    lightBlAddr = (byte) (kmec_cob_buffer_packet[13] & 0x0F); //Light ON/OFF Block Addr
                    lightBitData = (byte) (kmec_cob_buffer_packet[14]); //Light ON/OFF bit data
                    calc_RegisterFloor(lightBlAddr, lightBitData);
                    //kmec_cob_buffer_packet[15];
                    //kmec_cob_buffer_packet[16];
                }

                //Indicator제어 (packet[17] 이 0xFx일때) 또는 Option제어 (packet[17] 이 0xCx일때)
                if ((kmec_cob_buffer_packet[17] & 0xF0) == 0xF0) {//indicator제어
                    byte floor = (byte) (kmec_cob_buffer_packet[18] & 0x7F);//층
                    carPosition = kmec_cob_floorNames[floor];
                    //Log.e("carposition",String.valueOf(floor));
                    if (carPosition != null) {
                        floorName.setText(carPosition);
                    }
                    byte direction = (byte) (kmec_cob_buffer_packet[19] & 0x60);//방향
                    if (direction == 0x40) {//UP
                        arrowImage.setImageResource(R.drawable.arrow_up);
                    } else if (direction == 0x20) {//DOWN
                        arrowImage.setImageResource(R.drawable.arrow_down);
                    } else {//NONE
                        // 211110 arbert14@hangisool.co.kr 층 방향 표시가 변환되는 시점에 방향표시등 표시가 hide 되는 것 방어 코드 추가
//                        arrowImage.setImageResource(R.drawable.hide);
                    }

                    byte lamp = (byte) (kmec_cob_buffer_packet[20] & 0x07);//램프
                    if (lamp == 0x01) {//LAMP1 - 우선순위1
                        lampTitle = "비상";
                        lampName.setVisibility(View.VISIBLE);
                        lampName.setText(lampTitle);
                    } else if (lamp == 0x02) {//LAMP2 - 우선순위2
                        lampTitle = "만원";
                        lampName.setVisibility(View.VISIBLE);
                        lampName.setText(lampTitle);
                    } else if (lamp == 0x04) {//LAMP3 - 우선순위3
                        lampTitle = "점검";
                        lampName.setVisibility(View.VISIBLE);
                        lampName.setText(lampTitle);
                    } else {
                        lampName.setVisibility(View.INVISIBLE);
                    }
                } else if ((kmec_cob_buffer_packet[17] & 0xF0) == 0xC0) {//Option제어
                }

                //조작반제어3(Front장애정조작반), 7(Rear 장애 정조작반)
                if ((kmec_cob_buffer_packet[21] & 0xF0) == 0x90) {
                    lightBlAddr = (byte) (kmec_cob_buffer_packet[21] & 0x0F); //Light ON/OFF Block Addr
                    lightBitData = (byte) (kmec_cob_buffer_packet[22]); //Light ON/OFF bit data
                    calc_RegisterFloor(lightBlAddr, lightBitData);
                    //kmec_cob_buffer_packet[23];
                    //kmec_cob_buffer_packet[24];
                }

                //카상스테이션 제어 1(AAN)
                //kmec_cob_buffer_packet[25];
                //kmec_cob_buffer_packet[26];
                //kmec_cob_buffer_packet[27];
                //kmec_cob_buffer_packet[28];

                //조작반제어4(Front 장애부조작반), 8(Rear 장애부조작반)
                if ((kmec_cob_buffer_packet[29] & 0xF0) == 0x80) {
                    lightBlAddr = (byte) (kmec_cob_buffer_packet[29] & 0x0F); //Light ON/OFF Block Addr
                    lightBitData = (byte) (kmec_cob_buffer_packet[30]); //Light ON/OFF bit data
                    calc_RegisterFloor(lightBlAddr, lightBitData);
                    //kmec_cob_buffer_packet[31];
                    //kmec_cob_buffer_packet[32];
                }

                //Door Packet[5]
                //kmec_cob_buffer_packet[33];
                //kmec_cob_buffer_packet[34];
                //kmec_cob_buffer_packet[35];
                //kmec_cob_buffer_packet[36];
                //kmec_cob_buffer_packet[37];

                //ETX 0x03
                //kmec_cob_buffer_packet[38];

                for (int i = 0; i < numFloorNames; i++) {
                    if (registeredFloorMap_compare[i] != registeredFloorMap[i]) {//기존 패킷데이터와 비교했을때 새로들어온 패킷과 다른 정보가 있을 경우.
                        registeredFloorMap_compare[i] = registeredFloorMap[i];//새로들어온 패킷정보로 업데이트
                        if (registeredFloorMap[i]) {
                            adapterFloor.getListVO().get((numFloorNames - 1) - i).setTouched(true);
                            adapterFloor.notifyDataSetChanged();//이 메소드를 호출할때 adapter의 getView가 안먹는다. 따라서 호출 횟수를 최소화
                        } else {
                            adapterFloor.getListVO().get((numFloorNames - 1) - i).setTouched(false);
                            adapterFloor.notifyDataSetChanged();//이 메소드를 호출할때 adapter의 getView가 안먹는다. 따라서 호출 횟수를 최소화
                        }
                    }
                }

            }
        }
    };

    public void calc_RegisterFloor(byte lightBlAddr, byte lightBitData) {
        byte regiFlag = 0;
        int addr = 0;
        for (int i = 0; i < 8; i++) {
            regiFlag = (byte) ((lightBitData >> i) & 0x01);
            addr = ((lightBlAddr * 8) + (i + 1) - 1);
            if ((regiFlag & 0x01) == 0x01) {
                registeredFloorMap[addr] = true;
            } else {
                registeredFloorMap[addr] = false;
            }
        }
    }

    public void set_KMEC_FloorNames() {
        for (int i = 0; i < getFloorName_KMEC_CODE().split(",").length; i++) {
            kmec_cob_floorNames[i] = getFloorName_KMEC_CODE().split(",")[i];
            //Log.e("floorNames",String.valueOf(i)+"="+floorNames[i]);
        }
    }

    public void KMEC_Serial_Logic() {

        floorName = (TextView) findViewById(R.id.txtFloor);
        arrowImage = (ImageView) findViewById(R.id.imageArrow);
        lampName = (TextView) findViewById(R.id.txtLamp);
        final HashMap<String, UsbDevice> usbDevicesList = manager.getDeviceList();
        Iterator<UsbDevice> deviter = usbDevicesList.values().iterator();

        arrowImage.setImageResource(R.drawable.arrow_up);//초기 방향표시를 up으로 한다.

        if (usbDevicesList != null) {
            while (deviter.hasNext()) {
                UsbDevice d = deviter.next();
                String a = d.getDeviceName();
                Log.e("deviceName", a);
                Log.e("VID", String.valueOf(d.getVendorId()));
                Log.e("PID", String.valueOf(d.getProductId()));
                //if (d.getVendorId() == 1133 && d.getProductId() == 50479) {
                if (d.getVendorId() == VID && d.getProductId() == PID) {
                    Log.e("usbdevice", "matched");
                    usbDevice = d;
                    if (!manager.hasPermission(usbDevice)) {
                        manager.requestPermission(usbDevice, mPermissionIntent);
                    }
                }
            }

            if (usbDevice != null && manager.hasPermission(usbDevice)) {
                Toast.makeText(getApplicationContext(), usbDevice.getProductId() + "/" + usbDevice.getVendorId(), Toast.LENGTH_LONG).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        UsbDeviceConnection conn = manager.openDevice(usbDevice);
                        //handler.sendEmptyMessage(1);
                        Log.e("getinterfacecount", String.valueOf(usbDevice.getInterfaceCount()));

                        if (!conn.claimInterface(usbDevice.getInterface(0), true))
                            return;

                        UsbSerialControl usbSerialControl = new UsbSerialControl(usbDevice, manager);
                        usbSerialControl.setSerial(57600, 8, 0, 0, 0);

                        UsbEndpoint epIN = null;
                        UsbEndpoint epOUT = null;
                        UsbInterface usbIf = usbDevice.getInterface(0);


                        for (int i = 0; i < usbIf.getEndpointCount(); i++) {
                            if (usbIf.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                                if (usbIf.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN) {
                                    epIN = usbIf.getEndpoint(i);
                                } else {
                                    epOUT = usbIf.getEndpoint(i);
                                }
                            }
                        }

                        while (true) {
                            try {
                                Thread.sleep(100);


                                byte[] buffer = new byte[256];
                                int packetLength = conn.bulkTransfer(epIN, buffer, 9, 100);
                                //Log.e("packetLength", Integer.toString(packetLength));
                                if (packetLength >= 3) {
                                    //Log.e("in", buffer.toString());
                                    if (buffer[2] == 0x02) {

                                        StringBuilder str = new StringBuilder();
                                        int packetCnt = 0;
                                        for (packetCnt = 0; packetCnt < packetLength; packetCnt++) {
                                            //Log.e("packet", Integer.toHexString(buffer[packetCnt]));
                                            str.append(String.format("%c", buffer[packetCnt] & 0xff));
                                        }

/*
                                if(str.substring(7,9).equals("FF") || str.substring(7,9).equals("00")){
                                    carState="";
                                }else if(str.substring(7,9).equals("01")){
                                    carState="Under Maintenance";
                                }else if(str.substring(7,9).equals("03")){
                                    carState="Return for Parking";
                                }else if(str.substring(7,9).equals("05")){
                                    carState="VIP Mode";
                                }else if(str.substring(7,9).equals("06")){
                                    carState="Independent Operation";

                                }else if(str.substring(7,9).equals("07")){
                                    carState="Earthquake S-wave";
                                }else if(str.substring(7,9).equals("08")){
                                    carState="Earthquake p-wave";
                                }else if(str.substring(7,9).equals("0D")){
                                    carState="Fire emergency recall Operation";
                                }else if(str.substring(7,9).equals("0E")){
                                    carState="Fireman Operation";
                                }
*/
                                        String Direction = str.substring(3, 4);

                                        if (Direction.equals("U")) {
                                            //Log.e("Direction", "UP");
                                            carDirection = "UP";
                                        } else if (Direction.equals("D")) {
                                            // Log.e("Direction", "DOWN");
                                            carDirection = "DOWN";
                                        } else {
                                            //Log.e("Direction", "NONE");
                                            carDirection = "NONE";
                                        }

                                        String FloorName = str.substring(4, 6);
                                        carPosition = FloorName;

                                        //부가등 패킷
                                        String Lamp = str.substring(6, 7);
                                        if (Lamp.equals("1")) {
                                            lampTitle = "만원";
                                        } else if (Lamp.equals("2")) {
                                            lampTitle = "점검중";
                                        } else {
                                            lampTitle = "NONE";
                                        }
                                        //Log.e("lampTitle", lampTitle);
                                        //carPosition = Long.parseLong(str.substring(13, 15), 16) + "";
                                        //Log.e("packet", Integer.toHexString(buffer[6]));

                                          /*  for (packetCnt = 0; packetCnt < packetLength; packetCnt++) {
                                                Log.e("packet", Integer.toHexString(buffer[packetCnt]));
                                            }*/
                                        //Log.e("in", buffer.toString());
                                        //handler.sendEmptyMessage(0);
                                        handler.sendEmptyMessage(CAR_STATUS_KMEC);
                                    }
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }


        }
    }

    public void DSIDS_Serial_Logic() {//DEASEONG IDS // CAN COMMUNICATION

        floorName = (TextView) findViewById(R.id.txtFloor);
        arrowImage = (ImageView) findViewById(R.id.imageArrow);
        lampName = (TextView) findViewById(R.id.txtLamp);
        HashMap<String, UsbDevice> usbDevicesList = manager.getDeviceList();
        Iterator<UsbDevice> deviter = usbDevicesList.values().iterator();

        mImageArrowParams = (RelativeLayout.LayoutParams) arrowImage.getLayoutParams();
        mTxtFloorParams = (RelativeLayout.LayoutParams) floorName.getLayoutParams();

        if (usbDevicesList != null) {
            while (deviter.hasNext()) {
                UsbDevice d = deviter.next();
                String a = d.getDeviceName();
                Log.e("deviceName", a);
                Log.e("VID", String.valueOf(d.getVendorId()));
                Log.e("PID", String.valueOf(d.getProductId()));
                if (d.getVendorId() == VID && d.getProductId() == PID) {
                    usbDevice = d;
                    if (!manager.hasPermission(usbDevice)) {
                        manager.requestPermission(usbDevice, mPermissionIntent);
                    }
                }
            }

            if (usbDevice != null && manager.hasPermission(usbDevice)) {
                Toast.makeText(getApplicationContext(), usbDevice.getProductId() + "/" + usbDevice.getVendorId(), Toast.LENGTH_LONG).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        UsbDeviceConnection conn = manager.openDevice(usbDevice);
                        //handler.sendEmptyMessage(1);


                        if (!conn.claimInterface(usbDevice.getInterface(0), true))
                            return;

                        UsbSerialControl usbSerialControl = new UsbSerialControl(usbDevice, manager);
                        usbSerialControl.setSerial(115200, 8, 0, 0, 0);

                        UsbEndpoint epIN = null;
                        UsbEndpoint epOUT = null;
                        UsbInterface usbIf = usbDevice.getInterface(0);


                        for (int i = 0; i < usbIf.getEndpointCount(); i++) {
                            if (usbIf.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                                if (usbIf.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN) {
                                    epIN = usbIf.getEndpoint(i);
                                } else {
                                    epOUT = usbIf.getEndpoint(i);
                                }
                            }
                        }

                        byte[] savePacket = new byte[128];
                        boolean flag_savePacket = false;
                        int cnt_savePacket = 0;

                        while (true) {
                            try {
                                Thread.sleep(100);


                                byte[] buffer = new byte[64];
                                int packetLength = conn.bulkTransfer(epIN, buffer, 64, 100);
                                //Log.e("packetLength", Integer.toString(packetLength));
                                if (packetLength >= 3) {
                                    for (int i = 2; i < packetLength; i++) {
                                       /* //ASCII CR인지 체크 CR을 받으면 그다음 패킷부터 첫번째 바이트임
                                        if(((buffer[i] & 0xff) == 0x0d) && !flag_savePacket) {
                                            flag_savePacket = true;//패킷을 저장하는 플래그를 true
                                        }*/

                                        if ((buffer[i] & 0xff) == 0x0d) {//ASCII CR이 다시 오면 패킷 프레임 다 받은것으로 확인
                                            String a = new String(savePacket);
                                            Log.e("packetString", a + "NO-" + cnt_savePacket);
                                            DSIDS_EL_UI_CONTROLER(savePacket, cnt_savePacket);
                                            cnt_savePacket = 0;
                                        } else {//ASCII CR이 아니면 계속 패킷저장
                                            savePacket[cnt_savePacket] = buffer[i];
                                            cnt_savePacket += 1;
                                        }
                                    }
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            } else {
                showToast("통신 USB연결을 확인해주세요.");
            }
        }
    }

    public byte AsciiToHex(byte ascii) {
        byte hex = 0;
        switch (ascii) {
            case 0x30:
                hex = 0x00;
                break;
            case 0x31:
                hex = 0x01;
                break;
            case 0x32:
                hex = 0x02;
                break;
            case 0x33:
                hex = 0x03;
                break;
            case 0x34:
                hex = 0x04;
                break;
            case 0x35:
                hex = 0x05;
                break;
            case 0x36:
                hex = 0x06;
                break;
            case 0x37:
                hex = 0x07;
                break;
            case 0x38:
                hex = 0x08;
                break;
            case 0x39:
                hex = 0x09;
                break;
            case 0x61:
                hex = 0x0a;
                break;
            case 0x62:
                hex = 0x0b;
                break;
            case 0x63:
                hex = 0x0c;
                break;
            case 0x64:
                hex = 0x0d;
                break;
            case 0x65:
                hex = 0x0e;
                break;
            case 0x66:
                hex = 0x0f;
                break;
            default:
                Log.e("AsciiToHex", "It's not a ASCII DATA");
                break;
        }
        return hex;
    }

    public void DSIDS_EL_UI_CONTROLER(byte[] packet, int packetLen) {
        /*CAN TO SERIAL 컨버터에서 HEX값을 ASCII로 변환하여 주기 떄문에 1개의 Hex가 2byte로 들어온다.
        EX. HEX값 0x13은 13으로 들어오기때문에  0x31, 0x33 두개 ASCII 값 byte로 들어옴*/
        if (packetLen > 25) {//26 BYTE의 패킷이 모두 들어왔을 경우에만 실행
            byte tmpHex = 0;

            //초기화
            carPosition = "0";
            lampTitle = "NONE";
            carDirection = "NONE";

            /*1 S0_Floor 현재 층 값, 0이면 1층
            packet[10];//E/L DATA block1의 1번 byte 좌
            packet[11];//E/L DATA block1의 1번 byte 우*/

             /*2 S1_State
            packet[12];//E/L DATA block1의 2번 byte 좌
            packet[13];//E/L DATA block1의 2번 byte 우*/
            tmpHex = (byte) (((AsciiToHex(packet[12])) << 4) | AsciiToHex(packet[13]));
            if ((tmpHex & 0x80) == 0x80) {//엘리베이터가 상승방향이거나 상승중
                carDirection = "UP";
            }
            if ((tmpHex & 0x10) == 0x10) {//정전정지
                lampTitle = "정전정지";
            }
            if ((tmpHex & 0x08) == 0x08) {//비상정지
                lampTitle = "비상정지";
            }
            if ((tmpHex & 0x04) == 0x04) {//비상정지
                lampTitle = "정원초과";
            }

             /*3 S2_State
            packet[14];//E/L DATA block1의 3번 byte 좌
            packet[15];//E/L DATA block1의 3번 byte 우*/
            tmpHex = (byte) (((AsciiToHex(packet[14])) << 4) | AsciiToHex(packet[15]));
            if ((tmpHex & 0x01) == 0x01) {//엘리베이터가 하강방향이거나 하강중
                carDirection = "DOWN";
            }
            if ((tmpHex & 0x10) == 0x10) {//화재
                lampTitle = "화재";
            }

            /*4 S3_State
            packet[16];//E/L DATA block1의 4번 byte 좌
            packet[17];//E/L DATA block1의 4번 byte 우*/
            tmpHex = (byte) (((AsciiToHex(packet[16])) << 4) | AsciiToHex(packet[17]));
            if ((tmpHex & 0x80) == 0x80) {//침수 등 예측못한 에러 발생시
                lampTitle = "침수or고장";
            }
            if ((tmpHex & 0x20) == 0x20) {//만원
                lampTitle = "만원";
            }
            if ((tmpHex & 0x04) == 0x04) {//화살표가 움직임
            }
            if ((tmpHex & 0x01) == 0x01) {//전용운전
                lampTitle = "전용 운전";
            }

             /*5 S4_State
            packet[18];//E/L DATA block1의 5번 byte 좌
            packet[19];//E/L DATA block1의 5번 byte 우*/
            tmpHex = (byte) (((AsciiToHex(packet[18])) << 4) | AsciiToHex(packet[19]));
            if ((tmpHex & 0x02) == 0x02) {//독립운전
                lampTitle = "독립운전";
            }


            /*6 DEST_FLR 다음에 도착할 목적층 정보
            packet[20];//E/L DATA block1의 6번 byte 좌
            packet[21];//E/L DATA block1의 6번 byte 우*/

            /*7 DSP1 현재 층 문자 표시 정보, 첫번째 dot(십의자리) character값
            packet[22];//E/L DATA block1의 7번 byte 좌
            packet[23];//E/L DATA block1의 7번 byte 우*/
            byte[] floorRL = new byte[2];
            byte[] floorS = new byte[1];
            tmpHex = (byte) (((AsciiToHex(packet[22])) << 4) | AsciiToHex(packet[23]));
            floorRL[0] = tmpHex;

            /*8 DSP2 현재 층 문자 표시 정보, 두번째 dot(일의자리) character값
            packet[24];//E/L DATA block1의 8번 byte 좌
            packet[25];//E/L DATA block1의 8번 byte 우*/
            tmpHex = (byte) (((AsciiToHex(packet[24])) << 4) | AsciiToHex(packet[25]));
            floorRL[1] = tmpHex;
            floorS[0] = tmpHex;

            //층명세팅
            if ((floorRL[0] == 0x30) || (floorRL[0] == 0x20)) {//십의 자리 수가 0일 경우
                carPosition = new String(floorS);
            } else {
                carPosition = new String(floorRL);
            }
            Log.e("carPosition", carPosition);

            handler.sendEmptyMessage(CAR_STATUS_DSIDS);
        }
    }

    public void SAMIL_Serial_Logic() {

        floorName = (TextView) findViewById(R.id.txtFloor);
        arrowImage = (ImageView) findViewById(R.id.imageArrow);
        lampName = (TextView) findViewById(R.id.txtLamp);
        HashMap<String, UsbDevice> usbDevicesList = manager.getDeviceList();
        Iterator<UsbDevice> deviter = usbDevicesList.values().iterator();

        mTodayTextParams = (RelativeLayout.LayoutParams) todayText.getLayoutParams();
        mTodayTimeTextParams = (RelativeLayout.LayoutParams) todayTimeText.getLayoutParams();
        mImageArrowParams = (RelativeLayout.LayoutParams) arrowImage.getLayoutParams();
        mTxtFloorParams = (RelativeLayout.LayoutParams) floorName.getLayoutParams();

        Log.e("TEST", "NO-1");

        if (usbDevicesList != null) {
            while (deviter.hasNext()) {
                UsbDevice d = deviter.next();
                String a = d.getDeviceName();
                Log.e("deviceName", a);
                Log.e("VID", String.valueOf(d.getVendorId()));
                Log.e("PID", String.valueOf(d.getProductId()));
                if (d.getVendorId() == VID && d.getProductId() == PID) {
                    usbDevice = d;
                    if (!manager.hasPermission(usbDevice)) {
                        manager.requestPermission(usbDevice, mPermissionIntent);
                    }
                }
            }

            if (usbDevice != null && manager.hasPermission(usbDevice)) {
                Toast.makeText(getApplicationContext(), usbDevice.getProductId() + "/" + usbDevice.getVendorId(), Toast.LENGTH_LONG).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        UsbDeviceConnection conn = manager.openDevice(usbDevice);
                        //handler.sendEmptyMessage(1);


                        if (!conn.claimInterface(usbDevice.getInterface(0), true))
                            return;

                        UsbSerialControl usbSerialControl = new UsbSerialControl(usbDevice, manager);
                        usbSerialControl.setSerial(57600, 8, 0, 0, 0);

                        UsbEndpoint epIN = null;
                        UsbEndpoint epOUT = null;
                        UsbInterface usbIf = usbDevice.getInterface(0);


                        for (int i = 0; i < usbIf.getEndpointCount(); i++) {
                            if (usbIf.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                                if (usbIf.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN) {
                                    epIN = usbIf.getEndpoint(i);
                                } else {
                                    epOUT = usbIf.getEndpoint(i);
                                }
                            }
                        }
                        //img/carName.ini에 저장되어있는 최하층~최상층 층이름을 불러온다.
                        String[] floorNames = new String[64];

                        for (int i = 0; i < getFloorNameString().split(",").length; i++) {
                            floorNames[i] = getFloorNameString().split(",")[i];
//                            Log.e("floorNames",String.valueOf(i)+"="+floorNames[i]);
                        }
                        int cnt = 0;
                        while (true) {
                            try {
                                Thread.sleep(200);

                                //ContentsChangeThread가 실행중인지 확인하기 위한 로직
                                cnt++;
                                if (cnt == 5 * (scrollCycle / 1000) * 10) {
                                    Log.e("SAMIL_Serial_Logic", "Check lifeCount->" + lifeCount2);
                                    cnt = 0;
                                    if (lifeCount2 <= 0) {
                                        Intent intent = new Intent(getApplicationContext(), LcdActivity.class);
                                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        restartLCDActivity();
                                    } else {
                                        lifeCount2 = 0;
                                    }
                                }

                                byte[] buffer = new byte[256];
//                                Log.d("buffer", String.valueOf(buffer));
                                int packetLength = conn.bulkTransfer(epIN, buffer, 15, 100);
                                //Log.e("packetLength", Integer.toString(packetLength));
                                if (packetLength >= 3) {
                                    //Log.e("in", buffer.toString());
                                    //Log.e("packet", Integer.toHexString(buffer[7]));
                                    //Log.e("packet", Integer.toHexString(buffer[8]));
                                    //checksum
                                    int checksum = (buffer[9] & 0xff) + (buffer[10] & 0xff) + (buffer[11] & 0xff) + (buffer[12] & 0xff);
                                    if (((buffer[7] & 0xff) == 0xBE) &&
                                            ((buffer[8] & 0xff) == 0x01) &&
                                            (((buffer[13] & 0xff) == (checksum & 0xff)) &&
                                                    ((buffer[14] & 0xff) == (checksum >> 8)))) {
                                        Log.e("packet", Integer.toHexString(buffer[2])
                                                + " " + Integer.toHexString(buffer[3])
                                                + " " + Integer.toHexString(buffer[4])
                                                + " " + Integer.toHexString(buffer[5])
                                                + " " + Integer.toHexString(buffer[6])
                                                + " " + Integer.toHexString(buffer[7])
                                                + " " + Integer.toHexString(buffer[8])
                                                + " " + Integer.toHexString(buffer[9])
                                                + " " + Integer.toHexString(buffer[10])
                                                + " " + Integer.toHexString(buffer[11])
                                                + " " + Integer.toHexString(buffer[12])
                                                + " " + Integer.toHexString(buffer[13])
                                                + " " + Integer.toHexString(buffer[14])
                                        );

                                        StringBuilder str = new StringBuilder();
                                        str.append(String.format("%c", buffer[12] & 0xff));
                                        str.append(String.format("%c", buffer[11] & 0xff));

                                        Log.d("buffercarPosition", String.valueOf(buffer[9] * 0x3f));

                                        //층표시1 (0="00"  //1=최하층 //2~ = 최하층+1~ //63 = 최하층+62")
                                        int carPosition2 = buffer[9] & 0x3f;
                                        Log.e("carPosition2", String.valueOf(carPosition2 - 1));
                                        if (carPosition2 != 0) {
                                            carPosition = floorNames[carPosition2 - 1];
                                        }
                                        //층표시2 // ASCII로 2BYTE 받음
                                        //String carPosition1 = str.substring(0, 2);
                                        //carPosition = carPosition1;

                                        //방향
                                        if ((buffer[9] & 0xC0) == 0x40) {
                                            //Log.e("Direction", "UP");
                                            carDirection = "UP";
                                        } else if ((buffer[9] & 0xC0) == 0x80) {
                                            //Log.e("Direction", "DOWN");
                                            carDirection = "DOWN";
                                        } else {
                                            //Log.e("Direction", "NONE");
                                            carDirection = "NONE";
                                        }
                                        //램프표시
                                        if (((buffer[10] & 0xff) & 0x01) == 0x01) {//FULL
                                            lampTitle = "만원 입니다.";
                                            lampNumber = 1;
                                        } else if (((buffer[10] & 0xff) & 0x02) == 0x02) {//OVERLOAD
                                            lampTitle = "인원이 초과되었습니다.";
                                            lampNumber = 2;
                                        } else if (((buffer[10] & 0xff) & 0x08) == 0x08) {//Maintenance
                                            lampTitle = "이 승강기는 점검중 입니다. 불편을 드려 죄송합니다. 다른 승강기를 이용해 주세요.";
                                            lampNumber = 3;
                                        } else if (((buffer[10] & 0xff) & 0x10) == 0x10) {//AUTO
                                            lampTitle = "NONE";
                                            lampNumber = 4;
                                        } else if (((buffer[10] & 0xff) & 0x20) == 0x20) {//Earthquake
                                            lampTitle = "이 승강기는 지진운전입니다. 불편을 드려 죄송합니다. 다른 승강기를 이용해 주세요.";
                                            lampNumber = 5;
                                        } else if (((buffer[10] & 0xff) & 0x40) == 0x40) {//Fire
                                            lampTitle = "이 승강기는 소방운전입니다. 불편을 드려 죄송합니다. 다른 승강기를 이용해 주세요.";
                                            lampNumber = 6;
                                        } else if (((buffer[10] & 0xff) & 0x80) == 0x80) {//Parking
                                            lampTitle = "이 승강기는 휴지중 입니다. 불편을 드려 죄송합니다. 다른 승강기를 이용해 주세요.";
                                            lampNumber = 7;
                                        } else {
                                            lampTitle = "NONE";
                                            lampNumber = 8;
                                        }

                                        //화살표 스크롤 패킷
                                        if (((buffer[10] & 0xff) & 0x04) == 0x04) {

                                        }
                                        //Log.e("lampTitle", lampTitle);
                                        //carPosition = Long.parseLong(str.substring(13, 15), 16) + "";
                                        //Log.e("packet", Integer.toHexString(buffer[6]));

                                          /*  for (packetCnt = 0; packetCnt < packetLength; packetCnt++) {
                                                Log.e("packet", Integer.toHexString(buffer[packetCnt]));
                                            }*/
                                        //Log.e("in", buffer.toString());
                                        //handler.sendEmptyMessage(0);
                                        handler.sendEmptyMessage(CAR_STATUS_SAMIL);
                                    } else {
                                        Log.e("PacketError", "Checksum not matched");
                                        Log.e("packet", Integer.toHexString(buffer[2])
                                                + " " + Integer.toHexString(buffer[3])
                                                + " " + Integer.toHexString(buffer[4])
                                                + " " + Integer.toHexString(buffer[5])
                                                + " " + Integer.toHexString(buffer[6])
                                                + " " + Integer.toHexString(buffer[7])
                                                + " " + Integer.toHexString(buffer[8])
                                                + " " + Integer.toHexString(buffer[9])
                                                + " " + Integer.toHexString(buffer[10])
                                                + " " + Integer.toHexString(buffer[11])
                                                + " " + Integer.toHexString(buffer[12])
                                                + " " + Integer.toHexString(buffer[13])
                                                + " " + Integer.toHexString(buffer[14])
                                        );
                                    }
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }


        }
    }

    public void setFloorInfo_cob_hob_DEMO() {
        String floorInfo = "";
        ImgPath path;//경로가 Img폴더라서 ImgPath 클래스를 사용
        path = new ImgPath();
        ListView listview;
        adapterFloorDemo = new ListViewAdapterDemo(); // listview

        //변수 초기화
        listview = (ListView) findViewById(R.id.lvFloorList);
        //어뎁터 할당
        listview.setAdapter(adapterFloorDemo);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("selected_item", String.valueOf(position) + "asDASDAS");
            }
        });

        try {
            //파일객체 생성및 입력 스트림 생성
            FileReader filereader = new FileReader(new File(path.getImgPath() + "/floorInfo_cob_hob.ini"));
            //입력 버퍼 생성
            BufferedReader bufReader = new BufferedReader(filereader);
            String line = "";
            int lineNumber = 0;
            String FloorName = "";
            String FloorDetail = "";
            int up_cnt = 0;
            int down_cnt = 0;
            while ((line = bufReader.readLine()) != null) {
                Log.e("floorInfo_cob_hob", line);
                if (lineNumber > 0) {//첫째줄은 [FloorInfo]이므로 제외하기위해서
                    FloorName = line.split("@#")[0];
                    Log.e("floorInfo_FloorName", FloorName);
                    FloorDetail = line.split("@#")[1];
                    Log.e("floorInfo_FloorDetail", FloorDetail);
                    adapterFloorDemo.addVO(FloorName, FloorDetail);
                    numFloorNames += 1;
                    // 가져온 값의 첫번쨰 글자가 B면 지하층으로 판단
                    if (line.split("@#")[0].substring(0, 1).equals("B")) {
                        down_cnt += 1;
                    } else {
                        up_cnt += 1;
                    }
                }
                lineNumber++;
            }
            //.readLine()은 끝에 개행문자를 읽지 않는다.
            bufReader.close();
            up_Num = up_cnt; // listview 에 전달할 최대 값 담기
            down_Num = down_cnt; // listview 에 전달할 최대 값 담기

            adapterFloorDemo.up_cnt = up_Num;
            adapterFloorDemo.down_cnt = down_Num;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFloorInfo_cob_hob() {
        String floorInfo = "";
        ImgPath path;//경로가 Img폴더라서 ImgPath 클래스를 사용
        path = new ImgPath();
        ListView listview;

        listview = (ListView) findViewById(R.id.lvFloorList);
        //어뎁터 할당
        listview.setAdapter(adapterFloorDemo);
        //변수 초기화
        adapterFloor = new ListViewAdapter_KMEC();
        listview = (ListView) findViewById(R.id.lvFloorList);
        //어뎁터 할당
        listview.setAdapter(adapterFloor);
        try {
            //파일객체 생성및 입력 스트림 생성
            FileReader filereader = new FileReader(new File(path.getImgPath() + "/floorInfo_cob_hob.ini"));
            //입력 버퍼 생성
            BufferedReader bufReader = new BufferedReader(filereader);
            String line = "";
            int lineNumber = 0;
            String FloorName = "";
            String FloorDetail = "";
            while ((line = bufReader.readLine()) != null) {
                Log.e("floorInfo_cob_hob", line);
                if (lineNumber > 0) {//첫째줄은 [FloorInfo]이므로 제외하기위해서
                    FloorName = line.split("@#")[0];
                    Log.e("floorInfo_cob_hob", FloorName);
                    FloorDetail = line.split("@#")[1];
                    Log.e("floorInfo_cob_hob", FloorDetail);
                    adapterFloor.addVO(FloorName, FloorDetail);
                    numFloorNames += 1;
                }
                lineNumber++;
            }
            //.readLine()은 끝에 개행문자를 읽지 않는다.
            bufReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFloorNameString() {
        String floorNames = "";
        ImgPath path;
        path = new ImgPath();
        Wini wini = null;
        try {
            wini = new Wini(new File(path.getImgPath(), "floorName.ini"));
            floorNames = wini.get("floorName", "floorName");
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Log.d("floorName", floorNames);
        return floorNames;
    }

    public String getFloorName_KMEC_CODE() {
        String floorNames = "";
        ImgPath path;
        path = new ImgPath();
        Wini wini = null;

        try {
            wini = new Wini(new File(path.getImgPath(), "floorName_KMEC_CODE.ini"));
            floorNames = wini.get("floorName_KMEC_CODE", "floorName");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("floorName_KMEC_CODE", floorNames);
        return floorNames;
    }

    public interface TouchEventListener {
        void onReceivedEvent();
    }

    public void setOnSampleReceivedEvent(TouchEventListener listener) {
        mTouchEventListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int X = (int) event.getX();
        int Y = (int) event.getY();
        int eventaction = event.getAction();


        if (Y > 1700) {
            onWindowFocusChanged(true);
            //Toast.makeText(this, "ACTION_DOWN AT COORDS " + "X: " + X + " Y: " + Y, Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.e("onKeyDown", String.valueOf(keyCode));

        //키보드의 숫자1 입력이 비상통화요청
        if (keyCode == KeyEvent.KEYCODE_1) {
            reqEmerCall();
        } else if (keyCode == KeyEvent.KEYCODE_V) {
            Toast.makeText(mContext, "Version-3.1", Toast.LENGTH_LONG).show();
        } else if (keyCode == KeyEvent.KEYCODE_S) {
            Toast.makeText(mContext, "Setting mode", Toast.LENGTH_LONG).show();
            setImmotalFlag(false);
            Intent intent = new Intent(LcdActivity.this, SettingActivity.class);
            startActivity(intent);
            System.runFinalizersOnExit(true);
            System.exit(0);
            finish();
        } else if (keyCode == KeyEvent.KEYCODE_I) {
            String Deviceinfo = "DeviceINFO\n";
            Deviceinfo += "BOARD = " + Build.BOARD + "\n";
            Deviceinfo += "BRAND = " + Build.BRAND + "\n";
            Deviceinfo += "CPU_ABI = " + Build.CPU_ABI + "\n";
            Deviceinfo += "DEVICE = " + Build.DEVICE + "\n";
            Deviceinfo += "DISPLAY = " + Build.DISPLAY + "\n";
            Deviceinfo += "FINGERPRINT = " + Build.FINGERPRINT + "\n";
            Deviceinfo += "HOST = " + Build.HOST + "\n";
            Deviceinfo += "ID = " + Build.ID + "\n";
            Deviceinfo += "MANUFACTURER = " + Build.MANUFACTURER + "\n";
            Deviceinfo += "MODEL = " + Build.MODEL + "\n";
            Deviceinfo += "PRODUCT = " + Build.PRODUCT + "\n";
            Deviceinfo += "TAGS = " + Build.TAGS + "\n";
            Deviceinfo += "TYPE = " + Build.TYPE + "\n";
            Deviceinfo += "USER = " + Build.USER + "\n";
            Deviceinfo += "VERSION.RELEASE = " + Build.VERSION.RELEASE + "\n";
            Toast.makeText(this, Deviceinfo, Toast.LENGTH_LONG).show();
        } else if (keyCode == KeyEvent.KEYCODE_H) {//도움말
            String keyINFO = "key INFO\n";
            keyINFO += "1 = 비상통화버튼" + "\n";
            keyINFO += "V = 소프트웨어 버전확인" + "\n";
            keyINFO += "I = DEVICE INFORMATION 확인" + "\n";
            keyINFO += "Y = 앱 꺼짐 방지 ON/OFF(사동자가 앱을 끄는것을 감지하여 자동으로 켜지도록하는 기능)" + "\n";
            Toast.makeText(this, keyINFO, Toast.LENGTH_LONG).show();
        } else if (keyCode == KeyEvent.KEYCODE_Y) {
            if (getImmotalFlag().equals("true")) {
                setImmotalFlag(false);
                Toast.makeText(this, "앱꺼짐방지 OFF상태입니다.", Toast.LENGTH_SHORT).show();
            } else {
                setImmotalFlag(true);
                Toast.makeText(this, "앱꺼짐방지 ON상태입니다.", Toast.LENGTH_SHORT).show();
            }
        } else if (keyCode == KeyEvent.KEYCODE_T) {
        } else if (keyCode == KeyEvent.KEYCODE_A) {//지역설정 액티비티로 이동 AreaSettingActivity
            Toast.makeText(mContext, "Area Setting mode", Toast.LENGTH_LONG).show();
            setImmotalFlag(false);
            Intent intent = new Intent(LcdActivity.this, AreaSettingActivity.class);
            startActivity(intent);
            System.runFinalizersOnExit(true);
            System.exit(0);
            finish();
        } else if (keyCode == KeyEvent.KEYCODE_3) {//성원 데모 1 액티비티로 다시시작
            Log.e("message", "A");
            SharedPreferences pref = getSharedPreferences(LcdActivity.fileName, 0);
            SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
            editor.putString("ScreenMode", "A"); // 입력할 값
            editor.commit();

            LcdActivity.restartFlag = true;
            LcdActivity.finishFlag = true;
        } else if (keyCode == KeyEvent.KEYCODE_4) {//성원 데모 2 액티비티로 다시시작
            Log.e("message", "B");
            SharedPreferences pref = getSharedPreferences(LcdActivity.fileName, 0);
            SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
            editor.putString("ScreenMode", "B"); // 입력할 값
            editor.commit();

            LcdActivity.restartFlag = true;
            LcdActivity.finishFlag = true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public String getImmotalFlag() {
        SharedPreferences pref = getSharedPreferences(ImmotalFlagFile, 0);
        String flagImmotalMode = pref.getString("flag", "true");
        return flagImmotalMode;
    }

    public void setImmotalFlag(boolean flag) {
        SharedPreferences pref = getSharedPreferences(ImmotalFlagFile, 0);
        SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
        String flagImmotalMode;
        if (flag) {
            flagImmotalMode = "true";
            editor.putString("flag", flagImmotalMode); // 입력할 값
            editor.commit();
        } else {
            flagImmotalMode = "false";
            editor.putString("flag", flagImmotalMode); // 입력할 값
            editor.commit();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        //이 액티비티에서 쓰는 모든 텍스트를 특정 폰트로 고정하기 위해서 Typekit라이브러리 사용
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //컨텐츠 쓰레드
        Log.e("LifeCycle", "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("LifeCycle", "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("LifeCycle", "onDestroy");
        ActivityCompat.finishAffinity(LcdActivity.this);
        startActivity(new Intent(LcdActivity.this, LcdActivity.class));
        System.runFinalizersOnExit(true);
        System.exit(0);
        //endImmotalService();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {

        //권한이 없는 경우
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            //최초 거부를 선택하면 두번째부터 이벤트 발생 & 권한 획득이 필요한 이융를 설명
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
            }

            //요청 팝업 팝업 선택시 onRequestPermissionsResult 이동
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    },
                    1);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                //권한이 있는 경우
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                }
                //권한이 없는 경우
                else {
                    Toast.makeText(this, "Permission always deny", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void reqEmerCall() {
        try {
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            //LAN이 연결 되어있을 경우에만 서버에 데이터 요청

            SharedPreferences pref1;
            pref1 = getSharedPreferences("hash", 0);
            String hash1 = pref1.getString("hash", "");
            Log.e("HASH", hash1);

            if (activeNetwork != null) {
                SharedPreferences pref;
                pref = getSharedPreferences("hash", 0);
                String hash = pref.getString("hash", "");
                Log.e("HASH", hash);
                set_emergency_flag(hash, 1);
                if (hash == null || hash.equals("")) {

                    Toast.makeText(getApplicationContext(), "Can not found hash for emergency call...", Toast.LENGTH_SHORT).show();
                    Log.e("HASH", "Can not found hash for emergency call...");

                } else {
                    set_emergency_flag(hash, 1);//set to emergency_flag 1}
                }
            } else {
                Toast.makeText(getApplicationContext(), "비상영상통화기능은 인터넷 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void contentsChangeCheckThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(scrollCycle * 10);
                        // contentsChangeThread가 정지해서 lifeCount를 올리지않을때
                        if (lifeCount <= 0) {//lifeCount가 0이거나 0보다 작으면 contentschangeThread가 동작하지 않거나 handler가 null이기때문에 프로그램 재시작한다.
                            Intent intent = new Intent(getApplicationContext(), LcdActivity.class);
                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            restartLCDActivity();
                        } else {
                            lifeCount = 0;
                        }
                    } catch (Exception e) {
                        Log.e("contenChangeCkTh", "catch", e);
                    }
                }
            }
        }).start();
    }

    private void weatherAreaSelect() {
        SharedPreferences pref = getSharedPreferences(SP_weatherRegionFile, 0);
        String weather_region = pref.getString(SP_weatherRegionName, "seoul");
        WeatherParser.area = weather_region;
        showToast("날씨지역=>" + WeatherParser.area);
    }

    public void showToast(final String msg) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show());
    }

    private void modeSelect() {
        //프로그램시작전 스크린모드를 판별하여 모드에 맞는 레이아웃을 선택한다.
        SharedPreferences pref = getSharedPreferences(fileName, 0);
        screenMode = pref.getString("ScreenMode", "SUNGWON");

        //고정테스트용
//        screenMode = "COP_SCROLL_TOUR_INFO";
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 화면을 landscape(가로) 화면으로 고정하고 싶은 경우
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 화면을 portrait(세로) 화면으로 고정하고 싶은 경우

        Configuration newConfig = new Configuration();

        switch (screenMode) {
            case "SUNGWON":
                Log.e("case", "SUNGWON DEMO 1");
                setContentView(R.layout.activity_touch_29_type_1);
                break;
            case "B":
                Log.e("case", "SUNGWON DEMO 2");
                setContentView(R.layout.activity_bar_sungwon2);
                break;
            case "FULL_H_MODE":
                Log.e("case", "FULL_H_MODE");
                setContentView(R.layout.activity_lcd_full);
                break;
            case "FULL_V_MODE":
                Log.e("case", "FULL_V_MODE");
                setContentView(R.layout.activity_lcd_full);
                break;
            case "3PART_H_MODE":
                Log.e("case", "3PART_H_MODE");
                setContentView(R.layout.activity_lcd_3part_h_1);
                //setContentView(R.layout.activity_lcd_3part_h_2);
                break;
            case "2PART_V_MODE":
                Log.e("case", "2PART_V_MODE");
                setContentView(R.layout.activity_lcd_2part_v_1);
                break;
            case "3PART_V_MODE":
                Log.e("case", "3PART_V_MODE");
                setContentView(R.layout.activity_lcd_3part_v_1);
                break;
            case "3PART_V_MODE_FULLHD":
                Log.e("case", "3PART_V_MODE_FULLHD");
                setContentView(R.layout.activity_lcd_3part_v_fullhd);
                break;
            case "3PART_V_KMEC":
                Log.e("case", "3PART_V_KMEC");
                setContentView(R.layout.activity_lcd_3part_kmec);
                break;
            case "3PART_H_BAR"://미쓰비시 신공장 전용 바 타입
                Log.e("case", "3PART_H_BAR");
                setContentView(R.layout.activity_lcd_bar);
                break;
            case "3PART_H_BAR_VIDEO"://바타입 오른쪽 상단 비디오 표시모드
                Log.e("case", "3PART_H_BAR_VIDEO");
                setContentView(R.layout.activity_lcd_bar_video);
                break;
            case "3PART_H_BAR_VIDEO_POLICE"://바타입 오른쪽 상단 비디오 표시모드(인천지방경찰청용)
                Log.e("case", "3PART_H_BAR_VIDEO_POLICE");
                setContentView(R.layout.activity_bar_police_1);
                break;
            case "3PART_H_LOBBY_VIDEO_POLICE"://LOBBY 1920x1080(인천지방경찰청용)
                Log.e("case", "3PART_H_LOBBY_VIDEO_POLICE");
                setContentView(R.layout.activity_police_lobby);
                break;
            case "3PART_H_BAR_IPCAM":
                Log.e("case", "3PART_H_BAR_IPCAM");
                setContentView(R.layout.activity_bar_sh);
                break;
            case "HOP":
                Log.e("case", "HOP");
                setContentView(R.layout.activity_touch_29_type_1);
                break;
            case "COP":
                Log.e("case", "COP");
                setContentView(R.layout.activity_touch_bar_demo);
                break;
            case "HOP_2":
                Log.e("case", "HOP_2");
                setContentView(R.layout.activity_touch_29_type_1);
                break;
            case "COP_2":
                Log.e("case", "COP_2");
                setContentView(R.layout.activity_touch_bar_cop_2);
                break;
            case "VMD":
                Log.e("case", "VMD");
                setContentView(R.layout.activity_vmd);
                break;
            case "COP_SCROLL":
                setContentView(R.layout.activity_touch_bar_hop_2);
                break;
            case "COP_SCROLL3":
                setContentView(R.layout.activity_touch_bar_cop_3);
                break;
            case "COP_SCROLL_DEMO":
                setContentView(R.layout.activity_touch_bar_cop_scroll);
                break;
            case "DSIDS":
                setContentView(R.layout.activity_dsids_3part);
                break;
            case "WEB_GAME_DEMO":
                setContentView(R.layout.activity_touch_bar_cop_3);
                break;
            case "COP_SCROLL_MOVIE_INFO":
                setContentView(R.layout.activity_touch_bar_cop_3);
                break;
            case "COP_AIR_KOREA":
                setContentView(R.layout.activity_touch_bar_cop_3);
                break;
            case "COP_SCROLL_CRYPTO":
                setContentView(R.layout.activity_touch_bar_cop_3);
                break;
            case "COP_SCROLL_TOUR_INFO":
                setContentView(R.layout.activity_touch_bar_cop_3);
                break;
            default://기본 구성은 FULL_H_MODE이다.
                setContentView(R.layout.activity_lcd_full);
                Log.e("case", "default");
                break;
        }
    }

    private void viewSizeSelect() {
        //뷰사이즈 조절을 판별한다.
        SharedPreferences pref = getSharedPreferences(screenSizeFile, 0);
        screenSize = pref.getString("screenSizeFile", "");

        switch (screenSize) {
            case "FULL_MAINVIEW":
                Log.e("screenSize", "FULL_MAINVIEW");
                screenSizeMode = 0;
                break;
            case "RESIZE_MAINVIEW":
                Log.e("screenSize", "RESIZE_MAINVIEW");
                screenSizeMode = 1;
                break;
            default://기본 구성은 FULL_MAINVIEW
                Log.e("screenSize", "default");
                screenSizeMode = 0;
                break;
        }
    }

    private void clockTimer() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(DATE_UPDATE);
            }
        };

        timer.schedule(timerTask, 0, 1000);
    }

    private void contentsChangeThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    //LAN이 연결 되어있을 경우에만 ftp 연결 후 파일을 다운로드한다.
                    if (activeNetwork != null) {
                        ftpContentsManager = new FtpContentsManager("/", contentsPath.getContentsPath());

                        ftpContentsManager.connect();
                        ftpContentsManager.fileDownload();
                    } else {
                        //Toast.makeText(getApplicationContext(),"네트워크 연결상태를 확인해주세요.",Toast.LENGTH_SHORT).show();
                    }
                    setCollectionContents();
                } catch (Exception e) {
                    Log.e("contentsChangeThread", "Exception");
                    e.printStackTrace();
                }
                int contentsUpdateCount = 0;
                while (true) {
                    //Log.e("contentsChangeThread","alive");
                    if (ThreadStopFlag == true) {
                        break;
                    }
                    try {
                        //App.deleteLogFile();
                        int choice = 0;
                        if (newsOrNotice == 1) choice = NOTICE_UPDATE;
                        else if (newsOrNotice == 0) choice = NEWS_UPDATE;
                        if (handler != null) {
                            lifeCount++;
                            lifeCount2++;
                            Log.e("ContentsChangeThread", "lifeCount->" + lifeCount);

                            handler.sendEmptyMessage(WEATHER_UPDATE);
                            handler.sendEmptyMessage(choice);
                            handler.sendEmptyMessage(FRAGMENT_EXCHANGE);
                            handler.sendEmptyMessage(STOCK_PRICE_UPDATE);
                            Thread.sleep(scrollCycle);
                            fragmentTransaction.remove(ExchangeFragment.newInstance());
                            handler.sendEmptyMessage(choice);
                            handler.sendEmptyMessage(FRAGMENT_MOVIE_RANK);
                            handler.sendEmptyMessage(STOCK_PRICE_UPDATE);
                            Thread.sleep(scrollCycle);
                            fragmentTransaction.remove(MovieRankFragment.newInstance());
                            contentsUpdateCount++;
                            if (contentsUpdateCount > 10) {
                                ftpContentsManager = new FtpContentsManager("/", contentsPath.getContentsPath());
                                if (ftpContentsManager.connect()) {
                                    ftpContentsManager.fileDownload();
                                }
                                contentsUpdateCount = 0;
                            }
                            setCollectionContents();
                        } else {
                            Log.e("contentsChangeThread", "handler == null");
                        }
                    } catch (Exception e) {
                        Log.e("contentsChangeThread", "catch", e);
                    }
                }
            }
        }).start();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("LifeCycle", "onStop");
        if (getImmotalFlag().equals("true")) {
            Log.e("ImmotalFlag", "true");
            restartLCDActivity();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("LifeCycle", "onPause");
    }

    @Override
    public void onBackPressed() {
        //playList = null;
        Log.d("action", "onBackPressed");
        //자동 앱 꺼짐 방지 on이면 사용자가 BackPress를 눌러도 다시 켜짐
        if (getImmotalFlag().equals("true")) {
            restartLCDActivity();
        } else {//자동 앱 꺼짐 방지 off이면 나가짐
            System.runFinalizersOnExit(true);
            System.exit(0);
        }
    }

    public void restartLCDActivity() {
        //startActivity(new Intent(LcdActivity.this, LcdActivity.class));
        ActivityCompat.finishAffinity(LcdActivity.this);
    }

    private void fileDownLoad() {
        try {
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            //LAN이 연결 되어있을 경우에만 ftp 연결 후 파일을 다운로드한다.
            if (activeNetwork != null) {
                FtpFileManager ftpFileManager = new FtpFileManager("/playList", playPath.getPlayPath());
                if (ftpFileManager.connect()) {
                    if (ftpFileManager.playScriptDownload()) {

                        playList = SetPlayList.getPlayList(playPath.getPlayPath() + "/playList.txt");

                        playListLength = playList.size();

                        File directory = new File(playPath.getPlayPath());
                        File[] files = directory.listFiles();

                        for (int i = 0; i < files.length; i++) {
                            if (!files[i].getName().equals("playList.txt")) {
                                files[i].delete();
                            }
                        }

                        ArrayList<String> downList = new ArrayList<String>();
                        for (int i = 0; i < playList.size(); i++) {
                            if (playList.get(i).split(",")[0].contains("img") || playList.get(i).split(",")[0].contains("video")) {
                                downList.add(playList.get(i).split(",")[1].trim());
                            }
                        }
                        ftpFileManager.connect();
                        ftpFileManager.fileDownload(downList);

                        playListIndex = 0;
                    }
                }
            } else {
            }
            setCollectionContents();
        } catch (Exception e) {
            Log.e("fileDownLoad", "Exception");
            e.printStackTrace();
        }
    }

    private void setCollectionContents() {
        try {
            HashMap<String, String> exchangeMap = ExchangeParser.getExchange(new File(contentsPath.getContentsPath(), "exchange.ini").getPath());
            HashMap<String, String> weatherMap = WeatherParser.getWeather(new File(contentsPath.getContentsPath(), "news.ini").getPath());
            HashMap<String, String> stockPriceMap = StockPriceParser.getStockPriceMap(new File(contentsPath.getContentsPath(), "stock_price.ini").getPath());
            ArrayList<String> movieRankList = MovieRankParser.getMovieRank(new File(contentsPath.getContentsPath(), "movieRank.ini").getPath());
            ArrayList<String> newsList = NewsParser.getNews(new File(contentsPath.getContentsPath(), "news.ini").getPath());
            ArrayList<String> noticeList = NoticeParser.getNotice(new File(contentsPath.getContentsPath(), "notice.ini").getPath());


            if (exchangeMap != null) {
                Contents.exchangeMap = exchangeMap;
            }
            if (weatherMap != null) {
                Contents.weatherMap = weatherMap;
            }
            if (movieRankList != null) {
                Contents.movieRankList = movieRankList;
            }
            if (newsList != null) {
                Contents.newsList = newsList;
            }
            if (noticeList != null) {
                Contents.noticeList = noticeList;
            }
            if (stockPriceMap != null) {
                Contents.stockPriceMap = stockPriceMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == LISTVIEW_CHANGE_BACKGROUND) {
//                adapterFloorDemo.isClicked = true;
//                ListVO listVO = new ListVO();
//                listVO.setFloors(floor_idx);
//                Log.e("foloridx", String.valueOf(floor_idx));
//                adapterFloorDemo.notifyDataSetChanged();
            }

            if (msg.what == IMAGE_FRAGMENT_DISPLAY) {
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainView, ImageFragment.newInstance());
                //fragmentTransaction.setCustomAnimations(R.anim.layout_leftin, R.anim.layout_leftout);
                //fragmentTransaction.commit();
                fragmentTransaction.commitAllowingStateLoss();
            }
            if (msg.what == VIDEO_FRAGMENT_DISPLAY) {
                //Log.e("sendEmptymessage","VIDEO_FRAGMENT_DISPLAY");
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainView, VideoFragment.newInstance());
                //fragmentTransaction.setCustomAnimations(R.anim.layout_leftin, 0, 0, R.anim.layout_leftout);
                //fragmentTransaction.commit();
                fragmentTransaction.commitAllowingStateLoss();
            }
            if (msg.what == FRAGMENT_EXCHANGE) {
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.contentsView, ExchangeFragment.newInstance());
                //fragmentTransaction.setCustomAnimations(R.anim.layout_leftin, R.anim.layout_leftout);
                //fragmentTransaction.commit();
                fragmentTransaction.commitAllowingStateLoss();
            }
            if (msg.what == FRAGMENT_EXCHANG_ECONOMY) {
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.contentsView, ExchangeEconomyFragment.newInstance());
                //fragmentTransaction.setCustomAnimations(R.anim.layout_leftin, R.anim.layout_leftout, R.anim.layout_leftin, R.anim.layout_leftout);
                //fragmentTransaction.commit();
                fragmentTransaction.commitAllowingStateLoss();
            }
            if (msg.what == FRAGMENT_MOVIE_RANK) {
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.contentsView, MovieRankFragment.newInstance());
                //fragmentTransaction.setCustomAnimations(R.anim.layout_leftin, R.anim.layout_leftout, R.anim.layout_leftin, R.anim.layout_leftout);
                //fragmentTransaction.commit();
                fragmentTransaction.commitAllowingStateLoss();
            }
            if (msg.what == NEWS_UPDATE) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.news_anim);
                try {
                    newsText.startAnimation(animation);

                    newsText.setText(Contents.newsList.get(newsCount));
                    Log.e("asdads", Contents.newsList.get(newsCount));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                newsCount++;
                Log.e("newsCount", "" + newsCount);
                if (Contents.newsList != null) {
                    Log.e("newsList.size()", "" + Contents.newsList.size());
                    if (newsCount == Contents.newsList.size()) {
                        newsCount = 1;//마지막 끝에 있는 문장을 확인할때 List가 1더 길어짐
                    }
                }
            }
            if (msg.what == NOTICE_UPDATE) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.news_anim);
                try {
                    newsText.startAnimation(animation);

                    newsText.setText(Contents.noticeList.get(noticeCount));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                noticeCount++;
                if (Contents.noticeList != null) {
                    if (noticeCount == Contents.noticeList.size()) {
                        noticeCount = 1;//마지막 끝에 있는 문장을 확인할때 List가 1더 길어짐
                    }
                }
            }
            if (msg.what == DATE_UPDATE) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss/E");
                Calendar calendar = Calendar.getInstance();
                String time = simpleDateFormat.format(calendar.getTime());
//                Log.e("asdasdasd", time);

                //time = "2010/12/12/13/30/22";
                //Log.e("localDate", time);
                if (screenMode.equals("3PART_V_KMEC")) {
                    date1 = time.split("/")[0] + "." +
                            time.split("/")[1] + "." +
                            time.split("/")[2];
                } else if (screenMode.equals("3PART_H_BAR_VIDEO_POLICE") || screenMode.equals("3PART_H_LOBBY_VIDEO_POLICE")) {
                    date1 = time.split("/")[0] + "." +
                            time.split("/")[1] + "." +
                            time.split("/")[2] + " " +
                            time.split("/")[6];
                } else {
                    date1 = time.split("/")[0] + "년" +
                            time.split("/")[1] + "월" +
                            time.split("/")[2] + "일";
                }
                String pmam = "";
                //오후일 경우
                if (Integer.parseInt(time.split("/")[3]) >= 12) {
                    if (screenMode.equals("2PART_V_MODE")) {
                        pmam = "오후";
                    }
                    //LCD BAR TYPE일때 공백으로 처리
                    else if (screenMode.equals("3PART_H_BAR") || screenMode.equals("3PART_H_BAR_VIDEO") || screenMode.equals("3PART_H_BAR_VIDEO_POLICE") || screenMode.equals("3PART_H_LOBBY_VIDEO_POLICE") || screenMode.equals("VMD")) {
                        pmam = "";
                    } else {
                        pmam = "PM";
                    }
                    //오후 1시 이상일경우
                    if (Integer.parseInt(time.split("/")[3]) >= 13) {
                        //10시 이전일 경우 앞에 0을 붙여줘야 하기 때문에 ex) 2시이면 02시
                        if ((Integer.parseInt(time.split("/")[3]) - 12) < 10) {
                            date2 = pmam +
                                    "" +
                                    "0" +
                                    (Integer.parseInt(time.split("/")[3]) - 12) +
                                    ":" +
                                    time.split("/")[4];
                        } else {
                            date2 = pmam +
                                    "" +
                                    (Integer.parseInt(time.split("/")[3]) - 12) +
                                    ":" +
                                    time.split("/")[4];
                        }
                    } else {
                        date2 = pmam +
                                "" +
                                time.split("/")[3] +
                                ":" +
                                time.split("/")[4];
                    }
                }
                //오전일 경우
                else {
                    if (screenMode.equals("2PART_V_MODE")) {
                        pmam = "오전";
                    } else if (screenMode.equals("3PART_H_BAR") || screenMode.equals("3PART_H_BAR_VIDEO") || screenMode.equals("3PART_H_BAR_VIDEO_POLICE") || screenMode.equals("3PART_H_LOBBY_VIDEO_POLICE") || screenMode.equals("VMD")) {
                        pmam = "";
                    } else {
                        pmam = "AM";
                    }
                    date2 = pmam +
                            "" +
                            time.split("/")[3] +
                            ":" +
                            time.split("/")[4];
                }

                if (screenMode.equals("3PART_H_BAR_VIDEO_POLICE")) {
                    //5분간격으로 날짜,시간의 좌표를 변경해줌(잔상방지)
                    if (time.split("/")[4].equals("05") ||
                            time.split("/")[4].equals("15") ||
                            time.split("/")[4].equals("25") ||
                            time.split("/")[4].equals("35") ||
                            time.split("/")[4].equals("45") ||
                            time.split("/")[4].equals("55")) {
                        fiveMinCheckFlag = true;//방향이 없을때 층이름 좌표값을 어떻게 할지 선택하는 플래그
                        mTodayTextParams.topMargin = 220;
                        todayText.setLayoutParams(mTodayTextParams);
                        mTodayTimeTextParams.topMargin = 290;
                        todayTimeText.setLayoutParams(mTodayTimeTextParams);
                        Log.e("time position", "1");
                    } else if (time.split("/")[4].equals("00") ||
                            time.split("/")[4].equals("10") ||
                            time.split("/")[4].equals("20") ||
                            time.split("/")[4].equals("30") ||
                            time.split("/")[4].equals("40") ||
                            time.split("/")[4].equals("50")) {
                        fiveMinCheckFlag = false;
                        mTodayTimeTextParams.topMargin = 210;
                        todayTimeText.setLayoutParams(mTodayTimeTextParams);
                        mTodayTextParams.topMargin = 350;
                        todayText.setLayoutParams(mTodayTextParams);
                        Log.e("time position", "2");
                    }
                }
                if (screenMode.equals("3PART_H_LOBBY_VIDEO_POLICE")) {
                    //5분간격으로 날짜,시간의 좌표를 변경해줌(잔상방지)
                    if (time.split("/")[4].equals("05") ||
                            time.split("/")[4].equals("15") ||
                            time.split("/")[4].equals("25") ||
                            time.split("/")[4].equals("35") ||
                            time.split("/")[4].equals("45") ||
                            time.split("/")[4].equals("55")) {
                        fiveMinCheckFlag = true;//방향이 없을때 층이름 좌표값을 어떻게 할지 선택하는 플래그
                        mTodayTextParams.topMargin = 300;
                        todayText.setLayoutParams(mTodayTextParams);
                        mTodayTimeTextParams.topMargin = 370;
                        todayTimeText.setLayoutParams(mTodayTimeTextParams);
                        Log.e("time position", "1");
                    } else if (time.split("/")[4].equals("00") ||
                            time.split("/")[4].equals("10") ||
                            time.split("/")[4].equals("20") ||
                            time.split("/")[4].equals("30") ||
                            time.split("/")[4].equals("40") ||
                            time.split("/")[4].equals("50")) {
                        fiveMinCheckFlag = false;
                        mTodayTimeTextParams.topMargin = 290;
                        todayTimeText.setLayoutParams(mTodayTimeTextParams);
                        mTodayTextParams.topMargin = 430;
                        todayText.setLayoutParams(mTodayTextParams);
                        Log.e("time position", "2");
                    }
                    //날씨와 시간을 번갈아서 보여준다.
                    if (weatherOrTime) {
                        weatherOrTime = false;
                        //날씨
                        weatherImage.setVisibility(View.INVISIBLE);
                        tmxText2.setVisibility(View.INVISIBLE);
                        tmnText2.setVisibility(View.INVISIBLE);
                        celsiusText.setVisibility(View.INVISIBLE);
                        todayText.setVisibility(View.INVISIBLE);
                        //시간
                        todayText.setVisibility(View.VISIBLE);
                        todayTimeText.setVisibility(View.VISIBLE);
                    } else {
                        weatherOrTime = true;
                        //날씨
                        weatherImage.setVisibility(View.VISIBLE);
                        tmxText2.setVisibility(View.VISIBLE);
                        tmnText2.setVisibility(View.VISIBLE);
                        celsiusText.setVisibility(View.VISIBLE);
                        todayText.setVisibility(View.VISIBLE);
                        //시간
                        todayText.setVisibility(View.INVISIBLE);
                        todayTimeText.setVisibility(View.INVISIBLE);
                    }
                }

                todayText.setText(date1);
                todayTimeText.setText(date2);
            }
            if (msg.what == WEATHER_UPDATE) {
                try {
                    if (Contents.weatherMap.get("weather") != null && (weatherText != null)) {
                        weatherText.setText(Contents.weatherMap.get("weather"));
                        celsiusText.setText(Contents.weatherMap.get("temp") + "℃");
                        tmxText2.setText(Contents.weatherMap.get("tmx") + "℃");
                        tmnText2.setText(Contents.weatherMap.get("tmn") + "℃");
                        popText2.setText(Contents.weatherMap.get("pop") + "%");
                        humidityText2.setText(Contents.weatherMap.get("humidity") + "%");


                        String weather = Contents.weatherMap.get("weather");
                        if (weather != null) {
                            if (screenMode.equals("3PART_H_LOBBY_VIDEO_POLICE")) {
                                if (weather.equals("맑음")) {
                                    weatherImage.setBackgroundResource(R.drawable.w2_01);
                                } else if (weather.equals("구름 조금")) {
                                    weatherImage.setBackgroundResource(R.drawable.w2_02);
                                } else if (weather.equals("구름 많음")) {
                                    weatherImage.setBackgroundResource(R.drawable.w2_03);
                                } else if (weather.equals("흐림")) {
                                    weatherImage.setBackgroundResource(R.drawable.w2_04);
                                } else if (weather.equals("비")) {
                                    weatherImage.setBackgroundResource(R.drawable.w2_05);
                                } else if (weather.equals("눈/비")) {
                                    weatherImage.setBackgroundResource(R.drawable.w2_06);
                                } else if (weather.equals("눈")) {
                                    weatherImage.setBackgroundResource(R.drawable.w2_07);
                                }
                            } else {
                                if (weather.equals("맑음")) {
                                    weatherImage.setBackgroundResource(R.drawable.w_01);
                                } else if (weather.equals("구름 조금")) {
                                    weatherImage.setBackgroundResource(R.drawable.w_02);
                                } else if (weather.equals("구름 많음")) {
                                    weatherImage.setBackgroundResource(R.drawable.w_03);
                                } else if (weather.equals("흐림")) {
                                    weatherImage.setBackgroundResource(R.drawable.w_04);
                                } else if (weather.equals("비")) {
                                    weatherImage.setBackgroundResource(R.drawable.w_05);
                                } else if (weather.equals("눈/비")) {
                                    weatherImage.setBackgroundResource(R.drawable.w_06);
                                } else if (weather.equals("눈")) {
                                    weatherImage.setBackgroundResource(R.drawable.w_07);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (msg.what == STOCK_PRICE_UPDATE) {
                try {
                    if (stockPriceFlag == KOSDAQ) {
                        kospiText.setText("코스닥");
                        kospiPriceText.setText(Contents.stockPriceMap.get("kosdaq_price"));
                        if (Contents.stockPriceMap.get("kosdaq_contrast") != null) {
                            if (Double.parseDouble(Contents.stockPriceMap.get("kosdaq_contrast")) >= 0) {
                                kospiContastText.setTextColor(Color.parseColor("#FF5A5A"));
                                kospiContastText.setText("▲" + Contents.stockPriceMap.get("kosdaq_contrast"));
                            } else {
                                kospiContastText.setTextColor(Color.parseColor("#489CFF"));
                                kospiContastText.setText("▼" + Contents.stockPriceMap.get("kosdaq_contrast"));
                            }
                        }
                        stockPriceFlag = KOSPI;
                    } else if (stockPriceFlag == KOSPI) {
                        kospiText.setText("코스피");
                        kospiPriceText.setText(Contents.stockPriceMap.get("kospi_price"));
                        if (Contents.stockPriceMap.get("kospi_contrast") != null) {
                            if (Double.parseDouble(Contents.stockPriceMap.get("kospi_contrast")) >= 0) {
                                kospiContastText.setTextColor(Color.parseColor("#FF5A5A"));
                                kospiContastText.setText("▲" + Contents.stockPriceMap.get("kospi_contrast"));
                            } else {

                                kospiContastText.setTextColor(Color.parseColor("#489CFF"));
                                kospiContastText.setText("▼" + Contents.stockPriceMap.get("kospi_contrast"));
                            }
                        }
                        stockPriceFlag = KOSDAQ;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            if (msg.what == CAR_STATUS_KMEC) {
                try {
                    //층이름
                    if (carPosition != null) {
                        floorName.setText(carPosition);
                    }
                    //방향 표시
                    if (carDirection != null) {
                        if (carDirection.equals("UP")) {
                            arrowImage.setImageResource(R.drawable.arrow_otis_up);
                        } else if (carDirection.equals("DOWN")) {
                            arrowImage.setImageResource(R.drawable.arrow_otis_down);
                        } else {
                            // 211110 arbert14@hangisool.co.kr 층 방향 표시가 변환되는 시점에 방향표시등 표시가 hide 되는 것 방어 코드 추가
//                            arrowImage.setImageResource(R.drawable.hide);
                        }
                    }

                    //램프 표시
                    if (lampTitle != null) {
                        if (lampTitle.equals("점검중")) {
                            lampName.setVisibility(View.VISIBLE);
                            lampName.setText(lampTitle);
                        } else if (lampTitle.equals("만원")) {
                            lampName.setVisibility(View.VISIBLE);
                            lampName.setText(lampTitle);
                        } else {
                            lampName.setVisibility(View.INVISIBLE);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (msg.what == CAR_STATUS_SAMIL) {

                try {
                    //층이름
                    if (carPosition != null) {
                        floorName.setText(carPosition);
                    }
                    Log.e("carPosition -->", carPosition);
                    Log.e("carDirection -->", carDirection);
                    //방향 표시
                    if (carDirection != null) {
                        Log.e("carDirection2 -->", carDirection);
                        if (carDirection.equals("UP")) {
                            mImageArrowParams.leftMargin = 0;
                            arrowImage.setLayoutParams(mImageArrowParams);
                            mTxtFloorParams.leftMargin = 200;
                            floorName.setLayoutParams(mTxtFloorParams);
                            arrowImage.setImageResource(R.drawable.arrow_otis_up);
//                            Glide.with(getApplicationContext()).load(R.raw.arrow_up).into(arrowImage);
                        } else if (carDirection.equals("DOWN")) {
                            mImageArrowParams.leftMargin = 330;
                            arrowImage.setLayoutParams(mImageArrowParams);
                            mTxtFloorParams.leftMargin = -40;
                            floorName.setLayoutParams(mTxtFloorParams);
                            arrowImage.setImageResource(R.drawable.arrow_otis_down);
//                            Glide.with(getApplicationContext()).load(R.raw.arrow_down).into(arrowImage);
                        } else {
                            arrowImage.setImageResource(R.drawable.hide);
                        }
                    }

                    //램프 표시
                    if (lampTitle != null) {
                        switch (lampNumber) {
                            case 1:
                            case 2:
                            case 3:
                            case 5:
                            case 6:
                            case 7:
                            case 9:
                            case 10:
                                lampName.setText(lampTitle);
                                lampName.setVisibility(View.VISIBLE);
                                break;
                            case 4:
                            case 8:
                                lampName.setText(lampTitle);
                                lampName.setVisibility(View.INVISIBLE);
                                break;
                        }
                    }
                    SharedPreferences pref = LcdActivity.mContext.getSharedPreferences(LcdActivity.elevatorStateFile, 0);
                    SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요

                    editor.putString("floor", carPosition);
                    editor.putString("direction", carDirection);
                    editor.putString("lamp", lampTitle);
                    editor.putString("lampNumber", String.valueOf(lampNumber));
                    editor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (msg.what == CAR_STATUS_DSIDS) {
                //층이름
                if (carPosition != null) {
                    floorName.setText(carPosition);
                }
                //방향 표시
                if (carDirection != null) {
                    if (carDirection.equals("UP")) {
                        arrowImage.setImageResource(R.drawable.arrow_otis_up);
                    } else if (carDirection.equals("DOWN")) {
                        arrowImage.setImageResource(R.drawable.arrow_otis_down);
                    } else {
                        arrowImage.setImageResource(R.drawable.hide);
                    }
                }

                //램프 표시
                if (lampTitle != null) {
                    if (lampTitle.equals("NONE")) {
                        lampName.setVisibility(View.INVISIBLE);
                    } else {
                        lampName.setVisibility(View.VISIBLE);
                        lampName.setText(lampTitle);
                    }
                }
            }

            if (msg.what == REGISTER_FLOOR_UPDATE) {
                try {
                    btnTouched[cntFloor_demo] = true;
                    switchFloorButton(cntFloor_demo);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (msg.what == DRAW_BUTTON_NUMBER) {
                Log.e("registerFloor", registerFloor);
                txt_pushed_floor.setText(registerFloor);
                img_wait_touch.setVisibility(View.INVISIBLE);
                group_result.setVisibility(View.INVISIBLE);
                txt_pushed_floor.setVisibility(View.VISIBLE);
            }
            if (msg.what == DRAW_ASSIGN_RESULT) {
                if (hogiIndex == 0) {
                    txt_assigned_car.setText("A");
                    hogiIndex++;
                } else if (hogiIndex == 1) {
                    txt_assigned_car.setText("B");
                    hogiIndex++;
                } else if (hogiIndex == 2) {
                    txt_assigned_car.setText("C");
                    hogiIndex++;
                } else if (hogiIndex == 3) {
                    txt_assigned_car.setText("D");
                    hogiIndex = 0;
                }
                txt_assigned_floor.setText(registerFloor);
                registerFloor = "";
                img_wait_touch.setVisibility(View.INVISIBLE);
                group_result.setVisibility(View.VISIBLE);
                txt_pushed_floor.setVisibility(View.INVISIBLE);
                removeAssignResult();
            }
            if (msg.what == CHANGE_TO_WAIT_IMAGE) {
                img_wait_touch.setVisibility(View.VISIBLE);
                group_result.setVisibility(View.INVISIBLE);
                txt_pushed_floor.setVisibility(View.INVISIBLE);
            }
            if (msg.what == FRAGMENT_FLOORLIST_IMAGE) {
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.info_view, FloorListImageFragment.newInstance());
//                fragmentTransaction.setCustomAnimations(R.anim.layout_leftin, R.anim.layout_leftout, R.anim.layout_leftin, R.anim.layout_leftout);
//                fragmentTransaction.commit();
                fragmentTransaction.commitAllowingStateLoss();
            }
            if (msg.what == FRAGMENT_CONTENTS) {
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.info_view, MenuContentsFragment.newInstance());
                fragmentTransaction.commitAllowingStateLoss();
            }
            if (msg.what == USB_NOT_FOUND) {
                Toast.makeText(getApplicationContext(), "usb드라이버를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
            if (msg.what == EMERCALL_LAMP) {
                Flag_simulationThread = false;
                lampName.setText("비상영상통화를 요청중입니다.");
                lampName.setVisibility(View.VISIBLE);
            }
        }


    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d("Focus debug", "Focus changed !");

        if (!hasFocus) {
            Log.d("Focus debug", "Lost focus !");


            //option 1
            /*hideStatusBar();
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            getApplicationContext().sendBroadcast(closeDialog);*/
        }
        setMainViewSize();//모든 것이 그려지는 메인뷰의 사이즈를 저장한다.
    }

    public static void preventStatusBarExpansion(Context context) {
        //화면 최상단, status bar보다 위에 Layer를 올리는 방식으로 터치이벤트를 가로채서 사용자가 status bar를 제어 못하도록함
        WindowManager manager = ((WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;

        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = 0;
        if (resId > 0) {
            result = context.getResources().getDimensionPixelSize(resId);
        } else {
            // Use Fallback size:
            result = 60; // 60px Fallback
        }

        localLayoutParams.height = result;
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        CustomViewGroup view = new CustomViewGroup(context);
        manager.addView(view, localLayoutParams);
    }

    public static void preventNavigationBar(Context context) {
        //화면 최상단, NavigationBar보다 위에 Layer를 올리는 방식으로 터치이벤트를 가로채서 사용자가 Navigation Bar를 제어 못하도록함
        WindowManager manager = ((WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.BOTTOM;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;

        int result = 100;

        localLayoutParams.height = result;
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        CustomViewGroup view = new CustomViewGroup(context);
        manager.addView(view, localLayoutParams);
    }

    public static class CustomViewGroup extends ViewGroup {
        public CustomViewGroup(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            // Intercepted touch!
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case OVERLAY_PERMISSION_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(getApplicationContext())) {
                        // Do something with overlay permission
                        preventNavigationBar(this);
                        preventStatusBarExpansion(this);
                    } else {
                        // Show dialog which persuades that we need permission
                        Toast.makeText(getApplicationContext(), "화면 Overlay권한을 부여해주세요. " +
                                "그렇지 않으면 사용자가 무작위로 화면밖으로 나가는 기기제어가 가능해져 시스템이 불안할 수 있습니다.", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public void startOverlayWindowService(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(context)) {//버전이 마쉬멜로우 이상일경우에는 사용자가 직접 권한을 허용해야한다.
            setImmotalFlag(false);//앱꺼짐 방지 OFF
            this.startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION), OVERLAY_PERMISSION_CODE);
        } else {//버전이 그 이하일 경우는 화면 Oerlay가 manifests에서 정의만해도 되므로 바로 overlay시키면된다.
//            preventNavigationBar(this);
//            preventStatusBarExpansion(this);
            setImmotalFlag(true);//앱꺼짐 방지 OFF
        }
    }

    public void setMainViewSize() {
        View mainView = (View) findViewById(R.id.mainView);
        mainViewHeight = mainView.getHeight();
        mainViewWidth = mainView.getWidth();
        //Log.e("mainViewHegight", String.valueOf(LcdActivity.mainViewHeight));
        //Log.e("mainViewWidth", String.valueOf(LcdActivity.mainViewWidth));
    }

    public void checkWebRTCStart() {
        try {
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            //LAN이 연결 되어있을 경우에만 서버에 데이터 요청
            if (activeNetwork != null) {
                SharedPreferences pref;
                pref = getSharedPreferences("hash", 0);
                String hash = pref.getString("hash", "");
                if (hash == null || hash.equals(""))
                    Toast.makeText(getApplicationContext(), "Can not found hash for emergency call...", Toast.LENGTH_SHORT).show();
                else check_flag_calling(hash);
            }
        } catch (Exception e) {
        }
    }

    private void check_flag_calling(String hash) {

        new Thread() {
            public void run() {
                while (!webRTCQuitThread) {
                    check_calling_flag(hash);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void check_calling_flag(final String hash) {
        new Thread() {
            public void run() {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BackendService.URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(getUnsafeOkHttpClient().build())
                        .build();

                Log.e("test", "asd");

                BackendService service = retrofit.create(BackendService.class);

                service.getCarCall(hash).enqueue(new retrofit2.Callback<Data>() {
                    @Override
                    public void onResponse(retrofit2.Call<Data> call, retrofit2.Response<Data> response) {
                        Log.e("RetrofitSucced", response.message());
                        if (response.isSuccessful()) {
                            Data body = response.body();
                            if (body.getResult().equals("success")) {
                                try {
                                    JSONObject obj = new JSONObject(body.getMessage());

                                    //Log.e("emergency","=> "+obj.getInt("emergency"));
                                    //Log.e("calling","=> "+obj.getInt("calling"));
                                    if (obj.getInt("calling") == 1) {//if calling Flag turn to 1
                                        webRTCQuitThread = true;
                                        setImmotalFlag(false);
                                        Intent intent = new Intent(LcdActivity.this, WebRTCActivity.class);
                                        System.runFinalizersOnExit(true);
                                        System.exit(0);
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.e("test", e.getMessage());
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "getCarCall-" + body.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("data.result", body.getResult());
                                Log.e("data.message", body.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Data> call, Throwable t) {
                        Log.e("RetrofitFailed", t.getMessage());
                        Toast.makeText(getApplicationContext(), "getCarCall-" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();
    }

    public void set_emergency_flag(final String hash, final int emerFlag) {
        new Thread() {
            public void run() {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BackendService.URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(getUnsafeOkHttpClient().build())
                        .build();

                BackendService service = retrofit.create(BackendService.class);

                service.postSetCarEmer(hash, emerFlag, 0).enqueue(new retrofit2.Callback<Data>() {
                    @Override
                    public void onResponse(retrofit2.Call<Data> call, retrofit2.Response<Data> response) {
                        Log.e("RetrofitSucced", response.message());
                        if (response.isSuccessful()) {
                            Data body = response.body();
                            if (body.getResult().equals("success")) {
                                handler.sendEmptyMessage(EMERCALL_LAMP);
                                Log.e("data.result", body.getResult());
                                Log.e("data.message", body.getMessage());
                            } else {
                                Toast.makeText(getApplicationContext(), "getCarCall-" + body.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("data.result", body.getResult());
                                Log.e("data.message", body.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Data> call, Throwable t) {
                        Log.e("RetrofitFailed", t.getMessage());
                        Toast.makeText(getApplicationContext(), "postSetCarEmer-" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();
    }

    private View.OnTouchListener btnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //virtual_view.requestDisallowInterceptTouchEvent(true);
            //버튼에서 손을 대고 있을 때
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                switch (v.getId()) {
                    case R.id.floor_1:
                    case R.id.floor_2:
                    case R.id.floor_3:
                    case R.id.floor_4:
                    case R.id.floor_5:
                    case R.id.floor_6:
                    case R.id.floor_7:
                    case R.id.floor_8:
                    case R.id.floor_9:
                    case R.id.floor_10:
                    case R.id.floor_11:
                    case R.id.floor_12:
                    case R.id.floor_13:
                    case R.id.floor_14:
                    case R.id.floor_15:
                        //int view = v.getId();
                        //((TextView) findViewById(v.getId())).setBackgroundColor(Color.parseColor("#66000000"));
                        //((TextView) findViewById(v.getId())).setBackgroundColor(Color.parseColor("#e38c04"));
                        break;
                }
            }
            //버튼에서 손을 떼었을 때
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                switch (v.getId()) {
                    case R.id.floor_1:
                    case R.id.floor_2:
                    case R.id.floor_3:
                    case R.id.floor_4:
                    case R.id.floor_5:
                    case R.id.floor_6:
                    case R.id.floor_7:
                    case R.id.floor_8:
                    case R.id.floor_9:
                    case R.id.floor_10:
                    case R.id.floor_11:
                    case R.id.floor_12:
                    case R.id.floor_13:
                    case R.id.floor_14:
                    case R.id.floor_15:
                        //((TextView) findViewById(v.getId())).setBackgroundColor(Color.parseColor("#b1808080"));
                        break;
                }
            }
            //버튼에서 손을 대고 있을 때
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.e("TEST", "TENKEYAREA");
                switch (v.getId()) {
                    case R.id.btn_tenkey00:
                    case R.id.btn_tenkey01:
                    case R.id.btn_tenkey02:
                    case R.id.btn_tenkey03:
                    case R.id.btn_tenkey04:
                    case R.id.btn_tenkey05:
                    case R.id.btn_tenkey06:
                    case R.id.btn_tenkey07:
                    case R.id.btn_tenkey08:
                    case R.id.btn_tenkey09:
                    case R.id.btn_tenkey10:
                    case R.id.btn_tenkey11:
                        ((TextView) findViewById(v.getId())).setBackgroundColor(Color.parseColor("#e38c04"));
                        break;
                }
            }
            //버튼에서 손을 떼었을 때
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                switch (v.getId()) {
                    case R.id.btn_tenkey00:
                    case R.id.btn_tenkey01:
                    case R.id.btn_tenkey02:
                    case R.id.btn_tenkey03:
                    case R.id.btn_tenkey04:
                    case R.id.btn_tenkey05:
                    case R.id.btn_tenkey06:
                    case R.id.btn_tenkey07:
                    case R.id.btn_tenkey08:
                    case R.id.btn_tenkey09:
                    case R.id.btn_tenkey10:
                    case R.id.btn_tenkey11:
                        ((TextView) findViewById(v.getId())).setBackgroundColor(Color.parseColor("#b1808080"));
                        break;
                }
            }
            return false;
        }
    };

    private View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //COP
                case R.id.floor_1:
                    switchFloorButton(0);
                    break;
                case R.id.floor_2:
                    switchFloorButton(1);
                    break;
                case R.id.floor_3:
                    switchFloorButton(2);
                    break;
                case R.id.floor_4:
                    switchFloorButton(3);
                    break;
                case R.id.floor_5:
                    switchFloorButton(4);
                    break;
                case R.id.floor_6:
                    switchFloorButton(5);
                    break;
                case R.id.floor_7:
                    switchFloorButton(6);
                    break;
                case R.id.floor_8:
                    switchFloorButton(7);
                    break;
                case R.id.floor_9:
                    switchFloorButton(8);
                    break;
                case R.id.floor_10:
                    switchFloorButton(9);
                    break;
                case R.id.floor_11:
                    switchFloorButton(10);
                    break;
                case R.id.floor_12:
                    switchFloorButton(11);
                    break;
                case R.id.floor_13:
                    switchFloorButton(12);
                    break;
                case R.id.floor_14:
                    switchFloorButton(13);
                    break;
                case R.id.floor_15:
                    switchFloorButton(14);
                    break;

                //HOP
                case R.id.btn_next_hop:
                    //층정보 표시모드일경우에만
                    if ((cntBtnToggle == 0)) {
                        if ((cnt_floorlst_page < cntfloorPictureFolder)) {
                            cnt_floorlst_page += 1;
                            Log.e("btn_next", "touched");
                            handler.sendEmptyMessage(FRAGMENT_FLOORLIST_IMAGE);
                        } else {
                            Toast.makeText(getApplicationContext(), "마지막 페이지 입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.btn_back_hop:
                    //층정보 표시모드일경우에만
                    if ((cntBtnToggle == 0)) {
                        if (cnt_floorlst_page > 1) {
                            cnt_floorlst_page -= 1;
                            Log.e("btn_back", "touched");
                            handler.sendEmptyMessage(FRAGMENT_FLOORLIST_IMAGE);
                        } else {
                            Toast.makeText(getApplicationContext(), "첫번째 페이지 입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.btn_menu_hop:
                    if (cntBtnToggle == 0) {
                        cntBtnToggle = 1;
                        Log.e("btn_menu", "touched");
                        handler.sendEmptyMessage(FRAGMENT_CONTENTS);
                        btn_menu.setText("FLOOR");
                    } else if (cntBtnToggle == 1) {
                        cntBtnToggle = 0;
                        Log.e("btn_menu", "touched");
                        handler.sendEmptyMessage(FRAGMENT_FLOORLIST_IMAGE);
                        btn_menu.setText("CONTENTS");
                    }
                    break;
                /*START Tenkey*/
                case R.id.btn_tenkey00:
                case R.id.btn_tenkey01:
                case R.id.btn_tenkey02:
                case R.id.btn_tenkey03:
                case R.id.btn_tenkey04:
                case R.id.btn_tenkey05:
                case R.id.btn_tenkey06:
                case R.id.btn_tenkey07:
                case R.id.btn_tenkey08:
                case R.id.btn_tenkey09:
                case R.id.btn_tenkey10:
                case R.id.btn_tenkey11:
                    touchedFloor = String.valueOf(((TextView) findViewById(v.getId())).getText());
                    mTouchEventListener.onReceivedEvent();
                    break;
            }
        }
    };

    public void switchFloorButton(int number) {
        String color = "#00000000";
        if (btnTouched[number] == true) {
            btnTouched[number] = false;
            color = "#00000000";
        } else {
            btnTouched[number] = true;
            color = "#e38c04";
        }
        switch (number) {
            case 0:
                floor_1.setBackgroundColor(Color.parseColor(color));
                break;
            case 1:
                floor_2.setBackgroundColor(Color.parseColor(color));
                break;
            case 2:
                floor_3.setBackgroundColor(Color.parseColor(color));
                break;
            case 3:
                floor_4.setBackgroundColor(Color.parseColor(color));
                break;
            case 4:
                floor_5.setBackgroundColor(Color.parseColor(color));
                break;
            case 5:
                floor_6.setBackgroundColor(Color.parseColor(color));
                break;
            case 6:
                floor_7.setBackgroundColor(Color.parseColor(color));
                break;
            case 7:
                floor_8.setBackgroundColor(Color.parseColor(color));
                break;
            case 8:
                floor_9.setBackgroundColor(Color.parseColor(color));
                break;
            case 9:
                floor_10.setBackgroundColor(Color.parseColor(color));
                break;
            case 10:
                floor_11.setBackgroundColor(Color.parseColor(color));
                break;
            case 11:
                floor_12.setBackgroundColor(Color.parseColor(color));
                break;
            case 12:
                floor_13.setBackgroundColor(Color.parseColor(color));
                break;
            case 13:
                floor_14.setBackgroundColor(Color.parseColor(color));
                break;
            case 14:
                floor_15.setBackgroundColor(Color.parseColor(color));
                break;
        }
    }

    public void setFullScreen() {
        View decorView;
        int uiOption;
        decorView = getWindow().getDecorView();
        uiOption = getWindow().getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOption);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//화면을 항상 켜지게 설정
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.tourinfo_container, fragment).commit();
    }
}
