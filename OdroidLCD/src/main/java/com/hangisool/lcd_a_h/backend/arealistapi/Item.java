package com.hangisool.lcd_a_h.backend.arealistapi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("rnum")
    @Expose
    private Integer rnum;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRnum() {
        return rnum;
    }

    public void setRnum(Integer rnum) {
        this.rnum = rnum;
    }

}