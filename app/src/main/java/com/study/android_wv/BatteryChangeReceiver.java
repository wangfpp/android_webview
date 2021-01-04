package com.study.android_wv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.EventListener;

public class BatteryChangeReceiver extends BroadcastReceiver {
    @Override
    // 广播接收必须实现receive方法context为关联的activity intent为过滤器
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equalsIgnoreCase(Intent.ACTION_BATTERY_CHANGED)) { // 电池发生变化
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1); // 电池电量
            int temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1); // 电池温度
//            String charge = intent.getAction(BatteryManager.ACTION_CHARGING);
            JSONObject batterJson = new JSONObject();
            try {
                batterJson.put("level", level);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                batterJson.put("temp", temp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            Log.d("battery", String.valueOf(batterJson));
            myListener.onListener(batterJson);
        }
    }


    public MyListener myListener;

    public interface MyListener{
        public void onListener(JSONObject batt_obj);
    }

    public void setMyListener(MyListener myListener) {
        this.myListener = myListener;
    }
}
