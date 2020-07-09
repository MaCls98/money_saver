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
import com.huawei.hms.maps.MapFragment;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.MapsInitializer;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.SupportMapFragment;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.CameraPosition;
import com.huawei.hms.maps.model.Circle;
import com.huawei.hms.maps.model.CircleOptions;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.data.model.Goal;

public class FragmentGoalMap<mMapFragment> extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapViewDemoActivity";
    //HUAWEI map
    private HuaweiMap hMap;
    private MapView mMapView;
    private Circle mCircle;
    private Marker mMarker;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private Goal goal;
    private double latitude;
    private double longitude;

    private static final String[] RUNTIME_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goal_map, container, false);
        goal = (Goal) getArguments().getSerializable("goal");
        latitude = goal.getLatitude();
        longitude = goal.getLongitude();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = view.findViewById(R.id.mapView);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        MapsInitializer.setApiKey("CV8VCTb2SGaHXMkAN7YJTkCVOgIJPDi7E+tyszgsfwzIiW7ETxiur0+2f+9kaRcWgpBltJidiAxcVswjTkNfgkCxieCb");

        mMapView.onCreate(mapViewBundle);
        //get map instance
        mMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(HuaweiMap map) {
        Log.d(TAG, "onMapReady: ");
        hMap = map;
        hMap.setMyLocationEnabled(true);
        CameraPosition build = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(21).build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(build);
        hMap.animateCamera(cameraUpdate);
        hMap.setMaxZoomPreference(21);
        hMap.setMinZoomPreference(10);

        mMarker = hMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.huawei_icon))
                .clusterable(true));

        mMarker.showInfoWindow();

        mCircle = hMap.addCircle(new CircleOptions().center(new LatLng(latitude, longitude)).radius(5).fillColor(Color.GREEN));
        mCircle.setFillColor(Color.TRANSPARENT);
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
