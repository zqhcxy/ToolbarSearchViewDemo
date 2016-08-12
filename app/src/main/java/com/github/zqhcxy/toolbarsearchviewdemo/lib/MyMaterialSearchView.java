package com.github.zqhcxy.toolbarsearchviewdemo.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.zqhcxy.toolbarsearchviewdemo.R;
import com.github.zqhcxy.toolbarsearchviewdemo.adapter.CursorRecyclerAdapter;
import com.github.zqhcxy.toolbarsearchviewdemo.adapter.MaterialSearchBaseAdapter;
import com.github.zqhcxy.toolbarsearchviewdemo.adapter.SuggestinRecyclerAdapter;
import com.github.zqhcxy.toolbarsearchviewdemo.adapter.SuggestionAdapter;
import com.github.zqhcxy.toolbarsearchviewdemo.inf.SuggesstionAdapterInf;
import com.github.zqhcxy.toolbarsearchviewdemo.utils.AnimationUtil;

import java.util.List;

/**
 * 自定义SearchVoew
 *
 * <p>a、自定义的布局，有一个默认的Suggestionadapter，adapter只适应CursorRecyclerAdapter，RecyclerView的adapter。
 * <p>b、可以适应默认的适配器，默认的适配器是一个头像和两行textview的视图，使用的灵活性小，调用方式是传入一个数据源cursor就行。
 * <p>c、也可以自定义Aadpter，调用方式就是传入自定义的adapter，
 * <p>Created by zqh-pc on 2016/6/29.
 */
public class MyMaterialSearchView extends FrameLayout implements Filter.FilterListener, View.OnClickListener {

    public static final int RCY_VERTICAL = 0;
    public static final int RCY_HORIZONTAL = 1;

    public static final int SUG_CURSOR = 0;
    public static final int SUG_BASE = 1;

    private Context mContext;

    /**
     * 整个搜索界面布局
     */
    private View mSearchLayout;
    /**
     * 背景的的半透明
     */
    private View mTint_view;
    /**
     * 头部搜索布局
     */
    private RelativeLayout search_top_bar;
    /**
     * 返回键
     */
    private ImageButton search_back;
    /**
     * 清除搜索按钮
     */
    private ImageButton action_empty_btn;
    /**
     * 语言转文字按钮
     */
    private ImageButton action_voice_btn;
    /**
     * 搜索编辑框
     */
    private EditText searchEditText;
    /**
     * 搜索建议列表
     */
    private RecyclerView suggestion_rcy;

    /**
     * 建议列表item的头像
     */
    private Drawable mIcondrawable;
    /**
     * suggestion的适配器
     */
    private CursorRecyclerAdapter mAdapter;
    /**
     * suggestion的非Cursordapter
     */
    private MaterialSearchBaseAdapter mBaseAdapter;
    /**
     * 外部打界面的item
     */
    private MenuItem mMenuItem;
    /**
     * 是否是显示搜索界面
     */
    private boolean mIsSearchOpen = false;
    /**
     * 搜索界面的状态监听接口，打开还是关闭
     */
    private SearchViewStateListener mSearchViewListener;
    private OnQueryTextListener mOnQueryTextListener;

    /**
     * 动画的持续时间，只有在api16上才需要
     */
    private int mAnimationDuration;
    /**
     * 是否还有焦点
     */
    private boolean mClearingFocus;

    private boolean isShowVoice = false;
    /**
     * 上一个查找字段
     */
    private CharSequence oldQueryText;

    private int suggestionType;

    public MyMaterialSearchView(Context context) {
        this(context, null);
    }

    public MyMaterialSearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyMaterialSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        mContext = context;

        initiateView();

