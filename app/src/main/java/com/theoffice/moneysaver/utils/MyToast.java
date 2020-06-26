package com.theoffice.moneysaver.utils;

import android.content.Context;
import android.widget.Toast;

public class MyToast {

    public static void showShortToast(String message, Context context){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(String message, Context context){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

}
