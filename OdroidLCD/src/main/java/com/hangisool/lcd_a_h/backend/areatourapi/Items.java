package com.hangisool.lcd_a_h.backend.areatourapi;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Items {

    @SerializedName("item")
    @Expose
    private List<Item> item = null;

    public List<Item> getItem() {
        return item;
    }

    public void setItem(List<Item> item) {
        this.item = item;
    }

}