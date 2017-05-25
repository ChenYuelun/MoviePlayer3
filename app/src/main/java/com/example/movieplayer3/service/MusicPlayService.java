package com.example.movieplayer3.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.movieplayer3.IMusicPlayService;
import com.example.movieplayer3.R;
import com.example.movieplayer3.activity.LocalMusicPlayerActivity;
import com.example.movieplayer3.domain.MediaItem;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by chenyuelun on 2017/5/24.
 */

public class MusicPlayService extends Service {


    public static final String ONPREPARED = "myMusicisonprepared";
    private IMusicPlayService.Stub stub = new IMusicPlayService.Stub() {
        MusicPlayService service = MusicPlayService.this;

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void playMusic(int position) throws RemoteException {
            Log.e("TAG","stub_playMusic");
            service.playMusic(position);
        }

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public int duration() throws RemoteException {
            return service.duration();
        }

        @Override
        public int currentPosition() throws RemoteException {
            return service.currentPosition();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            service.seekTo(position);
        }

        @Override
        public String getMusicName() throws RemoteException {
            return service.getMusicName();
        }

        @Override
        public String getArtistName() throws RemoteException {
            return service.getArtistName();
        }

        @Override
        public String getMusicPath() throws RemoteException {
            return service.getMusicPath();
        }

        @Override
        public void playNext() throws RemoteException {
            service.playNext();
        }

        @Override
        public void playPre() throws RemoteException {
            service.playPre();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mediaPlayer.isPlaying();
        }

        @Override
        public void setPlayMode(int playMode) throws RemoteException {
            service.setPlayMode(playMode);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }
    };



    private int position;
    private MediaPlayer mediaPlayer;
    private MediaItem mediaItem;
    private NotificationManager notificationManager;

    public static int REPEAT_NORMAL = 1;
    public static int REPEAT_SINGLE = 2;
    public static int REPEAT_ALL = 3;
    public static int REPEAT_RANDOM = 4;

    private int playMode = REPEAT_NORMAL;
    public static boolean nextFromUser = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    private ArrayList<MediaItem> list;

    @Override
    public void onCreate() {
        super.onCreate();
        getLocalAudioDatas();
    }

    private void getLocalAudioDatas() {
        new Thread() {
            public void run() {
                list = new ArrayList<>();
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ARTIST};
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        Log.e("TAG", "name==" + name + ",duration==" + duration + ",data===" + data);

                        list.add(new MediaItem(name, duration, size, data,artist));
                    }

                    cursor.close();

                }

            }
        }.start();
    }

    //根据位置从集合取数据，开始播放
    private void playMusic(int position) {
        Log.e("TAG","service_playMusic");
        this.position = position;
        if(list != null && list.size()>0) {
            if(position < list.size()) {
                mediaItem = list.get(position);
                if(mediaPlayer != null) {
                    mediaPlayer.reset();
                    mediaPlayer = null;
                }

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(mediaItem.getData());
                    mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                    mediaPlayer.setOnErrorListener(new MyOnErrorListener());
                    mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                    mediaPlayer.prepareAsync();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            sendChange(ONPREPARED);
            start();
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Intent intent = new Intent(MusicPlayService.this, LocalMusicPlayerActivity.class);
            intent.putExtra("fromNotification",true);
            PendingIntent pi= PendingIntent.getActivity(MusicPlayService.this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new Notification.Builder(MusicPlayService.this)
                    .setSmallIcon(R.drawable.notification_music_playing)
                    .setContentTitle("MyMusic")
                    .setContentText("正在播放"+ mediaItem.getName())
                    .setContentIntent(pi)
                    .build();
            notificationManager.notify(0,notification);

        }
    }

    private void sendChange(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);

    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            playNext();
            return true;
        }
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            playNext();
        }
    }


    //开始播放音乐
    private void start() {
        mediaPlayer.start();
    }


    //暂停
    private void pause() {
        mediaPlayer.pause();
    }

    //获取总时长
    private int duration() {
        return mediaPlayer.getDuration();
    }

    //获取进度
    private int currentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    //设置进度
    private void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    //获取歌曲名
    private String getMusicName() {
        return mediaItem.getName();
    }


    //获取歌手名
    private String getArtistName() {
        return mediaItem.getArtist();
    }


    //获取歌曲路径
    private String getMusicPath() {
        return "";
    }

    //播放下一个
    private void playNext() {
        if(playMode != REPEAT_SINGLE || nextFromUser) {
            position++;
            nextFromUser = false;
            if(position > list.size()-1) {
                if(playMode == REPEAT_NORMAL || playMode == REPEAT_SINGLE) {
                    position = list.size()-1;
                    Toast.makeText(MusicPlayService.this, "已到最后一首", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(playMode == REPEAT_ALL) {
                    position = 0;
                }
            }
        }
        playMusic(position);

    }


    //播放上一个
    private void playPre() {
        if(playMode != REPEAT_SINGLE || nextFromUser) {
            position--;
            nextFromUser = false;
            if(position < 0) {
                if(playMode == REPEAT_NORMAL || playMode == REPEAT_SINGLE ) {
                    position = 0;
                    Toast.makeText(MusicPlayService.this, "已是第一首", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(playMode == REPEAT_ALL) {
                    position = list.size() - 1;
                }
            }

        }
        playMusic(position);

    }

    private int getPlayMode() {
        return playMode;
    }

    private void setPlayMode(int playMode) {
        this.playMode = playMode;
    }



}
