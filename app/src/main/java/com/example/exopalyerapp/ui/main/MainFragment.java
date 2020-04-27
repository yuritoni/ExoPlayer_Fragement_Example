package com.example.exopalyerapp.ui.main;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.SettingInjectorService;
import android.media.MediaMetadataRetriever;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.exopalyerapp.MainActivity;

import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.exopalyerapp.MainActivity;
import com.example.exopalyerapp.R;
import com.example.exopalyerapp.adapter.VideoAdapter;
import com.example.exopalyerapp.model.ViewModel;
import com.example.exopalyerapp.utils.RecyclerTouchListener;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import butterknife.ButterKnife;

public class MainFragment extends Fragment {

    //public Activity act;
    OnItemSelectedListener callback;

    FragmentActivity c;
    private static final int REQUEST_WRITE_STORAGE = 1;
    private VideoPlayerViewModel mViewModel;
    List<ViewModel> arrayList ;
    ViewModel selectedItem;
    public static MainFragment newInstance() {
        return new MainFragment();
    }
    RecyclerView recyclerView;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // return inflater.inflate(R.layout.main_fragment, container, false);
        final View view = inflater.inflate(R.layout.main_fragment, container, false);
        c = getActivity();
         recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        ButterKnife.bind(c);
        arrayList =new  ArrayList<ViewModel>();
       // GridLayoutManager layoutManager = new GridLayoutManager(c);
        recyclerView.setLayoutManager(new GridLayoutManager(c, 2));
        recyclerView.setHasFixedSize(true);

        askPerm();
        int permissionCheck = ContextCompat.checkSelfPermission(c, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(c, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE );
        } else {

            displayAllview();
        }



        new Thread(new Runnable() {
            @Override
            public void run() {
                  final VideoAdapter adapter = new VideoAdapter(c,arrayList);
                c.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        recyclerView.setAdapter(adapter);


                    }
                });
            }
        }).start();

        Log.v("list",arrayList.get(1).getVideo_name());
        Log.v("list2",arrayList.get(1).getDuration());
        return view;
    }

    public void askPerm(){
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M && !Settings.canDrawOverlays(getActivity()));
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+ Objects.requireNonNull(getActivity()).getPackageName()));
        startActivityForResult(intent,2084);
    }

    @SuppressLint("Recycle")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void displayAllview() {

        Uri uri;
        Cursor cursor;
        int column_index, thumb;
        String absolutePath = null;
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media._ID, MediaStore.Video.Thumbnails.DATA};
        final String orderBy = MediaStore.Video.Media.DEFAULT_SORT_ORDER;
        Context applicationContext = MainActivity.getContextOfApplication();
        cursor = c.getApplication().getContentResolver().query(uri, projection, null, null, orderBy);
        // assert cursor != null;
        column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        thumb = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
        while (cursor.moveToNext()) {
            absolutePath = cursor.getString(column_index);
            Uri myUri = Uri.fromFile(new File(absolutePath));
            String currentThumb = cursor.getString(thumb);
            String fileName = FilenameUtils.getBaseName(absolutePath);
            String extension = FilenameUtils.getExtension(absolutePath);

            String duration = getDuration(absolutePath);
            ViewModel viewModel = new ViewModel();
            viewModel.setDuration(duration);
            viewModel.setVideo_url(myUri.toString());
            viewModel.setVideo_path(absolutePath);
            viewModel.setVideo_name(fileName);
            viewModel.setVideo_thumb(cursor.getString(thumb));
            if(extension!=null){
                viewModel.setGetVideo_extension(extension);
            }else{
                viewModel.setGetVideo_extension("mp4");
            }

            if(duration!=null){
                viewModel.setDuration(duration);
            }
            else {
                viewModel.setDuration("00:00");
            }

            arrayList.add(viewModel);

        }

    }

    @SuppressLint("SimpleDateFormat")
    private String getDuration(String absolutePath){
        try{
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(getContext(),Uri.fromFile(new File(absolutePath)));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMilisec = Long.parseLong(time);
            retriever.release();

            return (new SimpleDateFormat("mm:ss")).format(new Date(timeInMilisec));
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                    Toast.makeText(getContext(),"silahkan mencoba lagi",Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;
        }
    }
    public void setOnHeadlineSelectedListener(OnItemSelectedListener callback) {
        this.callback = callback;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

       // act=getActivity();
        mViewModel = ViewModelProviders.of(this).get(VideoPlayerViewModel.class);
        // TODO: Use the ViewModel
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(c.getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        selectedItem = arrayList.get(position);
                        Log.v("selectedItem",selectedItem.getVideo_name());
                        mViewModel.selected.setValue(selectedItem);

                        callback.showView(selectedItem);


                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }

                })
        );


    }

    public interface OnItemSelectedListener {
        public void onItemSelected();
        public void showView(ViewModel data);
    }


}
