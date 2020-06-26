package com.theoffice.moneysaver;

import android.app.Application;

import okhttp3.OkHttpClient;

public class MoneySaverApplication extends Application {

    public static OkHttpClient client;

    @Override
    public void onCreate() {
        super.onCreate();
        client = new OkHttpClient();
    }
}
