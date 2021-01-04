package com.study.android_wv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
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
    // 当前Activity的全局变量 this找不到时使用全局变量
    WebView webView;
    Button load_wb_btn;
    int webViewLoadProgress = 0;

    @Override // 广播的事件监听 并调用JS的内部函数 render WebView HTML
    public void onListener(JSONObject batt_obj) {
        String jsfn = "javascript:batteryListener(" + String.valueOf(batt_obj) + ")";
        this.webView.loadUrl(String.format(jsfn));
    }

    // Android的生命周期函数 onCreate必须实现并且必须调用setContentView()
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = this; // 赋值全局变量

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
                String message = consoleMessage.message();
                int lineNum = consoleMessage.lineNumber();
                String souceId = consoleMessage.sourceId();
                Log.d("webview", "打印信息:" + message + "--行数: "+ String.valueOf(lineNum) + "文件:" + souceId);
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public void onProgressChanged(WebView webView, int progress) {
                Log.i("webview", "加载进度:" + String.valueOf(webViewLoadProgress) + "---" + String.valueOf(progress));
                if (progress == 100 &&  webViewLoadProgress != 100) {
                    sendMsg(context, webView);
                }
                if (webViewLoadProgress != progress) {
                    webViewLoadProgress = progress;
                }
            }
        });


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

        // webview调试模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webview.setWebContentsDebuggingEnabled(true);
        }
        // Webview设置
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true); // 允许js执行

//        webSettings.setMediaPlaybackRequiresUserGesture(false); // 视频可自动播放
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMixedContentMode(MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        webSettings.setUserAgentString("desktop"); // 可设置桌面环境还是移动端环境 影响排版布局
        webview.setBackgroundColor(2);

        // 加载Webview按钮
        load_wb_btn = (Button) findViewById(R.id.button);
//        webview.loadUrl("file:///android_asset/web/index.html");
        webview.loadUrl("https://120.26.89.217:19980/cef/index.html?local_ip=172.16.1.110&local_port=8899&janus_port=4145&janus_id=735940525973012&room=2345&type=local&screen=true&display=%E4%B8%AD%E5%BA%861%E7%8F%AD&ice_servers=[{%22urls%22:%22turn:120.26.89.217:3478%22,%22username%22:%22inter_user%22,%22credential%22:%22power_turn%22}]#/");
        // 注入java 函数 js调用Java的函数
        webview.addJavascriptInterface(new Jsinterface(this, load_wb_btn), "js");

        // 点击按钮 获取数据并给Webview传递消息postWebMessage
        load_wb_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "渲染Webview列表", Toast.LENGTH_SHORT).show();
//                load_wb_btn.setVisibility(View.GONE); // 隐藏不占空间 View.INVISIBLE隐藏占用空间位置
                Log.d("webview", "加载webview");
                if(webView.canGoBack()) {
                    webView.goBack();
                }

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

    // 给webview发送消息
    public void sendMsg(Context context, WebView webView) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray data_list = getData(context, "http://172.16.1.110:6081/api/subject/list");
                WebMessage web_msg;
                // 判断是否有数据
                if (data_list != null && data_list.length() > 0) {
                    web_msg = new WebMessage(String.valueOf(data_list));
                } else {
                    web_msg = new WebMessage(String.valueOf(new JSONArray()));
                }
                Log.i("webview", "给webview发送数据:" + String.valueOf(web_msg));
                if (webView != null) {
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            webView.postWebMessage(web_msg, Uri.EMPTY);
                        }
                    });
                }
            }
        }).start();
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