package com.theoffice.moneysaver.views.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.hmsscankit.WriterException;
import com.huawei.hms.ml.scan.HmsBuildBitmapOption;
import com.huawei.hms.ml.scan.HmsScan;
import com.theoffice.moneysaver.R;

import org.jetbrains.annotations.NotNull;

import static android.app.Activity.RESULT_OK;

public class FragmentHuaweiGoals extends Fragment implements View.OnClickListener{

    private static int CAMERA_REQ_CODE;
    private Button btnScann;
    private Button btnPrint;
    private ImageView img;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_scanner, container, false);
        btnScann = v.findViewById(R.id.button_esc);
        btnScann.setOnClickListener(this);

        btnPrint = v.findViewById(R.id.button_print);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                print();
            }
        });

        img = v.findViewById(R.id.image_code);
        return v;
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
        ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_REQ_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == CAMERA_REQ_CODE && grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ScanUtil.startScan(requireActivity(), requestCode, null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
