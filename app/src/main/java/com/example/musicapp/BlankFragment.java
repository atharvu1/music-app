package com.example.musicapp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
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
    String tagValue;
    fragment fr;
    public BlankFragment(){

    }

    public BlankFragment(String type){
        this.mType = type;
    }
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
        Log.d("TAG", "Tag: "+this.getTag());
        //setRetainInstance(true);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof fragment){
            fr = (fragment) context;
        }else{
            throw new RuntimeException(context.toString()+" must implement FragmentData");
        }

        this.tagValue = this.getTag();

    }

    //Using onResume function for data fetching
    @Override
    public void onResume() {
        super.onResume();
        System.out.println("On Resume");
        try {
            //Thread.sleep(10000); // 10 seconds
            String res = getApiResponse("https://itunes.apple.com/search?term="+mType+"&media="+mType+"&limit=14"); // change limit to 30
            convertStringToObjectArray(entityList, res);
            System.out.println("Data fetched");
        }catch (Exception e){
            e.printStackTrace();
        }



    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(mType, this.getTag());
        System.out.println("Putting state of " + mType);
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("onCreateView", this + " " + this.mType);
        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);
        if(savedInstanceState != null)
            for(String key : savedInstanceState.keySet()){
                Log.d ("myFragment ", key + " is a key in the bundle");
            }

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
                    try {
                        String entityResponse = getApiResponse("https://itunes.apple.com/search?term="+mType+"&media="+mType+"&limit=7&offset="+offset);
                        convertStringToObjectArray(entityList, entityResponse);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                isScrolling=false;
            }
        });

        fr.sendFragmentState(mType, tagValue);
        System.out.println("Fragment created " + this.mType + " " + this);
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
        System.out.println("Activity created, list populated " + this.mType + " " + this);
    }
    public void refreshFragemnt(){
        /*BlankFragment fr1 = (BlankFragment) getFragmentManager().findFragmentByTag("android:switcher:2131231024:0");
        BlankFragment fr2 = (BlankFragment) getFragmentManager().findFragmentByTag("android:switcher:2131231024:1");
        BlankFragment fr3 = (BlankFragment) getFragmentManager().findFragmentByTag("android:switcher:2131231024:2");
        System.out.println(fr1 + " " + fr1.mType);
        System.out.println(fr2 + " " + fr2.mType);
        System.out.println(fr3 + " " + fr3.mType);*/
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
    }

    public void searchQuery(String query){

        try {
            String entityResponse = getApiResponse("https://itunes.apple.com/search?term="+query+"&media="+mType+"&limit=14");
            entityList.clear();
            convertStringToObjectArray(entityList, entityResponse);
            refreshFragemnt();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public interface fragment{
        public void sendFragmentState(String key, String value);
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
