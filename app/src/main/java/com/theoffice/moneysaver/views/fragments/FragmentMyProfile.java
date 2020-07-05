package com.theoffice.moneysaver.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.arasthel.spannedgridlayoutmanager.SpanSize;
import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager;
import com.bumptech.glide.Glide;
import com.theoffice.moneysaver.ApplicationMoneySaver;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.adapters.ProfileRVAdapter;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.data.model.User;
import com.theoffice.moneysaver.utils.AppConstants;
import com.theoffice.moneysaver.viewmodels.SharedViewModel;
import com.theoffice.moneysaver.views.activities.LoginActivity;
import com.theoffice.moneysaver.views.activities.MainActivity;
import com.theoffice.moneysaver.views.dialogs.DialogShowGoal;

import java.util.ArrayList;

import kotlin.jvm.functions.Function1;

public class FragmentMyProfile extends Fragment {

    private User user;

    private SharedViewModel viewModel;

    private RecyclerView rvMyProfile;
    private ProfileRVAdapter rvAdapter;

    private ProgressBar pbGoals;
    private TextView tvNoGoalsMeesage;
    private TextView tvUserName;
    private TextView tvUserGoals;
    private Button btnLogOut;
    private ImageView ivUserPhoto;

    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_profile, container, false);
        user = ApplicationMoneySaver.getMainUser();
        sharedPreferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        initComponents(v);

        rvAdapter = new ProfileRVAdapter(getActivity(),
                user.getGoalList());


        rvAdapter.setOnItemClickListener(new ProfileRVAdapter.OnItemClickListener() {

            @Override
            public void onImageClick(int position) {
                Goal goal = user.getGoalList().get(rvAdapter.getRealPosition(position));
                launchGoalDialog(goal, rvAdapter.getRealPosition(position));
            }

            @Override
            public void onDeleteClick(int position) {
                //TODO Eliminar meta

            }
        });
        setLinealLayoutRV();
        rvMyProfile.setAdapter(rvAdapter);
        return v;
    }

    private void initComponents(View v) {
        rvMyProfile = v.findViewById(R.id.rv_my_profile);
        pbGoals = v.findViewById(R.id.pb_loading_goals);
        tvNoGoalsMeesage = v.findViewById(R.id.tv_no_goals);
        tvUserName = v.findViewById(R.id.tv_username);
        tvUserGoals = v.findViewById(R.id.tv_user_goals);
        btnLogOut = v.findViewById(R.id.btn_log_out);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).logOut();
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

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (rvAdapter.getItemViewType(position)){
                    case 1:
                        return 2;
                    case 0:

                        return 1;
                    default:
                        return -1;
                }
            }
        });
        rvMyProfile.setLayoutManager(layoutManager);
        rvAdapter.setRvLayout(AppConstants.RV_GRID_VIEW);
    }

    private void launchGoalDialog(Goal goal, int position) {
        DialogShowGoal dialogShowGoal = new DialogShowGoal();
        Bundle bundle = new Bundle();
        bundle.putSerializable("goal", goal);
        bundle.putInt("goalPos", position);
        dialogShowGoal.setArguments(bundle);
        dialogShowGoal.show(getParentFragmentManager(), dialogShowGoal.getTag());
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
