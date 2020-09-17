package com.lugeek.extclassmapper.example;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lugeek.extclassmapper.annotations.ExtClassMapper;
import com.lugeek.extclassmapper.api.ECMapper;

import java.util.HashMap;
import java.util.Map;


@ExtClassMapper(value = "main", group = "testGroup")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        for (String group : ECMapper.getsInstance().groupSet()) {
            Map<String, Class<?>> targetMap = ECMapper.getsInstance().getGroup(group);
            for (String target : targetMap.keySet()) {
                TextView textView = new TextView(this);
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setText(group + " : " + target + " : " + ECMapper.getsInstance().getClz(group, target).getSimpleName() + ".class");
                linearLayout.addView(textView);
            }
        }
    }
}