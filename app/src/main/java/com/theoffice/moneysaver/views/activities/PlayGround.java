package com.theoffice.moneysaver.views.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.nativead.MediaView;
import com.huawei.hms.ads.nativead.NativeAd;
import com.huawei.hms.ads.nativead.NativeAdConfiguration;
import com.huawei.hms.ads.nativead.NativeAdLoader;
import com.huawei.hms.ads.nativead.NativeView;
import com.theoffice.moneysaver.R;

public class PlayGround extends AppCompatActivity {

    private int layoutId;
    private NativeAd globalNativeAd;
    private ScrollView adScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_ground);
        adScrollView = findViewById(R.id.scroll_view_ad);
        loadAd(getAdId());
    }

    private void loadAd(String adId) {
        NativeAdLoader.Builder builder = new NativeAdLoader.Builder(this, adId);

        builder.setNativeAdLoadedListener(new NativeAd.NativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                showNativeAd(nativeAd);
            }
        }).setAdListener(new AdListener(){
            @Override
            public void onAdFailed(int i) {
                super.onAdFailed(i);
            }
        });

        NativeAdConfiguration adConfiguration = new NativeAdConfiguration.Builder()
                .setChoicesPosition(NativeAdConfiguration.ChoicesPosition.BOTTOM_RIGHT)
                .build();
        NativeAdLoader nativeAdLoader = builder.setNativeAdOptions(adConfiguration).build();
        nativeAdLoader.loadAd(new AdParam.Builder().build());
    }

    private void showNativeAd(NativeAd nativeAd){
        globalNativeAd = nativeAd;

        // Obtain NativeView.
        NativeView nativeView = (NativeView) getLayoutInflater().inflate(layoutId, null);

        // Register and populate a native ad material view.
        initNativeAdView(globalNativeAd, nativeView);

        // Add NativeView to the app UI.
        adScrollView.removeAllViews();
        adScrollView.addView(nativeView);
    }

    private String getAdId(){
        String adId;
        layoutId = R.layout.native_small_template;
        adId = getString(R.string.ad_id_native_small);
        return adId;
    }

    private void initNativeAdView(NativeAd nativeAd, NativeView nativeView) {
        // Register a native ad material view.
        nativeView.setTitleView(nativeView.findViewById(R.id.ad_title));
        nativeView.setMediaView((MediaView) nativeView.findViewById(R.id.ad_media));
        nativeView.setAdSourceView(nativeView.findViewById(R.id.ad_source));
        nativeView.setCallToActionView(nativeView.findViewById(R.id.ad_call_to_action));

        // Populate a native ad material view.
        ((TextView) nativeView.getTitleView()).setText(nativeAd.getTitle());
        nativeView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        if (null != nativeAd.getAdSource()) {
            ((TextView) nativeView.getAdSourceView()).setText(nativeAd.getAdSource());
        }
        nativeView.getAdSourceView()
                .setVisibility(null != nativeAd.getAdSource() ? View.VISIBLE : View.INVISIBLE);

        if (null != nativeAd.getCallToAction()) {
            ((Button) nativeView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        nativeView.getCallToActionView()
                .setVisibility(null != nativeAd.getCallToAction() ? View.VISIBLE : View.INVISIBLE);

        // Register a native ad object.
        nativeView.setNativeAd(nativeAd);
    }

    /**
     * Update tip and status of the load button.
     *
     * @param text           tip.
     * @param loadBtnEnabled status of the load button.
     */
    private void updateStatus(String text, boolean loadBtnEnabled) {
        if (null != text) {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (null != globalNativeAd) {
            globalNativeAd.destroy();
        }

        super.onDestroy();
    }

}