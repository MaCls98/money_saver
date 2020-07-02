package com.theoffice.moneysaver.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.theoffice.moneysaver.ApplicationMoneySaver;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.adapters.ProfileRVAdapter;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.data.model.User;
import com.theoffice.moneysaver.utils.AppConstants;
import com.theoffice.moneysaver.viewmodels.ProfileViewModel;
import com.theoffice.moneysaver.views.activities.PlayGround;
import com.theoffice.moneysaver.views.dialogs.DialogShowGoal;

import java.util.ArrayList;

public class FragmentMyProfile extends Fragment {

    private User user;

    private ProfileViewModel viewModel;

    private RecyclerView rvMyProfile;
    private ProfileRVAdapter rvAdapter;
    private ProgressBar pbGoals;
    private TextView tvNoGoalsMeesage;
    private TextView tvUserName;
    private TextView tvUserGoals;
    private Button btnScann;
    private ImageView ivUserPhoto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_profile, container, false);
        user = ApplicationMoneySaver.getMainUser();
        initComponents(v);

        rvAdapter = new ProfileRVAdapter(getActivity(),
                user.getGoalList());


        rvAdapter.setOnItemClickListener(new ProfileRVAdapter.OnItemClickListener() {
            @Override
            public void onLikeClick(int position) {
                //TODO Dar like a la meta

            }

            @Override
            public void onImageClick(int position) {
                launchGoalDialog(position);
            }

            @Override
            public void onDeleteClick(int position) {
                //TODO Eliminar meta

            }
        });
        setGridLayoutRV();
        rvMyProfile.setAdapter(rvAdapter);
        return v;
    }

    private void initComponents(View v) {
        rvMyProfile = v.findViewById(R.id.rv_my_profile);
        pbGoals = v.findViewById(R.id.pb_loading_goals);
        tvNoGoalsMeesage = v.findViewById(R.id.tv_no_goals);
        tvUserName = v.findViewById(R.id.tv_username);
        tvUserGoals = v.findViewById(R.id.tv_user_goals);
        btnScann = v.findViewById(R.id.btn_scann_product);
        btnScann.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PlayGround.class);
                getContext().startActivity(intent);
            }
        });
        ivUserPhoto = v.findViewById(R.id.iv_user_photo);

        tvUserName.setText(user.getUserName());
        tvUserGoals.setText(getContext().getString(R.string.goals, user.getGoalList().size()));

        Glide.with(getContext())
                .load(user.getUserPhotoUrl())
                .placeholder(R.drawable.user_icon)
                .into(ivUserPhoto);
    }

    public void changeRecyclerViewLayout(int rvLayout){
        switch (rvLayout){
            case AppConstants.RV_LIST_VIEW:
                if (rvAdapter.getRvLayout() != AppConstants.RV_LIST_VIEW) setLinealLayoutRV();
                break;
            case AppConstants.RV_GRID_VIEW:
                if (rvAdapter.getRvLayout() != AppConstants.RV_GRID_VIEW) setGridLayoutRV();
                break;
        }
        rvMyProfile.setAdapter(rvAdapter);
        rvAdapter.notifyDataSetChanged();
    }

    private void setLinealLayoutRV(){
        rvMyProfile.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvAdapter.setRvLayout(AppConstants.RV_LIST_VIEW);
    }

    private void setGridLayoutRV(){
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        /*
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (rvAdapter.getItemViewType(position)){
                    case AppConstants
                            .TYPE_HEADER:
                        return 2;
                    case AppConstants.TYPE_ITEM:

                        return 1;
                    default:
                        return -1;
                }
            }
        });

         */
        rvMyProfile.setLayoutManager(layoutManager);
        rvAdapter.setRvLayout(AppConstants.RV_GRID_VIEW);
    }

    private void launchGoalDialog(int goal) {
        DialogShowGoal dialogShowGoal = new DialogShowGoal();
        Bundle bundle = new Bundle();
        bundle.putInt("goal", goal);
        dialogShowGoal.setArguments(bundle);
        dialogShowGoal.show(getParentFragmentManager(), dialogShowGoal.getTag());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        viewModel.init();
        viewModel.getGoalMutableLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Goal>>() {
            @Override
            public void onChanged(ArrayList<Goal> goals) {
                ApplicationMoneySaver.getMainUser().setGoalList(goals);
                if (user.getGoalList().size() > 0){
                    pbGoals.setVisibility(View.GONE);
                    tvNoGoalsMeesage.setVisibility(View.GONE);
                }else {
                    pbGoals.setVisibility(View.GONE);
                    tvNoGoalsMeesage.setVisibility(View.VISIBLE);
                }
                tvUserGoals.setText(getContext().getString(R.string.goals, user.getGoalList().size()));
                rvAdapter.notifyDataSetChanged();
            }
        });
    }
}
