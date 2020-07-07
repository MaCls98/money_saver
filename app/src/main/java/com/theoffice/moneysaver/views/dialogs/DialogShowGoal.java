package com.theoffice.moneysaver.views.dialogs;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.theoffice.moneysaver.ApplicationMoneySaver;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.data.model.Contribution;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.utils.AppConstants;
import com.theoffice.moneysaver.utils.MyFileUtils;
import com.theoffice.moneysaver.utils.MyToast;
import com.theoffice.moneysaver.viewmodels.SharedViewModel;
import com.theoffice.moneysaver.views.fragments.FragmentGlobalGoals;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

    private TextView tvGoalName;
    private TextView tvGoalValue;
    private TextView tvGoalDate;
    private TextView tvGoalLikes;
    private TextView tvGoalContributions;
    private ImageView ivGoalPhoto;
    private Button btnAddContribution;
    private ImageButton ibLikeGoal;
    private ProgressBar pbGoalProgress;

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
                updateView();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_goal, container, false);
        initComponents(v);
        updateView();
        return v;
    }

    private void updateView() {
        tvGoalName.setText(this.goal.getGoalName());
        tvGoalValue.setText(getString(R.string.money_progress, this.goal.getGoalActualMoney(), this.goal.getGoalCost()));
        ZonedDateTime parse = ZonedDateTime.parse(this.goal.getGoalDate());
        tvGoalDate.setText(parse.toLocalDateTime().format(DateTimeFormatter.ofPattern(AppConstants.DATE_FORMAT_TO_SHOW)));
        tvGoalLikes.setText(getString(R.string.likes, goal.getGoalLikes().size()));
        tvGoalContributions.setText(getString(R.string.contributions, this.goal.getContributionCount()));
        for (String s : goal.getGoalLikes()){
            if (s.equals(ApplicationMoneySaver.getMainUser().getUserId())){
                ibLikeGoal.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_red_light), PorterDuff.Mode.MULTIPLY);
            }
        }

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
        pbGoalProgress.setProgress(calculatePercentage(goal));
    }

    private void initComponents(View v) {
        tvGoalName = v.findViewById(R.id.tv_goal_name);
        tvGoalValue = v.findViewById(R.id.tv_goal_actual_money);
        tvGoalDate = v.findViewById(R.id.tv_goal_date);
        tvGoalLikes = v.findViewById(R.id.tv_goal_likes);
        tvGoalLikes.setText(goal.getGoalLikes().size() + "Me gusta");
        tvGoalContributions = v.findViewById(R.id.tv_goal_contribution);
        tvGoalContributions.setPaintFlags(tvGoalContributions.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvGoalContributions.setOnClickListener(this);
        ivGoalPhoto = v.findViewById(R.id.iv_goal_photo);
        btnAddContribution = v.findViewById(R.id.btn_add_contribution);
        btnAddContribution.setOnClickListener(this);
        ibLikeGoal = v.findViewById(R.id.ib_like_goal);
        ibLikeGoal.setOnClickListener(this);
        pbGoalProgress = v.findViewById(R.id.pb_goal_progress);
        pbGoalProgress.setProgress(calculatePercentage(goal));
        ImageButton ibLikeGoal = v.findViewById(R.id.ib_like_goal);
        ibLikeGoal.setOnClickListener(this);

        if (getTargetFragment() instanceof FragmentGlobalGoals){
            btnAddContribution.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_goal_contribution:
                showContributionsHistory();
                break;
            case R.id.btn_add_contribution:
                addContribution();
                break;
            case R.id.ib_like_goal:

                try {
                    likeGoal();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
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
                        ibLikeGoal.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.darker_gray), PorterDuff.Mode.MULTIPLY);
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
                                    ibLikeGoal.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.darker_gray), PorterDuff.Mode.MULTIPLY);
                                    viewModel.removeLike(goal.getGoalId(), ApplicationMoneySaver.getMainUser().getUserId());
                                }else {
                                    ibLikeGoal.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_red_light), PorterDuff.Mode.MULTIPLY);
                                    viewModel.addLike(goal.getGoalId(), ApplicationMoneySaver.getMainUser().getUserId());
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    private void showContributionsHistory() {
        MyToast.showShortToast("Cargando contribuciones", getActivity());
        getContributions();
    }

    private void getContributions() {
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
                    final ArrayList<Contribution> contributions = new ArrayList<>();
                    String strResponse = Objects.requireNonNull(response.body()).string();
                    Log.d("RESPONSE", strResponse);
                    JSONObject jsonObject = new JSONObject(strResponse);
                    final JSONArray arrayContributions = jsonObject.getJSONArray("contributions");
                    for (int i = 0; i < arrayContributions.length(); i++){
                        JSONObject object = arrayContributions.getJSONObject(i);
                        contributions.add(new Contribution(
                                object.getString("contribution_id"),
                                object.getInt("value"),
                                object.getString("date")
                        ));
                    }

                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogGoalContributions dialogGoalContributions = new DialogGoalContributions();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("contributions", contributions);
                            dialogGoalContributions.setArguments(bundle);
                            dialogGoalContributions.show(getParentFragmentManager(), dialogGoalContributions.getTag());
                        }
                    });

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
        dialogAddContribution.show(getParentFragmentManager(), dialogAddContribution.getTag());
    }

    private int calculatePercentage(Goal goal) {
        int cost = goal.getGoalCost();
        int actual = goal.getGoalActualMoney();
        return (actual * 100) / cost;
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Objects.requireNonNull(getDialog().getWindow()).setWindowAnimations(R.style.AppTheme_Slide);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
