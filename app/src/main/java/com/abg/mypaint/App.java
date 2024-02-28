package com.abg.mypaint;

import android.app.Application;
import android.util.Log;

import com.abg.mypaint.ui.ads.AppOpenAdManager;
import com.yandex.mobile.ads.common.MobileAds;

public class App extends Application {

    private static final String YANDEX_MOBILE_ADS_TAG = "YandexMobileAds";

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, () -> Log.d(YANDEX_MOBILE_ADS_TAG, "SDK initialized"));
    }
}
