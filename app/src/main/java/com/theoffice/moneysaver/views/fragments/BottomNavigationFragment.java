package com.theoffice.moneysaver.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.utils.AppConstants;
import com.theoffice.moneysaver.utils.MyToast;
import com.theoffice.moneysaver.views.activities.MainActivity;

public class BottomNavigationFragment extends BottomSheetDialogFragment  {

    private NavigationView navigationView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_menu, container, false);
        navigationView = view.findViewById(R.id.navigation_view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    /*
                    case R.id.reports:
                        changeActivityFragment(AppConstants.HOME);
                        break;
                     */
                    case R.id.my_profile:
                        changeActivityFragment(AppConstants.MY_PROFILE);
                        break;
                    case R.id.global_goals:
                        changeActivityFragment(AppConstants.GLOBAL_GOALS);
                        break;
                    case R.id.scan_product:
                        changeActivityFragment(AppConstants.SCAN_PRODUCT);
                        break;
                    case R.id.about:
                        showAboutDialog();
                        break;
                }
                return true;
            }
        });
    }

    private void showAboutDialog() {
        MyToast.showShortToast("Acerca de", getContext());
        dismiss();
    }

    private void changeActivityFragment(int fragmentConstant){
        ((MainActivity)getActivity()).changeFragment(fragmentConstant);
        dismiss();
    }
}
