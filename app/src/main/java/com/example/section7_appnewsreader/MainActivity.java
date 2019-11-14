package com.example.section7_appnewsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    String urlArticlesID = "https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty";
    String urlNews = "https://hacker-news.firebaseio.com/v0/item/ENTERIDHERE.json?print=pretty";
    String[] id = new String[30];
    String[] urlArticleJSON = new String[30];
    HashMap<String, String> titleUrlMap = new HashMap<>();
    SQLiteDatabase newsDatabase;
    ListView titlesListView;
    final ArrayList<String> titlesArrayList = new ArrayList<>();
    ProgressDialog loading;

    @SuppressLint("StaticFieldLeak")
    public class DownloadTask extends AsyncTask<String, Void, HashMap<String, String>> {

        protected void onPreExecute() {
            loading= new ProgressDialog(MainActivity.this);
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.setTitle("Loading content");
            loading.setMessage("Please wait...");
            loading.setIndeterminate(true);
            loading.setCanceledOnTouchOutside(false);
            loading.show();
        }

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

                for (int i = 0; i < 30; i++) {
                    id[i] = jsonURLArticlesArray.getString(i);

                    Pattern p = Pattern.compile("item/(.*?)\\.json?");
                    Matcher m = p.matcher(urlNews);
                    if (m.find()) {

                        urlNews = urlNews.replace(m.group(1), id[i]);
                        urlArticleJSON[i] = urlNews;

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
                    titleUrlMap.put(matcher.group(1), matcher.group(2));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return titleUrlMap;
        }

        protected void onPostExecute(HashMap<String, String> hashMap) {
            super.onPostExecute(hashMap);
            loading.dismiss();
            try {
                Set entries = titleUrlMap.entrySet();
                Iterator iterator = entries.iterator();
                ContentValues contentValues = new ContentValues();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    Object title = entry.getKey();
                    Object url = entry.getValue();
                    contentValues.put("title", String.valueOf(title));
                    contentValues.put("url", String.valueOf(url));
                    newsDatabase.insert("news", null, contentValues);
                }

                @SuppressLint("Recycle") Cursor c = newsDatabase.rawQuery("SELECT * FROM news", null);
                int titleIndex = c.getColumnIndex("title");
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    titlesArrayList.add(c.getString(titleIndex));
                    c.moveToNext();
                }
                titlesListView = findViewById(R.id.titlesListView);
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, titlesArrayList);
                titlesListView.setAdapter(arrayAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }


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

    public void createDatabase() {
        newsDatabase = this.openOrCreateDatabase("News", MODE_PRIVATE, null);
        newsDatabase.execSQL("CREATE TABLE IF NOT EXISTS news (id INTEGER PRIMARY KEY, title VARCHAR, url VARCHAR)");
        newsDatabase.execSQL("DELETE FROM news");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createDatabase();
        downloadNews();

    }
}
