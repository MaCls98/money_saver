package com.theoffice.moneysaver.views.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.theoffice.moneysaver.ApplicationMoneySaver;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.data.model.Contribution;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.data.model.User;
import com.theoffice.moneysaver.utils.AppConstants;
import com.theoffice.moneysaver.utils.MyDatePicker;
import com.theoffice.moneysaver.viewmodels.ProfileViewModel;
import com.theoffice.moneysaver.views.activities.MainActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DialogAddContribution extends DialogFragment {

    private Goal goal;

    private TextInputLayout tilMoney;
    private Button btnAddContribution;

    private ProfileViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        Bundle bundle = getArguments();
        int p = bundle.getInt("goal");
        goal = viewModel.getGoalMutableLiveData().getValue().get(p);

        btnAddContribution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tilMoney.getEditText().getText().length() > 0){
                    tilMoney.setErrorEnabled(false);
                    int value = Integer.parseInt(tilMoney.getEditText().getText().toString());
                    if ( (goal.getGoalActualMoney() + value) > goal.getGoalCost() ){
                        tilMoney.setErrorEnabled(true);
                        tilMoney.setError("El aporte no puede ser mayor que el dinero faltante");
                    }else {
                        try {
                            uploadNewContribution();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    tilMoney.setErrorEnabled(true);
                    tilMoney.setError("El aporte no puede ser 0");
                }
            }
        });
    }

    private void uploadNewContribution() throws JSONException {
        JSONObject contributionObject = new JSONObject();
        contributionObject.put("goalId", goal.getGoalId())
                .put("contribution", new JSONObject()
                    .put("date", MyDatePicker.convertDate(Calendar.getInstance().getTimeInMillis()))
                    .put("value", Integer.parseInt(tilMoney.getEditText().getText().toString())));

        RequestBody body = RequestBody.create(String.valueOf(contributionObject), AppConstants.JSON);
        Request request = new Request.Builder()
                .url(AppConstants.BASE_URL + AppConstants.ADD_CONTRIBUTION)
                .post(body)
                .build();
        ApplicationMoneySaver.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    ArrayList<Contribution> contributions = new ArrayList<>();
                    String strResponse = response.body().string();
                    Log.d("GOAL", goal.toString());
                    JSONObject objectContribution = new JSONObject(strResponse);
                    JSONArray arrayContribution = objectContribution.getJSONArray("contributions");
                    int c = 0;
                    for (int i = 0; i < arrayContribution.length(); i++){
                        JSONObject tmpContribution = arrayContribution.getJSONObject(i);
                        c += tmpContribution.getInt("value");
                        contributions.add(new Contribution(
                            tmpContribution.getString("contribution_id"),
                                tmpContribution.getInt("value"),
                            tmpContribution.getString("date")
                        ));
                    }
                    goal.increaseContribution();
                    goal.setGoalActualMoney(c);
                    viewModel.updateGoal(goal);
                    dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_contribution, container, false);
        tilMoney = view.findViewById(R.id.til_money);
        btnAddContribution = view.findViewById(R.id.btn_add_contribution);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(getDialog().getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}