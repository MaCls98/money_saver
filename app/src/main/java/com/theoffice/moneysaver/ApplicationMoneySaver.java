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
        //TODO Remover seteo cuando se trabaje con usuarios reales
        ApplicationMoneySaver.mainUser.setUserId("5ef00cbb4de149bdd14c0c5d");
    }

    public static OkHttpClient getOkHttpClient(){
        return new OkHttpClient();
    }

    public static User getMainUser() {
        return mainUser;
    }
}
