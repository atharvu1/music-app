package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.Settings;
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
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements BlankFragment.fragment{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageButton searchButton;
    private EditText searchText;
    HashMap<String, String> map;

    ViewPagerAdapter viewPagerAdapter;
    BlankFragment musicFragment, movieFragment, podcastFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        map = new HashMap<>();
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null){
            System.out.println("In Main having saved instances");
        }

        else{

            System.out.println("Creating for the first time");
            tabLayout = findViewById(R.id.tablayout);
            viewPager = findViewById(R.id.viewPager);
            searchButton = findViewById(R.id.searchButton);
            searchText = findViewById(R.id.searchText);
            final String[] musicURL = {"https://itunes.apple.com/search?term=music&media=music&limit=14"};
            final String[] movieURL = {"https://itunes.apple.com/search?term=movie&media=movie&limit=14"};
            final String[] podcastsURL = {"https://itunes.apple.com/search?term=podcast&media=podcast&limit=14"};

            tabLayout.setupWithViewPager(viewPager);
            setupViewPager(viewPager);



            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                int currentFragmentId = viewPager.getCurrentItem();
                System.out.println("Current Fragment item : " + currentFragmentId);
                String query = searchText.getText().toString();
                System.out.println("Search text : " + query);
                query.replaceAll(" ", "+");
                query.toLowerCase();
                switch (currentFragmentId) {
                    case 0:
                        musicFragment.searchQuery(query);
                        break;
                    case 1:
                        movieFragment.searchQuery(query);
                        break;
                    case 2:
                        podcastFragment.searchQuery(query);
                        break;
                    default:
                        System.out.println("No fragment found");
                }

                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("stateMap", map);
        System.out.println("Saving instances in MainActivity");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewPager);
        searchButton = findViewById(R.id.searchButton);
        searchText = findViewById(R.id.searchText);
        System.out.println("Recreating");
        for(String key : savedInstanceState.keySet()){
            Log.d ("myApplication ", key + " is a key in the bundle");
        }
        map = (HashMap<String, String>) savedInstanceState.getSerializable("stateMap");
        System.out.println(map.get("music"));
        System.out.println(map.get("movie"));
        System.out.println(map.get("podcast"));

        musicFragment = (BlankFragment) getSupportFragmentManager().findFragmentByTag(map.get("music"));
        movieFragment = (BlankFragment) getSupportFragmentManager().findFragmentByTag(map.get("movie"));
        podcastFragment = (BlankFragment) getSupportFragmentManager().findFragmentByTag(map.get("podcast"));


        System.out.println("Frag Obj " + musicFragment);
        System.out.println("Frag Obj " + movieFragment);
        System.out.println("Frag Obj " + podcastFragment);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }


    private void setupViewPager(ViewPager viewPager){
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),100);

        musicFragment = new BlankFragment("music");
        movieFragment = new BlankFragment("movie");
        podcastFragment = new BlankFragment("podcast");

        System.out.println("Music Ref : " + musicFragment);
        System.out.println("Movie Ref : " + movieFragment);
        System.out.println("Podcast Ref : " + podcastFragment);

        //System.out.println(musicFragment.mType);

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


    @Override
    public void sendFragmentState(String key, String value) {
        Log.d("Frag tag", key + " " + value);
        map.put(key, value);
    }
}
