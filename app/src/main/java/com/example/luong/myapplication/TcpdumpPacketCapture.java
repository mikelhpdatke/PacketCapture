package com.example.luong.myapplication;

import android.app.Activity;

import android.util.Log;
import android.widget.TextView;


import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * @Author: Tom
 */
public class TcpdumpPacketCapture {

    private static Activity activity;
    private static Shell.Interactive rootTcpdumpShell;

    private static boolean isInitialised = false;

    public static void initialiseCapture(Activity _activity) {
        activity = _activity;

        Log.e("Debug_Tom","Initialising Capture");
        Log.e("Debug_Tom","Please wait while packet capture is initialised...");

        if (rootTcpdumpShell != null) {
            if(!isInitialised)
                throw new RuntimeException("rootTcpdump shell: not null, initialized:false");
            startTcpdumpCapture();
        }
        else {
            rootTcpdumpShell = new Shell.Builder().
                useSU().
                setWantSTDERR(false).
                setMinimalLogging(true).
                open(new Shell.OnCommandResultListener() {
                    @Override
                    public void onCommandResult(int commandVal, int exitVal, List<String> out) {
                        //Callback checking successful shell start.
                        if (exitVal == Shell.OnCommandResultListener.SHELL_RUNNING) {
                            isInitialised = true;
                            Log.e("Debug_Tom","Starting packet capture..");
                            startTcpdumpCapture();
                        }
                        else {
                            Log.e("Debug_Tom","There was an error starting root shell. Please grant root permissions or try again.");
                        }
                    }
                });
        }
    }

    private static void startTcpdumpCapture() {

        try{
            // Check busybox.bin
            List<String> out = Shell.SH.run("ps | grep busybox.bin");
            if(out.size() > 0){
                //One process already running. Don't start another.
                ((TextView)activity.findViewById(R.id.tv_status))
                        .setText("Busybox "+out.size()+" process already running at pid: " + (out.get(0).split("\\s+"))[1] );

                return;
            }

            //
            out = Shell.SH.run("ps | grep tcpdump.bin");
                if(out.size() > 0){
                //One process already running. Don't start another.
                ((TextView)activity.findViewById(R.id.tv_status))
                        .setText("Tcpdump "+out.size()+" process already running at pid: " + (out.get(0).split("\\s+"))[1] );

                return;
            }
            rootTcpdumpShell.addCommand(activity.getApplicationInfo().dataDir + "/files/tcpdump.bin -w - | " +
                            activity.getApplicationInfo().dataDir +
                    "/files/busybox.bin nc -l -p 2000", 0, new Shell.OnCommandLineListener() {
                @Override
                public void onCommandResult(int commandVal, int exitVal) {
                    if (exitVal < 0) {
                        Log.e("Debug_Tom","Error returned by shell command...");
                    }
                }
                @Override
                public void onLine(String line) {
                    appendOutput(line);
                }
            });
        }
        catch(Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        ((TextView)activity.findViewById(R.id.tv_status)).setText("Packet capture started..");
    }

    public static int stopTcpdumpCapture(Activity _activity){
        if(isInitialised == false) {
            //Uncommenting creates problem with sometimes main_tv output wrong. Not really required right now.
            //initialiseCapture(_activity);
        }
        if(rootTcpdumpShell == null)    {
            //Not really required right now.
            //throw new RuntimeException("rootTcpdumpShell is null in: stopTcpdumpCapture.");
        }
        // Bug: Showing progress dialogue here (with above two ifs uncommented obviously) causes app to crash on "progressBox.show();"
        //if(!progressBox.isShowing())
        //    progressBox.show();

        int retVal = 0;
        //progressBox.setMessage("Killing tcpdump process.");

        try{
            List<String> out = Shell.SH.run("ps | grep tcpdump.bin");
            for(String x : out) {
                String[] temp = x.split("\\s+");
                Integer pid =  Integer.valueOf(temp[1]);
                List<String> exitOutput =  Shell.SU.run("kill -9 " + pid.toString());
            }
            // busybox
            out = Shell.SH.run("ps | grep busybox.bin");
            for(String x : out) {
                String[] temp = x.split("\\s+");
                Integer pid =  Integer.valueOf(temp[1]);
                List<String> exitOutput =  Shell.SU.run("kill -9 " + pid.toString());
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
            //retVal = -1;
            throw ex;
        }
        //progressBox.dismiss();
        return retVal;
    }

    private static void appendOutput(String line) {
        StringBuilder out = (new StringBuilder()).
            append(line).
            append((char)10);
        ((TextView)activity.findViewById(R.id.tv_status)).append(out.toString());
    }
}