        initStyle(attrs, defStyleAttr);

    }

    private void initStyle(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.MaterialSearchView, defStyleAttr, 0);

        if (a != null) {
            if (a.hasValue(R.styleable.MaterialSearchView_searchBackground)) {
                setBackground(a.getDrawable(R.styleable.MaterialSearchView_searchBackground));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_android_textColor)) {
                setTextColor(a.getColor(R.styleable.MaterialSearchView_android_textColor, 0));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_android_textColorHint)) {
                setHintTextColor(a.getColor(R.styleable.MaterialSearchView_android_textColorHint, 0));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_android_hint)) {
                setHint(a.getString(R.styleable.MaterialSearchView_android_hint));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_searchVoiceIcon)) {
                setVoiceIcon(a.getDrawable(R.styleable.MaterialSearchView_searchVoiceIcon));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_searchCloseIcon)) {
                setCloseIcon(a.getDrawable(R.styleable.MaterialSearchView_searchCloseIcon));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_searchBackIcon)) {
                setBackIcon(a.getDrawable(R.styleable.MaterialSearchView_searchBackIcon));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_searchSuggestionBackground)) {
                setSuggestionBackground(a.getDrawable(R.styleable.MaterialSearchView_searchSuggestionBackground));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_searchSuggestionIcon)) {
                setSuggestionIcon(a.getDrawable(R.styleable.MaterialSearchView_searchSuggestionIcon));
            }

            a.recycle();
        }
    }

    /**
     * 初始化
     */
    private void initiateView() {
        LayoutInflater.from(mContext).inflate(R.layout.mysearch_view, this, true);
        mSearchLayout = findViewById(R.id.search_layout);
        mTint_view = mSearchLayout.findViewById(R.id.transparent_view);
        search_top_bar = (RelativeLayout) mSearchLayout.findViewById(R.id.search_top_bar);
        search_back = (ImageButton) mSearchLayout.findViewById(R.id.search_back);
        searchEditText = (EditText) mSearchLayout.findViewById(R.id.searchTextView);
        action_empty_btn = (ImageButton) mSearchLayout.findViewById(R.id.action_empty_btn);
        action_voice_btn = (ImageButton) mSearchLayout.findViewById(R.id.action_voice_btn);
        suggestion_rcy = (RecyclerView) mSearchLayout.findViewById(R.id.suggestion_rcy);

        mTint_view.setOnClickListener(this);
        search_back.setOnClickListener(this);
        action_empty_btn.setOnClickListener(this);
        action_voice_btn.setOnClickListener(this);
        suggestion_rcy.setVisibility(GONE);


        setRecyViewHV(RCY_VERTICAL);
        initSearchView();
        setShowVoiceBtn(false);

    }

    /**
     * 设置recyclerView显示样式横竖
     */
    public void setRecyViewHV(int type) {
        if (type == RCY_VERTICAL) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setStackFromEnd(false);
            suggestion_rcy.setLayoutManager(layoutManager);
        } else if (type == RCY_HORIZONTAL) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setStackFromEnd(false);
            suggestion_rcy.setLayoutManager(layoutManager);
        }

    }

    /**
     * 初始化searchview的一些监听事件
     */
    private void initSearchView() {
        //软件盘上搜索按钮
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                onSubmitQuery();
                return true;
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //进行建议条件筛选

                onSearchTextChanged(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showKeyboard(searchEditText);
                    showSuggestions();
                }
            }
        });

    }

    /**
     * 确认搜索
     */
    private void onSubmitQuery() {
        CharSequence query = searchEditText.getText();
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {
            if (mOnQueryTextListener == null || !mOnQueryTextListener.onQueryTextSubmit(query.toString())) {
                closeSearch();
                searchEditText.setText(null);
            }
        }
    }

    /**
     * searchView内容变化监听事件逻辑
     *
     * @param newText
     */
    private void onSearchTextChanged(CharSequence newText) {
        CharSequence oldeText = searchEditText.getText();

        if (TextUtils.isEmpty(oldeText)) {
            action_empty_btn.setVisibility(GONE);
            setShowVoiceBtn(true);
        } else {
            action_empty_btn.setVisibility(VISIBLE);
            setShowVoiceBtn(false);
        }

        if (mOnQueryTextListener != null && !TextUtils.equals(newText, oldQueryText)) {
            mOnQueryTextListener.onQueryTextChange(newText.toString());
        }
        oldQueryText = newText.toString();
    }

    /**
     * 设置是否显示语言按钮，默认不显示
     *
     * @param show
     */
    public void isShowVoice(boolean show) {
        isShowVoice = show;
    }

    private void setShowVoiceBtn(boolean show) {
        if (isShowVoice && show) {
            action_voice_btn.setVisibility(VISIBLE);
        } else {
            action_voice_btn.setVisibility(GONE);
        }
    }


    /**
     * 传入建议数据源。
     * <p> 这个Cursor，是以官方的suggestion的样式生成的列表。这里有用的地方是两个参数分别是：suggest_text_1；suggest_text_2
     *
     * @param cursor
     * @param querystr 当前搜索的字段
     */
    public void setSuggestionsCursor(Cursor cursor,String querystr) {
        if (mAdapter == null) {
            suggestionType=SUG_CURSOR;
            mAdapter = new SuggestionAdapter(mContext, cursor);
            ((SuggestionAdapter) mAdapter).setQueryText(querystr);
            suggestion_rcy.setAdapter(mAdapter);
        } else {
            ((SuggestionAdapter) mAdapter).setQueryText(querystr);
            mAdapter.changeCursor(cursor);
        }
        suggestion_rcy.scrollToPosition(0);
        showSuggestions();
    }

    public void setSuggetionsBaseAdapter(MaterialSearchBaseAdapter baseAdapter){
        mBaseAdapter=baseAdapter;
    }
    public void setSuggetionsBaseAdapter(List<String> list,String querystr){
        if(mBaseAdapter==null){//没有就new一个默认的
            mBaseAdapter=new SuggestinRecyclerAdapter(list,mContext);
            suggestionType=SUG_BASE;
            suggestion_rcy.setAdapter(mBaseAdapter);
        }else{
            mBaseAdapter.updateDates(list);
            mBaseAdapter.setQueryString(querystr);
            suggestion_rcy.setAdapter(mBaseAdapter);
        }
        showSuggestions();
    }


    /**
     * 会话和消息的suggestionItem点击事件
     *
     * @param inf
     */
    public void setSuggestionItemOnClcikListener(SuggesstionAdapterInf inf) {
        if(suggestionType==SUG_BASE){
            mBaseAdapter.setSuggesstionAdapterInf(inf);
        }else if(suggestionType==SUG_CURSOR){
            ((SuggestionAdapter) mAdapter).setSuggestionAdapterInf(inf);
        }

    }


    /**
     * 外部自己实现的adapter
     *
     * @param suggestionsAdapter
     */
    public void setSuggestionsAdapter(CursorRecyclerAdapter suggestionsAdapter) {
        suggestionType=SUG_CURSOR;
        mAdapter = suggestionsAdapter;
    }

    /**
     * 设置建议的头像
     * <p> 只能设置一个，全部一起变（如果要实现每个Item的头像都不一样 ，要自己的adapter种实现）
     *
     * @param icondrawable 自定义的头像
     */
    public void setSuggestionIcon(Drawable icondrawable) {
        mIcondrawable = icondrawable;
    }

    /**
     * 自定义建议列表的背景
     *
     * @param background
     */
    public void setSuggestionBackground(Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            suggestion_rcy.setBackground(background);
        } else {
            suggestion_rcy.setBackgroundDrawable(background);
        }
    }

    public void setBackIcon(Drawable drawable) {
        search_back.setImageDrawable(drawable);
    }

    public void setCloseIcon(Drawable drawable) {
        action_empty_btn.setImageDrawable(drawable);
    }

    public void setVoiceIcon(Drawable drawable) {
        action_voice_btn.setImageDrawable(drawable);
    }

    public void setHint(String string) {
        searchEditText.setHint(string);
    }

    public void setHintTextColor(int color) {
        searchEditText.setHintTextColor(color);
    }

    public void setTextColor(int color) {
        searchEditText.setTextColor(color);
    }


    @Override
    public void onFilterComplete(int count) {
        if (count > 0) {
            showSuggestions();
        } else {
            dismissSuggestions();
        }
    }

    /**
     * 显示建议列表
     */
    public void showSuggestions() {

        if(suggestionType==SUG_BASE){
            if (mBaseAdapter != null && mBaseAdapter.getItemCount() > 0 && suggestion_rcy.getVisibility() == GONE) {
                suggestion_rcy.setVisibility(VISIBLE);
            }
        }else{
            if (mAdapter != null && mAdapter.getItemCount() > 0 && suggestion_rcy.getVisibility() == GONE) {
                suggestion_rcy.setVisibility(VISIBLE);
            }
        }

    }

    /**
     * 隐藏建议列表
     */
    public void dismissSuggestions() {
        if (suggestion_rcy.getVisibility() == VISIBLE) {
            suggestion_rcy.setVisibility(GONE);
        }
    }

    /**
     * 设置点击的打开界面的item
     *
     * @param menuItem
     */
    public void setMenuItem(MenuItem menuItem) {
        this.mMenuItem = menuItem;
        mMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showSearch();
                return true;
            }
        });
    }

    /**
     * 打开搜索界面视图，默认附带动画
     */
    public void showSearch() {
        showSearch(true);
    }

    /**
     * 显示搜索界面视图，可选择是否显示动画
     *
     * @param animate true 就是显示动画
     */
    public void showSearch(boolean animate) {
        if (isSearchOpen()) {
            return;
        }

        //Request Focus
        searchEditText.setText(null);
        searchEditText.requestFocus();

        if (animate) {//是否显示动画
            setVisibleWithAnimation();
        } else {
            mSearchLayout.setVisibility(VISIBLE);
            if (mSearchViewListener != null) {
                mSearchViewListener.onSearchViewShown();
            }
        }
        mIsSearchOpen = true;
    }

    /**
     * 关闭搜索界面
     */
    public void closeSearch() {
        if (!isSearchOpen()) {
            return;
        }

        searchEditText.setText(null);
        dismissSuggestions();
        clearFocus();

        mSearchLayout.setVisibility(GONE);
        if (mSearchViewListener != null) {
            mSearchViewListener.onSearchViewClosed();
        }
        mIsSearchOpen = false;
    }

    /**
     * 判断是否搜索界面是否显示着
     *
     * @return
     */
    public boolean isSearchOpen() {
        return mIsSearchOpen;
    }


    /**
     * 设置显示的界面的的动画
     */
    private void setVisibleWithAnimation() {
        AnimationUtil.AnimationListener animationListener = new AnimationUtil.AnimationListener() {
            @Override
            public boolean onAnimationStart(View view) {
                return false;
            }

            @Override
            public boolean onAnimationEnd(View view) {
                if (mSearchViewListener != null) {
                    mSearchViewListener.onSearchViewShown();
                }
                return false;
            }

            @Override
            public boolean onAnimationCancel(View view) {
                return false;
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSearchLayout.setVisibility(View.VISIBLE);
            AnimationUtil.reveal(search_top_bar, animationListener);

        } else {
            AnimationUtil.fadeInView(mSearchLayout, mAnimationDuration, animationListener);
        }
    }

    /**
     * 动画的持续时间 ONLY FOR PRE-LOLLIPOP!!
     *
     * @param duration duration of the animation
     */
    public void setAnimationDuration(int duration) {
        mAnimationDuration = duration;
    }

    /**
     * 设置shearchView界面的状态监听
     *
     * @param listener
     */
    public void setOnSearchViewStateListener(SearchViewStateListener listener) {
        mSearchViewListener = listener;
    }

    /**
     * 设置searchView的内容变化监听
     *
     * @param onQueryTextListener
     */
    public void setOnQueryTextListener(OnQueryTextListener onQueryTextListener) {
        mOnQueryTextListener = onQueryTextListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_back://退出搜索界面
            case R.id.transparent_view:
                closeSearch();
                break;
            case R.id.action_empty_btn:
                searchEditText.setText(null);
                if(suggestionType==SUG_CURSOR){
                    mAdapter.changeCursor(null);
                }else if(suggestionType==SUG_BASE){
                    mBaseAdapter.clearData();
                }
                break;
            case R.id.action_voice_btn://语音转文字
                break;
        }
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        // Don't accept focus if in the middle of clearing focus
        if (mClearingFocus) return false;
        // Check if SearchView is focusable.
        if (!isFocusable()) return false;
        return searchEditText.requestFocus(direction, previouslyFocusedRect);
    }

    @Override
    public void clearFocus() {
        mClearingFocus = true;
        hideKeyboard(this);
        super.clearFocus();
        searchEditText.clearFocus();
        mClearingFocus = false;
    }

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void showKeyboard(View view) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 && view.hasFocus()) {
            view.clearFocus();
        }
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    /**
     * searchView的内容监听变化
     */
    public interface OnQueryTextListener {

        /**
         * Called when the user submits the query. This could be due to a key press on the
         * keyboard or due to pressing a submit button.
         * The listener can override the standard behavior by returning true
         * to indicate that it has handled the submit request. Otherwise return false to
         * let the SearchView handle the submission by launching any associated intent.
         *
         * @param query the query text that is to be submitted
         * @return true if the query has been handled by the listener, false to let the
         * SearchView perform the default action.
         */
        boolean onQueryTextSubmit(String query);

        /**
         * Called when the query text is changed by the user.
         *
         * @param newText the new content of the query text field.
         * @return false if the SearchView should perform the default action of showing any
         * suggestions if available, true if the action was handled by the listener.
         */
        boolean onQueryTextChange(String newText);
    }

    /**
     * 状态监听，是打开还是关闭
     */
    public interface SearchViewStateListener {
        void onSearchViewShown();

        void onSearchViewClosed();
    }


