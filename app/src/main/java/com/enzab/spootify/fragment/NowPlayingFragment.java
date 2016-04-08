package com.enzab.spootify.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.enzab.spootify.R;
import com.enzab.spootify.model.SearchItem;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NowPlayingFragment extends Fragment {

    private MediaPlayer mMusic;

    @Bind(R.id.album_cover)
    ImageView mAlbumCover;
    @Bind(R.id.option_button)
    ImageView mOptionButton;
    @Bind(R.id.play_button)
    ImageView mPlayButton;
    @Bind(R.id.previous_button)
    ImageView mPreviousButton;
    @Bind(R.id.next_button)
    ImageView mNextButton;
    @Bind(R.id.shuffle_button)
    ImageView mShuffleButton;
    @Bind(R.id.repeat_button)
    ImageView mRepeatButton;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.artist)
    TextView mArtist;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String MUSIC_ITEM = "music_item";

    private SearchItem mMusicItem;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param musicItem Parameter 1.
     * @return A new instance of fragment SearchFragment.
     */
    public static NowPlayingFragment newInstance(SearchItem musicItem) {
        NowPlayingFragment fragment = new NowPlayingFragment();
        Bundle args = new Bundle();
        args.putSerializable(MUSIC_ITEM, musicItem);
        fragment.setArguments(args);
        return fragment;
    }

    public NowPlayingFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mMusicItem = (SearchItem) getArguments().getSerializable(MUSIC_ITEM);
        } else {
            mMusicItem = null;
        }

        mMusic = MediaPlayer.create(getContext(), R.raw.strauss_also_sprach_zarathustra);
        mMusic.setOnCompletionListener(soundCompletionListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);

        ButterKnife.bind(this, view);
        mPlayButton.setTag(R.mipmap.ic_play_circle_outline_white_48dp);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mMusicItem != null) {
            mTitle.setText(mMusicItem.getTitle());
            mArtist.setText(mMusicItem.getArtist());
        }
        Picasso.with(getContext()).load(R.drawable.cover1).placeholder(R.drawable.default_cover).noFade().into(mAlbumCover);
    }

    @OnClick(R.id.play_button)
    public void playButtonPressed(View view) {
        if (String.valueOf(mPlayButton.getTag()).equals(String.valueOf(R.mipmap.ic_play_circle_outline_white_48dp))) {
            mMusic.start();
            mPlayButton.setTag(R.mipmap.ic_pause_circle_outline_white_48dp);
            mPlayButton.setImageResource(R.mipmap.ic_pause_circle_outline_white_48dp);
        } else {
            mMusic.pause();
            mPlayButton.setTag(R.mipmap.ic_play_circle_outline_white_48dp);
            mPlayButton.setImageResource(R.mipmap.ic_play_circle_outline_white_48dp);
        }
    }

    MediaPlayer.OnCompletionListener soundCompletionListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer sound) {
            sound.release();
        }
    };
}
