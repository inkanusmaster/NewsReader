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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    String urlArticlesID = "https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty";
    String urlNews = "https://hacker-news.firebaseio.com/v0/item/ENTERIDHERE.json?print=pretty";
    String[] id = new String[10];
    String[] urlArticleJSON = new String[10];
    HashMap<String, String> titleUrlMap = new HashMap<>();
    String titleOfArticle, urlOfArticle;

    @SuppressLint("StaticFieldLeak")
    public class DownloadTask extends AsyncTask<String, Void, HashMap<String, String>> {
        @Override
        protected HashMap<String, String> doInBackground(String... urls) {
            StringBuilder resultArticleID = new StringBuilder();
            StringBuilder resultNewsJSON = new StringBuilder();

            URL url, url2;
            HttpURLConnection urlConnection, urlConnection2;


            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    resultArticleID.append(current);
                    data = reader.read();
                }

                JSONArray jsonURLArticlesArray = new JSONArray(resultArticleID.toString());

                for (int i = 0; i < 10; i++) {
                    id[i] = jsonURLArticlesArray.getString(i);
//                        System.out.println("ID" + i + " : " + id[i]);

                    Pattern p = Pattern.compile("item/(.*?)\\.json?");
                    Matcher m = p.matcher(urlNews);
                    if (m.find()) {

                        urlNews = urlNews.replace(m.group(1), id[i]);
                        urlArticleJSON[i] = urlNews;
                        System.out.println("ID" + i + " : " + urlArticleJSON[i]);

                        url2 = new URL(urlArticleJSON[i]);
                        urlConnection2 = (HttpURLConnection) url2.openConnection();
                        InputStream input = urlConnection2.getInputStream();
                        InputStreamReader r = new InputStreamReader(input);
                        int dat = r.read();
                        while (dat != -1) {
                            char cur = (char) dat;
                            resultNewsJSON.append(cur);
                            dat = r.read();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            try {
                Pattern pattern = Pattern.compile("\"title\" : \"(.*?)\",\n" + " .*\n" + "  \"url\" : \"(.*?)\"");
                Matcher matcher = pattern.matcher(resultNewsJSON);
                while (matcher.find()) {
                    titleUrlMap.put(matcher.group(1),matcher.group(2));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return titleUrlMap;

        }

        protected void onPostExecute(HashMap<String, String> s) {
            super.onPostExecute(s);

            System.out.println("WYNICZEK\n" + s);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downloadNews();
    }
}
