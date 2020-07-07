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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.ads.App;
import com.huawei.hms.maps.CameraUpdate;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.CameraPosition;
import com.huawei.hms.maps.model.Circle;
import com.huawei.hms.maps.model.CircleOptions;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;
import com.theoffice.moneysaver.ApplicationMoneySaver;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.adapters.ProfileRVAdapter;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.data.model.User;
import com.theoffice.moneysaver.utils.AppConstants;
import com.theoffice.moneysaver.viewmodels.SharedViewModel;
import com.theoffice.moneysaver.views.dialogs.DialogShowGoal;

import java.util.ArrayList;

public class FragmentHuaweiGoals extends Fragment {

    private User user;
    private SharedViewModel viewModel;

    private RecyclerView rvHuaweiGoals;
    private ProfileRVAdapter rvAdapter;

    private ProgressBar pbGoals;
    private TextView tvNoGoalsMeesage;

    private ArrayList<Goal> huaweiGoals;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_huawei_goals, container, false);
        huaweiGoals = new ArrayList<>();
        user = ApplicationMoneySaver.getMainUser();
        pbGoals = v.findViewById(R.id.pb_loading_goals);
        tvNoGoalsMeesage = v.findViewById(R.id.tv_no_goals);
        rvHuaweiGoals = v.findViewById(R.id.rv_huawei_goals);
        rvHuaweiGoals.setLayoutManager(new LinearLayoutManager(getActivity()));

        rvAdapter = new ProfileRVAdapter(getActivity(), huaweiGoals);
        rvAdapter.setOnItemClickListener(new ProfileRVAdapter.OnItemClickListener() {

            @Override
            public void onImageClick(int position) {
                Goal goal = huaweiGoals.get(rvAdapter.getRealPosition(position));
                launchGoalDialog(goal, getRealPos(goal));
            }

            @Override
            public void onDeleteClick(int position) {
                //TODO Eliminar meta

            }
        });
        rvHuaweiGoals.setAdapter(rvAdapter);
        getHuaweiGoals();
        return v;
    }

    private int getRealPos(Goal goal) {
        for (int i = 0; i < user.getGoalList().size(); i++){
            Goal tmpGoal = user.getGoalList().get(i);
            if (tmpGoal.getGoalId().equals(goal.getGoalId())){
                return i;
            }
        }
        return 0;
    }

    private void getHuaweiGoals() {
        huaweiGoals.clear();
        for (Goal goal:
             user.getGoalList()) {
            if (goal.getGoalType().equals(AppConstants.GOAL_TYPE_HUAWEI)){
                huaweiGoals.add(goal);
            }
        }
        rvAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.init();
        viewModel.getGoalMutableLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Goal>>() {
            @Override
            public void onChanged(ArrayList<Goal> goals) {
                ApplicationMoneySaver.getMainUser().setGoalList(goals);
                if (user.getGoalList().size() > 0){
                    tvNoGoalsMeesage.setVisibility(View.GONE);
                    pbGoals.setVisibility(View.GONE);
                }
                getHuaweiGoals();
            }
        });
        viewModel.getIsLoadingComplete().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    if (user.getGoalList().size() == 0){
                        pbGoals.setVisibility(View.GONE);
                        tvNoGoalsMeesage.setVisibility(View.VISIBLE);
                    }
                }else {
                    pbGoals.setVisibility(View.VISIBLE);
                    tvNoGoalsMeesage.setVisibility(View.GONE);
                }
            }
        });
    }

    private void launchGoalDialog(Goal goal, int position) {
        DialogShowGoal dialogShowGoal = new DialogShowGoal();
        Bundle bundle = new Bundle();
        bundle.putSerializable("goal", goal);
        bundle.putInt("goalPos", position);
        dialogShowGoal.setArguments(bundle);
        dialogShowGoal.show(getParentFragmentManager(), dialogShowGoal.getTag());
    }
}
