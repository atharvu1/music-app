package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.*;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    ListView listView;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        System.out.println("--------------------End onCreate-----------------");
        listView = (ListView)findViewById(R.id.listView);

        new FetchData(MainActivity.this,listView, progressBar).execute();
    }
    public static String getApiResponse(String URL) throws  IOException{
        OkHttpClient client = new OkHttpClient();
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


    public static void convertStringToObjectArray(List<SongInfoModel> mod, String s){
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
class FetchData extends AsyncTask<Void, Void, Void>{
    List<SongInfoModel> music = new ArrayList<>();
    List<SongInfoModel> movie = new ArrayList<>();
    List<SongInfoModel> podcasts = new ArrayList<>();
    ListView listView;
    ProgressBar progressBar;
    Context mActivity;

    public FetchData(Context activity, ListView listview, ProgressBar progressbar) {
        mActivity = activity;
        listView = listview;
        progressBar = progressbar;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        fetchData();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        SongAdapter adapter = new SongAdapter(mActivity, R.layout.song_list, music);
        progressBar.setVisibility(View.GONE);
        listView.setAdapter(adapter);
    }


    public void fetchData(){

        System.out.println("Fetching data");
        try {
            String movieURL = "https://itunes.apple.com/search?term=movie&limit=25";
            String musicURL = "https://itunes.apple.com/search?term=music&limit=25";
            String podcastsURL = "https://itunes.apple.com/search?term=podcast&limit=10";

            String musicResponse = MainActivity.getApiResponse(musicURL);
            String movieResponse = MainActivity.getApiResponse(movieURL);
            String podcastsResponse = MainActivity.getApiResponse(podcastsURL);

            MainActivity.convertStringToObjectArray(music, musicResponse);
            MainActivity.convertStringToObjectArray(movie, movieResponse);
            MainActivity.convertStringToObjectArray(podcasts, podcastsResponse);

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


}
