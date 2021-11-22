package com.hangisool.lcd_a_h.contents;

import org.ini4j.Wini;
import java.io.File;
import java.util.HashMap;

public class ExchangeParser {
  public static HashMap<String,String> getExchange(String exchangePath){
    HashMap<String,String> exchangeMap = new HashMap<String, String>();

    try{
      if(!new File(exchangePath).exists()){
        return null;
      }else{
        Wini wini = new Wini(new File(exchangePath));

        exchangeMap.put("usa_buy", wini.get("exchange","usa_buy"));
        exchangeMap.put("usa_sell", wini.get("exchange","usa_sell"));
        exchangeMap.put("japan_buy", wini.get("exchange","japan_buy"));
        exchangeMap.put("japan_sell", wini.get("exchange","japan_sell"));
        exchangeMap.put("eur_buy", wini.get("exchange","eur_buy"));
        exchangeMap.put("eur_sell", wini.get("exchange","eur_sell"));
        exchangeMap.put("china_buy", wini.get("exchange","china_buy"));
        exchangeMap.put("china_sell", wini.get("exchange","china_sell"));

      }
    }catch (Exception e){
      e.printStackTrace();
      return null;
    }

    return exchangeMap;
  }
}
