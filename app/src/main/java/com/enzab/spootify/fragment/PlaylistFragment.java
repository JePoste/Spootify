package com.enzab.spootify.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.enzab.spootify.R;
import com.enzab.spootify.activity.interaction.OnItemSelectedListener;
import com.enzab.spootify.model.ISearchItem;
import com.enzab.spootify.model.Playlist;
import com.enzab.spootify.model.SearchItem;
import com.enzab.spootify.model.SongPlaylist;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import butterknife.OnItemClick;

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

    @OnItemClick(R.id.list)
    void onItemClick(int position) {
        try {
            OnItemSelectedListener activity = (OnItemSelectedListener) mContext;
            activity.onPlaylistSelected((Playlist) mItems.get(position));
        } catch (ClassCastException e) {
            throw new ClassCastException(mContext.toString() + " must implement OnItemSelectedListener");
        }
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
                        Playlist playlist = new Playlist(input.toString().toLowerCase().trim());
                        if (Playlist.find(Playlist.class, "name = ?", playlist.getName()).isEmpty()) {
                            playlist.save();
                            mAdapter.addItem(new SearchItem(playlist.getName(), "0"));
                            Snackbar.make(mSearchLayout, WordUtils.capitalize(playlist.getTitle()) +
                                    " created.", Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(mSearchLayout, "The playlist \"" +
                                    WordUtils.capitalize(playlist.getName()) +
                                    "\" already exists.", Snackbar.LENGTH_LONG).show();
                        }
                    }
                }).show();
    }

    @Override
    protected List<ISearchItem> getItemList() {
        List<Playlist> playlists = Playlist.listAll(Playlist.class);
        ArrayList<ISearchItem> searchItems = new ArrayList<>();
        for (Playlist playlist : playlists) {
            searchItems.add(playlist);
        }
        mItems = searchItems;
        return searchItems;
    }

    @Override
    public void onItemOptionSelection(ISearchItem item, String option) {
        Playlist playlist = (Playlist) item;
        if (mContext.getString(R.string.delete_playlist).equals(option)) {
            List<SongPlaylist> list = SongPlaylist.find(SongPlaylist.class, "playlist_id = ?", String.valueOf(playlist.getId()));
            for (SongPlaylist it : list) {
                it.delete();
            }
            mAdapter.deleteItem(item);
            Playlist.delete((Playlist) Playlist.find(Playlist.class, "name = ?", item.getTitle()).get(0));
            Snackbar.make(mSearchLayout, WordUtils.capitalize(item.getTitle()) +
                    " deleted.", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public String[] getOptionList() {
        return mContext.getResources().getStringArray(R.array.playlist_options);
    }
}
