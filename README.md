# douyu-Websocket
根据斗鱼api使用SpringBoot和Websocket开发，实现实时查看斗鱼直播间弹幕

部署后maven update即可运行，默认端口号8080，目前项目没有发现BUG，没有进行过多端测试，仅仅自己玩了下，小玩具还是不错的。

## 问题：
**1.关闭连接按钮仅仅是关闭Websocket连接，线程池内的线程还在继续跑浪费资源，暂时不知道咋解决**
