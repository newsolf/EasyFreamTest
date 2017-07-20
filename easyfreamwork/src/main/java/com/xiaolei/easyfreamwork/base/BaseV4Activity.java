package com.xiaolei.easyfreamwork.base;

/**
 * Created by xiaolei on 2017/3/1.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xiaolei.easyfreamwork.application.ApplicationBreage;
import com.xiaolei.easyfreamwork.common.listeners.Action;
import com.xiaolei.easyfreamwork.eventbus.Message;
import com.xiaolei.easyfreamwork.utils.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

public abstract class BaseV4Activity extends FragmentActivity
{
    protected Handler handler = null;
    private AlertDialog.Builder builder;
    private Toast toast;
    private String TAG = "BaseV4Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        ApplicationBreage.getInstance().addActivity(this);
        Log.d(TAG, this.getClass().getName() + ":onCreate");
        EventBus.getDefault().register(this);
        builder = new AlertDialog.Builder(this);
    }

    @Override
    public void setContentView(View view)
    {
        initObj();
        initData();
        super.setContentView(view);
        onSetContentView();
        ButterKnife.bind(this);
        initView();
        setListener();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params)
    {
        initObj();
        initData();
        super.setContentView(view, params);
        onSetContentView();
        ButterKnife.bind(this);
        initView();
        setListener();
    }

    @Override
    public void setContentView(int layoutResID)
    {
        initObj();
        initData();
        super.setContentView(layoutResID);
        onSetContentView();
        ButterKnife.bind(this);
        initView();
        setListener();
    }
    
    @Override
    protected void onStart()
    {
        super.onStart();
        loadData();
    }

    protected abstract void onSetContentView();

    public abstract void initObj();

    public abstract void initData();

    public abstract void initView();

    public abstract void setListener();

    public abstract void loadData();

    @Override
    protected void onDestroy()
    {
        EventBus.getDefault().unregister(this);
        handler.removeCallbacksAndMessages(null);
        ApplicationBreage.getInstance().removeActivity(this);
        super.onDestroy();
    }

    public void Alert(Object obj)
    {
        Alert(obj, "确定", null);
    }

    public void Alert(Object obj, Action rightListener)
    {
        Alert(obj, null, null, "确定", rightListener);
    }

    public void Alert(Object obj, String rightText, Action rightListener)
    {
        Alert(obj, null, null, rightText, rightListener);
    }

    public void Alert(Object obj
            , String leftText
            , Action leftListener
            , String rightText
            , Action rightListener)
    {
        Alert("提示信息", obj, leftText, leftListener, rightText, rightListener);
    }

    public void Alert(String title
            , Object obj
            , String leftText
            , Action leftListener
            , String rightText
            , Action rightListener)
    {
        builder.setTitle(title);
        builder.setMessage(obj + "");
        builder.setCancelable(false);
        if(leftText != null)
        {
            builder.setNeutralButton(leftText, leftListener);
        }
        if(rightText != null)
        {
            builder.setNegativeButton(rightText, rightListener);
        }
        builder.show();
    }

    public void Toast(String msg)
    {
        if(toast == null)
        {
            toast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.show();
    }
    
    public void startActivity(Class<? extends Activity> klass)
    {
        Intent intent = new Intent(this, klass);
        startActivity(intent);
    }
    
    /**
     * 这个的存在，是为了,内置一个刷新机制
     * @param message
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ReloadData(Message message)
    {
        if(Message.TYPE.FRESH.equals(message.Type))
        {
            Log.e(TAG,"ReloadData");
            loadData();
        }
    }
}