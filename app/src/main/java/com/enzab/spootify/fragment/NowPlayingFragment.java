package com.enzab.spootify.fragment;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.enzab.spootify.R;
import com.enzab.spootify.model.ISearchItem;
import com.enzab.spootify.model.Song;
import com.enzab.spootify.service.PlayerService;
import com.enzab.spootify.service.interaction.OnCompletionViewListener;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NowPlayingFragment extends Fragment implements OnCompletionViewListener {

    @Bind(R.id.album_cover)
    ImageView mAlbumCover;
    @Bind(R.id.option_button)
    ImageView mOptionButton;
    @Bind(R.id.repeat_button)
    ImageView mRepeatButton;
    @Bind(R.id.shuffle_button)
    ImageView mShuffleButton;
    @Bind(R.id.play_button)
    ImageView mPlayButton;
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
    private static final String SONG_QUEUE = "song_queue";

    private static final String TAG = "NOW_PLAYING_FRAGMENT";
    private Context mContext;
    private Handler mHandler = new Handler();
    private Timer mRefreshTimer;
    private TimerTask mRefreshSongProgressTimerTask;
    private PlayerService mPlayerService;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param songQueue Parameter 1.
     * @return A new instance of fragment SearchFragment.
     */
    public static NowPlayingFragment newInstance(ArrayList<ISearchItem> songQueue) {
        NowPlayingFragment fragment = new NowPlayingFragment();
        Bundle args = new Bundle();
        args.putSerializable(SONG_QUEUE, songQueue);
        fragment.setArguments(args);
        return fragment;
    }

    public NowPlayingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPlayerService = PlayerService.getInstance();
        mPlayerService.setOnCompletionViewListener(this);
        if (getArguments() != null) {
            ArrayList<Song> songQueue;
            songQueue = (ArrayList<Song>) getArguments().getSerializable(SONG_QUEUE);
            if (songQueue != null && songQueue.size() > 0) {
                mPlayerService.setSongQueue(songQueue);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        updateView(mPlayerService.getSong());
    }

    @OnClick(R.id.play_button)
    public void onPlayButtonClicked(View view) {
        if ("PLAYING".equals(mPlayButton.getTag())) {
            mPlayerService.pauseSong();
            mRefreshSongProgressTimerTask.cancel();
            mRefreshTimer.purge();
            mPlayButton.setTag("PAUSED");
            mPlayButton.setImageResource(R.mipmap.ic_play_circle_outline_white_48dp);
        } else if ("PAUSED".equals(mPlayButton.getTag())) {
            mPlayerService.playSong();
            mRefreshSongProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.post(new Runnable() {
                        public void run() {
                            mSongProgress.setText(getTimeString(mPlayerService.getCurrentPosition()));
                            mSongProgressBar.setProgress(mPlayerService.getCurrentPosition());
                        }
                    });
                }
            };
            mRefreshTimer = new Timer();
            mRefreshTimer.schedule(mRefreshSongProgressTimerTask, 0, 1000);
            mPlayButton.setTag("PLAYING");
            mPlayButton.setImageResource(R.mipmap.ic_pause_circle_outline_white_48dp);
        }
    }

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
        if (mRefreshSongProgressTimerTask != null && mRefreshTimer != null) {
            mRefreshSongProgressTimerTask.cancel();
            mRefreshTimer.purge();
        }
    }

    @Override
    public void updateView(Song song) {
        if (song != null) {
            mPlayButton.setTag("PAUSED");

            mSongTitle.setText(WordUtils.capitalize(song.getTitle()));
            mArtist.setText(WordUtils.capitalize(song.getArtist()));

            //RepeatButton init
            if (mPlayerService.isRepeatMode()) {
                mRepeatButton.setTag("REPEAT");
                mRepeatButton.setColorFilter(ContextCompat.getColor(mContext, R.color.accent), PorterDuff.Mode.MULTIPLY);
            } else {
                mRepeatButton.setTag("NORMAL");
                mRepeatButton.setColorFilter(null);
            }
            //ShuffleButton init
            if (mPlayerService.isShuffleMode()) {
                mShuffleButton.setTag("SHUFFLE");
                mShuffleButton.setColorFilter(ContextCompat.getColor(mContext, R.color.accent), PorterDuff.Mode.MULTIPLY);
            } else {
                mShuffleButton.setTag("NORMAL");
                mShuffleButton.setColorFilter(null);
            }

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(song.getFilePath());
            byte[] picture = mmr.getEmbeddedPicture();
            if (picture != null)
                mAlbumCover.setImageBitmap(BitmapFactory.decodeByteArray(picture, 0, picture.length));
            else
                Picasso.with(mContext).load(R.drawable.default_cover).noFade().into(mAlbumCover);

            mSongProgressBar.setMax(mPlayerService.getDuration());
            mSongDuration.setText(getTimeString(mPlayerService.getDuration()));
            mSongProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int progress = seekBar.getProgress();
                    mPlayerService.seekTo(progress);
                    mSongProgress.setText(getTimeString(progress));
                }
            });

            onPlayButtonClicked(null);
        } else {
            Picasso.with(mContext).load(R.drawable.default_cover).noFade().into(mAlbumCover);
        }
    }

    @Override
    public void pause() {
        onPlayButtonClicked(null);
    }

    @OnClick(R.id.next_button)
    void onNextButtonClicked(View view) {
        mPlayerService.next();
    }

    @OnClick(R.id.previous_button)
    void onPreviousButtonClicked(View view) {
        mPlayerService.previous();
    }

    @OnClick(R.id.repeat_button)
    void onRepeatButtonClicked(View view) {
        ImageView img = (ImageView) view;
        if ("REPEAT".equals(view.getTag())) {
            view.setTag("NORMAL");
            mPlayerService.setRepeatMode(false);
            img.setColorFilter(null);
        } else {
            view.setTag("REPEAT");
            img.setColorFilter(ContextCompat.getColor(mContext, R.color.accent), PorterDuff.Mode.MULTIPLY);
            mPlayerService.setRepeatMode(true);
        }
    }

    @OnClick(R.id.shuffle_button)
    void onShuffleButtonClicked(View view) {
        ImageView img = (ImageView) view;
        if ("SHUFFLE".equals(view.getTag())) {
            view.setTag("NORMAL");
            mPlayerService.setShuffleMode(false);
            img.setColorFilter(null);
        } else {
            view.setTag("SHUFFLE");
            img.setColorFilter(ContextCompat.getColor(mContext, R.color.accent), PorterDuff.Mode.MULTIPLY);
            mPlayerService.setShuffleMode(true);
        }
    }

}
