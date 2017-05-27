package com.example.movieplayer3.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.example.movieplayer3.domain.Lyric;
import com.example.movieplayer3.utils.DensityUtil;

import java.util.ArrayList;

/**
 * Created by chenyuelun on 2017/5/26.
 */

public class LyricView extends TextView {
    private final Context context;
    private int width;
    private int height;
    private Paint paint;
    private ArrayList<Lyric> lyrics;
    private float textHeight;
    private float currentPosition;
    private float duration;
    private float timePoint;

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initData();
    }

    private void initData() {
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        paint.setTextSize(DensityUtil.dip2px(context,18));
//        lyrics = new ArrayList<>();
//        for(int i = 0; i < 1000; i++) {
//          Lyric lyric = new Lyric();
//            lyric.setContent(i+"aaaaaaaa"+i);
//            lyric.setDuration(2000);
//            lyric.setTimePoint(2000*i);
//            lyrics.add(lyric);
//        }
        textHeight = DensityUtil.dip2px(context,25);

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    private int index = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawText("未搜索到歌词", width / 2, height / 2, paint);

        if (lyrics != null && lyrics.size() > 0) {
            if(index != lyrics.size()-1) {
                float push = 0;

                if (duration == 0) {
                    push = 0;
                } else {
                    // 这一句花的时间： 这一句休眠时间  =  这一句要移动的距离：总距离(行高)
                    //这一句要移动的距离 = （这一句花的时间/这一句休眠时间） * 总距离(行高)
                    push = ((currentPosition - timePoint) / duration) * textHeight;
                    Log.e("TAG","push" + push);
                }

                canvas.translate(0, -push);

            }



            paint.setColor(Color.GREEN);
            String currentContent = lyrics.get(index).getContent();
            canvas.drawText(currentContent, width / 2, height / 2, paint);


            //绘制上面部分
            paint.setColor(Color.WHITE);
            float tempY = height / 2;

            for (int i = index - 1; i >= 0; i--) {
                tempY = tempY - textHeight;
                if (tempY < 0) {
                    break;
                }
                String preContent = lyrics.get(i).getContent();
                canvas.drawText(preContent, width / 2, tempY, paint);
            }


            //绘制下面的部分

            tempY = height / 2;

            for (int i = index + 1; i < lyrics.size(); i++) {
                tempY = tempY + textHeight;
                if (tempY > height) {
                    break;
                }
                String preContent = lyrics.get(i).getContent();
                canvas.drawText(preContent, width / 2, tempY, paint);
            }
        } else {
            canvas.drawText("未搜索到歌词", width / 2, height / 2, paint);
        }
    }

    public void setShowNextLyric(int currentPosition) {
        this.currentPosition = currentPosition;
        if (lyrics != null && lyrics.size() > 0) {
            for (int i = 1; i < lyrics.size(); i++) {
                if (currentPosition < lyrics.get(i).getTimePoint()) {
                    index = i - 1;
                    duration = lyrics.get(index).getDuration();
                    timePoint = lyrics.get(index).getTimePoint();
                    break;
                }
                if (currentPosition > lyrics.get(lyrics.size() - 1).getTimePoint()) {
                    index = lyrics.size() - 1;

                    break;
                }
            }

            invalidate();
        }



    }

    public void setLysicData(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;

    }
}
