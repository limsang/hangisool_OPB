package com.hangisool.lcd_a_h.filepath;

import android.os.Environment;
import android.util.Log;
import java.io.File;

public class ContentsPath {
  private String contentsPath;
  public ContentsPath(){
    File file = null;
    String sdcard = Environment.getExternalStorageState();

    if ( !sdcard.equals(Environment.MEDIA_MOUNTED))
    {
      // SD카드가 마운트되어있지 않음
      file = Environment.getRootDirectory();
      file = new File(file.getPath() , "contents");
      contentsPath = file.getPath();
      if(!file.exists())
      {
        Log.d("path1", file.getPath());
        file.mkdir();
      }
    }
    else {
      // SD카드가 마운트되어있음
      file = Environment.getExternalStorageDirectory();
      file = new File(file.getPath() , "contents");
      contentsPath = file.getPath();
      if(!file.exists())
      {
        Log.d("path_2", file.getPath());
        file.mkdir();
      }
    }
  }
  public String getContentsPath(){
    return contentsPath;
  }
}
