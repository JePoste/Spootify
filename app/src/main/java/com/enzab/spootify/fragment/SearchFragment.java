package com.enzab.spootify.fragment;


import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.enzab.spootify.R;
import com.enzab.spootify.activity.interaction.OnMusicSelectedListener;
import com.enzab.spootify.adapter.SearchListAdapter;
import com.enzab.spootify.model.SearchItem;
import com.enzab.spootify.model.Song;
import com.orm.SugarContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private static final String TAG = "SEARCH_FRAGMENT";
    protected Context mContext;
    @Bind(R.id.list)
    ListView mListView;
    @Bind(R.id.fab)
    FloatingActionButton mFab;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    protected List<Song> mItems;
    protected SearchListAdapter mAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mItems = new ArrayList<>();
        SugarContext.init(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);

        mAdapter = new SearchListAdapter(mContext, getItemList());
        mListView.setAdapter(mAdapter);
        return view;
    }

    @OnItemClick(R.id.list)
    void onItemClick(int position) {
        try {
            OnMusicSelectedListener activity = (OnMusicSelectedListener) mContext;
            activity.onMusicSelected(mItems.get(position));
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
        SugarContext.terminate();
    }

    protected List<SearchItem> getItemList() {
        File[] fileList = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles();
        List<SearchItem> searchItems = new ArrayList<>();
        if (fileList != null) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            Song song;
            for (File file : fileList) {
                if (!file.isDirectory()) {
                    mmr.setDataSource(file.getPath());
                    song = new Song(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                            mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                            file.getPath());
                    mItems.add(song);
                    searchItems.add(new SearchItem(song.getTitle(), song.getArtist()));
                    if (Song.find(Song.class, "file_path = ?", song.getFilePath()).size() == 0) {
                        song.save();
                    }
                }
            }
        }
        Collections.sort(mItems, Song.songTitleComparator);
        return searchItems;
    }
}
