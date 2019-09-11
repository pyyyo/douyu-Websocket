package com.plus.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import com.plus.thread.CrawlerThread;
import com.plus.thread.ThreadPool;

/**
 * @ServerEndPoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端，
 * 注解的值将被用于监听用户连接的终端访问URL地址，客户端可以通过这个URL连接到websocket服务器端
 */
@ServerEndpoint("/websocket/{roomId}")
@Component
public class WebSocket {
    private static int onlineCount=0;
    private Session session;
    private static final Map<String, Set<Session>> rooms = new ConcurrentHashMap<String, Set<Session>>();
    
    @OnOpen
    public void onOpen(@PathParam("roomId") String roomId,Session session){
		if (!rooms.containsKey(roomId)) {
			// 创建房间不存在时，创建房间
			Set<Session> room = new HashSet<Session>();
			// 添加用户
			room.add(session);
			rooms.put(roomId, room);
		} else {
			// 房间已存在，直接添加用户到相应的房间
			rooms.get(roomId).add(session);
		}

        this.session=session;
        addOnlineCount();
        System.out.println("有新连接加入！当前在线人数为"+getOnlineCount());
    }
 
    @OnClose
    public void onClose(@PathParam("roomId") String roomId){
    	rooms.get(roomId).remove(session);
        subOnlineCount();
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }
 
    @OnMessage
    public void onMessage(@PathParam("roomId") String roomId,String message,Session session) throws IOException{
        System.out.println("请求房间号："+roomId);
        
        //设置线程池最大数
        ThreadPool pool = new ThreadPool(8);
        pool.execute(new CrawlerThread(Integer.valueOf(roomId)));
        
        session.getBasicRemote().sendText("***连接" + roomId + "房间中，请稍候***");
    }
    
    public void sendDanmu(String roomId,String message) throws IOException {
    	sendMessage(roomId, message);
    }
 
    @OnError
    public void onError(Session session,Throwable throwable){
        System.out.println("发生错误！");
        throwable.printStackTrace();
    }
    //下面是自定义的一些方法
    public void sendMessage(String roomId,String message) throws IOException {
    	for (Session session : rooms.get(roomId)) {
    		synchronized (session) {
    			session.getBasicRemote().sendText(message);
			}
		}
    }
    
    public static synchronized int getOnlineCount(){
        return onlineCount;
    }
    public static synchronized void addOnlineCount(){
        WebSocket.onlineCount++;
    }
    public static synchronized void subOnlineCount(){
        WebSocket.onlineCount--;
    }
}