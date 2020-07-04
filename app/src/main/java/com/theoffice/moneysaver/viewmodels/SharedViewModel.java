package com.theoffice.moneysaver.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.theoffice.moneysaver.ApplicationMoneySaver;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.data.repositories.MoneySaverRepository;
import com.theoffice.moneysaver.utils.AppConstants;

import java.util.ArrayList;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Goal>> goalMutableLiveData;
    private MoneySaverRepository repository;

    public void init(){
        if (goalMutableLiveData != null){
            return;
        }
        repository = MoneySaverRepository.getInstance();
        goalMutableLiveData =
                repository.getGoals(AppConstants.BASE_URL + AppConstants.GOALS_URL,
                        ApplicationMoneySaver.getMainUser().getUserId());
    }

    public void updateGoalsList(){
        goalMutableLiveData =
                repository.getGoals(AppConstants.BASE_URL + AppConstants.GOALS_URL,
                        ApplicationMoneySaver.getMainUser().getUserId());
    }

    public void updateGoalContribution(Goal goal){
        ArrayList<Goal> tmpGoalList = goalMutableLiveData.getValue();
        for (int i = 0; i < tmpGoalList.size(); i++) {
            Goal tmpGoal = tmpGoalList.get(i);
            if (tmpGoal.getGoalId().equals(goal.getGoalId())){
                tmpGoalList.get(i).setContributionCount(goal.getContributionCount());
            }
        }
        goalMutableLiveData.postValue(tmpGoalList);
    }

    public MutableLiveData<ArrayList<Goal>> getGoalMutableLiveData() {
        return goalMutableLiveData;
    }
}
