package com.theoffice.moneysaver.views.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.cloudinary.android.MediaManager;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.data.model.Product;
import com.theoffice.moneysaver.hms.ppskit.OaidCallback;
import com.theoffice.moneysaver.utils.AppConstants;
import com.theoffice.moneysaver.utils.MyAdsManager;
import com.theoffice.moneysaver.utils.MyToast;
import com.theoffice.moneysaver.views.dialogs.DialogAddGoal;
import com.theoffice.moneysaver.views.dialogs.DialogProduct;
import com.theoffice.moneysaver.views.fragments.BottomNavigationFragment;
import com.theoffice.moneysaver.views.fragments.FragmentGlobalGoals;
import com.theoffice.moneysaver.views.fragments.FragmentMyHome;
import com.theoffice.moneysaver.views.fragments.FragmentMyProfile;
import com.theoffice.moneysaver.views.fragments.FragmentHuaweiGoals;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.theoffice.moneysaver.utils.AppConstants.REQUEST_SCANNER;

public class MainActivity extends AppCompatActivity implements OaidCallback {

    private BottomAppBar mainAppBar;
    private FloatingActionButton fbShowMenu;
    private ExtendedFloatingActionButton fbAddGoal;
    private ExtendedFloatingActionButton fbScanProduct;

    private SharedPreferences sharedPreferences;

    boolean isFABOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Map config = new HashMap();
        config.put("cloud_name", AppConstants.CLOUDINARY_NAME);
        MediaManager.init(getBaseContext(), config);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        setContentView(R.layout.activity_main);
        mainAppBar = findViewById(R.id.bottom_app_bar);
        mainAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
        fbShowMenu = findViewById(R.id.fb_show_menu);
        fbAddGoal = findViewById(R.id.fb_add_goal);
        fbAddGoal.shrink();
        fbScanProduct = findViewById(R.id.fb_scan_product);
        fbScanProduct.shrink();

        setSupportActionBar(mainAppBar);
        setListeners();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fg_container_view, new FragmentMyProfile()).commit();
        HwAds.init(this);
        getIdentifierThread.start();
    }

    public void changeFragment(int fragmentConstant){
        switch (fragmentConstant){
            case AppConstants.HOME:
                showFragment(new FragmentMyHome());
                break;
            case AppConstants.MY_PROFILE:
                showFragment(new FragmentMyProfile());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainAppBar.replaceMenu(R.menu.profile_appbar_menu);
                    }
                });
                break;
            case AppConstants.GLOBAL_GOALS:
                showFragment(new FragmentGlobalGoals());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainAppBar.replaceMenu(R.menu.global_appbar_menu);
                    }
                });
                break;
            case AppConstants.SCAN_PRODUCT:
                showFragment(new FragmentHuaweiGoals());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainAppBar.replaceMenu(R.menu.global_appbar_menu);
                    }
                });
                break;
        }
    }

    private void showFragment(Fragment fragment) {
        hideFABS();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fg_container_view, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setListeners() {
        mainAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.rectangle_menu_view:
                        changeRecyclerViewLayout(AppConstants.RV_LIST_VIEW);
                        break;
                    case R.id.grid_menu_view:
                        changeRecyclerViewLayout(AppConstants.RV_GRID_VIEW);
                        break;
                }
                return false;
            }
        });
        mainAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomMenu();
                hideFABS();
            }
        });
        fbShowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addGoal();
                openGoalsMenu();
            }
        });
        fbAddGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideFABS();
                addGoal();
            }
        });
        fbScanProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideFABS();
                launchScanner();
            }
        });
    }

    private void launchScanner() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_SCANNER);
    }

    private void openGoalsMenu() {
        if (!isFABOpen){
            showFABS();
        }else {
            hideFABS();
        }
    }

    private void hideFABS() {
        isFABOpen = false;
        fbScanProduct.shrink();
        fbAddGoal.shrink();
        fbAddGoal.animate().translationY(0);
        fbScanProduct.animate().translationY(0);
    }

    private void showFABS() {
        isFABOpen = true;
        fbScanProduct.extend();
        fbAddGoal.extend();
        fbAddGoal.animate().translationY(-380);
        fbScanProduct.animate().translationY(-200);
    }

    private void changeRecyclerViewLayout(int rvSquareView) {
        hideFABS();
        FragmentMyProfile myProfile = (FragmentMyProfile) getSupportFragmentManager().findFragmentById(R.id.fg_container_view);
        assert myProfile != null;
        myProfile.changeRecyclerViewLayout(rvSquareView);
    }

    private void addGoal() {
        DialogAddGoal dialogAddGoal = new DialogAddGoal();
        dialogAddGoal.show(getSupportFragmentManager(), dialogAddGoal.getTag());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_appbar_menu, menu);
        return true;
    }

    private void showBottomMenu() {
        BottomNavigationFragment navigationFragment = new BottomNavigationFragment();
        navigationFragment.show(getSupportFragmentManager(), navigationFragment.getTag());
    }

    public void logOut(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getString(R.string.auto_login_key), false);
        editor.apply();
        Intent loginAct = new Intent(this, LoginActivity.class);
        startActivity(loginAct);
        finish();
    }

    private void launchProductDialog(HmsScan obj) {
        JSONObject productJson = null;
        try {
            productJson = new JSONObject(obj.getShowResult());
            if (productJson.has("qr_type")){
                Product product = new Product(
                        productJson.getString("product_name"),
                        productJson.getInt("product_value"),
                        productJson.getString("product_image")
                );
                DialogProduct dialogProduct = new DialogProduct();
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", product);
                dialogProduct.setArguments(bundle);
                dialogProduct.show(getSupportFragmentManager(), dialogProduct.getTag());
            }else {

            }
        } catch (JSONException e) {
            MyToast.showShortToast(getString(R.string.wrong_qr_code), getBaseContext());
            e.printStackTrace();
        }
    }

    private Thread getIdentifierThread = new Thread(){

        @Override
        public void run() {
            getOaid();
        }
    };

    private void getOaid() {
        MyAdsManager.getOaid(this, this);
    }

    @Override
    public void onSuccuss(String oaid, boolean isOaidTrackLimited) {
        Log.d("ADS", oaid + " - " + isOaidTrackLimited);
    }

    @Override
    public void onFail(String errMsg) {
        Log.d("ADS", errMsg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SCANNER) {
            try {
                HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
                if (obj != null) {
                    launchProductDialog(obj);
                }
            }catch (Exception e){

            }
        }

        for (Fragment fragment: getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_SCANNER && grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ScanUtil.startScan(this, requestCode, null);
        }

        for (Fragment fragment:
             getSupportFragmentManager().getFragments()) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}