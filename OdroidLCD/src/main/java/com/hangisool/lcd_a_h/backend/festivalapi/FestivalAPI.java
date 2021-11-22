package com.hangisool.lcd_a_h.backend.festivalapi;

import com.hangisool.lcd_a_h.backend.areatourapi.AreaTourData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FestivalAPI {
    String API_SERVER_URL = "http://api.visitkorea.or.kr";
    String AUTH_KEY = "l8jJoQB5WtucDxLecpqOLq8vaOZGc59kG8WVJi%2BskBZ4awLXt795ewDXbvYaymzdPWBEKLacJZXvghTW3pXg6Q%3D%3D";
    String API_PATH = "/openapi/service/rest";
    String LANG_KOR = "/KorService";
    String LANG_JPN = "/JpnService";
    String LANG_ENG = "/EngService";
    String LANG_CHN = "/ChtService";
    String SEARCHFESTI = "/searchFestival";
    String MobileApp = "LCDApp";
    String MobileOS = "AND";
    String _type = "json";

    @GET(API_PATH+LANG_KOR+SEARCHFESTI)
    Call<FestivalData> getKOR_Data(@Query(value = "serviceKey",encoded = true) String serviceKey,
                                   @Query("pageNo") int pageNo,
                                   @Query("numOfRows") int numOfRows,
                                   @Query("MobileApp") String MobileApp,
                                   @Query("MobileOS") String MobileOS,
                                   @Query("arrange") String arrange,
                                   @Query("areaCode") int areaCode,
                                   @Query("eventStartDate") String eventStartDate,
                                   @Query("_type") String _type
    );

    @GET(API_PATH+LANG_ENG+SEARCHFESTI)
    Call<FestivalData> getENG_Data(@Query(value = "serviceKey",encoded = true) String serviceKey,
                                   @Query("pageNo") int pageNo,
                                   @Query("numOfRows") int numOfRows,
                                   @Query("MobileApp") String MobileApp,
                                   @Query("MobileOS") String MobileOS,
                                   @Query("arrange") String arrange,
                                   @Query("areaCode") int areaCode,
                                   @Query("eventStartDate") String eventStartDate,
                                   @Query("_type") String _type
    );

    @GET(API_PATH+LANG_CHN+SEARCHFESTI)
    Call<FestivalData> getCHN_Data(@Query(value = "serviceKey",encoded = true) String serviceKey,
                                   @Query("pageNo") int pageNo,
                                   @Query("numOfRows") int numOfRows,
                                   @Query("MobileApp") String MobileApp,
                                   @Query("MobileOS") String MobileOS,
                                   @Query("arrange") String arrange,
                                   @Query("areaCode") int areaCode,
                                   @Query("eventStartDate") String eventStartDate,
                                   @Query("_type") String _type
    );

    @GET(API_PATH+LANG_JPN+SEARCHFESTI)
    Call<FestivalData> getJPN_Data(@Query(value = "serviceKey",encoded = true) String serviceKey,
                                   @Query("pageNo") int pageNo,
                                   @Query("numOfRows") int numOfRows,
                                   @Query("MobileApp") String MobileApp,
                                   @Query("MobileOS") String MobileOS,
                                   @Query("arrange") String arrange,
                                   @Query("areaCode") int areaCode,
                                   @Query("eventStartDate") String eventStartDate,
                                   @Query("_type") String _type
    );
}
