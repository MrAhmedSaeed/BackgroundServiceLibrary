package com.fetchsky.fsbackgroundservicelibrary;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import static android.content.ContentValues.TAG;

public class Notification {
    //    private static String FCM_TOKEN=null;
    Geofencing geofencing=new Geofencing();
    private static final Notification ourInstance = new Notification();

    public static Notification getInstance() {
        return ourInstance;
    }

    private Notification() {
    }

    public static String getFCMToken(){

        final String[] fcmToken = {null};
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.i("TestGPS FCMFAILd", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        fcmToken[0] = task.getResult().getToken();
                        Log.i("TestGPS fcmToken", fcmToken[0]);
//                            FCM_TOKEN=fcmToken[0];

                    }
                });
        return fcmToken[0];
    }

    public String getDeviceId(){
        return Settings.Secure.getString(geofencing.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }


}
