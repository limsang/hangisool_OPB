package com.hangisool.lcd_a_h.contents;

import org.ini4j.Wini;
import java.io.File;
import java.util.ArrayList;

public class MovieRankParser {
  public static ArrayList<String> getMovieRank(String movieRankPath){
    ArrayList<String> movieRankList = new ArrayList<String>();

    try{
      if(!new File(movieRankPath).exists()){
        return null;
      }else{
        Wini wini = new Wini(new File(movieRankPath));

        movieRankList.add(wini.get("TICKET_RANK", "1"));
        movieRankList.add(wini.get("TICKET_RANK", "2"));
        movieRankList.add(wini.get("TICKET_RANK", "3"));
        movieRankList.add(wini.get("TICKET_RANK", "4"));
        movieRankList.add(wini.get("TICKET_RANK", "5"));
      }
    }catch (Exception e){
      e.printStackTrace();
      return null;
    }

    return movieRankList;
  }
}
