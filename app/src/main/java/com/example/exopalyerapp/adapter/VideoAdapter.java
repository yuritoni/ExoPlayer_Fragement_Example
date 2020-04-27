package com.example.exopalyerapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.exopalyerapp.R;
import com.example.exopalyerapp.model.ViewModel;
import com.google.android.exoplayer2.audio.TeeAudioProcessor;

import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder> {



    @NonNull
    @Override
    public VideoAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new VideoAdapterViewHolder(view);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VideoAdapterViewHolder holder, int position) {

        Glide.with(context).load(arrayList.get(position).getVideo_thumb()).into(holder.image);
        holder.name.setText(arrayList.get(position).getVideo_name()+"."+arrayList.get(1).getGetVideo_extension());
        holder.duration.setText(arrayList.get(position).getDuration());

    }




    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    Context context;
    List<ViewModel> arrayList;

    public VideoAdapter(Context context, List<ViewModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public static class VideoAdapterViewHolder extends RecyclerView.ViewHolder {


        public ImageView image;
        TextView name, duration;

        public VideoAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.vid_image);
            name = (TextView) itemView.findViewById(R.id.vid_name);
            duration = (TextView) itemView.findViewById(R.id.vid_duration);

        }
    }
}
