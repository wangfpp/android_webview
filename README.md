### Webview

- Webview就是Chrome内核　就有语法兼容性/功能性问题

  `````javascript
  // 解决方法
  1. 升级Webview版本
  	https://cloud.tencent.com/developer/article/1388497
  2. 降低低版本的语法
  	2.1 使用低版本语法
      2.2 编译高版本语法
      "browserslist": {
          "production": [
            ">0.2%",
            "not dead",
            "not op_mini all",
            "Chrome > 50"
          ],
          "development": [
            "last 3 chrome version",
            "last 1 firefox version",
            "last 1 safari version"
          ]
        },
  `````

  

- 加载远程HTTP[s] 

  - Android的网络请求权限

    ```xml
    // AndroidManifest.xml需要添加网络权限
    <uses-permission android:name="android.permission.INTERNET" />
    ```

    

  - SSL错误处理   原生更方便

    ```java
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) 	{
        	Log.d("webview", String.valueOf(error));
    		handler.proceed();
    	}
    // 在RN上面需要修改Webview库的源码 可以自己使用内外NPM发布一个插件
    // http://172.16.1.110:4873/-/web/detail/react-native-webview
    // nrm 管理npm源
    ```

    

    

- 加载本地HTML JS等

  - 注入方法 js能调用Webview外的方法

    ````java
    webSettings.setJavaScriptEnabled(true); // 需开启此配置 允许运行js
    ````

    

  - JS内部的console无法显示 alert无法生效

    ```java
    webview.setWebChromeClient(new WebChromeClient() {
    	@Override
    	public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
    		Log.d("webview", String.valueOf(consoleMessage));
    		return super.onConsoleMessage(consoleMessage);
    	}
    });
    // 实例化WebChromeClient就可使alert显示
    // 重新实现consoleMessage可以自定义cosole输出
    ```

- setWebChromeClient和setWebViewClient

  ```xml
  setWebChromeClient 窗口管理　alert fileSelect  相当于是浏览器
   
  setWebViewClient 请求管理 SSL_ERROR url_loading等　相当于是页面
  ```

  
  
- 在Webview调用原生方法 比如获取电池电量

  ```
  1. 广播
  2. 监听事件
  3. 调用JS函数
  ```
  
- RN的调试是只有一个client从Server获取数据　Android是一个完成的APK包.




.
├── app
│   ├── build.gradle
│   ├── libs
│   ├── proguard-rules.pro
│   ├── release
│   │   ├── output-metadata.json
│   │   └── webview_study-0.0.1_2021-01-05.apk
│   └── src
│       └── main
│           ├── AndroidManifest.xml
│           ├── assets
│           │   └── web
│           │       ├── css
│           │       │   ├── comm.css
│           │       │   ├── detail.css
│           │       │   └── index.css
│           │       ├── html
│           │       │   └── detail.html
│           │       ├── img
│           │       ├── index.html
│           │       └── js
│           │           └── index.js
│           ├── java
│           │   └── com
│           │       └── study
│           │           └── android_wv
│           │               ├── BatteryChangeReceiver.java
│           │               ├── Jsinterface.java
│           │               ├── MainActivity.java
│           │               └── ViewDetailActivity.java
│           └── res
│               ├── drawable
│               │   └── ic_launcher_background.xml
│               ├── drawable-v24
│               │   └── ic_launcher_foreground.xml
│               ├── layout
│               │   ├── activity_main.xml
│               │   └── view_detail.xml
│               ├── mipmap-anydpi-v26
│               │   ├── ic_launcher_round.xml
│               │   └── ic_launcher.xml
│               ├── mipmap-hdpi
│               │   ├── ic_launcher.png
│               │   └── ic_launcher_round.png
│               ├── mipmap-mdpi
│               │   ├── ic_launcher.png
│               │   └── ic_launcher_round.png
│               ├── mipmap-xhdpi
│               │   ├── ic_launcher.png
│               │   └── ic_launcher_round.png
│               ├── mipmap-xxhdpi
│               │   ├── ic_launcher.png
│               │   └── ic_launcher_round.png
│               ├── mipmap-xxxhdpi
│               │   ├── ic_launcher.png
│               │   └── ic_launcher_round.png
│               ├── values
│               │   ├── colors.xml
│               │   ├── strings.xml
│               │   └── themes.xml
│               └── values-night
│                   └── themes.xml
├── build.gradle
├── CEF.md
├── gradle.properties
├── gradlew
├── gradlew.bat
├── local.properties
├── README.md
├── settings.gradle
└── version.json

27 directories, 44 files
