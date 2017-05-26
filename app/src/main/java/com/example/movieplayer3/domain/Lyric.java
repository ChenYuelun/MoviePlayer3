package com.example.movieplayer3.domain;

/**
 * Created by chenyuelun on 2017/5/26.
 */

public class Lyric {
    //单行歌词内容
    private String content;
    //高亮时间
    private long duration;
    //时间戳
    private long timePoint;

    public Lyric() {
    }

    public Lyric(String content, long duration, long timePoint) {
        this.content = content;
        this.duration = duration;
        this.timePoint = timePoint;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(long timePoint) {
        this.timePoint = timePoint;
    }

    @Override
    public String toString() {
        return "Lyric{" +
                "content='" + content + '\'' +
                ", duration=" + duration +
                ", timePoint=" + timePoint +
                '}';
    }
}
