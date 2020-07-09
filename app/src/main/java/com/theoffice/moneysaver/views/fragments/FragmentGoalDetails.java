package com.theoffice.moneysaver.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.data.model.Goal;

public class FragmentGoalDetails extends Fragment {

    private Goal goal;
    private TextView tvGoalCost, tvGoalMoney, tvGoalDate, tvGoalLikes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goal_details, container, false);
        goal = (Goal) getArguments().getSerializable("goal");
        Log.d("GOAL", goal.getGoalDate());
        tvGoalCost = v.findViewById(R.id.tv_goal_cost);
        tvGoalCost.setText("Meta: $" + goal.getGoalCost());
        tvGoalMoney = v.findViewById(R.id.tv_goal_money);
        tvGoalMoney.setText("Ahorro de: " + "$" + goal.getGoalActualMoney());
        tvGoalDate = v.findViewById(R.id.tv_goal_date);
        tvGoalDate.setText("Meta creada en: "  + goal.getGoalDate());
        tvGoalLikes = v.findViewById(R.id.tv_goal_likes);
        tvGoalLikes.setText( goal.getGoalLikes().size() + " likes");

        return v;
    }
}
