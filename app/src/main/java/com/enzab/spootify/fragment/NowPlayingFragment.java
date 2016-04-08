package com.enzab.spootify.fragment;

import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.enzab.spootify.R;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NowPlayingFragment extends Fragment {

    static double PLAY_BUTTON_RATIO = 1.5;
    static int playButtonWidth;
    static int playButtonHeight;

    MediaPlayer music;

    @Bind(R.id.album_cover)
    ImageView albumCover;
    @Bind(R.id.option_button)
    ImageView optionButton;
    @Bind(R.id.play_button)
    ImageView playButton;
    @Bind(R.id.previous_button)
    ImageView previousButton;
    @Bind(R.id.next_button)
    ImageView nextButton;
    @Bind(R.id.shuffle_button)
    ImageView shuffleButton;
    @Bind(R.id.repeat_button)
    ImageView repeatButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BitmapFactory.Options dimensions = new BitmapFactory.Options();
        dimensions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.mipmap.ic_play_circle_outline_white_48dp, dimensions);
        playButtonWidth = (int) (dimensions.outWidth * PLAY_BUTTON_RATIO);
        playButtonHeight = (int) (dimensions.outHeight * PLAY_BUTTON_RATIO);

        music = MediaPlayer.create(getContext(), R.raw.strauss_also_sprach_zarathustra);
        music.setOnCompletionListener(soundCompletionListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);

        ButterKnife.bind(this, view);
        playButton.setTag(R.mipmap.ic_play_circle_outline_white_48dp);

        Picasso.with(getContext()).load(R.drawable.cover1).into(albumCover);
        return view;
    }

    @OnClick(R.id.play_button)
    public void playButtonPressed(View view) {
        if (String.valueOf(playButton.getTag()).equals(String.valueOf(R.mipmap.ic_play_circle_outline_white_48dp))) {
            music.start();
            playButton.setTag(R.mipmap.ic_pause_circle_outline_white_48dp);
            playButton.setImageResource(R.mipmap.ic_pause_circle_outline_white_48dp);
        } else {
            music.pause();
            playButton.setTag(R.mipmap.ic_play_circle_outline_white_48dp);
            playButton.setImageResource(R.mipmap.ic_play_circle_outline_white_48dp);
        }
    }

    MediaPlayer.OnCompletionListener soundCompletionListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer sound) {
            sound.release();
        }
    };
}
