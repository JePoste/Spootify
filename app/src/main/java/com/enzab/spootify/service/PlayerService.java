package com.enzab.spootify.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import com.enzab.spootify.model.Song;

public class PlayerService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private MediaPlayer mMediaPlayer;
    private ArrayList<Song> mSongList;
    private int mSongPosition;
    private final IBinder mMusicBinder = new MusicBinder();

    public void playSong() {
        mMediaPlayer.stop(); // TODO: maybe check if its playing playing before topping, if not handled
        mMediaPlayer.reset();

        Song playSong = mSongList.get(mSongPosition);
//        String currentSong = playSong.getFilePath();
        String currentSong = "/";

        try {
            mMediaPlayer.setDataSource(currentSong);
            mMediaPlayer.setOnPreparedListener(this); // TODO: test without setting the listener each time
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            // SONG NOT FOUND
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

    }

    public void setSong(int songId){
        mSongPosition = songId;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMusicBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mSongPosition = 0;
        mMediaPlayer = new MediaPlayer();
        initMediaPlayer();

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    private void initMediaPlayer() {
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
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
