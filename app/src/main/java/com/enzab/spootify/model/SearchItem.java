package com.enzab.spootify.model;

import java.io.Serializable;

/**
 * Created by linard_f on 4/7/16.
 */
public class SearchItem implements Serializable {

    private String title;
    private String artist;

    public SearchItem() {}

    public SearchItem(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
