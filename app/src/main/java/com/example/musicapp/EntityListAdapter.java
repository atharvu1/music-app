package com.example.musicapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class EntityListAdapter extends RecyclerView.Adapter<EntityHolder> {

    ArrayList<SongInfoModel> mEntityList;
    Context mContext;
    int mResource;
    SongInfoModel songInfoModel;

    public EntityListAdapter(Context context, int resource, ArrayList<SongInfoModel> entityList){
        mEntityList = entityList;
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public EntityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mResource,parent,false);
        Button artistName = view.findViewById(R.id.artistName);
        Button albumName = view.findViewById(R.id.albumName);

        if(mContext.getClass().getSimpleName().equals("DetailedActivity")){
            artistName.setEnabled(false);
            albumName.setEnabled(false);
        }

        artistName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext.getApplicationContext(), DetailedActivity.class);
                String artist = songInfoModel.getArtistName();
                i.putExtra("value", artist);
                i.putExtra("title","Artist Tracks");
                mContext.startActivity(i);
            }
        });

        albumName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext.getApplicationContext(), DetailedActivity.class);
                String album = songInfoModel.getCollectionName();
                i.putExtra("value", album);
                i.putExtra("title","Album Tracks");
                mContext.startActivity(i);
            }
        });

        return new EntityHolder(mContext,view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntityHolder holder, int position) {
        this.songInfoModel = mEntityList.get(position);
        holder.bindEntity(songInfoModel);
    }

    @Override
    public int getItemCount() {
        return mEntityList.size();
    }
}
