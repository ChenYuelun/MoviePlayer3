// IMusicPlayService.aidl
package com.example.movieplayer3;

// Declare any non-default types here with import statements

interface IMusicPlayService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);


             //根据位置从集合取数据，开始播放
                 void playMusic(int position);


                //开始播放音乐
                 void start ();



                //暂停
                 void pause();

                //获取总时长
                 int duration();

                //获取进度
                 int currentPosition();

                //设置进度
                 void seekTo(int position);

                //获取歌曲名
                 String getMusicName();


                //获取歌手名
                 String getArtistName();


                //获取歌曲路径
                 String getMusicPath();

                //播放下一个
                 void playNext();


                //播放上一个
                 void playPre();

                 boolean isPlaying();
}
