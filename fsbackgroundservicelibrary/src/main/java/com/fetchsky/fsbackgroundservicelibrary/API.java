package com.fetchsky.fsbackgroundservicelibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class API {
    public static String deviceToken=null;
//    public static SharedPreferences prefs;
    private int requestFailCount = 0;
    Queue<HashMap> offlineLocation = new LinkedList<>();

    Geofencing geofencing=new Geofencing();
    private static final Object INSTANCE_WRITE_LOCK = new Object();
    private static volatile API ourInstance;

//    public static API getInstance() {
//        return ourInstance;
//    }
    public static API getInstance() {
        if (ourInstance == null) {
            synchronized (INSTANCE_WRITE_LOCK) {
                if (ourInstance == null) {
                    ourInstance = new API();
                }
            }
        }
        return ourInstance;
    }

    private API() {
//        prefs = geofencing.getAppContext().getSharedPreferences("localStorage", Context.MODE_PRIVATE);
    }

    private String getAuthToken() {

        if (deviceToken == null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(geofencing.getAppContext());
            deviceToken = preferences.getString("deviceToken", null);

//            try{
//                FileInputStream fin = appContext.openFileInput("AuthToken");
//                int c;
//
//                try{
//                    while( (c = fin.read()) != -1){
//                        deviceToken = deviceToken + Character.toString((char)c);
//                    }
//                    fin.close();
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//
//            }catch (FileNotFoundException e){
//                e.printStackTrace();
//            }
        }
        Log.i("TestGPS deviceToken", deviceToken);
        return deviceToken;
        // Set token in API header
    }

    private  void setAuthToken(String token) {
        deviceToken = token;
        // Set token in API header
//        // Set token in local storage
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(geofencing.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
//        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("deviceToken", token);
        editor.apply();
//        try {
//            FileOutputStream fos = appContext.openFileOutput("AuthToken", Context.MODE_PRIVATE);
//           try{
//               fos.write(token.getBytes());
//               fos.close();
//           }catch (Exception e){
//               e.printStackTrace();
//           }
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

    }

    public void getDeviceToken(String uuid, final HashMap locationData){
        Log.i("TestGPS getDeviceToken", locationData.toString());

        String URL="http://www.mocky.io/v2/5cd3db28350000de307a50f7";
//        Map<String, String> params = new HashMap();
//        params.put("uuid", uuid);

        RequestQueue requestQueue = Volley.newRequestQueue(geofencing.getAppContext());
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("TestGPS DeviceToken", response.toString());
                        try {
                            String token = response.getJSONObject("data").getString("token");
                            setAuthToken(token);
                            Toast.makeText(geofencing.getAppContext(), response.toString(), Toast.LENGTH_LONG).show();
                            registerDeviceLocation(locationData);
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

    public void registerDeviceLocation(HashMap locationData) {

        Log.i("TestGPS RDeviceLocation", locationData.toString());
        while(!offlineLocation.isEmpty()){
            String URL = "http://www.mocky.io/v2/5cd95b973000006f35c015c8";
            //Send lat Long and FCM_ID time and Platfrom as Body in Request.
//            Map<String, String> params = new HashMap();
//            params.put("recipientId", notifyObject.getFCMToken());
//            params.put("latitude", toString().valueOf(offlineLocation.peek().get("latitude")));
//            params.put("longitude", toString().valueOf(offlineLocation.peek().get("longitude")));
//            params.put("timeZone", "+05:00");
//            params.put("platform", "ANDROID / IOS");
    //        JSONObject bodyObject = new JSONObject(params);

            RequestQueue requestQueue = Volley.newRequestQueue(geofencing.getAppContext());
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("TestGPS DeviceLocation", response.toString());
                            offlineLocation.poll();
                            Toast.makeText(geofencing.getAppContext(), response.toString(), Toast.LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("TestGPS error", error.toString());
                    requestFailCount++;
                }
            }) {
                //Passing some request header
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization",getAuthToken());
                    return headers;
                }
            };
            requestQueue.add(postRequest);

            if (requestFailCount > 2) {
                requestFailCount = 0;
                break;
            }
        }

    }

    public void sendDeviceLocation(String deviceId,HashMap data) {
        offlineLocation.add(data);
        Log.i("TestGPS data", data.toString());
        Log.i("TestGPS device Id", deviceId);
        if(getAuthToken() == null) {
            // Register device
            getDeviceToken(deviceId,data);
        } else {
            registerDeviceLocation(data);
        }
    }
}
