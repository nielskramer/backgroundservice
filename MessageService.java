package com.example.backgroundservice;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.*;


public class MessageService extends Service {
    public static final String BROADCAST_ACTION = "com.example.backgroundservice.displayevent";
    private final Handler handler = new Handler();
    Intent intent;
    int counter = 0;
    ActivityManager mActivityManager;
    boolean isRunning;

    @Override
    public synchronized void onCreate() {
        super.onCreate();
        mActivityManager =(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); 
        return super.onStartCommand(intent, flags, startId);
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        private boolean messageSend = false;
        public void run() {
            if(isRunning){
                RunningAppProcessInfo proces = getForegroundApp(true);
                boolean process = proces.processName.equals("com.android.browser");
                if( process && !messageSend){
                    DisplayLoggingInfo();
                    messageSend = true;
                }else{
                    RunningAppProcessInfo procesBackground = getForegroundApp(false);
                    if(procesBackground != null){
                        messageSend = false;
                    }
                }
                handler.postDelayed(this, 100); 
            }
        }
    };

    private void DisplayLoggingInfo() {
        intent.putExtra("time", new Date().toString());
        intent.putExtra("counter", String.valueOf(++counter));
        sendBroadcast(intent);
    }

    @Override
    public synchronized void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }

    private RunningAppProcessInfo getForegroundApp(boolean forground) {
        RunningAppProcessInfo result = null, info;

        if(mActivityManager==null)
            mActivityManager = (ActivityManager)MessageService.this.getSystemService(Context.ACTIVITY_SERVICE);
        List <RunningAppProcessInfo> l = mActivityManager.getRunningAppProcesses();
        Iterator <RunningAppProcessInfo> i = l.iterator();
        while(i.hasNext()){
            info = i.next();
            boolean runningService = isRunningService(info.processName);
            boolean system = info.processName.equals("system");
            boolean browser = info.processName.equals("com.android.browser");
            if(info.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && !runningService
                    && !system
                    && forground){
                result=info;
                break;
            }else if(info.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND
                    && !runningService
                    && browser
                    && !forground){
                result=info;
                break;
            }
        }
        return result;
    }

    private boolean isRunningService(String processname){
        if(processname==null)
            return false;
        RunningServiceInfo service;
        if(mActivityManager==null)
            mActivityManager = (ActivityManager) MessageService.this.getSystemService(Context.ACTIVITY_SERVICE);

        List <RunningServiceInfo> l = mActivityManager.getRunningServices(9999);
        Iterator <RunningServiceInfo> i = l.iterator();
        while(i.hasNext()){
            service = i.next();
            if(service.process.equals(processname))
                return true;
        }
        return false;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
