package com.enzab.spootify.fragment;

import com.enzab.spootify.model.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linard_f on 4/7/16.
 */
public class AlbumFragment extends SearchFragment {

    @Override
    protected List<Song> getItemList() {
        mItems = new ArrayList<>();
        mItems.add(new Song("Prelude", "Richard Strauss"));
        mItems.add(new Song("Jurrassic park (the album)", "John Williams"));
        return mItems;
    }


}
