package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageButton searchButton;
    private EditText searchText;

    ArrayList<SongInfoModel> music = new ArrayList<>();
    ArrayList<SongInfoModel> movie = new ArrayList<>();
    ArrayList<SongInfoModel> podcast = new ArrayList<>();

    BlankFragment musicFragment, movieFragment, podcastFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchButton = findViewById(R.id.searchButton);
        searchText = findViewById(R.id.searchText);

        final String[] musicURL = {"https://itunes.apple.com/search?term=music&media=music&limit=10"};
        final String[] movieURL = {"https://itunes.apple.com/search?term=movie&media=movie&limit=10"};
        final String[] podcastsURL = {"https://itunes.apple.com/search?term=podcast&media=podcast&limit=10"};

        fetchDatafromAPI(musicURL[0], movieURL[0], podcastsURL[0]);

        /* Below onClick not working, actually fetching but not updating the fragment */
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = searchText.getText().toString();
                query.replaceAll(" ", "+");
                query.toLowerCase();
                musicURL[0] = "https://itunes.apple.com/search?term="+query+"&media=music&limit=10";
                movieURL[0] = "https://itunes.apple.com/search?term="+query+"&media=movie&limit=10";
                podcastsURL[0] = "https://itunes.apple.com/search?term="+query+"&media=podcast&limit=10";
                fetchDatafromAPI(musicURL[0],movieURL[0],podcastsURL[0]);
            }
        });

//        try {
//            String movieURL = "https://itunes.apple.com/search?term=movie&limit=10";
//            String musicURL = "https://itunes.apple.com/search?term=music&limit=10";
//            String podcastsURL = "https://itunes.apple.com/search?term=podcast&limit=10";
//
//
//            long start = System.currentTimeMillis();
//
//            String musicResponse = getApiResponse(musicURL);
//            String movieResponse = getApiResponse(movieURL);
//            String podcastsResponse = getApiResponse(podcastsURL);
//
//            long end = System.currentTimeMillis();
//            long elapsedTime = end - start;
//            Log.d("TAG", "Time elapsed: getApiResponse: "+elapsedTime);
//
//
//
//            start = System.currentTimeMillis();
//
//            convertStringToObjectArray(music, musicResponse);
//            convertStringToObjectArray(movie, movieResponse);
//            convertStringToObjectArray(podcast, podcastsResponse);
//
//            end = System.currentTimeMillis();
//            elapsedTime = end - start;
//            Log.d("TAG", "Time elapsed: convertStringToOBjectArray: "+elapsedTime);
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//
//        tabLayout = findViewById(R.id.tablayout);
//        viewPager = findViewById(R.id.viewPager);
//
//        setupViewPager(viewPager);
//        tabLayout.setupWithViewPager(viewPager);

    }

    private void fetchDatafromAPI(String musicURL,String movieURL,String podcastsURL){
        try {
            String musicResponse = getApiResponse(musicURL);
            String movieResponse = getApiResponse(movieURL);
            String podcastsResponse = getApiResponse(podcastsURL);

            convertStringToObjectArray(music, musicResponse);
            convertStringToObjectArray(movie, movieResponse);
            convertStringToObjectArray(podcast, podcastsResponse);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewPager);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),100);

        musicFragment = new BlankFragment(music);
        movieFragment = new BlankFragment(movie);
        podcastFragment = new BlankFragment(podcast);

        viewPagerAdapter.addFragment(musicFragment,"Music");
        viewPagerAdapter.addFragment(movieFragment,"Movies");
        viewPagerAdapter.addFragment(podcastFragment,"Podcasts");

        viewPager.setAdapter(viewPagerAdapter);
    }

    public static String getApiResponse(String URL) throws IOException {
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
    public static void convertStringToObjectArray(ArrayList<SongInfoModel> mod, String s){
        try{
            JSONObject obj = new JSONObject(s);
            JSONArray arr = obj.getJSONArray("results"); // working
            for(int i = 0; i < arr.length(); i++){
                String trackName = arr.getJSONObject(i).getString("trackName");
                String artistName = arr.getJSONObject(i).getString("artistName");
                String collectionName = arr.getJSONObject(i).getString("collectionName");
//                Log.d("one: ",collectionName);
//                Log.d("two",arr.getJSONObject(i).getString("collectionName"));
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
