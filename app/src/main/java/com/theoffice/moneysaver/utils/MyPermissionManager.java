package com.theoffice.moneysaver.utils;

import android.app.Activity;
import androidx.appcompat.app.AlertDialog ;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.theoffice.moneysaver.R;

public class MyPermissionManager {

    private static Activity stActivity;

    public static boolean checkPermission(final Activity activity, final String permissionRequested){
        stActivity = activity;
        if (ContextCompat.checkSelfPermission(activity, permissionRequested)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionRequested)){
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setCancelable(true);
                builder.setTitle(R.string.necesary_permission);
                builder.setMessage(R.string.permission_request_message);
                builder.setPositiveButton(
                        activity.getString(R.string.acept), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(
                                        activity,
                                        new String[]{permissionRequested},
                                        AppConstants.PERMISSION_REQUEST
                                );
                            }
                        }
                );
                AlertDialog dialog = builder.create();
                dialog.show();
            }else {
                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{permissionRequested},
                        AppConstants.PERMISSION_REQUEST
                );
            }
            return false;
        }
        return true;
    }

}
