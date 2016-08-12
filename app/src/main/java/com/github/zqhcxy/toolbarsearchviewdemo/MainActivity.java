package com.github.zqhcxy.toolbarsearchviewdemo;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.zqhcxy.toolbarsearchviewdemo.lib.MyMaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar my_toolbar;
    private MyMaterialSearchView mysearch_view;

    private List<String> queryDatas;
    private List<String> oldQueryDatas=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();

        queryDatas=new ArrayList<>();
        queryDatas.add("A");
        queryDatas.add("BBBBBBBB");
        queryDatas.add("CCCCCCCC");
        queryDatas.add("DDDDDDDD");
        queryDatas.add("11111111");
        queryDatas.add("22222222");
        queryDatas.add("1A2B3A4D");
        queryDatas.add("2A3B4C5D");
        queryDatas.add("3A4B5C6D");

    }

    private void findView() {
        my_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        my_toolbar.setNavigationIcon(R.mipmap.ic_return);
        my_toolbar.setTitle("风中一匹狼");
        my_toolbar.setTitleTextColor(Color.WHITE);
        my_toolbar.setSubtitle("383838438");
        my_toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(my_toolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_toolbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.lookup);
        mysearch_view = (MyMaterialSearchView) findViewById(R.id.mysearch_view);
        mysearch_view.setMenuItem(menuItem);
        mysearch_view.setOnQueryTextListener(new MyMaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(TextUtils.isEmpty(newText)){
//                    mysearch_view.setSuggestionsCursor(null,null);
                    oldQueryDatas.clear();
                    mysearch_view.setSuggetionsBaseAdapter(null,null);
                }else {
                    //这个是测试显示而已，纯粹打印本地图库图片地址。
//                    mysearch_view.setSuggestionsCursor(getCursor(null),null);
                    List<String> lists=new ArrayList<String>();
                    List<String> searchdata;
                    if(oldQueryDatas.size()>0){
                        searchdata=oldQueryDatas;
                    }else{
                        searchdata=queryDatas;
                    }
                    for(String str:searchdata){
                        if(str.indexOf(newText)!=-1){
                            lists.add(str);
                        }
                    }
                    oldQueryDatas=lists;
                    mysearch_view.setSuggetionsBaseAdapter(lists,newText);
                }
                return true;
            }
        });

//        setSearchView(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private Cursor getCursor(String folderPath) {
        Cursor cursor = null;
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;
        String key_ID = MediaStore.Images.Media._ID;
        String selection;
        if (folderPath != null) {// 查找特定路径下的数据
            selection = key_DATA + " like " + DatabaseUtils.sqlEscapeString(folderPath + '%') + " and (" + key_MIME_TYPE + "=? or "
                    + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?)";
        } else {
            selection = key_MIME_TYPE + "=? or "
                    + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?";
        }
        Log.e("zqh-allphoto", "photo-uri: " + mImageUri.toString());
        ContentResolver mContentResolver = this.getContentResolver();
        // 只查询jpg和png的图片,按最新修改排序
        try {
            cursor = mContentResolver.query(mImageUri,
                    new String[]{key_DATA, key_ID}, selection,
                    new String[]{"image/jpg", "image/jpeg", "image/png", "image/gif", "image/vnd.wap.wbmp"},
                    MediaStore.Images.Media.DATE_TAKEN + " desc");
            if (cursor != null)
                Log.e("zqh-allphoto", "cursor: " + cursor.getCount());
        } catch (Exception e) {
//            CommonUtil.printStackTrace(e);
            Log.i("zqhToolbar",e.getMessage());
        }
        return cursor;
    }
}
