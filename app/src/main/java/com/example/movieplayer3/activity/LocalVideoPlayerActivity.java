package com.example.movieplayer3.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieplayer3.R;
import com.example.movieplayer3.domain.MediaItem;
import com.example.movieplayer3.utils.Utils;
import com.example.movieplayer3.view.VideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.key;
import static android.view.View.X;


public class LocalVideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PROCESS = 1;
    private static final int HIDEMEDIACONTROLLER = 2;
    private static final int NEWTIME = 3;
    private static final int SHOW_NET_SPEED = 4;
    private VideoView videoview;
    private ArrayList<MediaItem> mediaItems;
    private int position;


    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private Button btnSwitchPlayer;
    private LinearLayout llBottom;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnPre;
    private Button btnStartPause;
    private Button btnNext;
    private Button btnSwitchScreen;
    private GestureDetector detector;
    private Utils utils;
    private int duration;
    private boolean isShowMediaController = true;
    private MyBroadCastReceiver receiver;
    private Uri uri;

    private boolean isFullScreen = true;

    private int screenWidth;
    private int screenHeight;

    private int videoWidth;
    private int videoHeight;
    private final int DEFUALT_SCREEN = 3;
    private final int FULL_SCREEN = 4;

    private AudioManager am;
    private int maxVoice;
    private int currentVoice;
    private boolean isMute = false;
    
    private boolean isNetUri;

    private LinearLayout ll_buffering;
    private TextView tv_net_speed;

    private LinearLayout ll_loading;
    private TextView tv_loading_net_speed;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-05-22 08:38:00 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_local_video_player);
        videoview = (VideoView) findViewById(R.id.videoview);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btnSwitchPlayer = (Button) findViewById(R.id.btn_switch_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnPre = (Button) findViewById(R.id.btn_pre);
        btnStartPause = (Button) findViewById(R.id.btn_start_pause);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnSwitchScreen = (Button) findViewById(R.id.btn_switch_screen);
        ll_buffering = (LinearLayout)findViewById(R.id.ll_buffering);
        tv_net_speed = (TextView)findViewById(R.id.tv_net_speed);
        ll_loading = (LinearLayout)findViewById(R.id.ll_loading);
        tv_loading_net_speed = (TextView)findViewById(R.id.tv_loading_net_speed);

        btnVoice.setOnClickListener(this);
        btnSwitchPlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnStartPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnSwitchScreen.setOnClickListener(this);

        handler.sendEmptyMessage(SHOW_NET_SPEED);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-05-22 08:38:00 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnVoice) {
            // Handle clicks for btnVoice
            if (!isMute) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                seekbarVoice.setProgress(0);
                isMute = true;
            } else {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVoice, 0);
                seekbarVoice.setProgress(currentVoice);
                isMute = false;
            }
        } else if (v == btnSwitchPlayer) {
            switchPlayer();
            // Handle clicks for btnSwitchPlayer
        } else if (v == btnExit) {
            finish();
            // Handle clicks for btnExit
        } else if (v == btnPre) {
            playPreVideo();
            // Handle clicks for btnPre
        } else if (v == btnStartPause) {
            playOrPause();
            // Handle clicks for btnStartPause
        } else if (v == btnNext) {
            playNextVideo();
            // Handle clicks for btnNext
        } else if (v == btnSwitchScreen) {
            SetScreenType();
            // Handle clicks for btnSwitchScreen
        }
    }

    private void switchPlayer() {
        new AlertDialog.Builder(this)
                    .setTitle("切换播放器")
                    .setMessage("当前使用系统播放器播放，若播放有问题请选择万能播放器播放")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startVitamioPlayer();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
    }

    private int preCurrentPosition;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROCESS:
                    int currentPosition = videoview.getCurrentPosition();
                    seekbarVideo.setProgress(currentPosition);
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));
                    sendEmptyMessageDelayed(PROCESS, 1000);
                    if(isNetUri){
                        int bufferPercentage = videoview.getBufferPercentage();//0~100;
                        int totalBuffer = bufferPercentage*seekbarVideo.getMax();
                        int secondaryProgress =totalBuffer/100;
                        seekbarVideo.setSecondaryProgress(secondaryProgress);
                    }else{
                        seekbarVideo.setSecondaryProgress(0);
                    }
                    
                    if(isNetUri && videoview.isPlaying()) {
                        int duration = currentPosition - preCurrentPosition;
                        if(duration < 500) {
                            ll_buffering.setVisibility(View.VISIBLE);
                        }else {
                            ll_buffering.setVisibility(View.GONE);
                        }
                        preCurrentPosition = currentPosition;
                    }



                    break;

                case HIDEMEDIACONTROLLER:

                    hideOrShowMediaController();
                    break;

                case NEWTIME:
                    tvSystemTime.setText(getSystemTime());
                    handler.sendEmptyMessageDelayed(NEWTIME, 60000);
                    break;

                case SHOW_NET_SPEED:
                    String speed = utils.getNetSpeed(LocalVideoPlayerActivity.this);
                    tv_net_speed.setText(speed+"kb/s");
                    tv_loading_net_speed.setText(speed+"kb/s");
                    sendEmptyMessageDelayed(SHOW_NET_SPEED,1000);
                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();
        getData();
        initData();
        setListener();

        setData();


    }

    private void initData() {
        utils = new Utils();

        receiver = new MyBroadCastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, filter);

        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                playOrPause();
                super.onLongPress(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                SetScreenType();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                hideOrShowMediaController();
                return super.onSingleTapConfirmed(e);
            }
        });

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;
        //初始化声音相关
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekbarVoice.setMax(maxVoice);
        seekbarVoice.setProgress(currentVoice);
    }

    private float startY;
    private float newY;
    private int touchRang;
    private int mVocie;

    private float startX;
    private float newX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                touchRang = Math.min(screenHeight, screenWidth);
                mVocie = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                handler.removeMessages(HIDEMEDIACONTROLLER);
                break;
            case MotionEvent.ACTION_MOVE:
                newY = event.getY();
                newX = event.getX();
                float distanceY = startY - newY;
                if(startX < screenWidth/2) {
                    //左半区，调亮度
                    final double FLING_MIN_DISTANCE = 0.5;
                    final double FLING_MIN_VELOCITY = 0.5;
                    if (distanceY > FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
                        setBrightness(10);
                    }
                    if (distanceY < FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
                        setBrightness(-10);
                    }

                }else {
                    //右半区，调声音
                    int changVoice = (int) ((distanceY / touchRang) * maxVoice);
                    if (changVoice != 0) {
                        int voice = Math.min((Math.max(mVocie + changVoice, 0)), maxVoice);
                        updataVoice(voice);
                    }
                }


                break;
            case MotionEvent.ACTION_UP:
                handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER, 5000);
                break;
        }


        return super.onTouchEvent(event);
    }

    private void setBrightness(float brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
        } else if (lp.screenBrightness < 0.1) {
            lp.screenBrightness = (float) 0.1;
        }
        getWindow().setAttributes(lp);

    }


    private void setData() {
        if (mediaItems != null && mediaItems.size() > 0) {
            MediaItem mediaItem = mediaItems.get(position);
            isNetUri = utils.isNetUri(mediaItem.getData());
            tvName.setText(mediaItem.getName());
            videoview.setVideoPath(mediaItem.getData());
        }
        if (uri != null) {
            isNetUri = utils.isNetUri(uri.toString());
            videoview.setVideoURI(uri);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                handler.removeMessages(HIDEMEDIACONTROLLER);
                currentVoice--;
                seekbarVoice.setProgress(currentVoice);
                am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVoice, 0);
                handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER, 5000);

                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                handler.removeMessages(HIDEMEDIACONTROLLER);
                currentVoice++;
                seekbarVoice.setProgress(currentVoice);
                am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVoice, 0);
                handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER, 5000);

                break;

        }

        return super.onKeyDown(keyCode, event);
    }

    private void getData() {
        Intent intent = getIntent();

        uri = intent.getData();
        mediaItems = (ArrayList<MediaItem>) intent.getSerializableExtra("videoList");
        position = intent.getIntExtra("position", 0);
    }

    private void setListener() {
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoWidth = mp.getVideoWidth();
                videoHeight = mp.getVideoHeight();
                duration = videoview.getDuration();
                tvDuration.setText(utils.stringForTime(duration));
                seekbarVideo.setMax(duration);
                videoview.start();
                ll_loading.setVisibility(View.GONE);
                setButtonStatus();
                SetScreenType();
                hideOrShowMediaController();
                handler.sendEmptyMessage(PROCESS);
                handler.sendEmptyMessage(NEWTIME);
                if(videoview.isPlaying()){
                    btnStartPause.setBackgroundResource(R.drawable.btn_pause_selector);
                }else {
                    btnStartPause.setBackgroundResource(R.drawable.btn_start_selector);
                }

            }
        });


        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
