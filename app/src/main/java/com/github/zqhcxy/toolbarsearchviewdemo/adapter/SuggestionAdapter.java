package com.github.zqhcxy.toolbarsearchviewdemo.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.zqhcxy.toolbarsearchviewdemo.R;
import com.github.zqhcxy.toolbarsearchviewdemo.inf.SuggesstionAdapterInf;

/**
 * Created by zqh-pc on 2016/6/30.
 */
public class SuggestionAdapter extends CursorRecyclerAdapter<SuggestionAdapter.ViewHolder> {
    private LayoutInflater mInflator;
    private String queryText;
    private SuggesstionAdapterInf suggestionAdapterInf;

    /**
     * Recommended constructor.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     *                <p>Flags used to determine the behavior of the adapter; may
     *                be any combination of {@link #FLAG_AUTO_REQUERY} and
     *                {@link #FLAG_REGISTER_CONTENT_OBSERVER}.
     */
    public SuggestionAdapter(Context context, Cursor c) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
        mInflator = LayoutInflater.from(context);
    }

//    public interface SuggestionAdapterInf {
//        void onItemClick(View v, String[] str);
//
//        boolean onItemLongClcik(View v, String[] str);
//    }
    public void setSuggestionAdapterInf(SuggesstionAdapterInf inf) {
        suggestionAdapterInf = inf;
    }

    public void setQueryText(String quertstr) {
        queryText = quertstr;
    }


    @Override
    public void bindViewHolder(SuggestionAdapter.ViewHolder holder, Context context, Cursor cursor) {
        String filepath = cursor.getString(0);
        holder.suggest_name.setText(cursor.getPosition() + "");
        holder.suggest_content.setText(filepath);
    }

    @Override
    public SuggestionAdapter.ViewHolder createViewHolder(Context context, ViewGroup parent, int viewType) {
        View view = mInflator.inflate(R.layout.search_suggestion_item, parent, false);
        return new ViewHolder(view);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView suggest_icon;//头像
        private TextView suggest_name;//名字
        private TextView suggest_content;//内容

        public ViewHolder(View itemView) {
            super(itemView);
            suggest_icon = (ImageView) itemView.findViewById(R.id.suggest_icon);
            suggest_name = (TextView) itemView.findViewById(R.id.suggest_name);
            suggest_content = (TextView) itemView.findViewById(R.id.suggest_content);
        }
    }

}
