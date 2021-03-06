package com.theoffice.moneysaver.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.arasthel.spannedgridlayoutmanager.SpanSize;
import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager;
import com.huawei.hianalytics.scankit.HiAnalyticsTools;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.adapters.GlobalRVAdapter;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.data.repositories.MoneySaverRepository;
import com.theoffice.moneysaver.views.dialogs.DialogShowGoal;

import java.util.ArrayList;

import kotlin.jvm.functions.Function1;

public class FragmentGlobalGoals extends DialogFragment {

    private GlobalRVAdapter rvAdapter;
    private ProgressBar pbGlobals;
    private ArrayList<Goal> goals = new ArrayList<>();
    private MutableLiveData<ArrayList<Goal>> goalMutableLiveData;
    private MutableLiveData<Boolean> isLoadingComplete = new MutableLiveData<>(false);
    private int currentPage = 0;
    private HiAnalyticsInstance instance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_global_goals, container, false);
        pbGlobals = v.findViewById(R.id.pb_loading_globals);
        RecyclerView rvMyProfile = v.findViewById(R.id.rv_goal_list);
        goalMutableLiveData = MoneySaverRepository.getInstance().getGlobalGoals(currentPage, isLoadingComplete);
        rvAdapter = new GlobalRVAdapter(getActivity(),
                goals);
        HiAnalyticsTools.enableLog();
        instance = HiAnalytics.getInstance(getActivity());

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
                registryAnalytics(goal);
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

    private void registryAnalytics(Goal goal) {
        Bundle bundle = new Bundle();
        bundle.putString("goal_id", goal.getGoalId());
        bundle.putString("goal_name", goal.getGoalName());
        instance.onEvent("view_goal", bundle);
    }

    private void requestGoals(int page) {
        MutableLiveData<ArrayList<Goal>> tmpLiveData = MoneySaverRepository.getInstance().getGlobalGoals(page, isLoadingComplete);
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
        isLoadingComplete.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    pbGlobals.setVisibility(View.GONE);
                }else {
                    pbGlobals.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
