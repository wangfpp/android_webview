package com.study.android_wv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebMessage;
import android.webkit.WebMessagePort;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW;

public class MainActivity extends AppCompatActivity implements BatteryChangeReceiver.MyListener {
    WebView webView;

    @Override
    public void onListener(int level) {
        String jsfn = "javascript:batteryListener(" + String.valueOf(level) + ")";
        this.webView.loadUrl(String.format(jsfn));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = this;

        // 广播接收
        BatteryChangeReceiver batteryChangeReceiver = new BatteryChangeReceiver();
        Intent batteryIntent = context.registerReceiver(batteryChangeReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        batteryChangeReceiver.setMyListener(this);



        // webview
        WebView webview = (WebView) findViewById(R.id.webview);
        this.webView = webview; // 赋值webview
        // ChromeClient
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("webview", String.valueOf(consoleMessage));
                return super.onConsoleMessage(consoleMessage);
            }
        });
        webview.loadUrl("file:///android_asset/web/index.html");
//        String jsfn = "javascript:batteryListener(" + String.valueOf(level) + ")";

        // 注入java 函数
        webview.addJavascriptInterface(new Jsinterface(this), "js");


        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                return false;
            }

            @Override // SSL证书错误
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.d("webview", String.valueOf(error));
               handler.proceed();
            }
        });
        // Webview设置
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true); // 允许js执行
        webSettings.setMediaPlaybackRequiresUserGesture(false); // 视频可自动播放
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMixedContentMode(MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAllowContentAccess(true);
//        webSettings.setUserAgentString("desktop"); // 可设置桌面环境还是移动端环境 影响排版布局
        webview.setBackgroundColor(2);

        // 按钮
        Button first_btn = (Button) findViewById(R.id.button);
        first_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "点击我了吗", Toast.LENGTH_SHORT).show();
                first_btn.setVisibility(View.GONE); // 隐藏
                Log.d("webview", "加载webview");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                       JSONArray data_list = getData(context, "http://172.16.1.110:6081/api/subject/list");
                       WebMessage web_msg = new WebMessage(String.valueOf(data_list));
                       webview.post(new Runnable() {
                           @Override
                           public void run() {
                               webview.postWebMessage(web_msg, Uri.EMPTY);
                           }
                       });
                    }
                }).start();
                // webview.loadUrl(String.format("javascript:jsfun('wangfpp', 20)")); // 无返回值
                // 有返回值
                webview.evaluateJavascript(String.format("javascript:jsfun('wangfpp', 20)"), new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        Log.d("webview", "接收到数据" + s);
                    }
                });
            }
        });


    }
    // AJAX请求
    public JSONArray getData(Context context, String url) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            String s = response.body().string(); // 获取到的JSON数据
            JSONObject jsonObject = new JSONObject(s);
            int code = jsonObject.getInt("rc");
            JSONArray subject_list = jsonObject.getJSONArray("data");
            Log.d("request", String.valueOf(code));
            Log.d("request", String.valueOf(subject_list.length()));
            if (code == 0) {
                return jsonObject.getJSONArray("data");
            } else {
                String msg = jsonObject.getString("msg");
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        } catch (IOException | JSONException e) {
            Log.d("request", String.valueOf(e));
        }
        return null;
    }

}