package com.example.movieplayer3.pager;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.movieplayer3.R;
import com.example.movieplayer3.activity.LocalVideoPlayerActivity;
import com.example.movieplayer3.adapter.LocalVideoAdapter;
import com.example.movieplayer3.domain.MediaItem;
import com.example.movieplayer3.fragment.BaseFragment;

import java.util.ArrayList;

/**
 * Created by chenyuelun on 2017/5/19.
 */

public class LocalVideoPager extends BaseFragment {
    private ListView lv;
    private TextView tv_nodata;
    private ArrayList<MediaItem> list;

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.fragment_local_video_pager, null);
        lv = (ListView) view.findViewById(R.id.lv);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                MediaItem item = list.get(position);
//                String data = item.getData();
//                Intent intent = new Intent(context,LocalVideoPlayerActivity.class);
//                intent.setDataAndType(Uri.parse(data),"video/*");
//                startActivity(intent);
                Intent intent = new Intent(context,LocalVideoPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("videoList",list);
                intent.putExtras(bundle);
                intent.putExtra("position",position);
                startActivity(intent);

            }
        });

        return view;
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(list!= null && list.size() >0) {
                tv_nodata.setVisibility(View.GONE);
                lv.setAdapter(new LocalVideoAdapter(context,list));
            }else {
                tv_nodata.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void initDatas() {
        super.initDatas();
        getLocalVideoDatas();
    }

    private void getLocalVideoDatas() {
        new Thread(){
            public void run(){
                list = new ArrayList<>();
                ContentResolver resolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATA};
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                        while (cursor.moveToNext()) {
                            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                            Log.e("TAG", "name==" + name + ",duration==" + duration + ",data===" + data);

                            list.add(new MediaItem(name, duration, size, data));
                        }

                    handler.sendEmptyMessage(1);
                    cursor.close();

                    }

            }
        }.start();
    }
}
