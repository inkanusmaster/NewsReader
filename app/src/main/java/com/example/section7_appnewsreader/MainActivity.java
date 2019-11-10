package com.example.section7_appnewsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    String urlStoriesID = "https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty";
    String urlNews = "https://hacker-news.firebaseio.com/v0/item/ENTERIDHERE.json?print=pretty";
    String idStory;
    String id[] = new String[30];
    boolean dlStoryId = true;

    @SuppressLint("StaticFieldLeak")
    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result.append(current);
                    data = reader.read();
                }
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(dlStoryId){
                try {
                    JSONArray jsonURLStoriesArray = new JSONArray(s);
                    for (int i = 0; i < 30; i++) {
                        id[i] = jsonURLStoriesArray.getString(i);
                        System.out.println(id[i]);
                        dlStoryId = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //tutaj downloadNews wchodzi
                //pamietaj zmienić URL regexpem

            }
        }
    }

    public void downloadStoriesIDs() {
        DownloadTask task = new DownloadTask();
        try {
            task.execute(urlStoriesID).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void downloadNews() {
        DownloadTask task = new DownloadTask();
        try {
            task.execute(urlNews).get(); //pamiętaj zmienić URL regexpem
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downloadStoriesIDs();


    }
}
