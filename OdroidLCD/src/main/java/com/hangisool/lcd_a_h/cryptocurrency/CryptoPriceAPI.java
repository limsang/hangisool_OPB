package com.hangisool.lcd_a_h.cryptocurrency;

import retrofit2.Call;
import java.util.*;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CryptoPriceAPI {
    String API_SERVER_URL = "https://api.upbit.com";
    String API_PATH = "/v1/ticker";

    @GET(API_PATH)
    Call<List<CryptoCurrencyData>> getCurrencyData(@Query("markets") String markets);
}
