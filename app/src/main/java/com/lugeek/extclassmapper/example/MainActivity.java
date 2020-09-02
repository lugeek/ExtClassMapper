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


@ExtClassMapper("main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        for (String key : ECMapper.targetsIndex.keySet()) {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setText(key + " : " + ECMapper.getsInstance().getClz(key).getSimpleName() + ".class");
            linearLayout.addView(textView);
        }

    }
}