package com.example.movieplayer3.pager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.movieplayer3.R;
import com.example.movieplayer3.activity.LocalVideoPlayerActivity;
import com.example.movieplayer3.adapter.NetVideoAdapter;
import com.example.movieplayer3.domain.MediaItem;
import com.example.movieplayer3.domain.MovieInfo;
import com.example.movieplayer3.fragment.BaseFragment;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.provider.MediaStore;

/**
 * Created by chenyuelun on 2017/5/19.
 */

public class NetVideoPager extends BaseFragment {
    private ListView lv;
    private TextView tv_nodata;
    private NetVideoAdapter adapter;
    private ArrayList<MediaItem> mediaItems;
    private SharedPreferences sp;

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.fragment_net_video_pager, null);
        lv = (ListView) view.findViewById(R.id.lv);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);
        sp = context.getSharedPreferences("netVideoCache", Context.MODE_PRIVATE);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context,LocalVideoPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("videoList",mediaItems);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void initDatas() {
        super.initDatas();
        getDataFromNet();
    }

    private void getDataFromNet() {
        RequestParams request = new RequestParams("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG","联网成功");
                setData(result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG","联网失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void setData(String json) {
        MovieInfo movieInfo = new Gson().fromJson(json, MovieInfo.class);
        List<MovieInfo.TrailersBean> trailers = movieInfo.getTrailers();

        if (trailers != null && trailers.size() > 0) {
            adapter = new NetVideoAdapter(context, trailers);
            lv.setAdapter(adapter);
            tv_nodata.setVisibility(View.GONE);
        } else {
            tv_nodata.setVisibility(View.VISIBLE);
        }

        mediaItems = new ArrayList<>();
        for (int i = 0; i < trailers.size(); i++) {

            MovieInfo.TrailersBean bean = trailers.get(i);

            String name = bean.getMovieName();
            long duraition = bean.getVideoLength() * 1000;
            long size = 0;
            String data = bean.getUrl();
            mediaItems.add(new MediaItem(name, duraition, size, data));
            Log.e("TAG", data);

        }

    }


}
