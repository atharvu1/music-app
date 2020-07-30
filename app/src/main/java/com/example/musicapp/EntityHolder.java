package com.example.musicapp;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import androidx.recyclerview.widget.RecyclerView;

public class EntityHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    ImageView thumbnail;
    TextView entityName;
    Button artistName, albumName;

    public EntityHolder(Context context, View itemView) {
        super(itemView);

        this.thumbnail = itemView.findViewById(R.id.thumbnail);
        this.entityName = itemView.findViewById(R.id.entityName);
        this.artistName = itemView.findViewById(R.id.artistName);
        this.albumName = itemView.findViewById(R.id.albumName);
        itemView.setOnClickListener(this);


    }

    public void bindEntity(SongInfoModel songInfoModel){
        Picasso.get().load(songInfoModel.getThumbnailURL()).into(thumbnail);
        entityName.setText(songInfoModel.getTrackName());
        if(songInfoModel.getArtistName().length()<=20)
            artistName.setText(songInfoModel.getArtistName());
        else
            artistName.setText(songInfoModel.getArtistName().substring(0,17)+"...");

        if(songInfoModel.getCollectionName().length()<=20)
            albumName.setText(songInfoModel.getCollectionName());
        else
            albumName.setText(songInfoModel.getCollectionName().substring(0,17)+"...");

    }

    @Override
    public void onClick(View view) {

    }
}
