package com.gudong.browser.browserhomepage.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by gudd on 2024/5/7.
 */
public class BaseActivity extends AppCompatActivity {

    private StatusBarColorFlag statusBarColorFlag = StatusBarColorFlag.WHITE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatusBarColorFlag(statusBarColorFlag);
    }

    private void setStatusBarColorFlag(StatusBarColorFlag flag){
        if (flag == StatusBarColorFlag.WHITE) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (flag == StatusBarColorFlag.BLACK) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    enum StatusBarColorFlag{
        WHITE,BLACK
    }
}
