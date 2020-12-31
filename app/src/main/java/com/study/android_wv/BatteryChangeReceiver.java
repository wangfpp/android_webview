package com.study.android_wv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

import java.util.EventListener;

public class BatteryChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equalsIgnoreCase(Intent.ACTION_BATTERY_CHANGED)) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            myListener.onListener(level);
        }
    }


    public MyListener myListener;

    public interface MyListener{
        public void onListener(int leval);
    }

    public void setMyListener(MyListener myListener) {
        this.myListener = myListener;
    }
}
