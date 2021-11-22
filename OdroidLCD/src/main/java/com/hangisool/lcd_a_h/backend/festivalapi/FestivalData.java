package com.hangisool.lcd_a_h.backend.festivalapi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//날짜로 페스티벌 검색한 데이터를 받음 JSON
public class FestivalData {
    @SerializedName("response")
    @Expose
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
