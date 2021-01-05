package com.study.android_wv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebMessage;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW;

public class MainActivity extends AppCompatActivity implements BatteryChangeReceiver.MyListener {
    // 当前Activity的全局变量 this找不到时使用全局变量
    WebView webView;
    Button load_wb_btn;
    RelativeLayout progressLayout;
    Context context;
    int webViewLoadProgress = 0;
    long backTime = 0;
    BatteryChangeReceiver batteryChangeReceiver;

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
        context = this; // 赋值全局变量
        registerMyReceiver(); // 注册广播

        loadWebview(); // 加载webview

        load_wb_btn = (Button) findViewById(R.id.button);
        progressLayout = (RelativeLayout) findViewById(R.id.progress);
        webView.loadUrl("https://www.baidu.com");
//        webView.loadUrl("file:///android_asset/web/index.html");
//        webview.loadUrl("https://120.26.89.217:19980/cef/index.html?local_ip=172.16.1.110&local_port=8899&janus_port=4145&janus_id=735940525973012&room=2345&type=local&screen=true&display=%E4%B8%AD%E5%BA%861%E7%8F%AD&ice_servers=[{%22urls%22:%22turn:120.26.89.217:3478%22,%22username%22:%22inter_user%22,%22credential%22:%22power_turn%22}]#/");
        // 注入java 函数 js调用Java的函数
        webView.addJavascriptInterface(new Jsinterface(this, load_wb_btn), "js");

        // 点击按钮 获取数据并给Webview传递消息postWebMessage
        load_wb_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "渲染Webview列表", Toast.LENGTH_SHORT).show();
//                load_wb_btn.setVisibility(View.GONE); // 隐藏不占空间 View.INVISIBLE隐藏占用空间位置
                Log.d("webview", "加载webview");

                webviewBack(null);
                // webview.loadUrl(String.format("javascript:jsfun('wangfpp', 20)")); // 无返回值
                // 有返回值
                webView.evaluateJavascript(String.format("javascript:jsfun('wangfpp', 20)"), new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        Log.d("webview", "接收到数据" + s);
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(batteryChangeReceiver);
        webView.destroy();
    }

    /**
     * 定义一个接口　由具体对象实现其方法
     */
    public interface backCallback {
        void extiApp();
    }

    /**
     * Webview是否可返回　以及退出APP的事件处理
     * @param callback　回调函数
     */
    public void webviewBack(backCallback callback) {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
           if(callback != null) {
               callback.extiApp();
           }
        }
    }

    /**
     * 注册广播接受器
     */
    public void registerMyReceiver() {
        // 广播接收
        batteryChangeReceiver = new BatteryChangeReceiver();
        Intent batteryIntent = context.registerReceiver(batteryChangeReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        batteryChangeReceiver.setMyListener(this);
    }



    // 给webview发送消息
    public void sendMsg(Context context, WebView webView) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                WebMessage web_msg;
                JSONArray doubanlist = doubanApi("https://movie.douban.com/j/search_subjects?type=movie&tag=%E7%83%AD%E9%97%A8&sort=recommend&page_limit=100&page_start=0");
                if(doubanlist != null) {
                    Log.i("douban", String.valueOf(doubanlist.length()));
                }
                // 判断是否有数据
                if (doubanlist != null && doubanlist.length() > 0) {
                    web_msg = new WebMessage(String.valueOf(doubanlist));
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

    /**
     * 请求豆瓣数据
     * @param doubanUrl
     * @return JSONArray
     */
    public JSONArray doubanApi(String doubanUrl) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(doubanUrl).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            String res = response.body().string();
            JSONObject jsonObject = new JSONObject(res);
            JSONArray doubanlist = jsonObject.getJSONArray("subjects");
            return doubanlist;
        }catch (IOException | JSONException e) {
            Log.e("request", String.valueOf(e));
            Looper.prepare();
            Toast.makeText(context, String.valueOf(e), Toast.LENGTH_LONG).show();
            Looper.loop();
            return  null;
        }
    }

    /**
     *　Ajax请求数据
     * @param context
     * @param url 请求的url
     * @return　JSONArray
     */
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
            Looper.prepare();
            Toast.makeText(context, String.valueOf(e), Toast.LENGTH_LONG).show();
            Looper.loop();
        }
        return null;
    }

    /**
     * 加载Webview
     **/
    private void loadWebview() {
        webView = (WebView) findViewById(R.id.webview);
        // ChromeClient
        webView.setWebChromeClient(new WebChromeClient() {
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
                if (progress == 0) {
                    progressLayout.setVisibility(View.VISIBLE);
                }
                if (progress == 100 &&  webViewLoadProgress != 100) {
                    sendMsg(context, webView);
                    progressLayout.setVisibility(View.GONE);
                }
                if (webViewLoadProgress != progress) {
                    webViewLoadProgress = progress;
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            // 打开网页时不调用系统浏览器　而是直接在webview上显示 true拦截 false　webview加载url
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){
                String url = String.valueOf(request.getUrl());
                String[] url_list = url.split("://");
                String scheme = url_list[0];
                JSONArray schemes = new JSONArray();
                schemes.put("https");
                schemes.put("http");
                schemes.put("file");
                return  true;
//                if(Arrays.asList(schemes).contains(scheme)) {
//                    return false;
//                } else {
//                    Log.d("webview", "看看是什么URL:" + scheme + url_list[1]);
////                    https://boxer.baidu.com/scheme?source=1023751x&channel=1023764q&p1=1023764q&p2=844b&p3=1023751x&p4={%22browserid%22:%2224%22,%22baiduid%22:%228CD66C1D09B409090F31019DFB436247%22}&tokenData=%257B%2522activity_id%2522%253A227%252C%2522url%2522%253A%2522f157imj5AtTRFONWqSgD8Rj1Qyrjs0B2%252Fpages%252Fhfiveservicesearchmiddlepage%252Findex%252F%253Ftype%253D1%2526province_name%253D%2525E5%25258C%252597%2525E4%2525BA%2525AC%2525E5%2525B8%252582%2526rfrom%253D1023751x%2526rchannel%253D1023764q%2526ivk_p2%253D844b%2522%257D
//                    return true;
//                }
            }

            @Override // SSL证书错误
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.d("webview", String.valueOf(error));
                handler.proceed();
            }
        });

        // webview调试模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setWebContentsDebuggingEnabled(true);
        }
        // Webview设置
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // 允许js执行

//        webSettings.setMediaPlaybackRequiresUserGesture(false); // 视频可自动播放
        webSettings.setLoadsImagesAutomatically(true); // 自动加载图片
        webSettings.setAllowFileAccess(true); // 允许访问文件
        webSettings.setAllowContentAccess(true); //
        webSettings.setDomStorageEnabled(true);
        webSettings.setMixedContentMode(MIXED_CONTENT_ALWAYS_ALLOW);//https http都可以加载
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 缓存模式
//        webSettings.setUserAgentString("desktop"); // 可设置桌面环境还是移动端环境 影响排版布局
        webView.setBackgroundColor(2);
    }

    /**
     * 监听物理返回键
     */
    @Override
    public void onBackPressed() {
        backCallback _exit = new backCallback() {
            @Override
            public void extiApp() {
                if (backTime == 0) {
                    backTime = new Date().getTime();
                    Toast.makeText(context, "点击两次退出APP", Toast.LENGTH_LONG).show();
                } else {
                    long currTime = new Date().getTime();
                    if (currTime - backTime > 300) {
                        backTime = 0;
                        MainActivity.super.onBackPressed();
                    }
                }
            }
        };
        webviewBack(_exit);
    }
}