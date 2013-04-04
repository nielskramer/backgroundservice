package com.example.backgroundservice;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ReInitiateService extends Service {

    public static final String BROADCAST_ACTION = "com.example.backgroundservice.reinitiate";
    private final Handler handler = new Handler();
    Intent intent;
    int counter = 0;
    ActivityManager mActivityManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mActivityManager =(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.removeCallbacks(sendSignalToActivity);
        handler.postDelayed(sendSignalToActivity, 1000); 
        return super.onStartCommand(intent, flags, startId);
}


    private Runnable sendSignalToActivity = new Runnable() {
        public void run() {
            if(getForegroundApp(false) != null){
                if(getForegroundApp(false).processName.equals("com.android.browser")){
                    do{
                        //Do Nothing
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {

                        }
                    }while(!getForegroundApp(true).processName.equals("com.android.browser"));
                    sendIntentToActivity();
                }
            }else{
                //Do Nothing
            }
            handler.postDelayed(this, 100); 
        }
    };

    private void sendIntentToActivity() {
        intent.putExtra("time", new Date().toString());
        intent.putExtra("counter", String.valueOf(++counter));
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private ActivityManager.RunningAppProcessInfo getForegroundApp(boolean forground) {
        ActivityManager.RunningAppProcessInfo result=null, info;

        if(mActivityManager==null)
            mActivityManager = (ActivityManager)ReInitiateService.this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> l = mActivityManager.getRunningAppProcesses();
        Iterator<ActivityManager.RunningAppProcessInfo> i = l.iterator();
        while(i.hasNext()){
            info = i.next();
            boolean runningService = isRunningService(info.processName);
            boolean system = info.processName.equals("system");
            boolean browser = info.processName.equals("com.android.browser");
            if(info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && !runningService
                    && !system
                    && forground){
                result=info;
                break;
            }else if(info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND
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
        ActivityManager.RunningServiceInfo service;
        if(mActivityManager==null)
            mActivityManager = (ActivityManager) ReInitiateService.this.getSystemService(Context.ACTIVITY_SERVICE);

        List <ActivityManager.RunningServiceInfo> l = mActivityManager.getRunningServices(9999);
        Iterator <ActivityManager.RunningServiceInfo> i = l.iterator();
        while(i.hasNext()){
            service = i.next();
            if(service.process.equals(processname))
                return true;
        }
        return false;
    }

}
