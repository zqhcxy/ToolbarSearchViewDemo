package com.github.zqhcxy.toolbarsearchviewdemo.inf;

import android.view.View;

/**
 * Created by zqh-pc on 2016/8/12.
 */
public interface SuggesstionAdapterInf {
    void onItemClick(View v, String[] str);
    boolean onItemLongClcik(View v, String[] str);
}
