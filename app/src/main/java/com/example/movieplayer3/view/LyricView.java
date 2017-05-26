package com.example.movieplayer3.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.movieplayer3.domain.Lyric;

import java.lang.annotation.ElementType;
import java.util.ArrayList;

/**
 * Created by chenyuelun on 2017/5/26.
 */

public class LyricView extends TextView {
    private int width;
    private int height;
    private Paint paint;
    private ArrayList<Lyric> lyrics;
    private float textHeight;
    private int currentPosition;

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
        lyrics = new ArrayList<>();
        for(int i = 0; i < 1000; i++) {
          Lyric lyric = new Lyric();
            lyric.setContent(i+"aaaaaaaa"+i);
            lyric.setDuration(2000);
            lyric.setTimePoint(2000*i);
            lyrics.add(lyric);
        }
        textHeight = paint.getTextSize();

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
        if(lyrics != null && lyrics.size()>0) {
            paint.setColor(Color.GREEN);
            String currentContent = lyrics.get(index).getContent();
            canvas.drawText(currentContent, width / 2, height / 2, paint);


            //绘制上面部分
            paint.setColor(Color.WHITE);
            float tempY = height/2;

            for(int i = index -1; i >= 0 ; i--) {
                tempY = tempY - textHeight;
                if(tempY < 0) {
                    break;
                }
                String preContent = lyrics.get(i).getContent();
                canvas.drawText(preContent, width / 2, tempY, paint);
            }


            //绘制下面的部分

            tempY = height/2;

            for(int i = index +1; i < lyrics.size() ; i++) {
                tempY = tempY + textHeight;
                if(tempY > height) {
                    break;
                }
                String preContent = lyrics.get(i).getContent();
                canvas.drawText(preContent, width / 2, tempY, paint);
            }
        }else {
            canvas.drawText("未搜索到歌词", width / 2, height / 2, paint);
        }
    }

    public void setShowNextLyric(int currentPosition) {
        this.currentPosition = currentPosition;
        if(lyrics!= null && lyrics.size()>0) {
            for(int i = 1; i < lyrics.size(); i++) {
              if(currentPosition < lyrics.get(i).getTimePoint()) {
                  if(currentPosition>lyrics.get(i-1).getTimePoint()) {
                      index = i - 1;
                      break;
                  }

              }
            }

            invalidate();
        }



    }
}
