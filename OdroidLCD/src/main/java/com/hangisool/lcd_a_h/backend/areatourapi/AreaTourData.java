package com.hangisool.lcd_a_h.backend.areatourapi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AreaTourData {

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