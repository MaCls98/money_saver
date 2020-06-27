package com.theoffice.moneysaver;

import android.app.Application;

import com.theoffice.moneysaver.data.model.User;

import okhttp3.OkHttpClient;

public class MoneySaverApplication extends Application {

    public static OkHttpClient client;
    public static User mainUser;

    @Override
    public void onCreate() {
        super.onCreate();
        client = new OkHttpClient();
    }

    public static void setMainUser(User mainUser) {
        MoneySaverApplication.mainUser = mainUser;
        //TODO Remover seteo cuando se trabaje con usuarios reales
        MoneySaverApplication.mainUser.setUserId("5ef00cbb4de149bdd14c0c5d");
    }

    public static User getMainUser() {
        return mainUser;
    }
}
