package com.hangisool.lcd_a_h.backend.airkoreaapi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AirKoreaAPI {
    String API_SERVER_URL = "http://openapi.airkorea.or.kr";
    String API_PATH = "/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty";
    String AUTH_KEY = "l8jJoQB5WtucDxLecpqOLq8vaOZGc59kG8WVJi%2BskBZ4awLXt795ewDXbvYaymzdPWBEKLacJZXvghTW3pXg6Q%3D%3D";
    String _returnType = "json";

    @GET(API_PATH)
    Call<AirKoreaData> getAirData(@Query(value = "serviceKey",encoded = true) String serviceKey,
                                  @Query("numOfRows") int numOfRows,
                                  @Query("sidoName") String sidoName,
                                  @Query("ver") String ver,
                                  @Query("_returnType") String returnType
    );
}
