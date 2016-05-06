package com.enzab.spootify.fragment;


import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.enzab.spootify.R;
import com.enzab.spootify.activity.interaction.OnMusicSelectedListener;
import com.enzab.spootify.adapter.SearchListAdapter;
import com.enzab.spootify.model.ISearchItem;
import com.enzab.spootify.model.Playlist;
import com.enzab.spootify.model.Song;
import com.enzab.spootify.model.SongPlaylist;

import org.apache.commons.lang3.text.WordUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements SearchListAdapter.IProcessItemOptionSelection {

    private static final String TAG = "SEARCH_FRAGMENT";
    protected Context mContext;
    @Bind(R.id.list)
    ListView mListView;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.search_layout)
    FrameLayout mSearchLayout;

    protected List<ISearchItem> mItems;
    protected SearchListAdapter mAdapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItems = new ArrayList<>();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.filter);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.filter(newText);
                return false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);

        mAdapter = new SearchListAdapter(mContext, getItemList(), this);
        mListView.setAdapter(mAdapter);
        return view;
    }

    @OnItemClick(R.id.list)
    void onItemClick(int position) {
        try {
            OnMusicSelectedListener activity = (OnMusicSelectedListener) mContext;
            activity.onMusicSelected((Song) mItems.get(position));
        } catch (ClassCastException e) {
            throw new ClassCastException(mContext.toString() + " must implement OnMusicSelectedListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mContext = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected List<ISearchItem> getItemList() {
        File[] fileList = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).listFiles();
        if (fileList != null) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            Song song;
            for (File file : fileList) {
                if (!file.isDirectory() && file.getPath().substring(file.getPath().lastIndexOf('.') + 1).equals("mp3")) {
                    mmr.setDataSource(file.getPath());
                    List<Song> songs = Song.find(Song.class, "file_path = ?", file.getPath());
                    if (!songs.isEmpty()) {
                        song = songs.get(0);
                    } else {
                        song = new Song(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE).toLowerCase().trim(),
                                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST).toLowerCase().trim(),
                                file.getPath());
                        song.save();
                    }
                    mItems.add(song);
                }
            }
        }
        Collections.sort(mItems, Song.songTitleComparator);
        return mItems;
    }

    @Override
    public void onItemOptionSelection(final ISearchItem item, String option) {
        if (mContext.getString(R.string.add_to_playlist).equals(option)) {
            addToPlaylist((Song)item);
        }
    }

    protected void addToPlaylist(final Song song) {
        List<String> list = new ArrayList<>();
        final List<Playlist> playlists = Playlist.listAll(Playlist.class);
        for (Playlist playlist : playlists) {
            list.add(playlist.getName());
        }
        new MaterialDialog.Builder(mContext)
                .title(R.string.dialog_add_to_playlist)
                .autoDismiss(true)
                .adapter(new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, list),
                        new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                Playlist playlist = playlists.get(which);
                                SongPlaylist songPlaylist = new SongPlaylist(playlist.getId(), song.getId());
                                songPlaylist.save();
                                Snackbar.make(mSearchLayout, WordUtils.capitalize(song.getTitle()) +
                                        " added to " + playlist.getName() + ".", Snackbar.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        })
                .show();
    }

    @Override
    public String[] getOptionList() {
        return mContext.getResources().getStringArray(R.array.song_options);
    }
}
