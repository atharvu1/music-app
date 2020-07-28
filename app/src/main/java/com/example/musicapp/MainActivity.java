package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewPager);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),100);

        ArrayList<Object> musicEntityObjectArray = new ArrayList<>();
        ArrayList<Object> movieEntityObjectArray = new ArrayList<>();
        ArrayList<Object> podcastEntityObjectArray = new ArrayList<>();

        /* Data Fetching and object creation area: OPEN */




        /* Data Fetching and object creation area: CLOSE */

        musicEntityObjectArray.add("Music 1");
        movieEntityObjectArray.add("Movie 1");
        podcastEntityObjectArray.add("Podcast 1");

        viewPagerAdapter.addFragment(new BlankFragment(musicEntityObjectArray),"Music");
        viewPagerAdapter.addFragment(new BlankFragment(movieEntityObjectArray),"Movies");
        viewPagerAdapter.addFragment(new BlankFragment(podcastEntityObjectArray),"Podcasts");
        viewPager.setAdapter(viewPagerAdapter);
    }
}
