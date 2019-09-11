package com.plus.thread;

import com.github.binarywang.java.emoji.EmojiConverter;
import com.plus.common.DyUtil;
import com.plus.common.RoomConstant;
//import com.plus.controller.DyController;
import com.plus.controller.WebSocket;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.Map;

public class CrawlerThread implements Runnable {

    private Socket client;
    private int roomId;
    
    private EmojiConverter emojiConverter = EmojiConverter.getInstance();

    public CrawlerThread(int roomId) {
        this.roomId = roomId;
    }
    @Override
    public void run(){
        connectToDy();
        WebSocket webSocket = new WebSocket();
        while (true) {
            String s = DyUtil.receiveMsg(client);
            if (s.equals(DyUtil.INVALID_MSG)) {
                connectToDy();
                continue;
            }

            if (WebSocket.getOnlineCount() < 1) {
            	continue;
            }
            Map<String, String> m = DyUtil.getMsg(s);
            String danMu = m.get("txt");
            if (danMu != null && !danMu.equals("")) {
                String name = m.get("nn");
                String cardName = m.get("bnn");
                String cardLevel = m.get("bl");
                int roomId = Integer.valueOf(m.get("rid"));
                String level = m.get("level");
                String time = DyUtil.DF.format(new Date());

				try {
					String str = "";
					// ***注意：RoomConstant.roomMap.get 用来获取自己定义的主播名字，如果roomMap中通过key没有找到value将会报错。***
					// ***如不需要不显示主播名即可查看任意直播间的弹幕***
					if ("".equals(cardName) || null == cardName) {
						str = (time + " " + RoomConstant.roomMap.get(roomId) + " " + level + "级  " + "[" + name + "] : "
								+ danMu).toString();
					} else {
						str = (time + " " + RoomConstant.roomMap.get(roomId) + " " + level + "级  牌牌[" + cardName
								+ cardLevel + "级] " + "[" + name + "] : " + danMu).toString();
					}
					System.out.println(str);
					webSocket.sendDanmu(String.valueOf(roomId), str);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("format not correct");
				}
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void connectToDy() {
        try {
            if (client != null) {
                client.close();
            }
            client = new Socket(DyUtil.HOST, DyUtil.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String loginMsg = "type@=loginreq/roomid@="+ String.valueOf(roomId) + "/";
        DyUtil.sendRequest(client, loginMsg);
        String joinGroupMsg =  "type@=joingroup/rid@=" + String.valueOf(roomId) +"/gid@=-9999/";
        DyUtil.sendRequest(client, joinGroupMsg);
    }

    private void logout() {
        String logoutMsg = "type@=logout/";
        DyUtil.sendRequest(client, logoutMsg);
    }
}
