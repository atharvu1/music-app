package com.example.musicapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class EntityListAdapter extends ArrayAdapter<SongInfoModel> {

    private Context mContext;
    private int mResource;
    ArrayList<SongInfoModel> mEntityList;

    public EntityListAdapter(@NonNull Context context, int resource, ArrayList<SongInfoModel> entityList) {
        super(context, resource, entityList);
        mContext = context;
        mResource = resource;
        mEntityList = entityList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        String entityName = getItem(position);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource,parent,false);

        TextView entityTextView = convertView.findViewById(R.id.entityName);
        Button artistButton = convertView.findViewById(R.id.artistName);
        Button albumButton = convertView.findViewById(R.id.albumName);
        ImageView thumbnail = convertView.findViewById(R.id.thumbnail);

        final SongInfoModel songInfoModel = mEntityList.get(position);

        entityTextView.setText(songInfoModel.getTrackName());
        if(songInfoModel.getArtistName().length()<=20)
            artistButton.setText(songInfoModel.getArtistName());
        else
            artistButton.setText(songInfoModel.getArtistName().substring(0,17)+"...");

        if(songInfoModel.getCollectionName().length()<=20)
            albumButton.setText(songInfoModel.getCollectionName());
        else
            albumButton.setText(songInfoModel.getCollectionName().substring(0,17)+"...");

        try {
            URL url = new URL(songInfoModel.getThumbnailURL());
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            thumbnail.setImageBitmap(bmp);
        }
        catch (Exception e){
            e.printStackTrace();
        }



        if(mContext.getClass().getSimpleName().equals("DetailedActivity")){
            artistButton.setEnabled(false);
            albumButton.setEnabled(false);
        }

        artistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext.getApplicationContext(), DetailedActivity.class);
                String artist = songInfoModel.getArtistName();
                i.putExtra("value", artist);
                i.putExtra("title","Artist Tracks");
                mContext.startActivity(i);
            }
        });

        albumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext.getApplicationContext(), DetailedActivity.class);
                String album = songInfoModel.getCollectionName();
                i.putExtra("value", album);
                i.putExtra("title","Album Tracks");
                mContext.startActivity(i);
            }
        });

        return convertView;
    }
}
