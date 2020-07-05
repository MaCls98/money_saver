package com.theoffice.moneysaver.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_global_goals, container, false);
        RecyclerView rvMyProfile = v.findViewById(R.id.rv_goal_list);
        goalMutableLiveData = MoneySaverRepository.getInstance().getGlobalGoals(0);
        rvAdapter = new GlobalRVAdapter(getActivity(),
                goals);

        rvAdapter.setOnItemClickListener(new GlobalRVAdapter.OnItemClickListener() {

            @Override
            public void onImageClick(int position) {
                Goal goal = goalMutableLiveData.getValue().get(position);
                launchGoalDialog(goal);
            }
        });

        SpannedGridLayoutManager gridLayoutManager =
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
        return v;
    }

    private void launchGoalDialog(Goal goal) {
        DialogShowGoal dialogShowGoal = new DialogShowGoal();
        Bundle bundle = new Bundle();
        bundle.putSerializable("goal", goal);
        dialogShowGoal.setArguments(bundle);
        dialogShowGoal.setTargetFragment(this, 1);
        dialogShowGoal.show(getParentFragmentManager(), dialogShowGoal.getTag());
    }

    public void likeGoal(Goal goal){
        for (int i = 0; i < goals.size(); i++){
            Goal tmpGoal = goals.get(i);
            if (goal.getGoalId().equals(tmpGoal.getGoalId())){
                tmpGoal.setGoalLikes(goal.getGoalLikes());
                rvAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        goalMutableLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<Goal>>() {
            @Override
            public void onChanged(ArrayList<Goal> newGoals) {
                goals.clear();
                goals.addAll(newGoals);
                rvAdapter.notifyDataSetChanged();
            }
        });
    }
}
