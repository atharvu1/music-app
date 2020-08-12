package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
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
        this.tabLayout = findViewById(R.id.tablayout);
        this.viewPager = findViewById(R.id.viewPager);
        this.searchButton = findViewById(R.id.searchButton);
        this.searchText = findViewById(R.id.searchText);

        if(savedInstanceState != null){
            System.out.println("In Main having saved instances");
        }

        else{

            System.out.println("Creating for the first time");
            final String[] musicURL = {"https://itunes.apple.com/search?term=music&media=music&limit=14"};
            final String[] movieURL = {"https://itunes.apple.com/search?term=movie&media=movie&limit=14"};
            final String[] podcastsURL = {"https://itunes.apple.com/search?term=podcast&media=podcast&limit=14"};

            tabLayout.setupWithViewPager(viewPager);
            setupViewPager(viewPager);
        }

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
                        musicFragment = (BlankFragment) getSupportFragmentManager().findFragmentByTag(map.get("music"));
                        musicFragment.searchQuery(query);
                        musicFragment.refreshFragemnt();
                        break;
                    case 1:
                        movieFragment = (BlankFragment) getSupportFragmentManager().findFragmentByTag(map.get("movie"));
                        movieFragment.searchQuery(query);
                        movieFragment.refreshFragemnt();
                        break;
                    case 2:
                        podcastFragment = (BlankFragment) getSupportFragmentManager().findFragmentByTag(map.get("podcast"));
                        podcastFragment.searchQuery(query);
                        podcastFragment.refreshFragemnt();
                        break;
                    default:
                        System.out.println("No fragment found");
                }

            }
        });
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
//        System.out.println("Testing000:");
//        System.out.println(map.get("music"));
//        System.out.println(map.get("movie"));
//        System.out.println(map.get("podcast"));
//
//        musicFragment = (BlankFragment) getSupportFragmentManager().findFragmentByTag(map.get("music"));
//        movieFragment = (BlankFragment) getSupportFragmentManager().findFragmentByTag(map.get("movie"));
//        podcastFragment = (BlankFragment) getSupportFragmentManager().findFragmentByTag(map.get("podcast"));
//
//
//        System.out.println("Frag Obj " + musicFragment);
//        System.out.println("Frag Obj " + movieFragment);
//        System.out.println("Frag Obj " + podcastFragment);

        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);
    }

    /*public void fetchDatafromAPI(String musicURL, String movieURL, String podcastsURL){
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

        *//*setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);*//*
        System.out.println("All done");
    }*/


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
/*
class FetchData extends AsyncTask<Void, Void, Void> {
    Context mActivity;
    ViewPagerAdapter viewPagerAdapter;
    ViewPager viewPager;
    BlankFragment musicFragment, movieFragment, podcastFragment;
    private TabLayout tabLayout;


    public FetchData(Context activity, ViewPager viewPager, ViewPagerAdapter viewPagerAdapter, TabLayout tabLayout1) {
        mActivity = activity;
        this.viewPager = viewPager;
        this.viewPagerAdapter = viewPagerAdapter;
        tabLayout = tabLayout1;

    }

    @Override
    protected Void doInBackground(Void... voids) {
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
        System.out.println("Setting viewpager completed");
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        tabLayout.setupWithViewPager(viewPager);
        super.onPostExecute(aVoid);

    }
    private void setupViewPager(ViewPager viewPager){
        System.out.println("Setting viewpager");
    }

}
*/
