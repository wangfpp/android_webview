window.onload = function() {
    let btn_node = document.querySelector("#btn"),
    battery = document.querySelector("#battery"),
    root= document.querySelector("#root");
    let battery_ = js.getBattery();
    try{
        battery_ = JSON.parse(battery_);
        batteryListener(battery_);
    }catch(err) {
        console.error(err);
    }
    btn_node.onclick = function() {
        console.log("谁在Android上点击Webview的按钮了");
        js.sayName("wangfpp");
    }
}

/**
* 华氏度转摄氏度
**/
function f2c(f) {
    return (f - 32) / 1.8;
}

/**
*电池电量发生变化
**/
function batteryListener(level_obj) {
    try {
        let { temp, level, isCharge } = level_obj;
        if (temp) {
            battery.innerHTML = `电量:${level}%, 温度${temp/10}℃, 充电:${isCharge}`;
        } else {
            battery.innerHTML = `电量:${level}%, 充电:${isCharge}`;
        }

    }catch(err) {
        alert(err);
    }

}
/**
*监听Java的消息传递 驱动界面更新
**/
window.addEventListener("message", msg => {
    console.log("Webview的msg监听", msg);
    let { data } = msg,
    dom_str = "";
    try{
        data = JSON.parse(data);
    } catch(err) {
        data = [];
    }
    data.forEach(item => {
        let { id, subject, class_name, teacher } = item;
        let cover_img_path = `http://172.16.1.110:6081/static/media/${id}/student_cover_img.png`
        dom_str += `<div _data=${id} class="list">
            <img src="${cover_img_path}" _data=${id}  onerror="loadImgErr(this)"/>
            <div class="title" _data=${id}>班级:${class_name} 课程: ${subject} 教师:${teacher}</div>
        </div>`
    })
    root.innerHTML = dom_str;
    let list = document.querySelectorAll(".list");
    console.log(list);
    list.forEach(li_item => {
        li_item.onclick = e => {
            let { target } = e,
            id = target.getAttribute("_data");
            console.log(e, target);
            window.location.href  = `./html/detail.html?video_url=http://172.16.1.110:6081/static/media/${id}`;
        }
    })
}, false)




/**
*此函数是供给Java调用的
**/
function jsfun(name, age) {
    return `${name}-${age}`;
}

/**
* 图像加载错误 给一个默认图
**/
function loadImgErr (e) {
    e.src = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20180118%2Fa0163c6be9d247918669229bed6c7280.png&refer=http%3A%2F%2F5b0988e595225.cdn.sohucs.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1611992657&t=d4a0be3790b0f45f54529388fe4759ac"
}