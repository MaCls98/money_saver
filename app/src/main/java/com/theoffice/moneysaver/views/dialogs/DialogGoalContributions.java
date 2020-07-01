package com.theoffice.moneysaver.views.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.theoffice.moneysaver.ApplicationMoneySaver;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.utils.AppConstants;
import com.theoffice.moneysaver.viewmodels.ProfileViewModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class DialogGoalContributions extends DialogFragment {

    private ProfileViewModel viewModel;

    private Goal goal;
    private int goalPos;

    private LinearLayout llContributions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        Bundle bundle = getArguments();
        goalPos = bundle.getInt("goal");
        goal = viewModel.getGoalMutableLiveData().getValue().get(goalPos);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_goal_contributions, container, false);
        llContributions = v.findViewById(R.id.ll_contributions);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshContributions();
    }

    private void refreshContributions() {
        HttpUrl url = HttpUrl.parse(AppConstants.BASE_URL + AppConstants.GET_GOAL_CONTRIBUTIONS).newBuilder()
                .addQueryParameter("goalId", goal.getGoalId())
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        ApplicationMoneySaver.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response){
                try {
                    String strResponse = response.body().string();
                    Log.d("RESPONSE", strResponse);
                    JSONObject jsonObject = new JSONObject(strResponse);
                    final JSONArray arrayContributions = jsonObject.getJSONArray("contributions");
                    for (int i = 0; i < arrayContributions.length(); i++){

                        final int finalI = i;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    LinearLayout ll = new LinearLayout(getContext());
                                    ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    ll.setOrientation(LinearLayout.HORIZONTAL);

                                    JSONObject objectContribution = arrayContributions.getJSONObject(finalI);
                                    TextView tvValue = new TextView(getContext());
                                    tvValue.setText("$" + objectContribution.getInt("value"));

                                    TextView tvDate = new TextView(getContext());
                                    tvDate.setText(" "+ objectContribution.getString("date"));

                                    ll.addView(tvValue);
                                    ll.addView(tvDate);

                                    llContributions.addView(ll);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(getDialog().getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setWindowAnimations(R.style.AppTheme_Slide);
    }
}
