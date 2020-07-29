package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class DetailedActivity extends AppCompatActivity {


    List<SongInfoModel> model = new ArrayList<>();
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.progress);
        setContentView(R.layout.activity_detailed);
        Intent i = getIntent();
        String type = i.getStringExtra("type");
        String value = i.getStringExtra("value");

        //System.out.println(type + " " + value);

        value = value.replaceAll(" ", "+");
        value = value.toLowerCase();

        String URL = "https://itunes.apple.com/search?term="+value+"&limit=10";
        try {
            String res = MainActivity.getApiResponse(URL);
            MainActivity.convertStringToObjectArray(model, res);

        }catch (Exception e){
            e.printStackTrace();
        }

        /*
        Button artist = findViewById(R.id.buttonArtistName);
        Button album = findViewById(R.id.buttonAlbumName);
        album.setClickable(false);
        artist.setClickable(false);*/
        //artist.setEnabled(false);
        //album.setEnabled(false);

        listView = (ListView)findViewById(R.id.listViewSpecific);

        SongAdapter adapter = new SongAdapter(this, R.layout.song_list, model);

        listView.setAdapter(adapter);

    }

}
