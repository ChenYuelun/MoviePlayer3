package com.example.movieplayer3.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.movieplayer3.R;
import com.example.movieplayer3.domain.MediaItem;
import com.example.movieplayer3.domain.MovieInfo;
import com.example.movieplayer3.utils.Utils;
import com.squareup.picasso.Picasso;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：杨光福 on 2017/5/19 15:44
 * QQ：541433511
 * 微信：yangguangfu520
 * 作用：
 */

public class NetVideoAdapter extends BaseAdapter {
    private final Context context;
    private final List<MovieInfo.TrailersBean> datas;
    private Utils utils;
    //private ImageOptions options;



    public NetVideoAdapter(Context context, List<MovieInfo.TrailersBean> trailers) {
        this.context = context;
        this.datas = trailers;
        utils = new Utils();

//        options = new ImageOptions.Builder()
//                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
//                .setFailureDrawableId(R.drawable.video_default)
//                .setLoadingDrawableId(R.drawable.video_default)
//                .build();
    }


    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public MovieInfo.TrailersBean getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_net_video, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MovieInfo.TrailersBean bean = datas.get(position);
        viewHolder.tv_name.setText(bean.getMovieName());
        viewHolder.tv_content.setText(bean.getVideoTitle());
        viewHolder.tv_size.setText(utils.stringForTime(bean.getVideoLength()*1000));
        //x.image().bind(viewHolder.icon,bean.getCoverImg(),options);
        Picasso.with(context)
                .load(bean.getCoverImg())
                .placeholder(R.drawable.video_default)
                .error(R.drawable.video_default)
                .into(viewHolder.icon);


        return convertView;
    }

    static class ViewHolder {
        TextView tv_name;
        TextView tv_content;
        TextView tv_size;
        ImageView icon;
    }
}
