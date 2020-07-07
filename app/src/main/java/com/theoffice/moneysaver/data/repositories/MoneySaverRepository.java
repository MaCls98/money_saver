package com.theoffice.moneysaver.data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.theoffice.moneysaver.ApplicationMoneySaver;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.data.model.User;
import com.theoffice.moneysaver.utils.AppConstants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
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

    public String createUser(String huaweiAccountId, String huaweiUsername) throws InterruptedException {
        final User result = new User();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        HttpUrl url = HttpUrl.parse(AppConstants.BASE_URL + AppConstants.CREATE_USER_URL).newBuilder()
                .build();
        RequestBody formBody = new FormBody.Builder()
                .add("huaweiUserName", huaweiUsername)
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
                    result.setUserId(jsonResponse.getString("mongoId"));
                    countDownLatch.countDown();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
        return result.getUserId();
    }

    public String getUserId(String huaweiId) throws InterruptedException {
        final User result = new User();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        HttpUrl url = HttpUrl.parse(AppConstants.BASE_URL + AppConstants.VALIDATE_USER_URL).newBuilder()
                .addQueryParameter("huaweiUserId", huaweiId)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        ApplicationMoneySaver.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
               if(response.isSuccessful()){
                   try {
                       JSONObject jsonResponse = new JSONObject(response.body().string());
                       result.setUserId((String)(((JSONObject) jsonResponse.get("user")).get("_id")));
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
               countDownLatch.countDown();
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
        return result.getUserId();
    }

    public MutableLiveData<ArrayList<Goal>> getGoals(final String stringUrl, String userId){
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
                readGoals(response, goalsData);
            }
        });
        return goalsData;
    }

    public MutableLiveData<ArrayList<Goal>> getGlobalGoals(int page){
        final MutableLiveData<ArrayList<Goal>> goalsData = new MutableLiveData<>();
        goalsData.postValue(new ArrayList<Goal>());
        HttpUrl url = HttpUrl.parse(AppConstants.BASE_URL + AppConstants.GET_GLOBAL_GOALS_URL).newBuilder()
                .addQueryParameter("page", String.valueOf(page))
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
                readGoals(response, goalsData);
            }
        });
        return goalsData;
    }

    private void readGoals(@NotNull Response response, MutableLiveData<ArrayList<Goal>> goalsData) throws IOException {
        try {
            String strResponse = response.body().string();
            JSONObject jsonResponse = new JSONObject(strResponse);
            JSONArray goals = jsonResponse.getJSONArray("goals");
            ArrayList<Goal> tmpGoals = new ArrayList<>();
            for (int i = 0; i < goals.length(); i++){
                JSONObject jsonGoal = goals.getJSONObject(i);
                JSONArray likes = jsonGoal.getJSONArray("likes");
                String [] likeList = new String [likes.length()];
                for (int j = 0; j < likes.length(); j++){
                    likeList[j] = likes.getString(j);
                }
                ArrayList<String> tmpLikes = new ArrayList<>();
                for (String like:
                     likeList) {
                    tmpLikes.add(like);
                }
                Goal goal = new Goal(
                        jsonGoal.getString("goal_id"),
                        jsonGoal.getString("description"),
                        jsonGoal.getInt("cost"),
                        jsonGoal.getInt("actualMoney"),
                        jsonGoal.getString("start_date"),
                        jsonGoal.getString("image"),
                        jsonGoal.getString("status"),
                        tmpLikes,
                        jsonGoal.getInt("contributionsCount")
                );
                tmpGoals.add(goal);
            }
            goalsData.postValue(tmpGoals);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendTokenPushKit(String token) {
        HttpUrl url = HttpUrl.parse(AppConstants.BASE_URL + AppConstants.ADD_USER_PUSH_TOKEN_URL).newBuilder()
                .build();
        RequestBody formBody = new FormBody.Builder()
                .add("pushToken", token)
                .add("userId", ApplicationMoneySaver.getMainUser().getUserId())
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        ApplicationMoneySaver.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response){
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
        });
    }
}