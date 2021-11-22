package com.hangisool.lcd_a_h.backend.arealistapi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AreaListAPI {
    String API_SERVER_URL = "http://api.visitkorea.or.kr";
    String API_PATH = "/openapi/service/rest/KorService/areaCode";
    String AUTH_KEY = "l8jJoQB5WtucDxLecpqOLq8vaOZGc59kG8WVJi%2BskBZ4awLXt795ewDXbvYaymzdPWBEKLacJZXvghTW3pXg6Q%3D%3D";
    String MobileApp = "LCDApp";
    String MobileOS = "AND";
    String _type = "json";

    @GET(API_PATH)
    Call<AreaListData> getAreaList(@Query(value = "serviceKey",encoded = true) String serviceKey,
                                   @Query("numOfRows") int numOfRows,
                                   @Query("MobileApp") String MobileApp,
                                   @Query("MobileOS") String MobileOS,
                                   @Query("_type") String _type
    );
}
