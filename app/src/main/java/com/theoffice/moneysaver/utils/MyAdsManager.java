package com.theoffice.moneysaver.utils;

import android.content.Context;
import android.util.Log;

import com.huawei.hms.ads.identifier.AdvertisingIdClient;
import com.theoffice.moneysaver.hms.ppskit.OaidCallback;

import java.io.IOException;

public class MyAdsManager {

    private static final String TAG = "OaidActivity";

    public static void getOaid(Context context, OaidCallback callback) {
        if (null == context || null == callback) {
            Log.d(TAG, "invalid input param");
            return;
        }
        try {
            AdvertisingIdClient.Info info = AdvertisingIdClient.getAdvertisingIdInfo(context);
            if (null != info) {
                callback.onSuccuss(info.getId(), info.isLimitAdTrackingEnabled());
            } else {
                callback.onFail("oaid is null");
            }
        } catch (IOException e) {
            Log.d(TAG, "getAdvertisingIdInfo IOException");
            callback.onFail("getAdvertisingIdInfo IOException");
        }
    }

}
