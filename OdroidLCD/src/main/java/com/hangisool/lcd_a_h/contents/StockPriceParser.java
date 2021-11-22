package com.hangisool.lcd_a_h.contents;

import org.ini4j.Wini;
import java.io.File;
import java.util.HashMap;

public class StockPriceParser {
  public static HashMap<String,String> getStockPriceMap(String stockPricePath){
    HashMap<String,String> stockPriceMap = new HashMap<String, String>();

    try{
      if(!new File(stockPricePath).exists()){
        return null;
      }else{
        Wini wini = new Wini(new File(stockPricePath));

        stockPriceMap.put("kosdaq_price", wini.get("kosdaq","price"));
        stockPriceMap.put("kosdaq_contrast", wini.get("kosdaq","contrast"));
        stockPriceMap.put("kospi_price", wini.get("kospi","price"));
        stockPriceMap.put("kospi_contrast", wini.get("kospi","contrast"));
      }
    }catch (Exception e){
      e.printStackTrace();
      return null;
    }

    return stockPriceMap;
  }
}
