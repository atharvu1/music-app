package com.example.musicapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.w3c.dom.Entity;

import java.util.ArrayList;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment {

    LayoutInflater mLayoutInflater;
    ViewGroup mContainer;
    ListView listView;
    ArrayList<String> dummyList = new ArrayList<>();

    public BlankFragment(ArrayList<Object> entityObject) {

        for(int i=0;i<entityObject.size();i++)
        {
            dummyList.add((String) entityObject.get(i));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);

        mLayoutInflater=inflater;
        mContainer=container;
        listView = rootView.findViewById(R.id.listView);

        return rootView;
    }

    public void createEntityComponent(ArrayList<String> dummyList){

        EntityListAdapter entityListAdapter = new EntityListAdapter(getContext(),R.layout.entity_component,dummyList);
        listView.setAdapter(entityListAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createEntityComponent(dummyList);
    }
}
