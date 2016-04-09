package com.enzab.spootify.fragment;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.enzab.spootify.R;
import com.enzab.spootify.model.SearchItem;
import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NowPlayingFragment extends Fragment {

    private MediaPlayer mMediaPlayer;

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
    @Bind(R.id.song_title)
    TextView mSongTitle;
    @Bind(R.id.artist)
    TextView mArtist;
    @Bind(R.id.song_progress)
    TextView mSongProgress;
    @Bind(R.id.song_duration)
    TextView mSongDuration;
    @Bind(R.id.song_progress_bar)
    SeekBar mSongProgressBar;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String MUSIC_ITEM = "music_item";

    private SearchItem mMusicItem;
    private Context mContext;
    private Handler handler = new Handler();
    private Timer refreshTimer;
    private TimerTask refreshSongProgressTimerTask;


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

    public NowPlayingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mMusicItem = (SearchItem) getArguments().getSerializable(MUSIC_ITEM);
        } else {
            mMusicItem = null;
        }

        mMediaPlayer = MediaPlayer.create(mContext, R.raw.strauss_also_sprach_zarathustra);
        mMediaPlayer.setOnCompletionListener(soundCompletionListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        ButterKnife.bind(this, view);
        mPlayButton.setTag("PAUSED");

        if (mMusicItem != null) {
            mSongTitle.setText(mMusicItem.getTitle());
            mArtist.setText(mMusicItem.getArtist());
        }

        mSongProgressBar.setMax(mMediaPlayer.getDuration());
        mSongProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.seekTo(mSongProgressBar.getProgress());
                mSongProgress.setText(getTimeString(mMediaPlayer.getCurrentPosition()));
            }
        });

        Picasso.with(mContext).load(R.drawable.cover1).placeholder(R.drawable.default_cover).noFade().into(mAlbumCover);

        return view;
    }

    @OnClick(R.id.play_button)
    public void playButtonPressed(View view) {
        if (mPlayButton.getTag().equals("PAUSED")) {
            refreshSongProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            mSongProgress.setText(getTimeString(mMediaPlayer.getCurrentPosition()));
                            mSongProgressBar.setProgress(mMediaPlayer.getCurrentPosition());
                            mSongDuration.setText(getTimeString(mMediaPlayer.getDuration()));
                        }
                    });
                }
            };
            refreshTimer = new Timer();
            refreshTimer.schedule(refreshSongProgressTimerTask, 0, 1000);

            mMediaPlayer.start();
            mPlayButton.setTag("PLAYING");
            mPlayButton.setImageResource(R.mipmap.ic_pause_circle_outline_white_48dp);
        } else {
            mMediaPlayer.pause();
            refreshSongProgressTimerTask.cancel();
            refreshTimer.purge();
            mPlayButton.setTag("PAUSED");
            mPlayButton.setImageResource(R.mipmap.ic_play_circle_outline_white_48dp);
        }
    }

    MediaPlayer.OnCompletionListener soundCompletionListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
            mPlayButton.setTag("PAUSED");
            mPlayButton.setImageResource(R.mipmap.ic_play_circle_outline_white_48dp);
        }
    };

    private String getTimeString(long millis) {
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
        return String.format(Locale.US, "%02d", minutes) + ":" + String.format(Locale.US, "%02d", seconds);
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
}
