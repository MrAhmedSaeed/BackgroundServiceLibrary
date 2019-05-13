package com.fetchsky.fsbackgroundservicelibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static android.content.ContentValues.TAG;
import static android.text.TextUtils.isEmpty;

public class LocationPicker {
    private String networkType;
    private int requestFailCount = 0;
    private HashMap apiHeader = null;
    private int timeInterval = 1200; // default value 20 min
    private String deviceToken=null;
    private String FCMtoken=null;
    private static String deviceIdentifier;
    private static Context appContext;
    Queue<HashMap> offlineLocation = new LinkedList<>();

    public  void initializeLocationService(Context c){
        appContext=c;
        LocalBroadcastManager.getInstance(c).registerReceiver(locationMessageReceiver,
                new IntentFilter(LocationService.LOCATION_BROADCAST));
        deviceIdentifier = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);
        if(FCMtoken==null){
            FCMtoken=getFCMToken();
        }
    }

    private String getFCMToken(){
        final String[] fcmToken = {null};
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        fcmToken[0] = task.getResult().getToken();
                        Toast.makeText(appContext, fcmToken[0], Toast.LENGTH_SHORT).show();
                    }
                });
        return fcmToken[0];
    }

    private  BroadcastReceiver locationMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location locationMessage = intent.getParcelableExtra("locationMessage");
            sendEvent(locationMessage);
        }
    };

    public void startService(Context c) {

        String result = "successStart";
        if (!LocationService.serviceRunning) {
            Log.i("TestGPS", "startService");
            Intent intent = new Intent(c, LocationService.class);
            intent.putExtra("timeInterval", timeInterval);
            try {
                c.startService(intent);
            } catch (Exception e) {
                Log.i("TestGPS", "reject");

                return;
            }
        }
    }
//
//    public void show(String message) {
//        Toast.makeText(appContext, message, Toast.LENGTH_LONG).show();
//    }

    public void stopService(Context c) {
        String result = "successStop";
        Log.i("TestGPS", "stopService");
        try {
            c.stopService(new Intent(c, LocationService.class));
        } catch (Exception e) {
//            promise.reject(e);
            Log.i("TestGPS", "Exception");
            return;
        }
//        promise.resolve(result);

    }


//    public void setHeader(HashMap header) {
//        apiHeader = header;
//    }


    public void setTimeInterval(int intervalValue) {
        timeInterval = intervalValue * 60 * 1000;
    }

    public void sendLocation() {

        while (!offlineLocation.isEmpty()) {
            Object data = new Object();
            data = offlineLocation.peek();
            if(deviceToken==null){
                getToken();
            }else{
                registerDeviceLocation(data);
            }

            boolean apiRequestFailed = false; // depends on api request fail or success
            if (apiRequestFailed) {
                requestFailCount++;
            } else {
                offlineLocation.poll();
//        Log.i("TestGPS", "data## " + data);
            }

            if (requestFailCount > 2) {
                requestFailCount = 0;
                break;
            }

        }

    }

    private void getToken(){
        //Send deviceIdentifier as Body in Request
        String URL="http://www.mocky.io/v2/5cd3db28350000de307a50f7";

        RequestQueue requestQueue = Volley.newRequestQueue(appContext);
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("TestGPS RESponse", response.toString());

                        try {
                            JSONObject data = response.getJSONObject("data");
                            deviceToken=data.getString("token");
                            Log.i("TestGPS String Token", data.getString("token"));
                            Toast.makeText(appContext, deviceToken, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e1) {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("TestGPS error", error.toString());
                    }
                });
        requestQueue.add(postRequest);

    }

    private void registerDeviceLocation(Object data) {

        String URL = "http://www.mocky.io/v2/5cd95b973000006f35c015c8";
        //Send lat Long and FCM_ID time and Platfrom as Body in Request.
//        Map<String, String> params = new HashMap();
//        params.put("recipientId", FCMtoken);
//        params.put("latitude", "24.2334223");
//        params.put("longitude", "67.3948343");
//        params.put("timeZone", "+05:00");
//        params.put("platform", "ANDROID / IOS");
//        JSONObject bodyObject = new JSONObject(params);

        RequestQueue requestQueue = Volley.newRequestQueue(appContext);
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("TestGPS RESponse", response.toString());

                        Toast.makeText(appContext, response.toString(), Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TestGPS error", error.toString());
            }
        }) {
            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization",
                        "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6NzIsInJvbGUiOiJndWVzdCIsImlhdCI6MTU1Mzk1NzMzMCwianRpIjoiZnR0MVZUZ1ZGNHVsS2hBSlg4VHdYVWNPamdIUmpHNzYifQ.fkEhIbUjNdjC3AV64j9cCj9GhLrCsA2foyf8b76I9bw");
                return headers;
            }
        };
        requestQueue.add(postRequest);
    }

    public String getNetworkType() {
        ConnectivityManager connectivityManager = (ConnectivityManager) appContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo == null) {
            networkType = null;
        } else {
            networkType = netInfo.getTypeName();
        }
        return networkType;
    }

    // private void sendLocationEvent(ReactContext reactContext, String eventName,
    // @Nullable WritableMap params) {
    // reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName,
    // params);
    // }

    private void sendEvent(Location locationMessage) {
        HashMap<String, String> currentLocation = new HashMap<>();
        currentLocation.put("latitude", toString().valueOf(locationMessage.getLatitude()));
        currentLocation.put("longitude", toString().valueOf(locationMessage.getLongitude()));
        currentLocation.put("accuracy", toString().valueOf(locationMessage.getAccuracy()));
        currentLocation.put("altitude", toString().valueOf(locationMessage.getAltitude()));
        currentLocation.put("heading", toString().valueOf(locationMessage.getBearing()));
        currentLocation.put("speed", toString().valueOf(locationMessage.getSpeed()));
        currentLocation.put("timestamp", Calendar.getInstance().getTime().toString());
        offlineLocation.add(currentLocation);
        if (!isEmpty(getNetworkType())) {
            sendLocation();
        }

        // For send data to javascript
        // WritableMap currentLocation = Arguments.createMap();
        // currentLocation.putString("latitude",
        // toString().valueOf(locationMessage.getLatitude()));
        // currentLocation.putString("longitude",
        // toString().valueOf(locationMessage.getLongitude()));
        // currentLocation.putString("accuracy",
        // toString().valueOf(locationMessage.getAccuracy()));
        // currentLocation.putString("altitude",
        // toString().valueOf(locationMessage.getAltitude()));
        // currentLocation.putString("heading",
        // toString().valueOf(locationMessage.getBearing()));
        // currentLocation.putString("speed",
        // toString().valueOf(locationMessage.getSpeed()));
        // currentLocation.putString("timestamp",
        // Calendar.getInstance().getTime().toString());
        // sendLocationEvent(this.reactContext, "updatedLocation", currentLocation);

    }


}
