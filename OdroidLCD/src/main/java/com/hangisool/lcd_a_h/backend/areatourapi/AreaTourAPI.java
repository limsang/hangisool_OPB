package com.hangisool.lcd_a_h.backend.areatourapi;

import android.util.Log;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AreaTourAPI {
    String API_SERVER_URL = "http://api.visitkorea.or.kr";
    String AUTH_KEY = "l8jJoQB5WtucDxLecpqOLq8vaOZGc59kG8WVJi%2BskBZ4awLXt795ewDXbvYaymzdPWBEKLacJZXvghTW3pXg6Q%3D%3D";
//    String AUTH_KEY = "l8jJoQB5WtucDxLecpqOLq8vaOZGc59kG8WVJi%2BskBZ4awLXt795ewDXbvYaymzdPWBEKLacJZXvghTW3pXg6Q%3D%3D";
    String TOUR_API_PATH = "/openapi/service/rest";
    String LANG_KOR = "/KorService";
    String LANG_JPN = "/JpnService";
    String LANG_ENG = "/EngService";
    String LANG_CHN = "/ChtService";
    String AREABASED = "/areaBasedList";
    String MobileApp = "LCDApp";
    String MobileOS = "AND";
    String _type = "json";

    @GET(TOUR_API_PATH+LANG_KOR+AREABASED)
    Call<AreaTourData> getKOR_Data(@Query(value = "serviceKey",encoded = true) String serviceKey,
                                   @Query("pageNo") int pageNo,
                                   @Query("numOfRows") int numOfRows,
                                   @Query("MobileApp") String MobileApp,
                                   @Query("MobileOS") String MobileOS,
                                   @Query("arrange") String arrange,
                                   @Query("contentTypeId") int contentTypeId,
                                   @Query("listYN") String listYN,
                                   @Query("_type") String _type
    );

    @GET(TOUR_API_PATH+LANG_ENG+AREABASED)
    Call<AreaTourData> getENG_Data(@Query(value = "serviceKey",encoded = true) String serviceKey,
                                   @Query("pageNo") int pageNo,
                                   @Query("numOfRows") int numOfRows,
                                   @Query("MobileApp") String MobileApp,
                                   @Query("MobileOS") String MobileOS,
                                   @Query("arrange") String arrange,
                                   @Query("contentTypeId") int contentTypeId,
                                   @Query("listYN") String listYN,
                                   @Query("_type") String _type
    );

    @GET(TOUR_API_PATH+LANG_CHN+AREABASED)
    Call<AreaTourData> getCHN_Data(@Query(value = "serviceKey",encoded = true) String serviceKey,
                                   @Query("pageNo") int pageNo,
                                   @Query("numOfRows") int numOfRows,

                                   @Query("MobileApp") String MobileApp,
                                   @Query("MobileOS") String MobileOS,
                                   @Query("arrange") String arrange,
                                   @Query("contentTypeId") int contentTypeId,
                                   @Query("listYN") String listYN,
                                   @Query("_type") String _type
    );

    @GET(TOUR_API_PATH+LANG_JPN+AREABASED)
    Call<AreaTourData> getJPN_Data(@Query(value = "serviceKey",encoded = true) String serviceKey,
                                   @Query("pageNo") int pageNo,
                                   @Query("numOfRows") int numOfRows,
                                   @Query("MobileApp") String MobileApp,
                                   @Query("MobileOS") String MobileOS,
                                   @Query("arrange") String arrange,
                                   @Query("contentTypeId") int contentTypeId,
                                   @Query("listYN") String listYN,
                                   @Query("_type") String _type
    );
}
