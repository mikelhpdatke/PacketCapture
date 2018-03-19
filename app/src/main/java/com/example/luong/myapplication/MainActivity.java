package com.example.luong.myapplication;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    Adapter adapter = new Adapter(this);
    List<String> list = new List<String>() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @NonNull
        @Override
        public Iterator<String> iterator() {
            return null;
        }

        @NonNull
        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @NonNull
        @Override
        public <T> T[] toArray(@NonNull T[] a) {
            return null;
        }

        @Override
        public boolean add(String s) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(@NonNull Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(@NonNull Collection<? extends String> c) {
            return false;
        }

        @Override
        public boolean addAll(int index, @NonNull Collection<? extends String> c) {
            return false;
        }

        @Override
        public boolean removeAll(@NonNull Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(@NonNull Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public String get(int index) {
            return null;
        }

        @Override
        public String set(int index, String element) {
            return null;
        }

        @Override
        public void add(int index, String element) {

        }

        @Override
        public String remove(int index) {
            return null;
        }

        @Override
        public int indexOf(Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(Object o) {
            return 0;
        }

        @NonNull
        @Override
        public ListIterator<String> listIterator() {
            return null;
        }

        @NonNull
        @Override
        public ListIterator<String> listIterator(int index) {
            return null;
        }

        @NonNull
        @Override
        public List<String> subList(int fromIndex, int toIndex) {
            return null;
        }
    };
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
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

        //list.add("Dat");
        //adapter.setList(list);
        //recyclerView.setAdapter(adapter);

   //     textView = (TextView) findViewById(R.id.tv_name);

     //   Log.e("TextView Content", textView.getText() + "");


        recyclerView = (RecyclerView) findViewById(R.id.rycycleView);




        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(linearLayoutManager);


        adapter.setList(list);
        recyclerView.setAdapter(adapter);


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




    public void  startCapture(View v) {
        //

        String ip_server = "";
        TextView textView = (TextView) findViewById(R.id.tv_ipsv);
        ip_server = textView.getText().toString();
        String cmd_url = new StringBuffer().append("http://").append(ip_server).append(":9200").append("/test01/_search").toString();
        //"http://192.168.0.103:9200/test01/_search";

        new FetchData(this, adapter, recyclerView).execute(cmd_url);
        //
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