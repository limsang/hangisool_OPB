package com.hangisool.lcd_a_h.playmanager;

import com.hangisool.lcd_a_h.LcdActivity;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class SetPlayList {
  public static ArrayList<String> getPlayList(String playListFilePath) {
    ArrayList<String> playList = new ArrayList<String>();
    try {
      BufferedReader in = new BufferedReader(new FileReader(playListFilePath));
      String s;
      while ((s = in.readLine()) != null) {

        if(s.split(",")[0].contains("text")){
          LcdActivity.textValue = s.split(",")[1];
        }else{
          if(!s.equals("")) {
            playList.add(s.trim());
          }
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return playList;
  }
}