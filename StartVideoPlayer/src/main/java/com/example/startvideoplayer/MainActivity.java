package com.example.startvideoplayer;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void natives(View v) {
        Intent intent = new Intent();
        intent.setDataAndType(Uri.parse("http://192.168.31.40:8080/Web_Server/oppo - 2.mp4"), "video/*");
        startActivity(intent);
    }

    public void internet(View v) {
        Intent intent = new Intent();
        intent.setDataAndType(Uri.parse("http://vf1.mtime.cn/Video/2017/05/17/mp4/170517102706759383.mp4"), "video/*");
        startActivity(intent);

    }
}
