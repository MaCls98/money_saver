package com.theoffice.moneysaver.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arasthel.spannedgridlayoutmanager.SpanSize;
import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.adapters.GlobalRVAdapter;
import com.theoffice.moneysaver.adapters.ProfileRVAdapter;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.data.repositories.MoneySaverRepository;
import com.theoffice.moneysaver.utils.AppConstants;
import com.theoffice.moneysaver.views.dialogs.DialogShowGoal;

import java.util.ArrayList;

import kotlin.jvm.functions.Function1;

public class FragmentGlobalGoals extends DialogFragment {

    private GlobalRVAdapter rvAdapter;
    private ArrayList<Goal> goals = new ArrayList<>();
    private MutableLiveData<ArrayList<Goal>> goalMutableLiveData;
    private int currentPage = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_global_goals, container, false);
        RecyclerView rvMyProfile = v.findViewById(R.id.rv_goal_list);
        goalMutableLiveData = MoneySaverRepository.getInstance().getGlobalGoals(currentPage);
        rvAdapter = new GlobalRVAdapter(getActivity(),
                goals);

        rvAdapter.setOnItemClickListener(new GlobalRVAdapter.OnItemClickListener() {

            @Override
            public void onImageClick(String goalId) {
                Goal goal = null;
                for (Goal tmpGoal:
                     goals) {
                    if (tmpGoal.getGoalId().equals(goalId)){
                        goal = tmpGoal;
                    }
                }
                launchGoalDialog(goal);
            }
        });

        final SpannedGridLayoutManager gridLayoutManager =
                new SpannedGridLayoutManager(SpannedGridLayoutManager.Orientation.VERTICAL, 3);
        gridLayoutManager.setItemOrderIsStable(true);
        gridLayoutManager.setSpanSizeLookup(new SpannedGridLayoutManager.SpanSizeLookup(new Function1<Integer, SpanSize>(){
            @Override public SpanSize invoke(Integer position) {
                if (position % 7 == 1){
                    return new SpanSize(2, 2);
                }else {
                    return new SpanSize(1, 1);
                }
            }
        }));
        rvMyProfile.setLayoutManager(gridLayoutManager);
        rvMyProfile.setAdapter(rvAdapter);

        rvMyProfile.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    requestGoals(++currentPage);
                }
            }
        });
        requestGoals(++currentPage);
        return v;
    }

    private void requestGoals(int page) {
        MutableLiveData<ArrayList<Goal>> tmpLiveData = MoneySaverRepository.getInstance().getGlobalGoals(page);
        tmpLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<Goal>>() {
            @Override
            public void onChanged(ArrayList<Goal> tmpGoals) {
                goals.addAll(tmpGoals);
                rvAdapter.notifyDataSetChanged();
            }
        });
    }

    private void launchGoalDialog(Goal goal) {
        DialogShowGoal dialogShowGoal = new DialogShowGoal();
        Bundle bundle = new Bundle();
        bundle.putSerializable("goal", goal);
        dialogShowGoal.setArguments(bundle);
        dialogShowGoal.setTargetFragment(this, 1);
        dialogShowGoal.show(getParentFragmentManager(), dialogShowGoal.getTag());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        goalMutableLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<Goal>>() {
            @Override
            public void onChanged(ArrayList<Goal> newGoals) {
                goals.addAll(newGoals);
                rvAdapter.notifyDataSetChanged();
            }
        });
    }
}
