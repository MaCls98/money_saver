package com.theoffice.moneysaver.views.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.data.model.Goal;

import java.util.Objects;

public class DialogShowGoal extends DialogFragment implements View.OnClickListener {

    private Goal goal;

    private TextView tvGoalName;
    private TextView tvGoalValue;
    private TextView tvGoalDate;
    private TextView tvGoalLikes;
    private TextView tvGoalContributions;
    private ImageView ivGoalPhoto;
    private ImageButton ibLikeGoal;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        goal = (Goal) bundle.getSerializable("goal");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_goal, container, false);
        initComponents(v);
        return v;
    }

    private void initComponents(View v) {
        tvGoalName = v.findViewById(R.id.tv_goal_name);
        tvGoalName.setText(goal.getGoalName());
        tvGoalValue = v.findViewById(R.id.tv_goal_actual_money);
        tvGoalValue.setText("$" + goal.getGoalActualMoney() + " / " + "$" + goal.getGoalCost());
        tvGoalDate = v.findViewById(R.id.tv_goal_date);
        tvGoalDate.setText(goal.getGoalDate());
        tvGoalLikes = v.findViewById(R.id.tv_goal_likes);
        tvGoalLikes.setText("Total likes: " + goal.getGoalLikes().length);
        tvGoalContributions = v.findViewById(R.id.tv_goal_contribution);
        tvGoalContributions.setText("Cantidad de aportes: " + goal.getContributionCount());

        ivGoalPhoto = v.findViewById(R.id.iv_goal_photo);
        Glide.with(getActivity())
                .load(goal.getGoalPhotoPath())
                .into(ivGoalPhoto);
        ibLikeGoal = v.findViewById(R.id.ib_like_goal);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(getDialog().getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setWindowAnimations(R.style.AppTheme_Slide);
    }
}
