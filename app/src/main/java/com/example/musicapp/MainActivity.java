package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageButton searchButton;
    private EditText searchText;

    public static ArrayList<SongInfoModel> music = new ArrayList<>();
    public static ArrayList<SongInfoModel> movie = new ArrayList<>();
    public static ArrayList<SongInfoModel> podcast = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    boolean isClicked=false;
    BlankFragment musicFragment, movieFragment, podcastFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewPager);
        searchButton = findViewById(R.id.searchButton);
        searchText = findViewById(R.id.searchText);

        final String[] musicURL = {"https://itunes.apple.com/search?term=music&media=music&limit=14"};
        final String[] movieURL = {"https://itunes.apple.com/search?term=movie&media=movie&limit=14"};
        final String[] podcastsURL = {"https://itunes.apple.com/search?term=podcast&media=podcast&limit=14"};

        fetchDatafromAPI(musicURL[0], movieURL[0], podcastsURL[0]);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < 1; i++) {
                    BlankFragment fragment;
                    int currentFragmentId = viewPager.getCurrentItem();
                    switch (currentFragmentId) {
                        case 0:
                            fragment = musicFragment;
                            break;
                        case 1:
                            fragment = movieFragment;
                            break;
                        case 2:
                            fragment = podcastFragment;
                            break;
                        default:
                            fragment = null;
                    }
                    if (fragment != null) {

                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.remove(fragment);
                        fragmentTransaction.commit();

                        String query = searchText.getText().toString();
                        query.replaceAll(" ", "+");
                        query.toLowerCase();

                        String url;
                        switch (currentFragmentId) {
                            case 0:
                                url = "https://itunes.apple.com/search?term=" + query + "&media=music&limit=14";
                                break;
                            case 1:
                                url = "https://itunes.apple.com/search?term=" + query + "&media=movie&limit=14";
                                break;
                            case 2:
                                url = "https://itunes.apple.com/search?term=" + query + "&media=podcast&limit=14";
                                break;
                            default:
                                url = "null";
                        }

                        try {
                            String response = getApiResponse(url);

                            switch (currentFragmentId) {
                                case 0:
                                    music.clear();
                                    convertStringToObjectArray(music, response);
                                    break;
                                case 1:
                                    movie.clear();
                                    convertStringToObjectArray(movie, response);
                                    break;
                                case 2:
                                    podcast.clear();
                                    convertStringToObjectArray(podcast, response);
                                    break;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        setupViewPager(viewPager);
                        tabLayout.setupWithViewPager(viewPager);
                        Objects.requireNonNull(tabLayout.getTabAt(currentFragmentId)).select();
                    }
                }
            }
        });


    }

    public void fetchDatafromAPI(String musicURL,String movieURL,String podcastsURL){
        try {
            String musicResponse = getApiResponse(musicURL);
            String movieResponse = getApiResponse(movieURL);
            String podcastsResponse = getApiResponse(podcastsURL);

            music.clear();
            movie.clear();
            podcast.clear();

            convertStringToObjectArray(music, musicResponse);
            convertStringToObjectArray(movie, movieResponse);
            convertStringToObjectArray(podcast, podcastsResponse);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),100);

        musicFragment = new BlankFragment(music,"music");
        movieFragment = new BlankFragment(movie,"movie");
        podcastFragment = new BlankFragment(podcast,"podcast");

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
                String collectionName;
                if(arr.getJSONObject(i).has("collectionName"))
                    collectionName = arr.getJSONObject(i).getString("collectionName");
                else{
                    collectionName = "Unknown";
                }
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
