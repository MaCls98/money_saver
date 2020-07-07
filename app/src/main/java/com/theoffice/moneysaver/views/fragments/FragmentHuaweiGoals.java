package com.theoffice.moneysaver.views.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapFragment;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.ml.scan.HmsScan;
import com.theoffice.moneysaver.R;

public class FragmentHuaweiGoals extends Fragment implements OnMapReadyCallback {

    private MapFragment mMapFragment;
    private HuaweiMap hMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_scanner, container, false);

        mMapFragment = (MapFragment) (getActivity().getFragmentManager().findFragmentById(R.id.mapFragment));
        mMapFragment.getMapAsync(this);
        return v;
    }

    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        Log.d("alexxx", "onMapReady: ");
        hMap = huaweiMap;
        hMap.getUiSettings().setMyLocationButtonEnabled(false);
        hMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(48.893478, 2.334595), 10));
    }
}
