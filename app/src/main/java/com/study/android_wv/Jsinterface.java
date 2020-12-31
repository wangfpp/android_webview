package com.study.android_wv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.view.Gravity;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class Jsinterface {
    public Context context;

    public Jsinterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void sayName(String name) {
        String myname = "my name is " + name;
        Toast toast = Toast.makeText(context, myname, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @JavascriptInterface
    public int getBattery() {
        BatteryManager manager = (BatteryManager)context.getSystemService(context.BATTERY_SERVICE);
        int currentLevel = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        return currentLevel;
    }

    @JavascriptInterface
    public void listenBattery() {
//        BatteryChangeReceiver batteryChangeReceiver = new BatteryChangeReceiver();
//        Intent bateryIntent = context.registerReceiver(batteryChangeReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
//        int level = bateryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
//        Log.d("batery", String.valueOf(level));
    }

}
