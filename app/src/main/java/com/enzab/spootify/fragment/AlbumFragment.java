package com.enzab.spootify.fragment;

import com.enzab.spootify.model.ISearchItem;
import com.enzab.spootify.model.SearchItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linard_f on 4/7/16.
 */
public class AlbumFragment extends SearchFragment {

    @Override
    protected List<ISearchItem> getItemList() {
        List<ISearchItem> searchItems = new ArrayList<>();
        searchItems.add(new SearchItem("Prelude", "Richard Strauss"));
        searchItems.add(new SearchItem("Jurrassic park (the album)", "John Williams"));
        return searchItems;
    }


}
