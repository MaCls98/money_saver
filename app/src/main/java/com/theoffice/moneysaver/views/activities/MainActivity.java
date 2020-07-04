package com.theoffice.moneysaver.views.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.huawei.hms.ads.HwAds;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.hms.ppskit.OaidCallback;
import com.theoffice.moneysaver.utils.AppConstants;
import com.theoffice.moneysaver.utils.MyAdsManager;
import com.theoffice.moneysaver.views.dialogs.DialogAddGoal;
import com.theoffice.moneysaver.views.fragments.BottomNavigationFragment;
import com.theoffice.moneysaver.views.fragments.FragmentGlobalGoals;
import com.theoffice.moneysaver.views.fragments.FragmentMyHome;
import com.theoffice.moneysaver.views.fragments.FragmentMyProfile;
import com.theoffice.moneysaver.views.fragments.FragmentScanner;

public class MainActivity extends AppCompatActivity implements OaidCallback {

    private BottomAppBar mainAppBar;
    private FloatingActionButton fbShowMenu;
    private ExtendedFloatingActionButton fbAddGoal;
    private ExtendedFloatingActionButton fbScanProduct;

    boolean isFABOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void changeFragment(int fragmentConstant){
        switch (fragmentConstant){
            case AppConstants.HOME:
                showFragment(new FragmentMyHome());
                break;
            case AppConstants.MY_PROFILE:
                showFragment(new FragmentMyProfile());
                break;
            case AppConstants.GLOBAL_GOALS:
                showFragment(new FragmentGlobalGoals());
                break;
            case AppConstants.SCAN_PRODUCT:
                showFragment(new FragmentScanner());
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
                addGoal();
                hideFABS();
            }
        });
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
        fbAddGoal.animate().translationY(-200);
        fbScanProduct.animate().translationY(-380);
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
        inflater.inflate(R.menu.appbar_menu, menu);
        return true;
    }

    private void showBottomMenu() {
        BottomNavigationFragment navigationFragment = new BottomNavigationFragment();
        navigationFragment.show(getSupportFragmentManager(), navigationFragment.getTag());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment: getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (Fragment fragment:
             getSupportFragmentManager().getFragments()) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}