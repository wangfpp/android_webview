window.onload = function() {
    let btn_node = document.querySelector("#btn"),
    back = document.querySelector("#back"),
    root= document.querySelector("#root");
    btn_node.onclick = function() {
        console.log("谁在Android上点击Webview的按钮了")
        alert(111)
        js.sayName("wangfpp")
        let battery = js.getBattery();
        console.log(battery);
    }
}

/**
*监听Java的消息传递 驱动界面更新
**/
window.addEventListener("message", msg => {
    let { data } = msg,
    dom_str = "";
    try{
        data = JSON.parse(data);
    } catch(err) {
        data = [];
    }
    data.forEach(item => {
        let { id, subject, class_name, teacher } = item;
        console.log(JSON.stringify(item));
        let cover_img_path = `http://172.16.1.110:6081/static/media/${id}/student_cover_img.png`
        dom_str += `<div _data=${id} class="list">
            <img src="${cover_img_path}" onerror="loadImgErr(this)"/>
            <div>${class_name}-${subject}-${teacher}</div>
        </div>`
    })
    root.innerHTML = dom_str;
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