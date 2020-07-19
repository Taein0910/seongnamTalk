package com.icecream.snTalk;

public class Chat {

    static String username;
    static String msg;
    static String timestamp;

    public Chat(String idByANDROID_ID, String msg, String timestamp) {
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

    public static String getTimestamp() {
        return timestamp;
    }

    public static void setTimestamp(String timestamp) {
        Chat.timestamp = timestamp;
    }
}