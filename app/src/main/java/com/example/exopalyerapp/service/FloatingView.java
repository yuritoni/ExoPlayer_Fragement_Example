package com.example.exopalyerapp.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.exopalyerapp.MainActivity;
import com.example.exopalyerapp.R;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class FloatingView extends Service {
    public FloatingView() {
    }

    WindowManager windowManager;
    private View mFloatingView;
    SimpleExoPlayer exoPlayer;
    PlayerView playerView;
    BandwidthMeter bandwidthMeter;
    TrackSelector trackSelector;
    DefaultDataSourceFactory defaultDataSourceFactory;
    ExtractorsFactory extractorsFactory;

    Uri videoUrl;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int tanda, int startId) {

        if (intent != null) {
            String uri = intent.getStringExtra("videoUrl");
            videoUrl = Uri.parse(uri);
            if (windowManager != null && mFloatingView.isShown() && exoPlayer != null) {
                windowManager.removeView(mFloatingView);
                mFloatingView = null;
                windowManager = null;
                exoPlayer.setPlayWhenReady(false);
                exoPlayer.release();
                exoPlayer = null;

            }
            final WindowManager.LayoutParams params;
            mFloatingView = LayoutInflater.from(this).inflate(R.layout.custom_view_video, null);

            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                params = new WindowManager.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT
                );
            } else {
                params = new WindowManager.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT
                );
            }
            params.gravity = Gravity.TOP | Gravity.LEFT;
            params.x = 300;
            params.y = 300;


            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            windowManager.addView(mFloatingView,params);




            playerView = mFloatingView.findViewById(R.id.playerView);
            ImageView imageViewClose = mFloatingView.findViewById(R.id.imageViewDismiss);
            ImageView imageViewMax = mFloatingView.findViewById(R.id.imageViewMax);

            imageViewClose.setOnClickListener(v -> {

                if(windowManager!=null && mFloatingView.isShown() &exoPlayer!=null){
                    windowManager.removeView(mFloatingView);
                    mFloatingView =null;
                    windowManager =null;
                    exoPlayer.setPlayWhenReady(false);
                    exoPlayer.release();
                    exoPlayer =null;
                    stopSelf();
                    Intent serviceIntent = new Intent(FloatingView.this, MainActivity.class);
                    //serviceIntent.putExtra("videoUrl",data.getVideo_url());
                    startService(serviceIntent);
                }
            });

            imageViewMax.setOnClickListener(v -> {

                if(windowManager!=null && mFloatingView.isShown() &exoPlayer!=null){
                    windowManager.removeView(mFloatingView);
                    mFloatingView =null;
                    windowManager =null;
                    exoPlayer.setPlayWhenReady(false);
                    exoPlayer.release();
                    exoPlayer =null;
                    stopSelf();
                }
            });

            buildMediaSource(videoUrl);

            mFloatingView.findViewById(R.id.custom_view).setOnTouchListener(new View.OnTouchListener() {

                private int stateX, stateY;
                private float stateTouchX, stateTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            stateX = params.x;
                            stateY = params.y;
                            stateTouchX = event.getRawX();
                            stateTouchY = event.getRawY();
                            return true;
                        case MotionEvent.ACTION_UP:
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            params.x = stateX + (int) (event.getRawX() - stateTouchX);
                            params.y = stateY + (int) (event.getRawY() - stateTouchY);
                            windowManager.updateViewLayout(mFloatingView,params);
                            return true;
                    }
                    return false;
                }


            });


        }
        return super.onStartCommand(intent, tanda, startId);
    }


    private void buildMediaSource(Uri uri) {
        try {
            bandwidthMeter = new DefaultBandwidthMeter();
            trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            exoPlayer = ExoPlayerFactory.newSimpleInstance( this, trackSelector);
            extractorsFactory = new DefaultExtractorsFactory();

            String playerInfo = Util.getUserAgent(this, "videopalyer");
            DataSource.Factory dataSourceFactory =
                    new DefaultDataSourceFactory(this, playerInfo);
        /*return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);*/
            MediaSource mediaSource = new ExtractorMediaSource(videoUrl, dataSourceFactory, extractorsFactory, null, null);

            // new ProgressiveMediaSource.Factory(dataSourceFactory).setTag("video").createMediaSource(uri);
            playerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Tidak Dapat Memuat File", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) {
            windowManager.removeView(mFloatingView);
        }

    }
}

