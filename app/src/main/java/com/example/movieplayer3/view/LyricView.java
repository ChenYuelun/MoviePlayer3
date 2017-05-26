package com.example.movieplayer3.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by chenyuelun on 2017/5/26.
 */

public class LyricView extends TextView {
    private int width;
    private int height;
    private Paint paint;

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    private void initData() {
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        paint.setTextSize(23);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText("未搜索到歌词", width / 2, height / 2, paint);
    }
}
