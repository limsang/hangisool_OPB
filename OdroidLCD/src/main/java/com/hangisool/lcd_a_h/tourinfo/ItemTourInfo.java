package com.hangisool.lcd_a_h.tourinfo;

public class ItemTourInfo {
    private String profile_image;
    private String title;
    private String detail;

    public ItemTourInfo(String profile_image, String title, String detail) {
        this.profile_image = profile_image;
        this.title = title;
        this.detail = detail;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
