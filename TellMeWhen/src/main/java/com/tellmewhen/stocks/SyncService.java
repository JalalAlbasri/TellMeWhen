package com.tellmewhen.stocks;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SyncService extends Service {
	private static final String TAG = SyncService.class.getSimpleName();
	
	private static final Object sSyncAdapterLock = new Object();

    private static SyncAdapter sSyncAdapter = null;
    
    public SyncService() {
        super();
    }
    
    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
        	Log.d(TAG, "*********SyncService.OnCreate()!***********");
        	if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
    	Log.d(TAG, "*********SyncService.OnBind()!***********");
        return sSyncAdapter.getSyncAdapterBinder();
    }
}