package com.theoffice.moneysaver.views.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.theoffice.moneysaver.ApplicationMoneySaver;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.viewmodels.ProfileViewModel;

import java.util.ArrayList;
import java.util.Objects;

public class DialogShowGoal extends DialogFragment implements View.OnClickListener {

    private ProfileViewModel viewModel;

    private Goal goal;
    private int goalPos;

    private TextView tvGoalName;
    private TextView tvGoalValue;
    private TextView tvGoalDate;
    private TextView tvGoalLikes;
    private TextView tvGoalContributions;
    private ImageView ivGoalPhoto;
    private ImageButton ibLikeGoal;
    private Button btnAddContribution;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        Bundle bundle = getArguments();
        goalPos = bundle.getInt("goal");
        goal = viewModel.getGoalMutableLiveData().getValue().get(goalPos);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getGoalMutableLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Goal>>() {
            @Override
            public void onChanged(ArrayList<Goal> goals) {
                Log.d("GOAL", "Detectado en dialogo");
                goal = goals.get(goalPos);
                ApplicationMoneySaver.getMainUser().setGoalList(goals);
                updateView();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_goal, container, false);
        initComponents(v);
        updateView();
        return v;
    }

    private void updateView() {
        tvGoalName.setText(this.goal.getGoalName());
        tvGoalValue.setText("$" + this.goal.getGoalActualMoney() + " / " + "$" + this.goal.getGoalCost());
        tvGoalDate.setText(this.goal.getGoalDate());
        tvGoalLikes.setText("Total likes: " + this.goal.getGoalLikes().length);
        tvGoalContributions.setText("Cantidad de aportes: " + this.goal.getContributionCount());
        Glide.with(getActivity())
                .load(this.goal.getGoalPhotoPath())
                .into(ivGoalPhoto);
        if (goal.getGoalActualMoney() == goal.getGoalCost()){
            btnAddContribution.setVisibility(View.GONE);
        }

    }

    private void initComponents(View v) {
        tvGoalName = v.findViewById(R.id.tv_goal_name);
        tvGoalValue = v.findViewById(R.id.tv_goal_actual_money);
        tvGoalDate = v.findViewById(R.id.tv_goal_date);
        tvGoalLikes = v.findViewById(R.id.tv_goal_likes);
        tvGoalContributions = v.findViewById(R.id.tv_goal_contribution);
        ivGoalPhoto = v.findViewById(R.id.iv_goal_photo);
        ibLikeGoal = v.findViewById(R.id.ib_like_goal);
        btnAddContribution = v.findViewById(R.id.btn_add_contribution);
        btnAddContribution.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_like_goal:
                likeGoal();
                break;
            case R.id.btn_add_contribution:
                addContribution();
                break;
        }
    }

    private void addContribution() {
        DialogAddContribution dialogAddContribution = new DialogAddContribution();
        Bundle bundle = new Bundle();
        bundle.putInt("goal", goalPos);
        dialogAddContribution.setArguments(bundle);
        dialogAddContribution.show(getParentFragmentManager(), dialogAddContribution.getTag());
    }

    private void likeGoal() {

    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(getDialog().getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
