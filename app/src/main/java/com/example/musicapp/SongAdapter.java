package com.example.musicapp;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso; //IMAGE FETCHING AND CACHING

import java.util.List;

public class SongAdapter extends ArrayAdapter<SongInfoModel> {

    Context mCtx;
    int resource;
    List<SongInfoModel> songList;
    int i;
    public SongAdapter(Context mCtx, int resource, List<SongInfoModel> songList) {
        super(mCtx, resource, songList);

        this.mCtx = mCtx;
        this.resource = resource;
        this.songList = songList;
        i = 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(resource, null);

        TextView textView = view.findViewById(R.id.textViewMusic);
        Button buttonArtist = view.findViewById(R.id.buttonArtistName);
        Button buttonAlbum = view.findViewById(R.id.buttonAlbumName);
        ImageView imageView = view.findViewById(R.id.imageView);

        final SongInfoModel songInfoModel = songList.get(position);

        textView.setText(songInfoModel.getTrackName());
        buttonArtist.setText(songInfoModel.getArtistName());
        buttonAlbum.setText(songInfoModel.getCollectionName());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Picasso.get().load(songInfoModel.getThumbnailURL()).into(imageView);


        buttonArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), DetailedActivity.class);
                String artist = songInfoModel.getArtistName();
                i.putExtra("type", "artist");
                i.putExtra("value", artist);
                mCtx.startActivity(i);
            }
        });

        buttonAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), DetailedActivity.class);
                String album = songInfoModel.getCollectionName();
                i.putExtra("type", "album");
                i.putExtra("value", album);
                mCtx.startActivity(i);
            }
        });

        //return super.getView(position, convertView, parent);
        return view;
    }
}
