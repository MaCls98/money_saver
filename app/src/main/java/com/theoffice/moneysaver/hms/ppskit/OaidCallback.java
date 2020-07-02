package com.theoffice.moneysaver.hms.ppskit;

public interface OaidCallback {
    void onSuccuss(String oaid, boolean isOaidTrackLimited);

    void onFail(String errMsg);
}
