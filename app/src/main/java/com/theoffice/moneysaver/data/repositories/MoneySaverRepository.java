package com.theoffice.moneysaver.data.repositories;

import androidx.lifecycle.MutableLiveData;

import com.theoffice.moneysaver.ApplicationMoneySaver;
import com.theoffice.moneysaver.data.model.Goal;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MoneySaverRepository {

    private static MoneySaverRepository repository;

    public static MoneySaverRepository getInstance(){
        if (repository == null){
            repository = new MoneySaverRepository();
        }
        return repository;
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
                                jsonGoal.getString("start_date"),
                                jsonGoal.getString("image"),
                                jsonGoal.getString("status"),
                                1,
                                jsonGoal.getInt("cost")
                        );
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
