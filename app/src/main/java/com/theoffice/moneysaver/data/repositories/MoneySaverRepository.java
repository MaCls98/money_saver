package com.theoffice.moneysaver.data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.theoffice.moneysaver.ApplicationMoneySaver;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.utils.AppConstants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MoneySaverRepository {

    private static MoneySaverRepository repository;

    public static MoneySaverRepository getInstance() {
        if (repository == null) {
            repository = new MoneySaverRepository();
        }
        return repository;
    }

    public boolean validateUser(String huaweiId) throws IOException {
        final MutableLiveData<Boolean> result = new MutableLiveData<>();
        HttpUrl url = HttpUrl.parse(AppConstants.BASE_URL + AppConstants.VALIDATE_USER_URL).newBuilder()
                .addQueryParameter("huaweiUserId", huaweiId)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        ApplicationMoneySaver.getOkHttpClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response){
                result.postValue(response.isSuccessful());
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }
        });
        return Boolean.TRUE.equals(result.getValue());
    }

    public String createUser(String huaweiAccountId)  {
        final MutableLiveData<String> result = new MutableLiveData<>();
        HttpUrl url = HttpUrl.parse(AppConstants.BASE_URL + AppConstants.CREATE_USER_URL).newBuilder()
                .build();
        RequestBody formBody = new FormBody.Builder()
                .add("huaweiUserId", huaweiAccountId)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        ApplicationMoneySaver.getOkHttpClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String strResponse = response.body().string();
                try {
                    JSONObject jsonResponse = new JSONObject(strResponse);
                    result.postValue(jsonResponse.getString("mongoId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
        });
        return result.getValue();
    }

    public MutableLiveData<ArrayList<Goal>> getGoals(String stringUrl, String userId){
        final MutableLiveData<ArrayList<Goal>> goalsData = new MutableLiveData<>();
        goalsData.postValue(new ArrayList<Goal>());

        HttpUrl url = HttpUrl.parse(stringUrl).newBuilder()
                .addQueryParameter("userId", userId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        ApplicationMoneySaver.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String strResponse = response.body().string();
                    JSONObject jsonResponse = new JSONObject(strResponse);
                    JSONArray goals = jsonResponse.getJSONArray("goals");
                    ArrayList<Goal> tmpGoals = new ArrayList<>();
                    for (int i = 0; i < goals.length(); i++){
                        JSONObject jsonGoal = goals.getJSONObject(i);
                        Goal goal = new Goal(
                                jsonGoal.getString("goal_id"),
                                jsonGoal.getString("description"),
                                jsonGoal.getInt("cost"),
                                jsonGoal.getInt("actualMoney"),
                                jsonGoal.getString("start_date"),
                                jsonGoal.getString("image"),
                                jsonGoal.getString("status"),
                                new String[]{"1", "2", "3"},
                                jsonGoal.getInt("contributionsCount")
                        );
                        Log.d("GOAL", goal.toString());
                        tmpGoals.add(goal);
                    }
                    goalsData.postValue(tmpGoals);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return goalsData;
    }
}
