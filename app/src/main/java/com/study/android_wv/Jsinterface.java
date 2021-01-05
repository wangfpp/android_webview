package com.study.android_wv;

import android.app.MediaRouteButton;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.Toast;

import androidx.transition.Visibility;

import org.json.JSONException;
import org.json.JSONObject;

public class Jsinterface {
    public Context context;
    public Button load_btn;

    public Jsinterface(Context context, Button load_btn) {
        Log.e("weview", "ddddddddddddd:" + String.valueOf(load_btn));
        this.context = context;
        this.load_btn = load_btn;
    }

    @JavascriptInterface
    public void sayName(String name) {
        String myname = "my name is " + name;
        Toast toast = Toast.makeText(context, myname, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @JavascriptInterface
    public String getName() {
        return  "wangfpp";
    }

    @JavascriptInterface
    public String getBattery() {
        BatteryManager manager = (BatteryManager)context.getSystemService(context.BATTERY_SERVICE);
        int level = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        boolean isCharge = false;
        if(manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS) == BatteryManager.BATTERY_STATUS_CHARGING) {
            isCharge = true;
        }
        JSONObject batterObj = new JSONObject();
        try {
            batterObj.put("level", level);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            batterObj.put("isCharge", isCharge);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("battery", String.valueOf(batterObj));
        return String.valueOf(batterObj);
    }

    // 动态显示加载按钮
    @JavascriptInterface
    public void toggterShowBtn() {
        Boolean visivity = this.load_btn.getVisibility() == View.VISIBLE;
        Log.d("webview", "按钮当前是否显示了:" + String.valueOf(visivity));
//        new Thread(
////                new Runnable() {
//                    @Override
//                    public void run() {
//                        if (visivity) {
//                            load_btn.setVisibility(View.INVISIBLE);
//                        } else {
//                            load_btn.setVisibility(View.VISIBLE);
//                        }
//                    }
////                }.run();
//        ).start();
        Log.d("webview", "按钮当前是否显示了:" + String.valueOf(this.load_btn.getVisibility() == View.VISIBLE));

    }

}
