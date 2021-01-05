### 嵌入Csharp中的Webview

- 1. 减少原生开发压力
- 2. 和3399(Android webview)的模式保持一致
- 3. WebRTC多端统一
  

### 文档
[cef指令文档](https://www.showdoc.com.cn/984508533085475?page_id=5326537850423557)

### 遇到的问题
- 1. CEF中candidate信息错误  无法获取本机外网host IP
    
    ```
    // 现象描述  订阅远程时无画面
    1. Janus 后台打印错误 
        Error resolving mDNS address (f4843e32-4517-XXXXXX.local): Error resolving f4843e32-4517-XXXXXX.local: Name or service not known 
    2. Janus WebSocket 中candidate信息错误
        {"janus":"trickle","candidate":{"candidate":"candidate:105670005 1 udp 2113937151 f4843e32-4517-4247-a64d-05b40cbe5f8e.local 60232 typ host generation 0 ufrag QX9D network-cost 999","sdpMid":"audio","sdpMLineIndex":0},"transaction":"nvi6IpImr6AD","session_id":7320932495755019,"handle_id":6298521037923497}
    3. 正常的candidate信息
        "janus":"trickle","candidate":{"candidate":"candidate:2265211841 1 udp 1686052607 36.112.70.164 47543 typ srflx raddr 192.168.12.27 rport 53965 generation 0 ufrag 5xyL network-id 1 network-cost 50","sdpMid":"audio","sdpMLineIndex":0},"session_id":4022183580024424,"handle_id":7136488003820894,"transaction":"874f37e1bdf743a29ee97c07b06ae574"}

    // 查询的文档
    https://github.com/cefsharp/CefSharp/issues/3075<br/>
    https://bloggeek.me/psa-mdns-and-local-ice-candidates-are-coming/ 

    // 解决方法
    settings.CefCommandLineArgs.Add("disable-features", "WebRtcHideLocalIpsWithMdns");//禁用mdns，允许暴露内网ip

    ```

- 2. getUserMedia是发布的是桌面 
    - 2.1 现象描述：
        在有些电脑上出现发布的是桌面而不是摄像头
    - 2.2 问题原因：
        通过运行“ffmpeg -list_devices true -f dshow -i dummy”可以发现这些电脑上都安装了screen-capture-recorder这个桌面虚拟摄像头。cef自动选择摄像头是选到了这个虚拟摄像头。
    - 2.3 解决办法：
        取消桌面虚拟摄像头。管理员身份运行cmd，切换到教学互动安装目录，运行regsvr32 -u screen-capture-recorder.dll

- 3. 视频无法自动播放
  
```
// Google autoplay policy禁用了视频的带声音自动播放功能
https://developers.google.com/web/updates/2017/09/autoplay-policy-changes

// 解决方法 
settings.CefCommandLineArgs.Add("autoplay-policy", "no-user-gesture-required");//自动播放音视频 

// 对应的Android上Webview的参数设置
<WebView mediaPlaybackRequiresUserAction={false}></WebView>
```


### 其他相关文档和测试工具
[CefCsharp](https://github.com/cefsharp/CefSharp)<br/>
[CEF_Scharp_command_line](https://peter.sh/experiments/chromium-command-line-switches/)<br/>
[WebRTC ICE TEST](https://webrtc.github.io/samples/src/content/peerconnection/trickle-ice/)