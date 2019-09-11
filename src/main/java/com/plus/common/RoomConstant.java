package com.plus.common;

import java.util.HashMap;
import java.util.Map;

public class RoomConstant {
    private RoomConstant() {}

    private static final String DSM = "芜湖大司马丶";
    private static final String ENCHONG = "主播红老嗨";
    private static final String OTTO = "电棍";
    private static final String ZSF = "网友张顺飞";
    private static final String PDD = "PDD";
    private static final String LPL = "LPL";

    public static Map<Integer, String> roomMap = new HashMap<Integer, String>(){
        {
        	put(606118, DSM);
        	put(4430775, ENCHONG);
        	put(12306, OTTO);
        	put(2561707, ZSF);
        	put(101, PDD);
        	put(288016, LPL);
        }
    };

    public static final int ROOM_NUMS = 4;

}
