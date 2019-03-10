package com.example.myproject;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.lzy.okgo.OkGo;


public class MyApp extends Application {


    
    public static final String UPLOAD_AVATAR_URL="http://120.79.87.68:5000/insertAvatar";
    public static final String AVATAR_FILE_NAME = "spec_avatar.png";
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        OkGo.getInstance().init(this);

    }
}
