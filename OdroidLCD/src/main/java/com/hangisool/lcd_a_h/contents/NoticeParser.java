package com.hangisool.lcd_a_h.contents;

import org.ini4j.Wini;
import java.io.File;
import java.util.ArrayList;

public class NoticeParser {
  public static ArrayList<String> getNotice(String noticePath){
    ArrayList<String> noticeList = new ArrayList<String>();

    try{
      if(!new File(noticePath).exists()){
        return null;
      }else{
        Wini wini = new Wini(new File(noticePath));
        int i = 0;
        while(true){
          noticeList.add(wini.get("notice", "notice" + i));
          i++;
          if(wini.get("notice","notice"+i) == null){
            break;
          }
        }
      }
    }catch (Exception e){
      e.printStackTrace();
      return null;
    }

    return noticeList;
  }
}
