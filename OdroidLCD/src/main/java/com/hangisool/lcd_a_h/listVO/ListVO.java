package com.hangisool.lcd_a_h.listVO;
import android.graphics.drawable.Drawable;

public class ListVO {
    private Drawable img;
    private String Title;
    private String context;
    private String floor_str;
    private int[] floors;
    private boolean isTouched;

    public String getFloor_str() {
        return floor_str;
    }

    public void setFloor_str(String floor_str) {
        this.floor_str = floor_str;
    }

    public int[] getFloors() {
        return floors;
    }

    public void setFloors(int[] floors) {
        this.floors = floors;
    }

    public boolean isTouched() {
        return isTouched;
    }

    public Drawable getImg() {
        return img;
    }

    public void setImg(Drawable img) {
        this.img = img;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setTouched(boolean flag){
        isTouched = flag;
    }

    public boolean getTouched(){
        return isTouched;
    }
}