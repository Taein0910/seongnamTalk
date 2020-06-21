package com.icecream.snTalk;

public class Chat {

    static String username;
    static String msg;

    public Chat(String idByANDROID_ID, String msg) {
        // Constructor required for Firebase Database
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Chat.username = username;
    }

    public static String getMsg() {
        return msg;
    }

    public static void setMsg(String msg) {
        Chat.msg = msg;
    }
}