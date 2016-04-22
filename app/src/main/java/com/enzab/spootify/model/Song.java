package com.enzab.spootify.model;


import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by linard_f on 4/8/16.
 */
public class Song extends SugarRecord implements Serializable {

    private String artist;
    private String title;
    private String filePath;

    public Song() {}

    public Song(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }

    public Song(String title, String artist, String filePath) {
        this.title = title;
        this.artist = artist;
        this.filePath = filePath;
    }

    public static Comparator<Song> songTitleComparator = new Comparator<Song>() {
        @Override
        public int compare(Song lhs, Song rhs) {
            return lhs.getTitle().toLowerCase().compareTo(rhs.getTitle().toLowerCase());
        }
    };

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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
