package com.enzab.spootify.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.enzab.spootify.R;
import com.enzab.spootify.activity.interaction.OnMusicSelectedListener;
import com.enzab.spootify.fragment.AlbumFragment;
import com.enzab.spootify.fragment.NowPlayingFragment;
import com.enzab.spootify.fragment.PlaylistFragment;
import com.enzab.spootify.fragment.SearchFragment;
import com.enzab.spootify.model.Song;
import com.enzab.spootify.service.PlayerService;
import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMusicSelectedListener {

    private static final String TAG = "MAIN_ACTIVITY";
    private static final int REQUEST_READWRITE_STORAGE = 1;
    private PlayerService mPlayerService;
    private Intent mPlayIntent;
    private boolean mIsBoundToPlayerService = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new SearchFragment()).commit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        SugarContext.init(this);
////        List<Song> musics = (List<Song>) Song.find(Song.class, "artist=?", "Toto");
//        List<Song> musics = Song.listAll(Song.class);
//        for (Song music : musics) {
//            Log.v(TAG, music.getTitle() + " " + music.getArtist());
//        }
        requestPermissionToUser();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_READWRITE_STORAGE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            }
        }
    }

    private boolean requestPermissionToUser() {
        int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READWRITE_STORAGE);
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mPlayIntent == null) {
            mPlayIntent = new Intent(this, PlayerService.class);
            startService(mPlayIntent);
            bindService(mPlayIntent, mMusicConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onDestroy() {
//        stopService(mPlayIntent);
        unbindService(mMusicConnection);
        mPlayerService = null;
        super.onDestroy();
        SugarContext.terminate();
    }

    // connect to the service
    private ServiceConnection mMusicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.MusicBinder binder = (PlayerService.MusicBinder) service;
            mPlayerService = binder.getService();
            PlayerService.initialize(binder);
//            mPlayerService.setList(songList); // PASS SONG LIST HERE
            mIsBoundToPlayerService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBoundToPlayerService = false;
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_search) {
            fragment = new SearchFragment();
        } else if (id == R.id.nav_playlists) {
            fragment = new PlaylistFragment();
        } else if (id == R.id.nav_albums) {
            fragment = new AlbumFragment();
        } else if (id == R.id.nav_now_playing) {
            fragment = new NowPlayingFragment();
        } else if (id == R.id.nav_settings) {

        }
        if (fragment != null)
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMusicSelected(ArrayList<Song> songList, int songPosition) {
        if (songList.size() > 1)
            Collections.rotate(songList, -songPosition);
        Fragment fragment;
        fragment = NowPlayingFragment.newInstance(songList);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();
//        mPlayerService.setSong(songList.get(1)); // put song path here
    }

}
