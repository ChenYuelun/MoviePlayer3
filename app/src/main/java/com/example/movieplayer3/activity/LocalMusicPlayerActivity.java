package com.example.movieplayer3.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.movieplayer3.IMusicPlayService;
import com.example.movieplayer3.R;
import com.example.movieplayer3.domain.Lyric;
import com.example.movieplayer3.domain.MediaItem;
import com.example.movieplayer3.service.MusicPlayService;
import com.example.movieplayer3.utils.LyricsUtils;
import com.example.movieplayer3.utils.Utils;
import com.example.movieplayer3.view.LyricView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class LocalMusicPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int UPDATA_PROGRESS = 1;
    private static final int SHOW_LYRIC = 2;
    private RelativeLayout rlTop;
    private ImageView ivIcon;
    private TextView tvArtist;
    private TextView tvAudioname;
    private LinearLayout llBottom;
    private TextView tvTime;
    private SeekBar seekbarAudio;
    private Button btnPlaymode;
    private Button btnPre;
    private Button btnStartPause;
    private Button btnNext;
    private Button btnLyric;
    private IMusicPlayService service;
    private Utils utils;
    private LyricView lyric_view;
    //private MyReceiver receiver;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service = IMusicPlayService.Stub.asInterface(iBinder);
            Log.e("TAG","onServiceConnected");
            if(service != null) {
                try {
                    Log.e("TAG","position" + position);
                    if(fromNotification) {
                        setViewData(null);
                    }else {
                        service.playMusic(position);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("TAG","onServiceDisconnected");
        }
    };
    private int position;
    private int duration;
    private int currentPosition;
    private boolean fromNotification;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-05-24 22:04:49 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_local_music_player);
        rlTop = (RelativeLayout)findViewById( R.id.rl_top );
        ivIcon = (ImageView)findViewById( R.id.iv_icon );
        ivIcon.setBackgroundResource(R.drawable.animotion_bg);
        AnimationDrawable background = (AnimationDrawable) ivIcon.getBackground();
        background.start();
        tvArtist = (TextView)findViewById( R.id.tv_artist );
        tvAudioname = (TextView)findViewById( R.id.tv_audioname );
        llBottom = (LinearLayout)findViewById( R.id.ll_bottom );
        tvTime = (TextView)findViewById( R.id.tv_time );
        seekbarAudio = (SeekBar)findViewById( R.id.seekbar_audio );
        btnPlaymode = (Button)findViewById( R.id.btn_playmode );
        btnPre = (Button)findViewById( R.id.btn_pre );
        btnStartPause = (Button)findViewById( R.id.btn_start_pause );
        btnNext = (Button)findViewById( R.id.btn_next );
        btnLyric = (Button)findViewById( R.id.btn_lyric );
        lyric_view = (LyricView)findViewById(R.id.lyric_view);

        btnPlaymode.setOnClickListener( this );
        btnPre.setOnClickListener( this );
        btnStartPause.setOnClickListener( this );
        btnNext.setOnClickListener( this );
        btnLyric.setOnClickListener( this );
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-05-24 22:04:49 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == btnPlaymode ) {
            // Handle clicks for btnPlaymode
            setPlayMode();

        } else if ( v == btnPre ) {
            try {
                MusicPlayService.nextFromUser = true;
                service.playPre();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // Handle clicks for btnPre
        } else if ( v == btnStartPause ) {
            try {
                if(service.isPlaying()) {
                   service.pause();
                    btnStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                }else {
                    service.start();
                    btnStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // Handle clicks for btnStartPause
        } else if ( v == btnNext ) {
            try {
                MusicPlayService.nextFromUser = true;
                service.playNext();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // Handle clicks for btnNext
        } else if ( v == btnLyric ) {
            // Handle clicks for btnLyric
        }
    }

    private void setPlayMode() {
        try {
            int playMode = service.getPlayMode();
            if(playMode == MusicPlayService.REPEAT_NORMAL) {
                playMode = MusicPlayService.REPEAT_ALL;
            }else if(playMode == MusicPlayService.REPEAT_ALL) {
                playMode = MusicPlayService.REPEAT_SINGLE;
            }else if(playMode == MusicPlayService.REPEAT_SINGLE) {
                playMode = MusicPlayService.REPEAT_NORMAL;
            }




            service.setPlayMode(playMode);
            setBtnPlayModeImage();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void setBtnPlayModeImage() {

        try {
            int playMode = service.getPlayMode();
            if(playMode == MusicPlayService.REPEAT_NORMAL) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_normal_selector);
            }else if(playMode == MusicPlayService.REPEAT_ALL) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_all_selector);
            }else if(playMode == MusicPlayService.REPEAT_SINGLE) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_single_selector);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATA_PROGRESS :
                    try {
                        currentPosition = service.currentPosition();
                        seekbarAudio.setProgress(currentPosition);
                        tvTime.setText(utils.stringForTime(currentPosition) + "/" + utils.stringForTime(duration));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    sendEmptyMessageDelayed(UPDATA_PROGRESS,1000);

                    break;
                case SHOW_LYRIC:
                    try {
                        currentPosition = service.currentPosition();
                        lyric_view.setShowNextLyric(currentPosition);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }


                    removeMessages(SHOW_LYRIC);
                    sendEmptyMessage(SHOW_LYRIC);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        findViews();
        super.onCreate(savedInstanceState);
        getData();
        initData();
        startAndBindService();
        setListener();
        EventBus.getDefault().register(this);

    }

    private void setListener() {
        seekbarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    try {
                        service.seekTo(progress);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initData() {
        utils = new Utils();
//        receiver = new MyReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(MusicPlayService.ONPREPARED);
//        registerReceiver(receiver,filter);


    }

    private void getData() {
        fromNotification = getIntent().getBooleanExtra("fromNotification", false);
        if(!fromNotification) {
            position = getIntent().getIntExtra("position",0);
        }
    }

    private void startAndBindService() {
        Log.e("TAG","startAndBindService");
        Intent intent = new Intent(this,MusicPlayService.class);
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

//    class MyReceiver extends BroadcastReceiver{
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            switch (intent.getAction()){
//                case MusicPlayService.ONPREPARED:
//                    setViewData();
//                    break;
//
//
//            }
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setViewData(MediaItem main) {
        try {
            tvArtist.setText(service.getArtistName());
            tvAudioname.setText(service.getMusicName());
            duration = service.duration();
            seekbarAudio.setMax(duration);
            setBtnPlayModeImage();


            String audioPath = service.getMusicPath();
            String lyricPath = audioPath.substring(0,audioPath.lastIndexOf("."));
            File lrc = new File(lyricPath + ".lrc");
            Log.e("TAG","lrc.exists(.lrc)" + lrc.exists());
            if(!lrc.exists()) {
                lrc = new File(lyricPath + ".txt");
                Log.e("TAG","lrc.exists(.txt)" + lrc.exists());
            }
            if(lrc.exists()) {
                Log.e("TAG","lrc.exists()" + lrc.exists());
                LyricsUtils lyricsUtils = new LyricsUtils();
                lyricsUtils.readLyric(lrc);
                ArrayList<Lyric> lyrics = lyricsUtils.getLyrics();
                for(int i = 0; i < lyrics.size(); i++) {
                  Log.e("TAGT",lyrics.get(i).toString());
                }
                lyric_view.setLysicData(lyrics);
                handler.sendEmptyMessage(SHOW_LYRIC);

            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        handler.sendEmptyMessage(UPDATA_PROGRESS);

    }

    @Override
    protected void onDestroy() {
        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        if(conn != null) {
            unbindService(conn);
            conn = null;
        }
//        if(receiver!= null) {
//            unregisterReceiver(receiver);
//            receiver = null;
//        }

        EventBus.getDefault().unregister(this);
        
        super.onDestroy();
        
        
    }
}
