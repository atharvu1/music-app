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

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
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
    String mQuery;
    int offset=30;
    int scrolledOutItems;
    boolean isScrolling = false;
    SwipeRefreshLayout swipeRefreshLayout;
    String tagValue;
    fragment fr;
    String res;
    ArrayList<String> responses = new ArrayList<>();
    boolean createdFragment;
    ProgressBar progressBar;
    public BlankFragment() {
        createdFragment = true;
    }

    public BlankFragment(String type){
        this.mType = type;
        this.mQuery = type;
        this.createdFragment = true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d("TAG", "Tag: "+this.getTag());
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
        if(createdFragment){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(mType!=null) {
                            res = getApiResponse("https://itunes.apple.com/search?term=" + mQuery + "&media=" + mType + "&limit=30"); // change limit to 30
                            convertStringToObjectArray(entityList, res);
                            responses.add(res);
                            refreshFragemnt();
                        }
                        createdFragment = false;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }).start();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(mType, this.getTag());
        outState.putString("type",mType);
        outState.putString("query",mQuery);
        outState.putStringArrayList("responses",responses);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Log.d("onCreateView", this + " " + this.mType);
        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);

        if(savedInstanceState != null){

            mType=savedInstanceState.getString("type");
            mQuery=savedInstanceState.getString("query");

            if(savedInstanceState.getStringArrayList("responses")!=null){
                System.out.println("Yes, break!");
            }

            entityList.clear();
            for(String ent: savedInstanceState.getStringArrayList("responses")){
                convertStringToObjectArray(entityList, ent);
                System.out.println("-------------------------------------------");
                System.out.println(ent);
                System.out.println("-------------------------------------------");
            }
        }

        mLayoutInflater=inflater;
        mContainer=container;
        recyclerView = rootView.findViewById(R.id.recyclerView);
        swipeRefreshLayout = rootView.findViewById(R.id.pullDownRefresh);
        progressBar = rootView.findViewById(R.id.progressBarFragment);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                offset=30;
                MainActivity obj = new MainActivity();
                int max = 20;
                int min = 10;
                int range = max - min + 1;
                int offsetOnRefresh = (int) (Math.random() * range) + min;
                try {
                    String entityResponse = obj.getApiResponse("https://itunes.apple.com/search?term="+mQuery+"&media="+mType+"&limit=30&offset="+offsetOnRefresh);
                    entityList.clear();
                    responses.clear();
                    responses.add(entityResponse);
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
                //Log.d("TAG", "onScrolled: "+(scrolledOutItems+20)+", offset: "+offset);
                if(isScrolling && ((scrolledOutItems+20) > offset)){
                    offset+=10;
                    try {
                        String entityResponse = getApiResponse("https://itunes.apple.com/search?term="+mQuery+"&media="+mType+"&limit=10&offset="+offset);
                        convertStringToObjectArray(entityList, entityResponse);
                        responses.add(entityResponse);
                        refreshFragemnt();
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
    }

    public void refreshFragemnt(){
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
    }

    public void searchQuery(String query){
        try {
            String entityResponse = getApiResponse("https://itunes.apple.com/search?term="+query+"&media="+mType+"&limit=30");
            JSONObject ob = new JSONObject(entityResponse);
            int count = ob.getInt("resultCount");
            if(count == 0){
                Toast.makeText(getContext(), "No result found, Showing previous entries", Toast.LENGTH_LONG).show();
            }
            else {
                entityList.clear();
                responses.clear();
                responses.add(entityResponse);
                mQuery = query;
                convertStringToObjectArray(entityList, entityResponse);
                refreshFragemnt();
            }
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
