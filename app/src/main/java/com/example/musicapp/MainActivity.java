package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

import org.json.*;


public class MainActivity extends AppCompatActivity{

    private Button button;
    OkHttpClient client = new OkHttpClient();
    ArrayList<SongInfoModel> music = new ArrayList<>();
    ArrayList<SongInfoModel> movie = new ArrayList<>();
    ArrayList<SongInfoModel> podcasts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.buttonJSON);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView t = (TextView)findViewById(R.id.jsonResponse);
                t.setText("Button Clicked");
                try {

                    String movieURL = "https://itunes.apple.com/search?term=movie";
                    String musicURL = "https://itunes.apple.com/search?term=music";
                    String podcastsURL = "https://itunes.apple.com/search?term=podcast";

                    String musicResponse = getApiResponse(musicURL);
                    String movieResponse = getApiResponse(movieURL);
                    String podcastsResponse = getApiResponse(podcastsURL);

                    convertStringToObjectArray(music, musicResponse);
                    convertStringToObjectArray(movie, movieResponse);
                    convertStringToObjectArray(podcasts, podcastsResponse);

                    //System.out.println(music);
                    System.out.println(music);

                    for(SongInfoModel s : music){
                        System.out.println("-------------");
                        System.out.println("Artist Name : " + s.artistName);
                        System.out.println("Track Name : " + s.trackName);
                        System.out.println("Collection Name : " + s.collectionName);
                        System.out.println("Thumbnail URL : " + s.thumbnailURL);
                    }


                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    public String getApiResponse(String URL) throws  IOException{

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Request request = new Request.Builder().url(URL).build();
            Response response = client.newCall(request).execute();

            return response.body().string();

        }

        return null;
    }


    public void convertStringToObjectArray(ArrayList<SongInfoModel> mod, String s){
            try{
                JSONObject obj = new JSONObject(s);
                JSONArray arr = obj.getJSONArray("results"); // working

                for(int i = 0; i < arr.length(); i++){

                    String trackName = arr.getJSONObject(i).getString("trackName");
                    String artistName = arr.getJSONObject(i).getString("artistName");
                    String collectionName = arr.getJSONObject(i).getString("collectionName");
                    String thumbnailURL = arr.getJSONObject(i).getString("artworkUrl100");
                    SongInfoModel model = new SongInfoModel(trackName, artistName, collectionName, thumbnailURL);
                    mod.add(model);

                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
    }

}