//    public void speechToText(int type) {
//        if (CommonConfig.getUseChineseSpeech( mContext.getApplicationContext())) {
//            if (!ChineseSpeechUtils.getInstance( mContext.getApplicationContext())
//                    .checkPackage()) {
//                ChineseSpeechUtils
//                        .showDownloadPluginAlert( mContext);
//            } else {
//                // ChineseSpeechUtils.startRecognize(ComposeMessageActivity.this,
//                // REQ_CODE_CHINESE_SPEECH_RECOGNIZE);
//                ChineseSpeechUtils.startRecognizeForFullEditor(
//                        mContext,
//                        REQ_CODE_CHINESE_SPEECH_RECOGNIZE, type);
//            }
//        } else {
//            if (CommonUtil.isRecognizExist( mContext)) {
//
//                Intent intent = new Intent(
//                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
//                        mContext.getString(R.string.recognition_prompt_text));
//
//                PendingIntent pi = PendingIntent.getBroadcast(
//                        mContext,
//                        VOICE_RECOGNITION_REQUEST_CODE,
//                        new Intent(mContext,
//                                RecgReceiver.class), 0);
//
//                intent.putExtra(RecognizerIntent.EXTRA_RESULTS_PENDINGINTENT,
//                        pi);
//                Bundle bd = new Bundle();
//                bd.putInt(CommonConfig.TAG_RECONGNIZER_ACT, type);
//
//                intent.putExtra(
//                        RecognizerIntent.EXTRA_RESULTS_PENDINGINTENT_BUNDLE, bd);
//                isRecg = true;
//                mContext.startActivity(intent);
//
//            } else {
//                CommonUtil.showAlertMessage(
//                        mContext.getString(R.string.recognizer_not_present), mContext);
//            }
//        }
//    }
}
