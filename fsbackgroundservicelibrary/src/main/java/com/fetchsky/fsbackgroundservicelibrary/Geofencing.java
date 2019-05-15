package com.fetchsky.fsbackgroundservicelibrary;

import android.content.Context;
import android.widget.Toast;

import java.util.zip.CheckedOutputStream;

public class Geofencing {
    static LocationUpdater locationObject;
    public static Context appContext;

    public static void initialize(){
        locationObject= LocationUpdater.getInstance();
        locationObject.initializeLocationManager();
    }

    public static void setAppContext(Context context){
        appContext=context;
    }

    public static Context getAppContext(){
        return appContext;
    }

    public static void startTracking(){
        locationObject.startService();
    }

    public static void stopTracking(){
        locationObject.stopService();
    }

    public void setTimeInterval(long timeInterval){
        locationObject.setTimeInterval(timeInterval);
    }
}
