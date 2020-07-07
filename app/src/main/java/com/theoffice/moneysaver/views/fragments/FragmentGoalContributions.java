package com.theoffice.moneysaver.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.adapters.ContributionRVAdapter;
import com.theoffice.moneysaver.data.model.Contribution;

import java.util.ArrayList;

public class FragmentGoalContributions extends Fragment {

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
        View v = inflater.inflate(R.layout.fragment_goal_contributions, container, false);
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
}
