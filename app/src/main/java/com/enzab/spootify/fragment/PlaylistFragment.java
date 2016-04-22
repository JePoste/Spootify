package com.enzab.spootify.fragment;

import com.enzab.spootify.model.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linard_f on 4/7/16.
 */
public class PlaylistFragment extends SearchFragment {

    @Override
    protected List<Song> getItemList() {
        mItems = new ArrayList<>();
        mItems.add(new Song("Epic Playlist", "1"));
        mItems.add(new Song("Movie OST", "1"));
        return mItems;
    }

}
