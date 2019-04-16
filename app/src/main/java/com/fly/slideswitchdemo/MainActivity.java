package com.fly.slideswitchdemo;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity implements ToggleButtonView.OnToggleStateChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToggleButtonView tbv = (ToggleButtonView)findViewById(R.id.tbv);
        //设置开关背景显示
        tbv.setSwitchBackground(R.drawable.switch_background);
        //设置滑块显示
        tbv.setSlideImage(R.drawable.slide_button_background);

        //设置为打开状态
        tbv.setCurrentState(true);

        //设置状态监听
        tbv.setOnToggleStateChangedListener(this);
    }

    @Override
    public void onStateChanged(ToggleButtonView view, boolean state) {
        Toast.makeText(this,state ? "打开了开关！":"关闭了开关！",Toast.LENGTH_SHORT).show();
    }
}
