package com.plus.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DyUtil {

    private DyUtil() throws Exception{
        throw new Exception("Construct error");
    }

    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";

    public static final String HOST = "119.96.201.28"; // openbarrage.douyutv.com
    public static final int PORT = 8601;
    // 消息头长度 = 消息长度(4) + 消息类型,加密,保留字段(4) + 内容长度(?) + 结尾标识符(1)
    public static final int DATA_HEAD_LEN = 4 + 4 + 1;
    public static final int CODE = 689;
    public static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String INVALID_MSG = "-1";

    /**
     * 以小端模式将int转成byte[]
     * @param data int形式的数据
     * @return 字节形式的数据
     */
    private static byte[] intToBytes(int data) {
        byte[] src = new byte[4];
        src[3] = (byte) ((data >> 24) & 0xFF);
        src[2] = (byte) ((data >> 16) & 0xFF);
        src[1] = (byte) ((data >> 8) & 0xFF);
        src[0] = (byte) (data & 0xFF);
        return src;
    }

    /**
     * 以小端模式将byte[]转成int
     * @param src 字节形式的数组
     * @return int形式的数据
     */
    private static int bytesToInt(byte[] src) {
        return ((src[0] & 0xFF)
                | ((src[1] & 0xFF) << 8)
                | ((src[2] & 0xFF) << 16)
                | ((src[3] & 0xFF) << 24));
    }

    public static void sendRequest(Socket client, String msg){
    	//System.out.println("发送请求给斗鱼服务器。。。");
        try {
            int msgLength = 4 + 4 +msg.length() + 1;
            byte[] dataLength = intToBytes(msgLength);
            byte[] dataHead = intToBytes(DyUtil.CODE);
            byte[] data = msg.getBytes(StandardCharsets.ISO_8859_1);

            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

            byteArray.write(dataLength);
            byteArray.write(dataLength);
            byteArray.write(dataHead);
            byteArray.write(data);
            byteArray.write(0);

            OutputStream out = client.getOutputStream();
            out.write(byteArray.toByteArray());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String receiveMsg(Socket client) {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        try{
            InputStream inputStream = client.getInputStream();

            int dataLength = getResponseLength(inputStream);
            int contentLen2 = getResponseLength(inputStream);
            int msgType = getResponseLength(inputStream);
            //System.out.println("从输入流中拿值。。。数据长度："+dataLength);
            if (dataLength <= 8 || dataLength >= 1032) {
                return INVALID_MSG;
            }

            dataLength = dataLength - 8;

            int len;
            int readLen = 0;
            byte[] bytes = new byte[dataLength];
            while ((len = inputStream.read(bytes, 0 , dataLength - readLen)) != -1) {
                byteArray.write(bytes, 0 ,len);
                readLen += len;
                if (readLen == dataLength) {
                    break;
                }
            }

        } catch (IOException e){
            e.printStackTrace();
        }

        return byteArray.toString();
    }

    private static int getResponseLength(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[4];
        inputStream.read(bytes, 0 , 4);
        return bytesToInt(bytes);
    }

    public static Map<String, String> getMsg(String s) {
        String[] messages = s.split("/");
        Map<String, String> m = new HashMap<>();
        for (String message : messages) {
            String[] ms = message.split("@=");
            if (ms.length >= 2) {
                m.put(ms[0], ms[1]);
            }
        }
        return m;
    }

    /**
     * 检查用户名中是否包含特殊字符
     * @param userName 用户名
     * @return 如果包含特殊字符则无效
     */
    public static boolean isUserNameValid(String userName) {
        String specialCharacterRegEx="[`~!@#$%^&*()+=|{}':;,\\[\\].<>/?！￥…（）—【】‘；：”“’。，、？]";
        Pattern pattern = Pattern.compile(specialCharacterRegEx);
        Matcher matcher = pattern.matcher(userName);
        return !matcher.find();
    }
}
