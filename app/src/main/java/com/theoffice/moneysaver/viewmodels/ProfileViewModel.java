package com.theoffice.moneysaver.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.theoffice.moneysaver.MoneySaverApplication;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.data.repositories.MoneySaverRepository;
import com.theoffice.moneysaver.utils.AppConstants;
import com.theoffice.moneysaver.utils.MyTestHelper;

import java.util.ArrayList;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Goal>> goalMutableLiveData;
    private MoneySaverRepository repository;

    public void init(){
        if (goalMutableLiveData != null){
            return;
        }
        repository = MoneySaverRepository.getInstance();
        goalMutableLiveData =
                repository.getGoals(AppConstants.BASE_URL + AppConstants.GOALS_URL,
                        MoneySaverApplication.getMainUser().getUserId());
    }

    public MutableLiveData<ArrayList<Goal>> getGoalMutableLiveData() {
        return goalMutableLiveData;
    }
}
