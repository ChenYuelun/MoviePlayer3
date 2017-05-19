package com.example.movieplayer3;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioGroup;

import com.example.movieplayer3.fragment.BaseFragment;
import com.example.movieplayer3.pager.LocalAudioPager;
import com.example.movieplayer3.pager.LocalVideoPager;
import com.example.movieplayer3.pager.NetAudioPager;
import com.example.movieplayer3.pager.NetVideoPager;

import java.util.ArrayList;

import static com.example.movieplayer3.R.id.rb_local_video;

public class MainActivity extends AppCompatActivity {
    private RadioGroup rg_main;
    private ArrayList<BaseFragment> fragments;
    private int posotion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rg_main = (RadioGroup)findViewById(R.id.rg_main);
        ititFragments();
        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rg_main.check(rb_local_video);
    }

    private void ititFragments() {
        fragments = new ArrayList<>();
        fragments.add(new LocalVideoPager());
        fragments.add(new LocalAudioPager());
        fragments.add(new NetAudioPager());
        fragments.add(new NetVideoPager());
    }

    private class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case rb_local_video :
                    posotion = 0;
                    break;
                case R.id.rb_local_audio :
                    posotion = 1;
                    break;
                case R.id.rb_net_audio :
                    posotion =2;
                    break;
                case R.id.rb_net_video :
                    posotion = 3;
                    break;
            }

            BaseFragment CurrentFragment = fragments.get(posotion);
            addFragment(CurrentFragment);
        }
    }

    private BaseFragment tempFragment;
    private void addFragment(BaseFragment currentFragment) {
        if(tempFragment != currentFragment) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if(!currentFragment.isAdded()) {
                if(tempFragment != null) {
                    ft.hide(tempFragment);
                }
                ft.add(R.id.framelayout,currentFragment);
            }else {
                if(tempFragment != null) {
                    ft.hide(tempFragment);
                }
                ft.show(currentFragment);
            }
            ft.commit();
            tempFragment = currentFragment;

        }
    }
}
