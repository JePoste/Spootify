package com.enzab.spootify.activity.interaction;

import com.enzab.spootify.model.ISearchItem;
import com.enzab.spootify.model.Playlist;
import com.enzab.spootify.model.Song;

import java.util.ArrayList;

/**
 * Created by linard_f on 4/8/16.
 */
public interface OnItemSelectedListener {

    public void onMusicSelected(ArrayList<ISearchItem> songQueue, int songPosition);
    public void onPlaylistSelected(Playlist searchItem);
}
