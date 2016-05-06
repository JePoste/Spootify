package com.enzab.spootify.model;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by linard_f on 4/29/16.
 */
public class Playlist extends SugarRecord implements Serializable, ISearchItem {

    @Unique
    private String name;

    public Playlist() {}

    public Playlist(String name) {
        this.name = name;
    }

    public List<Song> getSongs() {
        List<SongPlaylist> songPlaylists = SongPlaylist.find(SongPlaylist.class, "playlist_id = ?", String.valueOf(getId()));
        List<Song> songs = new ArrayList<>();
        for (SongPlaylist songPlaylist : songPlaylists) {
            songs.add(Song.findById(Song.class, songPlaylist.getSongId()));
        }
        return songs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getTitle() {
        return getName();
    }

    @Override
    public String getDescription() {
        return String.valueOf(getSongs().size());
    }

}
