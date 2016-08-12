package com.github.zqhcxy.toolbarsearchviewdemo.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.zqhcxy.toolbarsearchviewdemo.R;
import com.github.zqhcxy.toolbarsearchviewdemo.inf.SuggesstionAdapterInf;

import java.util.List;

/**
 * Created by zqh-pc on 2016/8/12.
 */
public class SuggestinRecyclerAdapter extends MaterialSearchBaseAdapter {

//    private List<String> lists;

    public SuggestinRecyclerAdapter(List<String> datas, Context context) {
        super(datas, context);
//        lists=datas;
    }

    @Override
    public void mybindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).sug_recy_tv.setText((String)mDdtas.get(position));
        setSearchText( ((ViewHolder)holder).sug_recy_tv, (String)mDdtas.get(position),
                getQuystr(), ContextCompat.getColor(mContext, R.color.colorAccent));
    }
    @Override
    public RecyclerView.ViewHolder myCreateViewHolder(ViewGroup parent, int viewType) {
      View view=  LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_recycler_item,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void setSuggesstionInf(SuggesstionAdapterInf inf) {

    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView sug_recy_tv;

        public ViewHolder(View itemView) {
            super(itemView);
            sug_recy_tv= (TextView) itemView.findViewById(R.id.sug_recy_tv);
        }
    }

    public static void setSearchText(TextView textView, String fullStr,
                                     String searchStr, int colorRes) {

        if (fullStr.toLowerCase().indexOf(searchStr.toLowerCase()) != -1) {
            if (fullStr.toLowerCase().indexOf(searchStr.toLowerCase()) > 17) {
                fullStr = fullStr.substring(fullStr.toLowerCase().indexOf(
                        searchStr.toLowerCase()));
            }
            SpannableStringBuilder style = new SpannableStringBuilder(fullStr);
            int start = fullStr.toLowerCase().indexOf(searchStr.toLowerCase());
            int end = start + searchStr.length();
            style.setSpan(new ForegroundColorSpan(colorRes), start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(style);
        } else {
            textView.setText(fullStr);
        }

    }
}
