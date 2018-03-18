package com.example.luong.myapplication;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    RecyclerView recyclerView;
    Adapter adapter;
    List<String> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ///////////////////


        final Button bt = (Button)findViewById(R.id.button_start);

        Log.e("Debug_Tom","Initialising");
        Log.e("Debug_Tom","Requesting root permissions..");

        ////////////////


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Boolean isRootAvailable = Shell.SU.available();
                Boolean processExists = false;
                String pid = null;
                if(isRootAvailable) {
                    List<String> out = Shell.SH.run("ps | grep tcpdump.bin");
                    Log.e("Threaddddddd", Integer.toString(out.size()));
                    if(out.size() == 1) {
                        processExists = true;
                        pid = (out.get(0).split("\\s+"))[1];
                    }
                    else if(out.size() == 0) {
                        if (loadTcpdumpFromAssets() != 0)
                            throw new RuntimeException("Copying tcpdump binary failed.");
                    }
                    else
                        throw new RuntimeException("Searching for running process returned unexpected result.");

                    /// busybox.bin

                    out = Shell.SH.run("ps | grep busybox.bin");
                    if(out.size() == 1) {
                        processExists = true;
                        pid = (out.get(0).split("\\s+"))[1];
                    }
                    else if(out.size() == 0) {
                        if (loadBusyboxFromAssets() != 0)
                            throw new RuntimeException("Copying busybox.bin binary failed.");
                    }
                    else
                        throw new RuntimeException("Searching for running process returned unexpected result.");
                }

                final Boolean processExistsFinal = processExists;
                final String pidFinal = pid;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isRootAvailable) {
                            ((TextView)findViewById(R.id.tv_status)).setText("Root permission denied or phone is not rooted!");
                            (findViewById(R.id.button_start)).setEnabled(false);
                        }
                        else {
                            if(processExistsFinal){
                                ((TextView)findViewById(R.id.tv_status)).setText("Tcpdump already running at pid: " + pidFinal );
                                bt.setText("Stop  Capture");
                                bt.setTag(1);
                            }
                            else {
                                ((TextView)findViewById(R.id.tv_status)).setText("Initialization Successful.");
                                bt.setTag(0);
                            }
                        }
                    }
                });

            }
        };
        new Thread(runnable).start();



        /////////////////
        recyclerView = (RecyclerView) findViewById(R.id.rycycleView);

        adapter = new Adapter(this);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        list = new ArrayList<>();
        //list.add("Dat");
        //adapter.setList(list);
        //recyclerView.setAdapter(adapter);

   //     textView = (TextView) findViewById(R.id.tv_name);

     //   Log.e("TextView Content", textView.getText() + "");
        String cmd_url = "http://192.168.0.103:9200/test01/_search";
        new FetchData().execute(cmd_url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting: {
                Log.e("OnclickSetting", "Hello World Setting");
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private class FetchData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                Log.e("Dang xu ly", "WTF");
                HttpURLConnection urlConnection = null;
                StringBuffer stringBuffer = null;
                URL url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line ="";
                stringBuffer = new StringBuffer();
                while ((line = reader.readLine()) != null){
                    stringBuffer.append(line);
                }
                return stringBuffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
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
            recyclerView.setAdapter(adapter);
            super.onPostExecute(s);
        }
    }


    public void  startCapture(View v) {
        Button bt = (Button)findViewById(R.id.button_start);
        bt.setEnabled(false);
        if((int)bt.getTag() == 1){
            //Using progress dialogue from main. See comment in: TcpdumpPacketCapture.stopTcpdumpCapture
            Log.e("Debug_Tom","Killing Tcpdump && Busybox process.");
            TcpdumpPacketCapture.stopTcpdumpCapture(this);

            bt.setText("Start Capture");
            bt.setTag(0);
            ((TextView)findViewById(R.id.tv_status)).setText("Packet capture stopped");

        }
        else if ((int)bt.getTag() == 0){
            TcpdumpPacketCapture.initialiseCapture(this);
            bt.setText("Stop  Capture");
            bt.setTag(1);
        }
        bt.setEnabled(true);
    }

    public void stopAndExitActivity(View v) {
        TcpdumpPacketCapture.stopTcpdumpCapture(this);
        finish();
    }

    private int loadTcpdumpFromAssets(){
        int retval = 0;
        // updating progress message from other thread causes exception.
        // progressbox.setMessage("Setting up data..");
        String rootDataPath = getApplicationInfo().dataDir + "/files";
        String filePath = rootDataPath + "/tcpdump.bin";
        File file = new File(filePath);
        AssetManager assetManager = getAssets();

        try{
            if (file.exists()) {
                Shell.SH.run("chmod 755 " + filePath);
                return retval;
            }
            new File(rootDataPath).mkdirs();
            retval = copyFileFromAsset(assetManager, "tcpdump.bin", filePath);
            // Mark the binary executable
            Shell.SH.run("chmod 755 " + filePath);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            retval = -1;
        }
        return retval;
    }

    private int loadBusyboxFromAssets(){
        int retval = 0;
        // updating progress message from other thread causes exception.
        // progressbox.setMessage("Setting up data..");
        String rootDataPath = getApplicationInfo().dataDir + "/files";
        String filePath = rootDataPath + "/busybox.bin";
        File file = new File(filePath);
        AssetManager assetManager = getAssets();

        try{
            if (file.exists()) {
                Shell.SH.run("chmod 755 " + filePath);
                return retval;
            }
            new File(rootDataPath).mkdirs();
            retval = copyFileFromAsset(assetManager, "busybox.bin", filePath);
            // Mark the binary executable
            Shell.SH.run("chmod 755 " + filePath);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            retval = -1;
        }
        return retval;
    }

    private int copyFileFromAsset(AssetManager assetManager, String sourcePath, String destPath) {
        byte[] buff = new byte[1024];
        int len;
        InputStream in;
        OutputStream out;
        try {
            in = assetManager.open(sourcePath);
            new File(destPath).createNewFile();
            out = new FileOutputStream(destPath);
            // write file
            while((len = in.read(buff)) != -1){
                out.write(buff, 0, len);
            }
            in.close();
            out.flush();
            out.close();
        }
        catch(Exception ex) {
            ex.printStackTrace();
            return -1;
        }
        return 0;
    }

}