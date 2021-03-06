package com.xiaolei.easyfreamwork.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xiaolei.easyfreamwork.Config.Config;
import com.xiaolei.easyfreamwork.AlertDialog.CustomAlertDialog;
import com.xiaolei.easyfreamwork.common.listeners.Action;
import com.xiaolei.easyfreamwork.utils.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by xiaolei on 2017/3/7.
 */

public abstract class BaseFragment extends Fragment
{
    protected Handler handler = new Handler();
    protected final String TAG = "BaseFragment";
    protected Toast toast = null;
    private View contentView = null;
    private CustomAlertDialog alertDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        Log.e(TAG, this.getClass().getName() + ":onCreate");
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        initObj();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.e(TAG, this.getClass().getName() + ":onCreateView");
        if (contentView == null)
        {
            contentView = inflater.inflate(contentViewId(), null);
            initData();
            initView();
            setListener();
            loadData();
        }
        return contentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.d(TAG, this.getClass().getName() + ":onActivityCreated");
        onSetContentView();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        if (!hidden)
        {
            onSetContentView();
        }
        super.onHiddenChanged(hidden);
    }

    public abstract
    @LayoutRes
    int contentViewId();

    public abstract void initObj();

    public abstract void initData();

    public abstract void initView();

    public abstract void setListener();

    public abstract void loadData();

    protected abstract void onSetContentView();

    @Override
    public void onDestroyView()
    {
        Log.d(TAG, this.getClass().getName() + ":onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, this.getClass().getName() + ":onDestroy");
        handler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
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

    public void Alert(Object obj, String leftText, Action leftListener, String rightText, Action rightListener)
    {
        Alert(null, obj, leftText, leftListener, rightText, rightListener);
    }

    public void Alert(String title, Object obj, String leftText, Action leftListener, String rightText, Action rightListener)
    {
        if (alertDialog == null)
        {
            synchronized (this)
            {
                if (alertDialog == null)
                {
                    alertDialog = new CustomAlertDialog(this, Config.dialog_layout);
                }
            }
        }
        alertDialog.Alert(title, obj, leftText, leftListener, rightText, rightListener);
    }

    public <T extends View> T findViewByID(int id)
    {
        return (T) getView().findViewById(id);
    }

    public void startActivity(Class<? extends Activity> klass)
    {
        Intent intent = new Intent(getActivity(), klass);
        startActivity(intent);
    }

    public void Toast(String msg)
    {
        if (toast == null)
        {
            toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.show();
    }

    @Nullable
    @Override
    public View getView()
    {
        return contentView;
    }

    /**
     * 这个的存在，是为了,内置一个刷新机制
     *
     * @param message
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPrivateEvent(Message message)
    {
        onEvent(message);
    }

    public void onEvent(@NonNull Message msg)
    {
    }

    /**
     * 发送一个或者多个消息
     *
     * @param messages
     */
    public void post(@NonNull Message... messages)
    {
        if (messages != null)
        {
            for (int a = 0; a < messages.length; a++)
            {
                Message message = messages[a];
                EventBus.getDefault().post(message);
            }
        }
    }


    public CustomAlertDialog getAlertDialog()
    {
        if (alertDialog == null)
        {
            synchronized (this)
            {
                if (alertDialog == null)
                {
                    alertDialog = new CustomAlertDialog(this, Config.dialog_layout);
                }
            }
        }
        return alertDialog;
    }
}
