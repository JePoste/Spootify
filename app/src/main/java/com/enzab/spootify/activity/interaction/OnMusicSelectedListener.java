package com.enzab.spootify.activity.interaction;

import com.enzab.spootify.model.Playlist;
import com.enzab.spootify.model.Song;

/**
 * Created by linard_f on 4/8/16.
 */
//TODO Rename this into OnItemSelectedListener
public interface OnMusicSelectedListener {

    public void onMusicSelected(Song searchItem);
    public void onPlaylistSelected(Playlist searchItem);

}
