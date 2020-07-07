package com.theoffice.moneysaver.views.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.huawei.hms.maps.CameraUpdate;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.MapsInitializer;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.CameraPosition;
import com.huawei.hms.maps.model.Circle;
import com.huawei.hms.maps.model.CircleOptions;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.data.model.Goal;

public class FragmentGoalMap extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapViewDemoActivity";
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final int REQUEST_CODE = 100;
    private HuaweiMap hmap;
    private MapView mMapView;
    private Marker mMarker;
    private Circle mCircle;
    private double latitude;
    private double longitude;
    private Goal goal;

    private static final String[] RUNTIME_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goal_map, container, false);

        MapsInitializer.setApiKey("CV8VCTb2SGaHXMkAN7YJTkCVOgIJPDi7E+tyszgsfwzIiW7ETxiur0+2f+9kaRcWgpBltJidiAxcVswjTkNfgkCxieCb");

        goal = (Goal) getArguments().getSerializable("goal");
        latitude = goal.getLatitude();
        longitude = goal.getLongitude();

        if (!hasPermissions(this.getContext(), RUNTIME_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this.getActivity(), RUNTIME_PERMISSIONS, REQUEST_CODE);
        }

        // get mapView by layout view
        mMapView = v.findViewById(R.id.mapView);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);

        // get map by async method
        mMapView.getMapAsync(this);
        return v;
    }

    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        hmap = huaweiMap;
        hmap.setMyLocationEnabled(false);

        // move camera by CameraPosition param ,latlag and zoom params can set here
        CameraPosition build = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(5).build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(build);
        hmap.animateCamera(cameraUpdate);
        hmap.setMaxZoomPreference(5);
        hmap.setMinZoomPreference(2);

        // mark can be add by HuaweiMap
        mMarker = hmap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.badge_ph))
                .clusterable(true));

        mMarker.showInfoWindow();

        // circle can be add by HuaweiMap
        mCircle = hmap.addCircle(new CircleOptions().center(new LatLng(latitude, longitude)).radius(5000).fillColor(Color.GREEN));
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }
}
