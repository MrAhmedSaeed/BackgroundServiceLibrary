package com.fetchsky.fsbackgroundservicelibrary;

import android.content.Context;
import android.widget.Toast;

public class Geofencing {
    static LocationUpdater locationObject;

    public static void initialize(Context context){
        locationObject= LocationUpdater.getInstance();
        Notification notifyObject=Notification.getInstance();
        API apiObject=API.getInstance();
        locationObject.appContext=context;
        notifyObject.appContext=context;
        apiObject.appContext=context;
        locationObject.initializeLocationManager();
    }

    public static void startTracking(){
        locationObject.startService();
    }

    public static void stopTracking(){
        locationObject.stopService();
    }

    public static void setTimeInterval(long timeInterval){
        locationObject.setTimeInterval(timeInterval);
    }
}
