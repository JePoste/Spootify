package com.enzab.musicplayer.fragment;

import com.enzab.musicplayer.model.SearchItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linard_f on 4/7/16.
 */
public class AlbumFragment extends SearchFragment {

    @Override
    protected List<SearchItem> getItemList() {
        List<SearchItem> items = new ArrayList<>();
        items.add(new SearchItem("Prelude", "Richard Strauss"));
        items.add(new SearchItem("Jurrassic park (the album)", "John Williams"));
        return items;
    }


}
