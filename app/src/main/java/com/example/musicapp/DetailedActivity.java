package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;;
import android.widget.AbsListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailedActivity extends AppCompatActivity {

    ArrayList<SongInfoModel> model = new ArrayList<>();
    RecyclerView listView;
    TextView title;

    int offset=14;
    int scrolledOutItems;
    boolean isScrolling = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        title = findViewById(R.id.title);

        Intent i = getIntent();
        title.setText(i.getStringExtra("title"));
        String s = i.getStringExtra("value");
        s = s.replaceAll(" ", "+");
        s = s.toLowerCase();
        String URL = "https://itunes.apple.com/search?term="+ s +"&limit=14";
        try {
            String res = MainActivity.getApiResponse(URL);
            MainActivity.convertStringToObjectArray(model, res);
        }catch (Exception e){
            e.printStackTrace();
        }

        listView = findViewById(R.id.listViewSpecific);

        EntityListAdapter adapter = new EntityListAdapter(this, R.layout.entity_component, model);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(mLayoutManager);
        listView.setAdapter(adapter);

        final String finalS = s;
        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        String entityResponse = obj.getApiResponse("https://itunes.apple.com/search?term="+ finalS +"&limit=7&offset="+offset);
                        obj.convertStringToObjectArray(model, entityResponse);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                isScrolling=false;
            }
        });

    }
}
