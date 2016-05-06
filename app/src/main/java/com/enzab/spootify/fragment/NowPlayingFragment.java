package com.enzab.spootify.fragment;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.enzab.spootify.R;
import com.enzab.spootify.model.Song;
import com.enzab.spootify.service.PlayerService;
import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NowPlayingFragment extends Fragment {

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
     * @param musicItem Parameter 1.
     * @return A new instance of fragment SearchFragment.
     */
    public static NowPlayingFragment newInstance(Song musicItem) {
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

        mPlayerService = PlayerService.getInstance();
        if (getArguments() != null) {
            Song selectedSong;
            selectedSong = (Song) getArguments().getSerializable(MUSIC_ITEM);
            if (selectedSong != null)
                mPlayerService.setSong(selectedSong);
        } else {
            if (mPlayerService.isPlaying())
                return; // re-bind le service à la vue
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        ButterKnife.bind(this, view);

        Song currentSong = mPlayerService.getSong();
        if (currentSong != null) {
            mPlayButton.setTag("STOPPED");

            mSongTitle.setText(currentSong.getTitle());
            mArtist.setText(currentSong.getArtist());

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(currentSong.getFilePath());
            byte[] picture = mmr.getEmbeddedPicture();
            mAlbumCover.setImageBitmap(BitmapFactory.decodeByteArray(picture, 0, picture.length));

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

            playButtonPressed(view);
        } else {
            Picasso.with(mContext).load(R.drawable.default_cover).noFade().into(mAlbumCover);
        }
        return view;
    }

    @OnClick(R.id.play_button)
    public void playButtonPressed(View view) {
        if (mPlayButton.getTag().equals("PLAYING")) {
            mPlayerService.pauseSong();
            mRefreshSongProgressTimerTask.cancel();
            mRefreshTimer.purge();
            mPlayButton.setTag("PAUSED");
            mPlayButton.setImageResource(R.mipmap.ic_play_circle_outline_white_48dp);
        } else if (mPlayButton.getTag().equals("PAUSED") || mPlayButton.getTag().equals("STOPPED")) {
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
        mRefreshSongProgressTimerTask.cancel();
        mRefreshTimer.purge();
    }
}
