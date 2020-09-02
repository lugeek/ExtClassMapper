package com.lugeek.extclassmapper.example;

import android.app.Application;

import com.lugeek.extclassmapper.api.ECMapper;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ECMapper.init(this);
    }
}
