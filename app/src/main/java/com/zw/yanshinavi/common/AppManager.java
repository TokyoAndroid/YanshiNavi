package com.zw.yanshinavi.common;

import android.app.Activity;

import java.util.Stack;

/**
 * App管理类
 *
 * @author zhangwei
 *
 * @since 2019-2-22
 *
 */
public class AppManager {

    // 管理activity的栈
    private static Stack<Activity> mActivies;

    private static AppManager mInstance;

    private AppManager() {
        mActivies = new Stack<>();
    }

    /**
     * 返回 App管理类 实例
     * @return
     */
    public static AppManager getInstance() {
        return AppHolder.appManager;
    }

    /**
     * 添加actiivty进管理栈
     * @param activity
     */
    public void addToActivityStack(Activity activity){
        mActivies.add(activity);
    }

    /**
     * 从管理栈删除actiivty
     * @param activity
     */
    public void removeFromActivityStack(Activity activity) {
        mActivies.remove(activity);
    }

    /**
     * 获取当前activity
     * @return
     */
    public Activity getCurrentActivity(){
        return mActivies.lastElement();
    }

    /**
     * 结束某个activity
     * @param activity
     */
    public void finishActivity(Activity activity){
        if(activity != null) {
            mActivies.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束当前的activity
     */
    public void finishCurrentActivity(){
        Activity activity = mActivies.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束所有activity
     */
    public void finishAllActivity(){
        while (!mActivies.empty()) {
            Activity activity = mActivies.lastElement();
            mActivies.remove(activity);
            activity.finish();
        }
    }


    /**
     * 实现单例的内部类
     */
    private static class AppHolder {
        public static AppManager appManager = new AppManager();
    }
}
