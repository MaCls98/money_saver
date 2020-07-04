package com.theoffice.moneysaver.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.adapters.ProfileRVAdapter;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.data.model.User;
import com.theoffice.moneysaver.data.repositories.MoneySaverRepository;
import com.theoffice.moneysaver.utils.AppConstants;

import java.util.ArrayList;

public class FragmentGlobalGoals extends Fragment {

    private ProfileRVAdapter rvAdapter;
    private RecyclerView rvMyProfile;
    private MutableLiveData<ArrayList<Goal>> goalMutableLiveData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_global_goals, container, false);
        rvMyProfile = v.findViewById(R.id.rv_goal_list);
        goalMutableLiveData = MoneySaverRepository.getInstance().getGlobalGoals(0);
        initComponents(v);
        rvAdapter = new ProfileRVAdapter(getActivity(),
                goalMutableLiveData.getValue());

        rvAdapter.setOnItemClickListener(new ProfileRVAdapter.OnItemClickListener() {

            @Override
            public void onImageClick(int position) {
                //launchGoalDialog(rvAdapter.getRealPosition(position));
            }

            @Override
            public void onDeleteClick(int position) {
                //TODO Eliminar meta
            }
        });
        setLinealLayoutRV();
        rvMyProfile.setAdapter(rvAdapter);
        return v;
    }

    private void initComponents(View v) {
    }

    private void setLinealLayoutRV(){
        rvAdapter.setRvLayout(AppConstants.RV_LIST_VIEW);
    }
}
