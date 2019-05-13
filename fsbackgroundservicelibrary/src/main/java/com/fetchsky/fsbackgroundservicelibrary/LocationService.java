package com.fetchsky.fsbackgroundservicelibrary;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class LocationService extends Service {
    final Handler mHandler = new Handler();
    static final int JOB_ID = 247854; // Unique job ID.
    private static final String TAG = "TestGPS";
    private LocationManager mLocationManager = null;
    private static final float LOCATION_DISTANCE = 5f;

    public static final String LOCATION_BROADCAST = "LocationBroadcast";
    public static boolean serviceRunning = false;
    public static boolean stopByUser = false;
    private Intent ServiceIntent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendLocationMessage(Location location) {
        Intent intent = new Intent(LOCATION_BROADCAST);
        intent.putExtra("locationMessage", location);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.i(TAG, "LocationListeners " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged: " + location);

            sendLocationMessage(location);
            mLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] { new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER) };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceRunning = true;
        ServiceIntent = intent;
        initializeLocationManager();
        Log.i(TAG, "onStartCommand");
        int timeInterval = intent.getIntExtra("timeInterval", 1200000);
        super.onStartCommand(intent, flags, startId);

        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeInterval, LOCATION_DISTANCE,
                    mLocationListeners[0]);

        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        serviceRunning = false;
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.i(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    // This commented code is for jobIntentService
    // @Override
    // public void onCreate() {
    // super.onCreate();
    // showToast("Job Execution Started");
    // }
    //
    // public static void enqueueWork(Context context, Intent work) {
    // Log.i(TAG,"enqueueWork");
    // enqueueWork(context, LocationService.class, JOB_ID, work);
    // }
    //
    // @Override
    // protected void onHandleWork(@NonNull Intent intent) {
    // // This describes what will happen when service is triggered
    // Log.i(TAG, "Executing work: " + intent);
    //// initializeLocationManager();
    // Log.i(TAG, "onHandleWork");
    // int timeInterval = intent.getIntExtra("timeInterval", 1200000);
    // Log.i(TAG, "onHandleWork" + timeInterval);
    // mLocationManager = (LocationManager)
    // getApplicationContext().getSystemService(getApplicationContext().LOCATION_SERVICE);
    // MylocationListener = new LocationListenerClass();
    // try{
    // mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
    // 600000, 0, MylocationListener, Looper.getMainLooper());
    // } catch (java.lang.SecurityException ex) {
    // Log.i(TAG, "fail to request location update, ignore", ex);
    // } catch (IllegalArgumentException ex) {
    // Log.d(TAG, "network provider does not exist, " + ex.getMessage());
    // }
    // try{
    // mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600000,
    // 0, MylocationListener, Looper.getMainLooper());
    // } catch (java.lang.SecurityException ex) {
    // Log.i(TAG, "fail to request location update, ignore", ex);
    // } catch (IllegalArgumentException ex) {
    // Log.d(TAG, "network provider does not exist, " + ex.getMessage());
    // }
    // }

}

