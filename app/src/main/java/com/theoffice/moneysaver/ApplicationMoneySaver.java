package com.theoffice.moneysaver;

import android.app.Application;

import com.theoffice.moneysaver.data.model.User;

import okhttp3.OkHttpClient;

public class ApplicationMoneySaver extends Application {

    public static OkHttpClient client;
    public static User mainUser;

    @Override
    public void onCreate() {
        super.onCreate();
        client = new OkHttpClient();
    }

    public static void setMainUser(User mainUser) {
        ApplicationMoneySaver.mainUser = mainUser;
    }

    public static OkHttpClient getOkHttpClient(){
        return new OkHttpClient();
    }

    public static User getMainUser() {
        return mainUser;
    }
}
