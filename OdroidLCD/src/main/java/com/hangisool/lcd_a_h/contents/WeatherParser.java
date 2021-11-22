package com.hangisool.lcd_a_h.contents;

import org.ini4j.Wini;
import java.io.File;
import java.util.HashMap;

public class WeatherParser{
  public static String area = "seoul";//default seoul
  public static HashMap<String,String> getWeather(String weatherPath){
    HashMap<String,String> weatherMap = new HashMap<String, String>();

    try{
      if(!new File(weatherPath).exists()){
        return null;
      }else{
        Wini wini = new Wini(new File(weatherPath));

        weatherMap.put("temp", wini.get(area,"temp"));
        weatherMap.put("weather", wini.get(area,"weather"));
        weatherMap.put("tmn", wini.get(area,"tmn"));
        weatherMap.put("tmx", wini.get(area,"tmx"));
        weatherMap.put("pop", wini.get(area,"pop"));
        weatherMap.put("rainfall", wini.get(area,"rainfall"));
        weatherMap.put("snowfall", wini.get(area,"snowfall"));
        weatherMap.put("humidity", wini.get(area,"humidity"));
      }
    }catch (Exception e){
      e.printStackTrace();
      return null;
    }

    return weatherMap;
  }
}
