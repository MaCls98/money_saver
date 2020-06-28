package com.theoffice.moneysaver.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.theoffice.moneysaver.ApplicationMoneySaver;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.adapters.ProfileRVAdapter;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.data.model.User;
import com.theoffice.moneysaver.utils.AppConstants;
import com.theoffice.moneysaver.viewmodels.ProfileViewModel;

import java.util.ArrayList;

public class FragmentMyProfile extends Fragment {

    private ProfileViewModel viewModel;
    private RecyclerView rvMyProfile;
    private ProfileRVAdapter rvAdapter;
    private ProgressBar pbGoals;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_profile, container, false);

        rvMyProfile = v.findViewById(R.id.rv_my_profile);
        pbGoals = v.findViewById(R.id.pb_loading_goals);
        User user = ApplicationMoneySaver.getMainUser();
        rvAdapter = new ProfileRVAdapter(getActivity(),
                user);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (rvAdapter.getItemViewType(position)){
                    case AppConstants
                            .TYPE_HEADER:
                        return 2;
                    case AppConstants.TYPE_ITEM:

                        return 1;
                    default:
                        return -1;
                }
            }
        });
        rvMyProfile.setLayoutManager(layoutManager);
        rvMyProfile.setAdapter(rvAdapter);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        viewModel.init();
        viewModel.getGoalMutableLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Goal>>() {
            @Override
            public void onChanged(ArrayList<Goal> goals) {
                if (goals.size() > 0){
                    pbGoals.setVisibility(View.GONE);
                }
                ApplicationMoneySaver.getMainUser().setGoalList(goals);
                rvAdapter.notifyDataSetChanged();
            }
        });
    }
}
