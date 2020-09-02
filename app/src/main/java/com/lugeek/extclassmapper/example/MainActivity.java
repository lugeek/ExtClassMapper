package com.lugeek.extclassmapper.example;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lugeek.extclassmapper.annotations.ExtClassMapper;

@ExtClassMapper("haha")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}