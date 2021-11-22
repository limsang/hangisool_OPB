package com.hangisool.lcd_a_h.backend.arealistapi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AreaListData {

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