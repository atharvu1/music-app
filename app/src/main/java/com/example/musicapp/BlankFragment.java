package com.example.musicapp;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment {

    LayoutInflater mLayoutInflater;
    ViewGroup mContainer;
    RecyclerView recyclerView;
    ArrayList<SongInfoModel> entityList = new ArrayList<>();
    String mType;
    int offset=14;
    int scrolledOutItems;
    boolean isScrolling = false;
    SwipeRefreshLayout swipeRefreshLayout;

    public BlankFragment(ArrayList<SongInfoModel> entityObject,String type) {

        for(int i=0;i<entityObject.size();i++)
        {
            entityList.add(entityObject.get(i));
        }
        mType = type;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);

        mLayoutInflater=inflater;
        mContainer=container;
        recyclerView = rootView.findViewById(R.id.recyclerView);
        swipeRefreshLayout = rootView.findViewById(R.id.pullDownRefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                MainActivity obj = new MainActivity();
                int max = 14;
                int min = 7;
                int range = max - min + 1;
                int offsetOnRefresh = (int) (Math.random() * range) + min;
                try {
                    String entityResponse = obj.getApiResponse("https://itunes.apple.com/search?term="+mType+"&media="+mType+"&limit=14&offset="+offsetOnRefresh);
                    entityList.clear();
                    obj.convertStringToObjectArray(entityList, entityResponse);
                    refreshFragemnt();

                }catch (Exception e){
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrolledOutItems = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                if(isScrolling && (scrolledOutItems % 7 == 0) || (scrolledOutItems % 12 == 0)){
                    offset+=7;
                    MainActivity obj = new MainActivity();
                    try {
                        String entityResponse = obj.getApiResponse("https://itunes.apple.com/search?term="+mType+"&media="+mType+"&limit=7&offset="+offset);
                        obj.convertStringToObjectArray(entityList, entityResponse);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                isScrolling=false;
            }
        });

        return rootView;
    }

    public void createEntityComponent(ArrayList<SongInfoModel> entityList){

        EntityListAdapter entityListAdapter = new EntityListAdapter(getContext(),R.layout.entity_component,entityList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(entityListAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createEntityComponent(entityList);
    }
    public void refreshFragemnt(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
    }

    public void searchQuery(String query){
        MainActivity obj = new MainActivity();
        try {
            String entityResponse = obj.getApiResponse("https://itunes.apple.com/search?term="+query+"&media="+mType+"&limit=14");
            entityList.clear();
            obj.convertStringToObjectArray(entityList, entityResponse);
            refreshFragemnt();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
