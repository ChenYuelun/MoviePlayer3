package com.example.movieplayer3.pager;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.movieplayer3.R;
import com.example.movieplayer3.activity.ShowImageAndGifActivity;
import com.example.movieplayer3.adapter.NetAudioAdapter;
import com.example.movieplayer3.domain.NetAudioBean;
import com.example.movieplayer3.fragment.BaseFragment;
import com.google.gson.Gson;


import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.example.movieplayer3.R.id.lv;

/**
 * Created by chenyuelun on 2017/5/19.
 */

public class NetAudioPager extends BaseFragment {
    private static final String TAG = NetAudioPager.class.getSimpleName();
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.progressbar)
    ProgressBar progressbar;
    @Bind(R.id.tv_nomedia)
    TextView tvNomedia;
    private NetAudioAdapter myAdapter;
    private List<NetAudioBean.ListBean> datas;

    @Override
    public View initView() {
        Log.e(TAG, "网络音频UI被初始化了");
        View view = View.inflate(context, R.layout.net_audio_layout, null);
        ButterKnife.bind(this, view);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NetAudioBean.ListBean listBean = datas.get(position);
                if(listBean != null) {
                    Intent intent = new Intent(context,ShowImageAndGifActivity.class);
                    if(listBean.getType().equals("gif")){
                        String url = listBean.getGif().getImages().get(0);
                        intent.putExtra("url",url);
                        context.startActivity(intent);
                    }else if(listBean.getType().equals("image")){
                        String url = listBean.getImage().getBig().get(0);
                        intent.putExtra("url",url);
                        context.startActivity(intent);
                    }
                }

            }
        });


        return view;
    }

    @Override
    public void initDatas() {
        super.initDatas();
        Log.e("TAG", "网络视频数据初始化了...");


        getDataFromNet();
    }

    private void getDataFromNet() {
        RequestParams reques = new RequestParams("http://s.budejie.com/topic/list/jingxuan/1/budejie-android-6.2.8/0-20.json?market=baidu&udid=863425026599592&appname=baisibudejie&os=4.2.2&client=android&visiting=&mac=98%3A6c%3Af5%3A4b%3A72%3A6d&ver=6.2.8");
        x.http().get(reques, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("onError==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
            }
        });

    }

    private void processData(String json) {
        NetAudioBean netAudioBean = paraseJons(json);
//        LogUtil.e(netAudioBean.getList().get(0).getText()+"--------------");
        datas = netAudioBean.getList();
        if(datas != null && datas.size() >0){
            //有视频
            tvNomedia.setVisibility(View.GONE);
            //设置适配器
            myAdapter = new NetAudioAdapter(context,datas);
            listview.setAdapter(myAdapter);
        }else{
            //没有视频
            tvNomedia.setVisibility(View.VISIBLE);
        }

        progressbar.setVisibility(View.GONE);



    }

    /**
     * json解析数据
     * @param json
     * @return
     */
    private NetAudioBean paraseJons(String json) {
        return new Gson().fromJson(json,NetAudioBean.class);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
