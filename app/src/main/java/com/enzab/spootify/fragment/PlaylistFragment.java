package com.enzab.spootify.fragment;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.enzab.spootify.R;
import com.enzab.spootify.model.ISearchItem;
import com.enzab.spootify.model.Playlist;
import com.enzab.spootify.model.SearchItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

/**
 * Created by linard_f on 4/7/16.
 */
public class PlaylistFragment extends SearchFragment {

    private static final String TAG = "PLAYLIST_FRAGMENT";

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFab.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.fab)
    void onFabClick() {
        new MaterialDialog.Builder(mContext)
                .title(R.string.dialog_create_playlist_title)
                .content(R.string.dialog_create_playlist_content)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(R.string.dialog_create_playlist_content, R.string.empty, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        Playlist playlist = new Playlist(input.toString());
                        playlist.save();
                        mAdapter.addItem(new SearchItem(playlist.getName(), "0"));
                    }
                }).show();
    }

    @Override
    protected List<ISearchItem> getItemList() {
        List<Playlist> playlists = Playlist.listAll(Playlist.class);
        List<ISearchItem> songs = new ArrayList<>();
        for (Playlist playlist : playlists) {
            songs.add(new SearchItem(playlist.getName(), String.valueOf(playlist.getSongs().size())));
        }
        return songs;
    }

}
