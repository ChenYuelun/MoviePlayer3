package com.example.movieplayer3.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieplayer3.R;
import com.example.movieplayer3.adapter.NetVideoAdapter;
import com.example.movieplayer3.adapter.SearchAdapter;
import com.example.movieplayer3.domain.SearchBean;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etSousuo;
    private ImageView ivVoice;
    private TextView tvGo;
    private ListView lv;
    private ConcurrentHashMap<String, String> mIatResults = new ConcurrentHashMap<>();
    public static final String NET_SEARCH_URL = "http://hot.news.cntv.cn/index.php?controller=list&action=searchList&sort=date&n=20&wd=";
    private String url;
    private List<SearchBean.ItemsBean> datas;
    private SearchAdapter adapter;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-05-27 11:44:25 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_search);
        etSousuo = (EditText) findViewById(R.id.et_sousuo);
        ivVoice = (ImageView) findViewById(R.id.iv_voice);
        tvGo = (TextView) findViewById(R.id.tv_go);
        lv = (ListView) findViewById(R.id.lv);

        ivVoice.setOnClickListener(this);
        tvGo.setOnClickListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_voice:
                //语言输入
                showVoiceInputDialog();
                break;
            case R.id.tv_go:
                toSearch();
                break;
        }
    }

    private void toSearch() {


        String trim = etSousuo.getText().toString().trim();
        if(!TextUtils.isEmpty(trim)){
            //2.拼接
            url =  NET_SEARCH_URL+trim;
            //3.联网请求
            getDataFromNet(url);
        }else {
            Toast.makeText(SearchActivity.this, "请输入您要搜索的内容", Toast.LENGTH_SHORT).show();
        }

    }

    private void getDataFromNet(String url) {
        RequestParams request = new RequestParams(url);
        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG","请求成功-result=="+result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG","请求失败=="+ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void processData(String json) {
        SearchBean searchBean = new Gson().fromJson(json, SearchBean.class);
        datas = searchBean.getItems();
        if(datas != null && datas.size() >0){
            adapter = new SearchAdapter(this,datas);
            lv.setAdapter(adapter);
        }

    }

    private void showVoiceInputDialog() {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, new MyInitListener());
        //2.设置accent、 language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        //默认设置普通话
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解
        //结果
        // mDialog.setParameter("asr_sch", "1");
        // mDialog.setParameter("nlp_version", "2.0");
        //3.设置回调接口
        mDialog.setListener(new MyRecognizerDialogListener());
        //4.显示dialog，接收语音输入
        mDialog.show();
    }

    private class MyInitListener implements InitListener {
        @Override
        public void onInit(int i) {
            Log.e("TAG","onInit==i="+i);
            if (i == ErrorCode.SUCCESS) {
                Toast.makeText(SearchActivity.this, "初始化成功", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class MyRecognizerDialogListener implements RecognizerDialogListener {
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            String resultString = recognizerResult.getResultString();
            Log.e("TAG","onResult--resultString=="+resultString);
            printResult(recognizerResult);

        }

        @Override
        public void onError(SpeechError speechError) {
            Log.e("TAG","onError=="+speechError.getMessage());

        }
    }

    private void printResult(RecognizerResult results) {
        String text = com.example.movieplayer3.utils.JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        etSousuo.setText(resultBuffer.toString());
        etSousuo.setSelection(etSousuo.length());
    }
}
