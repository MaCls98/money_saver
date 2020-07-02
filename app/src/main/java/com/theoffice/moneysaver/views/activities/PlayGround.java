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
    private Button btnScann;
    private Button btnPrint;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_ground);

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