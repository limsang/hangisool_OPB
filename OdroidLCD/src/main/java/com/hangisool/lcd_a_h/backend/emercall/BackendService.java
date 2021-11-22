package com.hangisool.lcd_a_h.backend.emercall;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface BackendService {
    //String URL = "https://hgstest.b.lue.blue";
    String URL = "https://192.168.0.120:2000";//local network
    //String STUN_SERVER_URI = "stun:hgstest.b.lue.blue";
    String STUN_SERVER_URI = "stun:52.79.141.171";//local network

    //String TURN_SERVER_URI = "turn:hgstest.b.lue.blue:3478";
    String TURN_SERVER_URI = "turn:52.79.141.171:3478";//local network
    String TURN_SERVER_ID = "hangisool";
    String TURN_SERVER_PW = "han4143";

    @FormUrlEncoded
    @POST("/action/add-car.php")
    Call<Data> postAddCar(@Field("name") String name,
                          @Field("location") String location,
                          @Field("field_name") String field,
                          @Field("phone_number") String phoneNumber
    );

    @FormUrlEncoded
    @POST("/action/set-car-emergency-flag.php")
    Call<Data> postSetCarEmer(@Field("hash") String hash,
                              @Field("set_to") int set_to,
                              @Field("set_calling") int set_calling
    );

    @GET("/action/get-car-emergency-flag.php")
    Call<Data> getCarCall(@Query("hash") String hash
    );
}
