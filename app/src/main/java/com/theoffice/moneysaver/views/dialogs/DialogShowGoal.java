package com.theoffice.moneysaver.views.dialogs;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.theoffice.moneysaver.ApplicationMoneySaver;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.data.model.Contribution;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.utils.AppConstants;
import com.theoffice.moneysaver.utils.MyToast;
import com.theoffice.moneysaver.viewmodels.ProfileViewModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class DialogShowGoal extends DialogFragment implements View.OnClickListener {

    private ProfileViewModel viewModel;

    private Goal goal;
    private int goalPos;

    private TextView tvGoalName;
    private TextView tvGoalValue;
    private TextView tvGoalDate;
    private TextView tvGoalLikes;
    private TextView tvGoalContributions;
    private ImageView ivGoalPhoto;
    private Button btnAddContribution;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        Bundle bundle = getArguments();
        assert bundle != null;
        goalPos = bundle.getInt("goal");
        goal = Objects.requireNonNull(viewModel.getGoalMutableLiveData().getValue()).get(goalPos);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getGoalMutableLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Goal>>() {
            @Override
            public void onChanged(ArrayList<Goal> goals) {
                Log.d("GOAL", "Detectado en dialogo");
                goal = goals.get(goalPos);
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
        tvGoalLikes.setText(getString(R.string.likes, goal.getGoalLikes().length));
        tvGoalContributions.setText(getString(R.string.contributions, this.goal.getContributionCount()));

        if((25 - calculatePercentage(goal) / 4) > 0){
            Glide.with(requireActivity())
                    .load(goal.getGoalPhotoPath()).apply(RequestOptions.bitmapTransform(new BlurTransformation(25 - (calculatePercentage(goal) / 4), 1)))
                    .placeholder(R.drawable.money_icon)
                    .error(R.drawable.error_icon)
                    .into(ivGoalPhoto);
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

    }

    private void initComponents(View v) {
        tvGoalName = v.findViewById(R.id.tv_goal_name);
        tvGoalValue = v.findViewById(R.id.tv_goal_actual_money);
        tvGoalDate = v.findViewById(R.id.tv_goal_date);
        tvGoalLikes = v.findViewById(R.id.tv_goal_likes);
        tvGoalContributions = v.findViewById(R.id.tv_goal_contribution);
        tvGoalContributions.setPaintFlags(tvGoalContributions.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvGoalContributions.setOnClickListener(this);
        ivGoalPhoto = v.findViewById(R.id.iv_goal_photo);
        btnAddContribution = v.findViewById(R.id.btn_add_contribution);
        btnAddContribution.setOnClickListener(this);
        ImageButton ibLikeGoal = v.findViewById(R.id.ib_like_goal);
        ibLikeGoal.setOnClickListener(this);
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
                likeGoal();
                break;
        }
    }

    private void likeGoal() {

    }

    private void showContributionsHistory() {
        MyToast.showShortToast("Cargando contribuciones", getActivity());
        getContributions();
    }

    private void getContributions() {
        Log.d("GOAL", goal.getGoalId());
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
