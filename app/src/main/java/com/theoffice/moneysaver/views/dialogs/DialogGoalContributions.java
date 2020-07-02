package com.theoffice.moneysaver.views.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.theoffice.moneysaver.ApplicationMoneySaver;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.adapters.ContributionRVAdapter;
import com.theoffice.moneysaver.data.model.Contribution;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.utils.AppConstants;
import com.theoffice.moneysaver.viewmodels.ProfileViewModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class DialogGoalContributions extends DialogFragment {

    private ArrayList<Contribution> contributions;

    private RecyclerView rvContributions;
    private TextView tvNoContributions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        contributions = (ArrayList<Contribution>) bundle.getSerializable("contributions");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_goal_contributions, container, false);
        rvContributions = v.findViewById(R.id.rv_contributions);
        rvContributions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContributions.setAdapter(new ContributionRVAdapter(contributions));

        tvNoContributions = v.findViewById(R.id.tv_no_contributions);
        if (contributions.size() > 0){
            tvNoContributions.setVisibility(View.GONE);
        }else {
            tvNoContributions.setVisibility(View.VISIBLE);
        }
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(getDialog().getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setWindowAnimations(R.style.AppTheme_Slide);
    }
}
