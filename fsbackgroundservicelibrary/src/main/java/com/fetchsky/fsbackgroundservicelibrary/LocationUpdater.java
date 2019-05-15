package com.fetchsky.fsbackgroundservicelibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Calendar;
import java.util.HashMap;

import static android.text.TextUtils.isEmpty;

public class LocationUpdater {
    private long defaultTimeInterval=1200;
    private String networkType;
    Geofencing geofencing=new Geofencing();
    Notification notifyObject=Notification.getInstance();
    API apiObject=API.getInstance();

    private static final LocationUpdater ourInstance = new LocationUpdater();

    public static LocationUpdater getInstance() {
        return ourInstance;
    }

    private LocationUpdater() {
    }

    public void initializeLocationManager(){
        LocalBroadcastManager.getInstance(geofencing.getAppContext()).registerReceiver(locationMessageReceiver,
                new IntentFilter(LocationService.LOCATION_BROADCAST));
    }

    private BroadcastReceiver locationMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location locationMessage = intent.getParcelableExtra("locationMessage");
            sendEvent(locationMessage);
        }
    };

    public void startService() {
        if (!LocationService.serviceRunning) {
            Log.i("TestGPS", "startService");
            Intent intent = new Intent(geofencing.getAppContext(), LocationService.class);
            intent.putExtra("timeInterval", defaultTimeInterval);
            try {
                geofencing.getAppContext().startService(intent);
            } catch (Exception e) {
                Log.i("TestGPS", "startService Exception");
                return;
            }
        }
    }

    public void stopService() {
        Log.i("TestGPS", "stopService");
        try {
            geofencing.getAppContext().stopService(new Intent(geofencing.getAppContext(), LocationService.class));
        } catch (Exception e) {
            Log.i("TestGPS", "stopService Exception");
            return;
        }
    }

    public void setTimeInterval(long intervalValue) {
        defaultTimeInterval = intervalValue * 60 * 1000;
    }

    private void sendLocation(HashMap currentLocation) {


    }

    public String getNetworkType() {
        ConnectivityManager connectivityManager = (ConnectivityManager) geofencing.getAppContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo == null) {
            networkType = null;
        } else {
            networkType = netInfo.getTypeName();
        }
        return networkType;
    }

    private void sendEvent(Location locationMessage) {
        HashMap<String, String> currentLocation = new HashMap<>();
        currentLocation.put("latitude", toString().valueOf(locationMessage.getLatitude()));
        currentLocation.put("longitude", toString().valueOf(locationMessage.getLongitude()));
        currentLocation.put("accuracy", toString().valueOf(locationMessage.getAccuracy()));
        currentLocation.put("altitude", toString().valueOf(locationMessage.getAltitude()));
        currentLocation.put("heading", toString().valueOf(locationMessage.getBearing()));
        currentLocation.put("speed", toString().valueOf(locationMessage.getSpeed()));
        currentLocation.put("timestamp", Calendar.getInstance().getTime().toString());

        if (!isEmpty(getNetworkType())) {
            apiObject.sendDeviceLocation(notifyObject.getDeviceId(),currentLocation);
        }
    }
}
