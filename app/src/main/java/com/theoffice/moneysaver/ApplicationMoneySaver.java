package com.theoffice.moneysaver;

import android.app.Application;

import com.cloudinary.android.MediaManager;
import com.theoffice.moneysaver.data.model.User;
import com.theoffice.moneysaver.utils.AppConstants;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

public class ApplicationMoneySaver extends Application {

    public static OkHttpClient client;
    public static User mainUser;

    @Override
    public void onCreate() {
        super.onCreate();
        client = new OkHttpClient();
        Map config = new HashMap();
        config.put("cloud_name", AppConstants.CLOUDINARY_NAME);
        MediaManager.init(this, config);
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
