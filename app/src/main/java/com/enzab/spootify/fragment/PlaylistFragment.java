package com.enzab.spootify.fragment;

import com.enzab.spootify.model.SearchItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linard_f on 4/7/16.
 */
public class PlaylistFragment extends SearchFragment {

    @Override
    protected List<SearchItem> getItemList() {
        List<SearchItem> items = new ArrayList<>();
        items.add(new SearchItem("Epic Playlist", "1"));
        items.add(new SearchItem("Movie OST", "1"));
        return items;
    }

}
