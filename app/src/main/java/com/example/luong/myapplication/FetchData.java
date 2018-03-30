package com.example.luong.myapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luong on 3/18/2018.
 */

public class FetchData extends AsyncTask<String, String, String> {

    Activity contextParent;
    Adapter adapter;
    RecyclerView recyclerView;

    public FetchData(Activity contextParent, Adapter adapter, RecyclerView recyclerView) {
        this.contextParent = contextParent;
        this.adapter = adapter;
        this.recyclerView = recyclerView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(contextParent, "Starting fetch json data..", Toast.LENGTH_LONG).show();
    }

    protected String doInBackground(String... strings) {
        while (true) {
            Log.e("Background::","Fetching data from server..");
            try {
                Log.e("Dang xu ly", strings[0]);
                HttpURLConnection urlConnection = null;
                StringBuffer stringBuffer = null;

                URL url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                stringBuffer = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                publishProgress(stringBuffer.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        List<String> list = new ArrayList<>();
        String s = values[0];
        Log.e("Json Result", s);
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONObject("hits").getJSONArray("hits");
            Log.e("ArrLen", Integer.toString(jsonArray.length()));
            for(int i = 0; i < jsonArray.length(); i++) {
                Log.e("ii:", Integer.toString(i));
                Log.e("JSon Resultmm", jsonArray.getJSONObject(i).getJSONObject("_source").getString("resp_ht"));
                list.add(jsonArray.getJSONObject(i).getJSONObject("_source").getString("resp_ht").toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Set list && Adapter

        adapter.setList(list);
        recyclerView.swapAdapter(adapter, true);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Toast.makeText(contextParent, "Okie, Finished", Toast.LENGTH_SHORT).show();

    }
}