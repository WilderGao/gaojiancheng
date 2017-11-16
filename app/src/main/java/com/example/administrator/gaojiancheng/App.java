package com.example.administrator.gaojiancheng;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.facebook.stetho.Stetho;

import org.litepal.LitePalApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/29.
 */

public class App extends LitePalApplication implements Application.ActivityLifecycleCallbacks {
    public static App app;
    public static List<Activity> activityList = new ArrayList<>();
    @Override
    public void onCreate() {
        app = this;
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        activityList.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        activityList.remove(activity);
    }
}