//                Toast.makeText(LocalVideoPlayerActivity.this, "播放出错", Toast.LENGTH_SHORT).show();
                startVitamioPlayer();
                return true;
            }
        });

        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //Toast.makeText(LocalVideoPlayerActivity.this, "播放结束", Toast.LENGTH_SHORT).show();
                playNextVideo();

            }
        });

        seekbarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoview.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(HIDEMEDIACONTROLLER);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER, 5000);
            }
        });

        seekbarVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updataVoice(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(HIDEMEDIACONTROLLER);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER, 5000);
            }
        });


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            videoview.setOnInfoListener(new MediaPlayer.OnInfoListener() {
//                @Override
//                public boolean onInfo(MediaPlayer mp, int what, int extra) {
//                    switch (what) {
//                        case MediaPlayer.MEDIA_INFO_BUFFERING_START :
//                            ll_buffering.setVisibility(View.VISIBLE);
//                            break;
//                        case MediaPlayer.MEDIA_INFO_BUFFERING_END :
//                            ll_buffering.setVisibility(View.GONE);
//                            break;
//                    }
//
//                    return false;
//                }
//            });
//        }


    }

    private void startVitamioPlayer() {
        if(videoview != null){
            videoview.stopPlayback();
        }
        Intent intent = new Intent(this, VitamioVideoPlayerActivity.class);
        if(mediaItems != null && mediaItems.size() >0){
            Bundle bunlder = new Bundle();
            bunlder.putSerializable("videoList",mediaItems);
            intent.putExtra("position",position);
            intent.putExtras(bunlder);
        }else if(uri != null){
            intent.setData(uri);
        }
        startActivity(intent);
        finish();
    }

    private void updataVoice(int progress) {
        currentVoice = progress;
        am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVoice, 0);
        seekbarVoice.setProgress(currentVoice);
        if (currentVoice == 0) {
            isMute = true;
        } else {
            isMute = false;
        }
    }

    public class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);//主线程
            Log.e("TAG", "level==" + level);
            setBatteryView(level);
        }
    }

    private void setBatteryView(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    private void playNextVideo() {
        position++;
        if (position < mediaItems.size()) {
            ll_loading.setVisibility(View.VISIBLE);
            MediaItem mediaItem = mediaItems.get(position);
            isNetUri = utils.isNetUri(mediaItem.getData());
            videoview.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());
            setButtonStatus();
        } else {
            Toast.makeText(LocalVideoPlayerActivity.this, "视频列表已播放完毕", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void playPreVideo() {
        position--;
        if (position >= 0) {
            ll_loading.setVisibility(View.VISIBLE);
            MediaItem mediaItem = mediaItems.get(position);
            isNetUri = utils.isNetUri(mediaItem.getData());
            videoview.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());
            setButtonStatus();
        }
    }

    private void setButtonStatus() {
        if (mediaItems != null && mediaItems.size() > 0) {
            setEnable(true);
            if (position == 0) {
                btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
                btnPre.setEnabled(false);
            }

            if (position == mediaItems.size() - 1) {
                btnNext.setBackgroundResource(R.drawable.btn_next_gray);
                btnNext.setEnabled(false);
            }
        } else if (uri != null) {

            setEnable(false);
        }
    }

    private void setEnable(boolean b) {
        if (b) {
            btnPre.setBackgroundResource(R.drawable.btn_pre_selector);
            btnNext.setBackgroundResource(R.drawable.btn_next_selector);
        } else {
            btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnNext.setBackgroundResource(R.drawable.btn_next_gray);
        }
        btnPre.setEnabled(b);
        btnNext.setEnabled(b);
    }

    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date());
    }

    //长按播放或暂停
    private void playOrPause() {
        if (videoview.isPlaying()) {
            videoview.pause();
            btnStartPause.setBackgroundResource(R.drawable.btn_start_selector);
        } else {
            videoview.start();
            btnStartPause.setBackgroundResource(R.drawable.btn_pause_selector);
        }
    }

    private void hideOrShowMediaController() {

        if (isShowMediaController) {
            handler.removeMessages(HIDEMEDIACONTROLLER);
            llBottom.setVisibility(View.GONE);
            llTop.setVisibility(View.GONE);

            isShowMediaController = false;
        } else {
            llBottom.setVisibility(View.VISIBLE);
            llTop.setVisibility(View.VISIBLE);
            isShowMediaController = true;
            handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER, 5000);
        }
    }

    private void SetScreenType() {
        if (isFullScreen) {
            setVideoType(DEFUALT_SCREEN);
        } else {
            setVideoType(FULL_SCREEN);
        }
    }

    private void setVideoType(int VideoType) {
        switch (VideoType) {
            case DEFUALT_SCREEN:
                isFullScreen = false;
                btnSwitchScreen.setBackgroundResource(R.drawable.btn_switch_screen_full_selector);
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;

                int width = screenWidth;
                int height = screenHeight;

                if (mVideoWidth * height < width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    height = width * mVideoHeight / mVideoWidth;
                }
                videoview.setVideoSize(width, height);
                break;
            case FULL_SCREEN:
                isFullScreen = true;
                btnSwitchScreen.setBackgroundResource(R.drawable.btn_switch_screen_default_selector);
                videoview.setVideoSize(screenWidth, screenHeight);

                break;
        }

    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        super.onDestroy();
    }
}
