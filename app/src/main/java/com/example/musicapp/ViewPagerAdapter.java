package com.example.musicapp;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<Fragment> fragments = new ArrayList<>();
    private final ArrayList<String> titles = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return (Fragment) fragments.get(position);
    }

    @Override
    public int getCount() {

        //return titles.size();
        return 3;
    }

    public void addFragment(BlankFragment fragment,String title){ //Type changed for fragment
        fragments.add(fragment);
        titles.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        switch(position){
            case 0:
                title = "Music";
                break;
            case 1:
                title = "Movies";
                break;
            case 2:
                title = "Podcasts";
                break;
            default:
                title = "Null";
        }
        //return titles.get(position);
        return title;
    }
}
