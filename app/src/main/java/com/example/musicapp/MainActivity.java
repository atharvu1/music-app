package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
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
    static MixpanelAPI mixpanel;
    public static final String MIXPANEL_TOKEN = Token.MIXPANEL_TOKEN;

    // Initialize the library with your
    // Mixpanel project token, MIXPANEL_TOKEN, and a reference
    // to your application context.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        map = new HashMap<>();
        setContentView(R.layout.activity_main);

        /* *******************************************Mix Panel dummy event*******************************************/
        mixpanel = MixpanelAPI.getInstance(this, MIXPANEL_TOKEN);
        try {
            JSONObject props = new JSONObject();
            props.put("User Name", Token.USERNAME);
            mixpanel.registerSuperProperties(props);
            props.put("Current Activity", "MainActivity");
            String orientation = "";
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                orientation = "PORTRAIT";
            } else {
                orientation = "LANDSCAPE";
            }
            props.put("Initial Orientation", orientation);
            mixpanel.track("Activity Tracking", props);
            mixpanel.flush();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /* *******************************************End***********************************************************/


        this.tabLayout = findViewById(R.id.tablayout);
        this.viewPager = findViewById(R.id.viewPager);
        this.searchButton = findViewById(R.id.searchButton);
        this.searchText = findViewById(R.id.searchText);

        if(savedInstanceState != null){
            System.out.println("In Main having saved instances");
        }

        else{
            tabLayout.setupWithViewPager(viewPager);
            setupViewPager(viewPager);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //System.out.println("Tab Selected " + tab.getPosition());
                String tabName = (String) viewPagerAdapter.getPageTitle(tab.getPosition());
                JSONObject props = new JSONObject();
                try {
                    props.put("Current Tab",tabName );
                    mixpanel.track("Tab Switch Event", props);
                    mixpanel.flush();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentFragmentId = viewPager.getCurrentItem();
                String query = searchText.getText().toString();
                query.replaceAll(" ", "+");
                query.toLowerCase();

                //keyboard auto hiding
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);

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
        for(String key : savedInstanceState.keySet()){
            Log.d ("myApplication ", key + " is a key in the bundle");
        }
        map = (HashMap<String, String>) savedInstanceState.getSerializable("stateMap");
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager){
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),100);

        musicFragment = new BlankFragment("music");
        movieFragment = new BlankFragment("movie");
        podcastFragment = new BlankFragment("podcast");

        System.out.println("Music Ref : " + musicFragment);
        System.out.println("Movie Ref : " + movieFragment);
        System.out.println("Podcast Ref : " + podcastFragment);

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
        //Log.d("Frag tag", key + " " + value);
        map.put(key, value);
    }

    @Override
    protected void onDestroy() {
        mixpanel.flush();
        super.onDestroy();
    }
}