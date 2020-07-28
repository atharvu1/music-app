package com.example.musicapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;
import java.util.List;

public class SongAdapter extends ArrayAdapter<SongInfoModel> {

    Context mCtx;
    int resource;
    List<SongInfoModel> songList;
    public SongAdapter(Context mCtx, int resource, List<SongInfoModel> songList) {
        super(mCtx, resource, songList);

        this.mCtx = mCtx;
        this.resource = resource;
        this.songList = songList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);

        View view = inflater.inflate(resource, null);

        TextView textView = view.findViewById(R.id.textViewMusic);
        Button buttonArtist = view.findViewById(R.id.buttonArtistName);
        Button buttonAlbum = view.findViewById(R.id.buttonAlbumName);
        ImageView imageView = view.findViewById(R.id.imageView);

        SongInfoModel songInfoModel = songList.get(position);

        textView.setText(songInfoModel.getTrackName());
        buttonArtist.setText(songInfoModel.getArtistName());
        buttonAlbum.setText(songInfoModel.getCollectionName());
        try {
            URL url = new URL(songInfoModel.getThumbnailURL());
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            imageView.setImageBitmap(bmp);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        buttonArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        buttonAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //return super.getView(position, convertView, parent);
        return view;
    }
}
