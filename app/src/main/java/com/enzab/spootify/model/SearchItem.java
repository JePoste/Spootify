package com.enzab.spootify.model;

import java.io.Serializable;

/**
 * Created by linard_f on 4/7/16.
 */
public class SearchItem implements Serializable, ISearchItem {

    private String title;
    private String description;

    public SearchItem() {}

    public SearchItem(String title, String artist) {
        this.title = title;
        this.description = artist;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
