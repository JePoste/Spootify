package com.enzab.spootify.activity.interaction;

import com.enzab.spootify.model.Song;

import java.util.ArrayList;

/**
 * Created by linard_f on 4/8/16.
 */
public interface OnMusicSelectedListener {

    public void onMusicSelected(ArrayList<Song> songQueue, int songPosition);

}
