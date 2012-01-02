package com.goldmanalpha.dailydo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by IntelliJ IDEA.
 * User: Aaron
 * Date: 1/2/12
 * Time: 2:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class BackupService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);    //To change body of overridden methods use File | Settings | File Templates.



    }
}
