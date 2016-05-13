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
import com.enzab.spootify.activity.interaction.OnItemSelectedListener;
import com.enzab.spootify.fragment.NowPlayingFragment;
import com.enzab.spootify.fragment.PlaylistEditionFragment;
import com.enzab.spootify.fragment.PlaylistFragment;
import com.enzab.spootify.fragment.SearchFragment;
import com.enzab.spootify.model.ISearchItem;
import com.enzab.spootify.model.Playlist;
import com.enzab.spootify.service.PlayerService;
import com.orm.SugarContext;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnItemSelectedListener {

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
        SugarContext.init(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new SearchFragment()).commit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        requestPermissionToUser();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mIsBoundToPlayerService) {
            mPlayIntent = new Intent(this, PlayerService.class);
            startService(mPlayIntent);
            bindService(mPlayIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
            mIsBoundToPlayerService = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mIsBoundToPlayerService) {
            unbindService(mServiceConnection);
            mIsBoundToPlayerService = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SugarContext.terminate();
        if (isFinishing()) {
            stopService(mPlayIntent);
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.MusicBinder binder = (PlayerService.MusicBinder) service;
            mPlayerService = binder.getService();
            PlayerService.initialize(binder);
            mIsBoundToPlayerService = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBoundToPlayerService = false;
        }
    };

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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        String title = "Spootify";

        if (id == R.id.nav_search) {
            fragment = new SearchFragment();
            title = "Search";
        } else if (id == R.id.nav_playlists) {
            fragment = new PlaylistFragment();
            title = "Playlists";
        } else if (id == R.id.nav_now_playing) {
            fragment = new NowPlayingFragment();
        }
        if (fragment != null)
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();
        getSupportActionBar().setTitle(title);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMusicSelected(ArrayList<ISearchItem> songList, int songPosition) {
        if (songList.size() > 1)
            Collections.rotate(songList, -songPosition);
        Fragment fragment;
        fragment = NowPlayingFragment.newInstance(songList);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();
        getSupportActionBar().setTitle(WordUtils.capitalize("Spootify"));
    }

    @Override
    public void onPlaylistSelected(Playlist searchItem) {
        Fragment fragment;
        fragment = PlaylistEditionFragment.newInstance(searchItem);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();
        getSupportActionBar().setTitle(WordUtils.capitalize(searchItem.getName()));
    }

}
