package com.theoffice.moneysaver.views.dialogs;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.theoffice.moneysaver.ApplicationMoneySaver;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.adapters.TabAdapter;
import com.theoffice.moneysaver.data.model.Contribution;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.utils.AppConstants;
import com.theoffice.moneysaver.utils.MyFileUtils;
import com.theoffice.moneysaver.utils.MyToast;
import com.theoffice.moneysaver.viewmodels.SharedViewModel;
import com.theoffice.moneysaver.views.fragments.FragmentGlobalGoals;
import com.theoffice.moneysaver.views.fragments.FragmentGoalContributions;
import com.theoffice.moneysaver.views.fragments.FragmentGoalDetails;
import com.theoffice.moneysaver.views.fragments.FragmentGoalMap;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DialogShowGoal extends DialogFragment implements View.OnClickListener {

    private SharedViewModel viewModel;

    private Goal goal;
    private int goalPos;
    private ArrayList<Contribution> contributions = new ArrayList<>();

    private TabAdapter tabAdapter;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageButton ibLikeGoal;
    private TextView tvGoalTotal;
    private ImageView ivGoalPhoto;
    private ProgressBar pbGoalProgress;
    private Toolbar toolbarGoal;
    private Button btnAddContribution;
    private Button btnDeleteGoal;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        Bundle bundle = getArguments();
        assert bundle != null;
        goal = (Goal) bundle.getSerializable("goal");
        goalPos = bundle.getInt("goalPos");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getGoalMutableLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Goal>>() {
            @Override
            public void onChanged(ArrayList<Goal> goals) {
                ApplicationMoneySaver.getMainUser().setGoalList(goals);
                updateGoalInfo();
                updateView();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
        View v = inflater.inflate(R.layout.dialog_goal, container, false);
        initComponents(v);
        getContributions();
        updateView();
        return v;
    }

    private void updateView() {
        if((25 - calculatePercentage(goal) / 4) > 0){
            MyFileUtils.blurImage(requireContext(), ivGoalPhoto, goal);
        }else{
            Glide.with(requireActivity())
                    .load(goal.getGoalPhotoPath())
                    .placeholder(R.drawable.money_icon)
                    .error(R.drawable.error_icon)
                    .into(ivGoalPhoto);
        }
        if (goal.getGoalActualMoney() == goal.getGoalCost()){
            btnAddContribution.setVisibility(View.GONE);
        }
        tvGoalTotal.setText("$" + goal.getGoalActualMoney() + "/" + "$" + goal.getGoalCost());
        viewPager.getAdapter().notifyDataSetChanged();
        pbGoalProgress.setProgress(calculatePercentage(goal));
        pbGoalProgress.setScaleY(2f);
    }

    private void initComponents(View v) {
        tabAdapter = new TabAdapter(getChildFragmentManager());
        toolbarGoal = v.findViewById(R.id.toolbar_goal);
        toolbarGoal.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        toolbarGoal.setTitle(goal.getGoalName());
        tvGoalTotal = v.findViewById(R.id.tv_goal_total);
        tvGoalTotal.setText("$" + goal.getGoalActualMoney() + "/" + "$" + goal.getGoalCost());
        ivGoalPhoto = v.findViewById(R.id.iv_goal_photo);
        ibLikeGoal = v.findViewById(R.id.ib_like_goal);
        ibLikeGoal.setOnClickListener(this);
        btnAddContribution = v.findViewById(R.id.btn_add_contribution);
        btnAddContribution.setOnClickListener(this);
        btnDeleteGoal = v.findViewById(R.id.btn_delete_goal);
        btnDeleteGoal.setOnClickListener(this);
        viewPager = v.findViewById(R.id.vp_goal_container);
        tabLayout = v.findViewById(R.id.tl_goal);

        pbGoalProgress = v.findViewById(R.id.pb_goal_progress);
        pbGoalProgress.setProgress(calculatePercentage(goal));

        for(String str: goal.getGoalLikes()){
            if (str.equals(ApplicationMoneySaver.getMainUser().getUserId())){
                ibLikeGoal.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_red_light), PorterDuff.Mode.MULTIPLY);
            }else {
                ibLikeGoal.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.white), PorterDuff.Mode.MULTIPLY);
            }
        }

        if (getTargetFragment() instanceof FragmentGlobalGoals){
            btnAddContribution.setVisibility(View.GONE);
            btnDeleteGoal.setVisibility(View.GONE);
        }

        if (goal.getGoalType().equals(AppConstants.GOAL_TYPE_USER)){
            loadPersonalGoalInfo();
        }else {
            loadHuaweiGoalInfo();
        }
    }

    private void loadHuaweiGoalInfo() {
        FragmentGoalDetails goalDetails = new FragmentGoalDetails();
        Bundle bundle = new Bundle();
        bundle.putSerializable("goal", goal);
        goalDetails.setArguments(bundle);

        FragmentGoalContributions goalContributions = new FragmentGoalContributions();
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable("contributions", contributions);
        goalContributions.setArguments(bundle2);

        FragmentGoalMap goalMap = new FragmentGoalMap();
        goalMap.setArguments(bundle);

        tabAdapter.addFragment(goalDetails, "Detalles");
        tabAdapter.addFragment(goalContributions, "Aportes");
        tabAdapter.addFragment(goalMap, "Mapa");

        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void loadPersonalGoalInfo() {
        FragmentGoalDetails goalDetails = new FragmentGoalDetails();
        Bundle bundle = new Bundle();
        bundle.putSerializable("goal", goal);
        goalDetails.setArguments(bundle);

        FragmentGoalContributions goalContributions = new FragmentGoalContributions();
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable("contributions", contributions);
        goalContributions.setArguments(bundle2);

        tabAdapter.addFragment(goalDetails, "Detalles");
        tabAdapter.addFragment(goalContributions, "Aportes");

        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_like_goal:
                try {
                    likeGoal();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_add_contribution:
                addContribution();
                break;
            case R.id.btn_delete_goal:
                try {
                    deleteGoal();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void deleteGoal() throws JSONException {
        JSONObject contributionObject = new JSONObject();
        contributionObject.put("userId", ApplicationMoneySaver.getMainUser().getUserId())
                .put("goalId", goal.getGoalId());

        RequestBody body = RequestBody.create(String.valueOf(contributionObject), AppConstants.JSON);
        Request request = new Request.Builder()
                .url(AppConstants.BASE_URL + AppConstants.DELETE_GOAL)
                .post(body)
                .build();
        ApplicationMoneySaver.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewModel.deleteGoal(goal);
                        dismiss();
                    }
                });
            }
        });
    }

    private void likeGoal() throws JSONException {
        getDialog().setCancelable(false);
        JSONObject likeOject = new JSONObject();
        likeOject.put("userId", ApplicationMoneySaver.getMainUser().getUserId())
                .put("goalId", goal.getGoalId());

        RequestBody body = RequestBody.create(String.valueOf(likeOject), AppConstants.JSON);
        Request request = new Request.Builder()
                .url(AppConstants.BASE_URL + AppConstants.LIKE_GOAL)
                .post(body)
                .build();
        ApplicationMoneySaver.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getDialog().setCancelable(true);
                        MyToast.showShortToast("Ocurrio un error, por favor vuelve a intentarlo", getActivity());
                        ibLikeGoal.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.white), PorterDuff.Mode.MULTIPLY);
                        goal.getGoalLikes().remove(goal.getGoalLikes().size() -1);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getDialog().setCancelable(true);

                        if (goal.getGoalLikes().size() == 0){
                            ibLikeGoal.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_red_light), PorterDuff.Mode.MULTIPLY);
                            viewModel.addLike(goal.getGoalId(), ApplicationMoneySaver.getMainUser().getUserId());
                        }else {
                            for (String strLike : goal.getGoalLikes()){
                                if (strLike.equals(ApplicationMoneySaver.getMainUser().getUserId())){
                                    ibLikeGoal.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.white), PorterDuff.Mode.MULTIPLY);
                                    viewModel.removeLike(goal.getGoalId(), ApplicationMoneySaver.getMainUser().getUserId());
                                }else {
                                    ibLikeGoal.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_red_light), PorterDuff.Mode.MULTIPLY);
                                    viewModel.addLike(goal.getGoalId(), ApplicationMoneySaver.getMainUser().getUserId());
                                }
                            }
                        }
                        viewPager.getAdapter().notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void showContributionsHistory() {
        MyToast.showShortToast("Cargando contribuciones", getActivity());
        getContributions();
    }

    public void getContributions() {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(AppConstants.BASE_URL + AppConstants.GET_GOAL_CONTRIBUTIONS)).newBuilder()
                .addQueryParameter("goalId", goal.getGoalId())
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        ApplicationMoneySaver.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response){
                try {
                    String strResponse = Objects.requireNonNull(response.body()).string();
                    Log.d("RESPONSE", strResponse);
                    JSONObject jsonObject = new JSONObject(strResponse);
                    final JSONArray arrayContributions = jsonObject.getJSONArray("contributions");
                    contributions.clear();
                    for (int i = 0; i < arrayContributions.length(); i++){
                        JSONObject object = arrayContributions.getJSONObject(i);
                        contributions.add(new Contribution(
                                object.getString("contribution_id"),
                                object.getInt("value"),
                                object.getString("date")
                        ));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                viewPager.getAdapter().notifyDataSetChanged();
                            }
                        });
                    }

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }
        });
    }

    private void addContribution() {
        DialogAddContribution dialogAddContribution = new DialogAddContribution();
        Bundle bundle = new Bundle();
        bundle.putInt("goal", goalPos);
        dialogAddContribution.setArguments(bundle);
        dialogAddContribution.setTargetFragment(this, 1);
        dialogAddContribution.show(getParentFragmentManager(), dialogAddContribution.getTag());
    }

    public void updateGoalInfo(){
        viewPager.getAdapter().notifyDataSetChanged();
    }

    private int calculatePercentage(Goal goal) {
        int cost = goal.getGoalCost();
        int actual = goal.getGoalActualMoney();
        return (actual * 100) / cost;
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Objects.requireNonNull(getDialog().getWindow()).setWindowAnimations(R.style.AppTheme_Slide);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
