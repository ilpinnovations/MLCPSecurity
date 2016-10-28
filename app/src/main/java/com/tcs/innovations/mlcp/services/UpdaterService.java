package com.tcs.innovations.mlcp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by abhi on 2/22/2016.
 */
public class UpdaterService extends Service {

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
