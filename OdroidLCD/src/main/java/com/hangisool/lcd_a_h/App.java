package com.hangisool.lcd_a_h;


import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.tsengvn.typekit.Typekit;

import java.io.File;

import de.mindpipe.android.logging.log4j.LogConfigurator;

public class App extends Application{
    public static String logPath;
    final LogConfigurator logConfigurator = new LogConfigurator();

    public LogConfigurator getLogConfigurator() {
        return logConfigurator;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //settingLogSystem();

        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this,"fonts/NanumGothic.ttf"))
                .addBold(Typekit.createFromAsset(this,"fonts/NanumGothicBold_0.ttf"));
    }

    public void settingLogSystem(){
        File file = null;
        String sdcard = Environment.getExternalStorageState();

        if ( !sdcard.equals(Environment.MEDIA_MOUNTED))
        {
            // SD카드가 마운트되어있지 않음
            file = Environment.getRootDirectory();
            file = new File(file.getPath() , "Logs");
            logPath = file.getPath();
            if(!file.exists())
            {
                Log.d("path1", file.getPath());
                file.mkdir();
            }
        }
        else {
            // SD카드가 마운트되어있음
            file = Environment.getExternalStorageDirectory();
            file = new File(file.getPath() , "Logs");
            logPath = file.getPath();
            if(!file.exists())
            {
                Log.d("path_2", file.getPath());
                file.mkdir();
            }
        }
        logConfigurator.setFileName(logPath+"/logfile.log");
        logConfigurator.setMaxBackupSize(5000000);//파일사이즈가 넘어가면 다른이름으로 파일을 다시생성함
        logConfigurator.configure();
    }

    public static void deleteLogFile(){
        //로그파일의 용량이 일정크기 이상되면 지운다.
        File logFile = new File(App.logPath+"/logfile.log");
        if(logFile.exists()){
            long FileSize = logFile.length();
            if(FileSize>4000000){
                Log.e("logfile.log","deleted for storage");
                logFile.delete();
            }
        }
    }
}
