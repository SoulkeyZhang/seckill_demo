package com.zhang.entity;

import java.util.HashMap;

// 前端ajax请求得到的对象可以用hashmap来存储
public class Resp extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public Resp() {
        this.put("code",0);
    }

    public Resp(Integer status, Object msg) {
        this.put("code",status);
        this.put("msg",msg);
    }

    public static Resp error(int code, String msg) {
        return new Resp(code,msg);
    }

    public static Resp error(Object obj) {
        return new Resp(500,obj);
    }

    public static Resp error() {
        return error(500,"未知异常，请联系管理员");
    }

    public static Resp ok(int code,String msg) {
        return new Resp(code,msg);
    }

    public static Resp ok(Object obj) {
        return new Resp(200,obj);
    }

    public static Resp ok() {
        return ok(200,null);
    }

}
