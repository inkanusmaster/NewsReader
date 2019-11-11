package com.example.section7_appnewsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    String urlArticlesID = "https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty";
    String urlNews = "https://hacker-news.firebaseio.com/v0/item/ENTERIDHERE.json?print=pretty";
    String[] id = new String[10];
    boolean dlArticleIdEqualsTrue = true;

    @SuppressLint("StaticFieldLeak")
    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection;

//           PRZESLIJ DO ONPOSTEXECUTE ID ARTYKULOW
//           A W ONPOSTEXECUTE OGARNIJ TE 10 ARTYKULOW Z ODPOWIEDNIM ID (REGEXP) I PARSUJ JSONA
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
            if (dlArticleIdEqualsTrue) {
                try {
                    JSONArray jsonURLArticlesArray = new JSONArray(s);
                    for (int i = 0; i < 10; i++) {
                        id[i] = jsonURLArticlesArray.getString(i);
                        System.out.println(id[i]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } //else {
//                try {
//                    System.out.println("TEST ID[0] w else try " + id[0]);
//                    JSONObject jsonNews = new JSONObject(s);
//                    title = jsonNews.getString("title");
//                    System.out.println(title);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
        }

    }

    public void downloadNews() {
        DownloadTask task = new DownloadTask();
        try {
            task.execute(urlArticlesID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void downloadNews() {
//        System.out.println("TEST ID[0] w downloadnewsaaa " + Arrays.toString(id));
//        DownloadTask task = new DownloadTask();
//        try {
//            Pattern p = Pattern.compile("item/(.*?)\\.json?");
//            Matcher m = p.matcher(urlNews);
//            if (m.find()) {
//                urlNews = urlNews.replace(m.group(1), id[0]);
//                task.execute(urlNews).get();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downloadNews();
    }
}
