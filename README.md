### Webview

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

    