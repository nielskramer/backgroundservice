package com.example.backgroundservice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.os.*;

public class MessageActivity extends Activity {
    Intent MessageServiceIntent;
    Intent ReinitiateServiceIntent;
    private boolean firstRun = true;
    /**
     *  * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Debug.startMethodTracing();
        MessageServiceIntent = new Intent(this, MessageService.class);
        ReinitiateServiceIntent = new Intent(this, ReInitiateService.class);
        if(firstRun){
            Intent LaunchIntent = new Intent(Intent.ACTION_MAIN); 
            LaunchIntent.setClassName("com.sec.android.app.launcher", "com.android.launcher2.Launcher");
            LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(LaunchIntent);
            firstRun = false;
        }
    }

    @Override
    protected void onResume() {
        startService(MessageServiceIntent);
        registerReceiver(broadcastReceiver, new IntentFilter(MessageService.BROADCAST_ACTION));
        registerReceiver(reinitiateReceiver, new IntentFilter(ReInitiateService.BROADCAST_ACTION));
        super.onResume();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intents = new Intent(getApplicationContext(), MessageActivity.class);
            intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intents);
            alertBox();
        }
    };

    private BroadcastReceiver reinitiateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startService(MessageServiceIntent);
            registerReceiver(broadcastReceiver, new IntentFilter(MessageService.BROADCAST_ACTION));
        }
    };

    public void alertBox(){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setMessage("It is legally forbidden to gain unauthorized access to the system")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        unregisterReceiver(broadcastReceiver);
                        stopService(MessageServiceIntent);
                        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.android.browser");
                        startActivity(LaunchIntent);
                        startService(ReinitiateServiceIntent);
                        Debug.stopMethodTracing();
                    }
                });
        AlertDialog alert = alt_bld.create();
        alert.setTitle("Let Op!");
        alert.show();
    }
}
