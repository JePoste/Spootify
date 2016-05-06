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

import com.enzab.spootify.model.Song;
import com.enzab.spootify.service.interaction.OnCompletionViewListener;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private MediaPlayer mMediaPlayer;
    private final IBinder mMusicBinder = new MusicBinder();
    private static PlayerService mPlayerService;
    private Song mSong;
    private ArrayList<Song> mSongQueue;
    private int mSongPlayingPosition;
    private OnCompletionViewListener mOnCompletionViewListener;

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

    public void next() {
        ++mSongPlayingPosition;
        if (mSongPlayingPosition >= mSongQueue.size()) {
            mSongPlayingPosition = 0;
        }
        setCurrentSongToMediaPlayer();
        mOnCompletionViewListener.updateView(mSong);
    }

    public void previous() {
        --mSongPlayingPosition;
        if (mSongPlayingPosition < 0) {
            mSongPlayingPosition = mSongQueue.size() - 1;
        }
        setCurrentSongToMediaPlayer();
        mOnCompletionViewListener.updateView(mSong);
    }

    public void seekTo(int time) {
        mMediaPlayer.seekTo(time);
    }

    public void setSongQueue(ArrayList<Song> songQueue) {
        mSongPlayingPosition = 0;
        mSongQueue = songQueue;
        setCurrentSongToMediaPlayer();
    }

    public void addToSongQueue(Song song) {
        mSongQueue.add(song);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        ++mSongPlayingPosition;
        if (mSongPlayingPosition < mSongQueue.size()) {
            setCurrentSongToMediaPlayer();
            mOnCompletionViewListener.updateView(mSong);
        } else {
            mSongPlayingPosition = 0;
            setCurrentSongToMediaPlayer();
            mOnCompletionViewListener.updateView(mSong);
            mOnCompletionViewListener.pause();
        }
    }

    private void setCurrentSongToMediaPlayer() {
        mSong = mSongQueue.get(mSongPlayingPosition);
        if (mMediaPlayer.isPlaying())
            mMediaPlayer.stop();
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(mSong.getFilePath());
            mMediaPlayer.prepare();
        } catch (IOException e) {
            // SONG NOT FOUND
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mSong = null;
        mSongQueue = null;
        mSongPlayingPosition = 0;
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

    public void setOnCompletionViewListener(OnCompletionViewListener onCompletionViewListener) {
        this.mOnCompletionViewListener = onCompletionViewListener;
    }

    public class MusicBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }
}
