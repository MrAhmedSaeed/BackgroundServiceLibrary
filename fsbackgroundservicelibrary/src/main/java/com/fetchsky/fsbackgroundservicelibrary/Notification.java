package com.fetchsky.fsbackgroundservicelibrary;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


public class Notification {
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

                        fcmToken[0] = task.getResult().getToken();

                    }
                });
        return fcmToken[0];
    }

    public String getDeviceId(){
        return Settings.Secure.getString(geofencing.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }


}
