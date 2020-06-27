package com.theoffice.moneysaver.views.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theoffice.moneysaver.ApplicationMoneySaver;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.utils.AppConstants;
import com.theoffice.moneysaver.utils.MyToast;
import com.theoffice.moneysaver.views.dialogs.DialogAddGoal;
import com.theoffice.moneysaver.views.fragments.BottomNavigationFragment;
import com.theoffice.moneysaver.views.fragments.FragmentGlobalGoals;
import com.theoffice.moneysaver.views.fragments.FragmentMyHome;
import com.theoffice.moneysaver.views.fragments.FragmentMyProfile;

public class MainActivity extends AppCompatActivity{

    private BottomAppBar mainAppBar;
    private FloatingActionButton btnAddGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainAppBar = findViewById(R.id.bottom_app_bar);
        btnAddGoal = findViewById(R.id.btn_add_goal);

        MyToast.showShortToast("Bienvenido " + ApplicationMoneySaver.getMainUser().getUserName(), this);

        setSupportActionBar(mainAppBar);
        setListeners();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fg_container_view, new FragmentMyProfile()).commit();
    }

    public void changeFragment(int fragmentConstant){
        switch (fragmentConstant){
            case AppConstants.MY_HOME:
                showFragment(new FragmentMyHome());
                break;
            case AppConstants
                    .MY_PROFILE:
                showFragment(new FragmentMyProfile());
                break;
            case AppConstants.MY_GOALS:
                showFragment(new FragmentGlobalGoals());
                break;
        }
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fg_container_view, fragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    private void setListeners() {
        mainAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.about:
                        Toast.makeText(MainActivity.this, "About", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
        mainAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomMenu();
            }
        });
        btnAddGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addGoal();
            }
        });
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