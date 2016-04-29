package com.enzab.spootify.model;

import com.orm.SugarRecord;

/**
 * Created by linard_f on 4/29/16.
 */
public class SongPlaylist extends SugarRecord {

    private Long playlistId;
    private Long songId;

    public SongPlaylist() {

    }

    public SongPlaylist(Long playlistId, Long songId) {
        this.playlistId = playlistId;
        this.songId = songId;
    }

    public Long getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(Long playlistId) {
        this.playlistId = playlistId;
    }

    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
    }
}
