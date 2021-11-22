package com.hangisool.lcd_a_h.contents;

import org.ini4j.Wini;
import java.io.File;
import java.util.ArrayList;

public class NewsParser {
  public static ArrayList<String> getNews(String newsPath){
    ArrayList<String> newsList = new ArrayList<String>();

    try{
      if(!new File(newsPath).exists()){
        return null;
      }else{
        Wini wini = new Wini(new File(newsPath));

        for(int i=1; i<37; i++) {
          newsList.add(wini.get("news", "news" + i));
        }
      }
    }catch (Exception e){
      e.printStackTrace();
      return null;
    }

    return newsList;
  }
}
