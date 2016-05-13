package com.enzab.spootify.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import java.util.Collections;
import java.util.Random;

import com.enzab.spootify.R;
import com.enzab.spootify.activity.MainActivity;

public class PlayerService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private MediaPlayer mMediaPlayer;
    private final IBinder mMusicBinder = new MusicBinder();
    private static PlayerService mPlayerService;
    private Song mSong;
    private ArrayList<Song> mSongQueue;
    private ArrayList<Song> mShuffleSongQueue;
    private int mSongPlayingPosition;
    private int mTmpSongPlayingPosition;
    private OnCompletionViewListener mOnCompletionViewListener;
    private boolean mRepeatMode = false;
    private boolean mShuffleMode = false;
    private int mNotificationId;

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

    public void setRepeatMode(boolean repeatMode) {
        this.mRepeatMode = repeatMode;
    }

    public boolean isRepeatMode() {
        return mRepeatMode;
    }

    public void setShuffleMode(boolean shuffleMode) {
        this.mShuffleMode = shuffleMode;
        if (shuffleMode) {
            mShuffleSongQueue = new ArrayList<>(mSongQueue);
            mShuffleSongQueue.remove(mSongPlayingPosition);
            Collections.shuffle(mShuffleSongQueue, new Random(System.nanoTime()));
            mShuffleSongQueue.add(0, mSong);
            mTmpSongPlayingPosition = mSongPlayingPosition;
            mSongPlayingPosition = 0;
        } else {
            mSongPlayingPosition = mTmpSongPlayingPosition;
        }
    }

    public boolean isShuffleMode() {
        return mShuffleMode;
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
        if (mSongQueue != null)
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
            if (!mRepeatMode)
                mOnCompletionViewListener.pause();
        }
    }

    private void setCurrentSongToMediaPlayer() {
        ArrayList<Song> list;
        list = new ArrayList<>(mShuffleMode ? mShuffleSongQueue : mSongQueue);
        mSong = list.get(mSongPlayingPosition);
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
        mNotificationId = 0;
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
    public void onRebind(Intent intent) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(mNotificationId);
    }


    @Override
    public boolean onUnbind(Intent intent) {
        if (isPlaying())
            startNotification();
        return true;
    }

    public void startNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent playIntent = new Intent(this, notificationPlayButtonListener.class);
        PendingIntent pendingPlayIntent = PendingIntent.getBroadcast(this, 0, playIntent, 0);
        Notification.Action playAction = new Notification.Action(0, "Play/Pause", pendingPlayIntent);

        Intent previousIntent = new Intent(this, notificationPreviousButtonListener.class);
        PendingIntent pendingPreviousIntent = PendingIntent.getBroadcast(this, 0, previousIntent, 0);
        Notification.Action previousAction = new Notification.Action(R.mipmap.ic_skip_previous_white_36dp, "", pendingPreviousIntent);

        Intent nextIntent = new Intent(this, notificationNextButtonListener.class);
        PendingIntent pendingNextIntent = PendingIntent.getBroadcast(this, 0, nextIntent, 0);
        Notification.Action nextAction = new Notification.Action(R.mipmap.ic_skip_next_white_36dp, "", pendingNextIntent);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_shuffle_white_36dp)
                .setContentTitle("Now playing...")
                .setContentText("on Spootify")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingNotificationIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .addAction(previousAction)
                .addAction(playAction)
                .addAction(nextAction)
//                .setAutoCancel(true)
                .build();
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(++mNotificationId, notification);
    }

    public static class notificationPlayButtonListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mPlayerService.isPlaying())
                mPlayerService.pauseSong();
            else
                mPlayerService.playSong();
        }
    }

    public static class notificationPreviousButtonListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPlayerService.previous();
        }
    }

    public static class notificationNextButtonListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPlayerService.next();
        }
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
