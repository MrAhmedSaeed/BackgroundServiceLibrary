package com.fetchsky.fsbackgroundservicelibrary;

import android.content.Context;

public class Geofencing {
    static LocationUpdater locationUpdater;
    public static Context appContext;

    public static void initialize(){
        locationUpdater= LocationUpdater.getInstance();
        locationUpdater.initializeLocationManager();
    }

    public static void setAppContext(Context context){
        appContext=context;
    }

    public static Context getAppContext(){
        return appContext;
    }

    public static void startTracking(){
        locationUpdater.startService();
    }

    public static void stopTracking(){
        locationUpdater.stopService();
    }

    public void setTimeInterval(long timeInterval){
        locationUpdater.setTimeInterval(timeInterval);
    }
}
