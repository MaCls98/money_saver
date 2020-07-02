package com.theoffice.moneysaver.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.nativead.MediaView;
import com.huawei.hms.ads.nativead.NativeAd;
import com.huawei.hms.ads.nativead.NativeAdConfiguration;
import com.huawei.hms.ads.nativead.NativeAdLoader;
import com.huawei.hms.ads.nativead.NativeView;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.hmsscankit.WriterException;
import com.huawei.hms.ml.scan.HmsBuildBitmapOption;
import com.huawei.hms.ml.scan.HmsScan;
import com.theoffice.moneysaver.R;

public class PlayGround extends AppCompatActivity implements View.OnClickListener {

    private static int CAMERA_REQ_CODE;
    private int layoutId;
    private NativeAd globalNativeAd;
    private ScrollView adScrollView;
    private Button btnScann;
    private Button btnPrint;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_ground);
        adScrollView = findViewById(R.id.scroll_view_ad);
        loadAd(getAdId());

        btnScann = findViewById(R.id.button_esc);
        btnScann.setOnClickListener(this);

        btnScann = findViewById(R.id.button_print);
        btnScann.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                print();
            }
        });

        img = findViewById(R.id.image_code);
    }

    private void print() {
        String content = "QR Code Content";
        int type = HmsScan.QRCODE_SCAN_TYPE;
        int width = 400;
        int height = 400;
        HmsBuildBitmapOption options = new HmsBuildBitmapOption.Creator().setBitmapBackgroundColor(Color.RED).setBitmapColor(Color.BLUE).setBitmapMargin(3).create();
        try {
            Bitmap qrBitmap = ScanUtil.buildBitmap(content, type, width, height, options);
            img.setImageBitmap(qrBitmap);
        } catch (WriterException e) {
            Log.w("buildBitmap", e);
        }
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

    @Override
    public void onClick(View view) {
        Log.i("alexxx","scann");
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_REQ_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_REQ_CODE && grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ScanUtil.startScan(this, requestCode, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        if (requestCode == CAMERA_REQ_CODE) {
            HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj != null) {
                showResult(obj);
            }
        }
    }

    private void showResult(HmsScan obj) {
        Log.i("alexxx", obj.getOriginalValue());
    }
}