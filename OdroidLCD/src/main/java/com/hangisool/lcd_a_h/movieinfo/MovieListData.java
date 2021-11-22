package com.hangisool.lcd_a_h.movieinfo;

public class MovieListData {
    //리퀘스로 받아 오는 Json 데이터를 참조해서 만들자.

    private String id;
    private String vote_average;
    private String title;
    private String original_title;
    private String poster_path;
    private String overview;
    private String backdrop_path;
    private String release_date;

    public MovieListData(String id, String vote_average, String title, String original_title, String poster_path, String overview, String backdrop_path, String release_date) {
        this.id = id;
        this.vote_average = vote_average;
        this.title = title;
        this.original_title = original_title;
        this.poster_path = poster_path;
        this.overview = overview;
        this.backdrop_path = backdrop_path;
        this.release_date = release_date;
    }

    public String getTitle() {
        return title;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }
}
