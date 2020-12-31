package com.study.android_wv;

import android.content.Context;
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
    public void getChatty() {

    }
}
