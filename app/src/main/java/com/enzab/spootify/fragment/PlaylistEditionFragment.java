package com.enzab.spootify.fragment;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import com.enzab.spootify.R;
import com.enzab.spootify.model.ISearchItem;
import com.enzab.spootify.model.Playlist;
import com.enzab.spootify.model.Song;
import com.enzab.spootify.model.SongPlaylist;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaylistEditionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistEditionFragment extends SearchFragment {

    private static final String ARG_PLAYLIST = "ARG_PLAYLIST";
    private static final String TAG = "PLAYLIST_EDIT_FRAGMENT";
    private Playlist mPlaylist;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param playlist Parameter 1.
     * @return A new instance of fragment PlaylistEditionFragment.
     */
    static public PlaylistEditionFragment newInstance(Playlist playlist) {
        PlaylistEditionFragment fragment = new PlaylistEditionFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLAYLIST, playlist);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaylistEditionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPlaylist = (Playlist) getArguments().getSerializable(ARG_PLAYLIST);
        }
    }

    @Override
    protected List<ISearchItem> getItemList() {
        ArrayList<ISearchItem> list = new ArrayList<>();
        for (Song song : mPlaylist.getSongs()) {
            list.add(song);
        }
        mItems = list;
        return mItems;
    }

    @Override
    public void onItemOptionSelection(ISearchItem item, String option) {
        Song song = (Song) item;
        if (mContext.getString(R.string.add_to_playlist).equals(option)) {
            addToPlaylist(song);
        } else if (mContext.getString(R.string.delete_from_playlist).equals(option)) {
            List<SongPlaylist> list = SongPlaylist.find(SongPlaylist.class,
                    "playlist_id = ? and song_id = ?", String.valueOf(mPlaylist.getId()), String.valueOf(song.getId()));
            list.get(0).delete();
            mAdapter.deleteItem(item);
            Snackbar.make(mSearchLayout, WordUtils.capitalize(item.getTitle()) +
                    " deleted.", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public String[] getOptionList() {
        return mContext.getResources().getStringArray(R.array.playlist_edition_options);
    }

}
