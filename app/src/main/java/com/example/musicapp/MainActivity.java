package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import org.json.*;

public class MainActivity extends AppCompatActivity{
    private Button button;
    OkHttpClient client = new OkHttpClient();
    //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy().Builder().permit
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.buttonJSON);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView t = (TextView)findViewById(R.id.jsonResponse);
                t.setText("Button Clicked");
                try {

                    String movieURL = "https://itunes.apple.com/search?term=movie";
                    String musicURL = "https://itunes.apple.com/search?term=music";
                    String podcastURL = "https://itunes.apple.com/search?term=podcast";

                    String URL = musicURL;

                    String s = getApiResponse(URL);
                    //System.out.println(s);
                    //t.setText(s);
                    JSONObject obj = new JSONObject(s);
                    JSONArray arr = obj.getJSONArray("results"); // working
                    //t.setText();
                    //JSONObject ob = new JSONObject(arr.getInt(0));

                    //System.out.println();


                    for(int i = 0; i < arr.length(); i++){
                        String trackName = arr.getJSONObject(i).getString("trackName");
                        String artistName = arr.getJSONObject(i).getString("artistName");
                        String collectionName = arr.getJSONObject(i).getString("collectionName");
                        System.out.println("---------------------------");
                        System.out.println("Artist Name :" + artistName + "\nTrack Name :  " + trackName + "\nCollection Name : " + collectionName + "\n");
                        System.out.println("---------------------------");
                    }


                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    public String getApiResponse(String URL) throws  IOException{

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Request request = new Request.Builder().url(URL).build();
            Response response = client.newCall(request).execute();

            return response.body().string();

        }

        return null;
    }


}
