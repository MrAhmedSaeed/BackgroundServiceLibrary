package com.fetchsky.fsbackgroundservicelibrary;

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
import java.util.Queue;

public class API {
    public static String deviceToken=null;

    Geofencing geofencing=new Geofencing();

    private static final API ourInstance = new API();

    public static API getInstance() {
        return ourInstance;
    }

    private API() {
    }

    private String getAuthToken() {
        if (deviceToken == null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(geofencing.getAppContext());
            deviceToken = preferences.getString("deviceToken", null);
        }
        return deviceToken;
    }

    private  void setAuthToken(String token) {
        deviceToken = token;
        // Set token in local storage
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(geofencing.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("deviceToken", token);
        editor.apply();
    }

    public void getDeviceToken(String uuid, final HashMap locationData){
        String URL="http://www.mocky.io/v2/5cd3db28350000de307a50f7";
//        Map<String, String> params = new HashMap();
//        params.put("uuid", uuid);

        RequestQueue requestQueue = Volley.newRequestQueue(geofencing.getAppContext());
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("TestGPS Token response", response.toString());
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
        String URL = "http://www.mocky.io/v2/5cd95b973000006f35c015c8";
        //Send lat Long and FCM_ID time and Platfrom as Body in Request.
//        Map<String, String> params = new HashMap();
//        params.put("recipientId", notifyObject.getFCMToken());
//        params.put("latitude", toString().valueOf(offlineLocation.peek().get("latitude")));
//        params.put("longitude", toString().valueOf(offlineLocation.peek().get("longitude")));
//        params.put("timeZone", "+05:00");
//        params.put("platform", "ANDROID / IOS");
//        JSONObject bodyObject = new JSONObject(params);


        RequestQueue requestQueue = Volley.newRequestQueue(geofencing.getAppContext());
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("TestGPS DeviceLocation", response.toString());
                        Toast.makeText(geofencing.getAppContext(), response.toString(), Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TestGPS error", error.toString());
            }
        });
//            {
//                //Passing some request header
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    HashMap<String, String> headers = new HashMap<String, String>();
//                    headers.put("Content-Type", "application/json");
//                    headers.put("Authorization",getAuthToken());
//                    return headers;
//                }
//            };
        requestQueue.add(postRequest);
    }

    public void sendDeviceLocation(String deviceId,HashMap data) {
        if(getAuthToken() == null) {
            // Register device
            getDeviceToken(deviceId,data);
        } else {
            registerDeviceLocation(data);
        }
    }
}
