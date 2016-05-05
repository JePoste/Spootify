package com.enzab.spootify.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import com.enzab.spootify.R;
import com.enzab.spootify.model.Song;

public class PlayerService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private MediaPlayer mMediaPlayer;
    private ArrayList<Song> mSongList;
    private Song mSong;
    private final IBinder mMusicBinder = new MusicBinder();
    private static PlayerService mPlayerService;

    public static PlayerService getInstance() {
        return mPlayerService;
    }

    public static void initialize(MusicBinder playerBinder) {
        mPlayerService = playerBinder.getService();
    }

    public void pauseSong() {
        mMediaPlayer.pause();
    }

    public void playSong() {
        mMediaPlayer.start();
    }

    public void seekTo(int time) {
        mMediaPlayer.seekTo(time);
    }

    public void setSong(Song song) {
        mSong = song;
        if (mMediaPlayer.isPlaying())
            mMediaPlayer.stop();
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(mSong.getFilePath());
            mMediaPlayer.setOnPreparedListener(this); // TODO: test without setting the listener each time
            mMediaPlayer.prepare();
        } catch (IOException e) {
            // SONG NOT FOUND
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
//            mediaPlayer.seekTo(0);
//            mPlayButton.setTag("PAUSED");
//            mPlayButton.setImageResource(R.mipmap.ic_play_circle_outline_white_48dp);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mSong = null;
        mMediaPlayer = new MediaPlayer();
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMusicBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
//        if(mMediaPlayer != null) {
//            if(mMediaPlayer.isPlaying())
//                mMediaPlayer.stop();
//            mMediaPlayer.reset();
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//        }
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public Song getSong() {
        return mSong;
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void setList(ArrayList<Song> songs) {
        mSongList = songs;
    }

    public class MusicBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }
}
