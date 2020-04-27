package com.example.exopalyerapp.ui.main;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.exopalyerapp.R;
import com.example.exopalyerapp.model.ViewModel;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.net.URI;

import butterknife.ButterKnife;

public class VideoPlayerFragment extends Fragment {

    private VideoPlayerViewModel mViewModel;
    ViewModel viewModel;
    PlayerView playerView;
    FragmentActivity act;
    ExoPlayer exoPlayer;
    ExtractorsFactory extractorsFactoryl;
    private static final int REQUEST_WRITE_STORAGE = 1;
    Uri videoUrl;

    public static VideoPlayerFragment newInstance() {
        return new VideoPlayerFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.playviews_fragment, container, false);
        playerView = (PlayerView) v.findViewById(R.id.player);
        act = getActivity();
        assert act != null;
        ButterKnife.bind(act);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
        exoPlayer = ExoPlayerFactory.newSimpleInstance(act, trackSelector);
        extractorsFactoryl = new DefaultExtractorsFactory();

        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        } else {
        }


        new Thread(new Runnable() {
            @Override
            public void run() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                    }
                });
            }
        }).start();

        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                    Toast.makeText(getContext(), "silahkan mencoba lagi", Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //try {
        mViewModel =  ViewModelProviders.of(act).get(VideoPlayerViewModel.class);

        mViewModel.getSelected().observe(act, viewModel2 -> {
            Log.d("bananas", viewModel2.getVideo_name());
            viewModel = viewModel2;
            String uri = viewModel.getVideo_url();
            videoUrl = Uri.parse(uri);
            Log.d("Uri", videoUrl.toString());
            Log.d("URI", URI.create(uri).toString());
        });



       buildMediaSource(videoUrl);

        // TODO: Use the ViewModel
    }

    private void buildMediaSource(Uri uri) {
        try {
            String playerInfo = Util.getUserAgent(act, "videopalyer");
            DataSource.Factory dataSourceFactory =
                    new DefaultDataSourceFactory(act, playerInfo);
        /*return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);*/
            MediaSource mediaSource =  new ExtractorMediaSource(videoUrl,dataSourceFactory,extractorsFactoryl,null,null);

                   // new ProgressiveMediaSource.Factory(dataSourceFactory).setTag("video").createMediaSource(uri);
            playerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        } catch (Exception e) {
            Toast.makeText(act.getApplicationContext(), "Tidak Dapat Memuat File", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onPause() {
        Log.e("DEBUG", "OnPause of loginFragment");
        super.onPause();
        exoPlayer.setPlayWhenReady(false);
    }





}
