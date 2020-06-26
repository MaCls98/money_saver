package com.theoffice.moneysaver.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.adapters.ProfileRVAdapter;
import com.theoffice.moneysaver.data.model.User;
import com.theoffice.moneysaver.utils.MyTestHelper;
import com.theoffice.moneysaver.views.activities.MainActivity;

public class FragmentMyProfile extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_profile, container, false);
        MainActivity activity = (MainActivity) getActivity();
        RecyclerView rvMyProfile = v.findViewById(R.id.rv_my_profile);
        rvMyProfile.setLayoutManager(new LinearLayoutManager(getActivity()));
        if(activity.getHuaweiAccount() != null){
            User testUser = new User(1, activity.getHuaweiAccount().getDisplayName(), MyTestHelper.getGoalList());
            ProfileRVAdapter rvAdapter = new ProfileRVAdapter(getActivity(),
                    testUser);
            rvMyProfile.setAdapter(rvAdapter);
        }
        return v;
    }
}
