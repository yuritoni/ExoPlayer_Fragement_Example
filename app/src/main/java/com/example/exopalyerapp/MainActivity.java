package com.example.exopalyerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.CircularPropagation;
import androidx.transition.Explode;
import androidx.transition.Slide;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.example.exopalyerapp.model.ViewModel;
import com.example.exopalyerapp.service.FloatingView;
import com.example.exopalyerapp.ui.main.MainFragment;
import com.example.exopalyerapp.ui.main.VideoPlayerFragment;
import com.example.exopalyerapp.ui.main.VideoPlayerViewModel;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

public class MainActivity extends AppCompatActivity implements MainFragment.OnItemSelectedListener {

    public static Context contextOfApplication;

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }

    MainFragment main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        main = (MainFragment.newInstance());
        //contextOfApplication = getApplicationContext();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, main)
                    .commitNow();
        }
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        if (fragment instanceof MainFragment) {
            //MainFragment headlinesFragment = (MainFragment.newInstance()) ;
            main.setOnHeadlineSelectedListener(this);
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);

    }
    // This metho

    public void addItem(Uri uri) {
        // Add mediaId (e.g. uri) as tag to the MediaSource.
        MediaSource mediaSource =
                new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(this, "exoplayer-codelab"))
                        .setTag("video")
                        .createMediaSource(uri);
        MediaSource firstSource =
                new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(this, "exoplayer-codelab")).createMediaSource(uri);
        MediaSource secondSource =
                new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(this, "exoplayer-codelab")).createMediaSource(uri);
// Plays the first video, then the second video.
        ConcatenatingMediaSource concatenatedSource =
                new ConcatenatingMediaSource(firstSource, secondSource);
        concatenatedSource.addMediaSource(mediaSource);

    }


    @Override
    public void onItemSelected() {

/*
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment main = new VideoPlayerFragment();
        Explode e = new Explode();
        e.setDuration(800);
        e.setMode(Explode.MODE_OUT);
        e.setPropagation(new CircularPropagation());
        e.setInterpolator(new AccelerateInterpolator());
        main.setExitTransition(e);

        Slide slide = new Slide();
        slide.setDuration(500);
        slide.setInterpolator(new DecelerateInterpolator());
        main.setEnterTransition(slide);
        transaction.replace(R.id.container, main);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();*/
    }

    @Override
    public void showView(ViewModel data) {

        Intent serviceIntent = new Intent(MainActivity.this, FloatingView.class);
        serviceIntent.putExtra("videoUrl",data.getVideo_url());
        startService(serviceIntent);

    }
}
