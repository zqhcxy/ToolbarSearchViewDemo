package com.github.zqhcxy.toolbarsearchviewdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.github.zqhcxy.toolbarsearchviewdemo.inf.SuggesstionAdapterInf;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqh-pc on 2016/8/12.
 */
public abstract class MaterialSearchBaseAdapter <VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>{

    public Context mContext;
    public List<Object> mDdtas;
    private String quystr;

    public MaterialSearchBaseAdapter(List<Object> datas, Context context){
        mContext=context;
        if(datas==null){
            datas=new ArrayList<>();
        }else{
            mDdtas=datas;
        }
    }


    public void setSuggesstionAdapterInf(SuggesstionAdapterInf inf){
        setSuggesstionInf(inf);
    }
    /**
     * 查找的字段
     * @param qstr
     */
    public void setQueryString(String qstr){
        quystr=qstr;
    }

    public String getQuystr() {
        return quystr;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return myCreateViewHolder(parent,viewType);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        mybindViewHolder(holder,position);
    }

    @Override
    public int getItemCount() {
        if(mDdtas!=null){
            return mDdtas.size();
        }
        return 0;
    }

    /**
     * 清空数据
     */
    public void clearData(){
        if(mDdtas!=null){
            mDdtas.clear();
            notifyDataSetChanged();
        }
    }

    /**
     * 更新数据
     * @param lists
     */
    public void updateDates(List<Object> lists){
        if(mDdtas==null){
            mDdtas=new ArrayList<>();
        }
        if(lists!=null){
            mDdtas.clear();
            mDdtas.addAll(lists);
        }else{
            mDdtas.clear();
        }
    }


    public abstract void mybindViewHolder(VH holder,int position);

    /**
     * @see RecyclerView.Adapter(Context, ViewGroup, int)
     */
    public abstract VH myCreateViewHolder(ViewGroup parent, int viewType);
//    public abstract void setqstr(String qstr);
    public abstract void setSuggesstionInf(SuggesstionAdapterInf inf);
}
