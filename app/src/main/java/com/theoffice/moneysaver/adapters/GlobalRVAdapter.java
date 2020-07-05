package com.theoffice.moneysaver.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.nativead.MediaView;
import com.huawei.hms.ads.nativead.NativeAd;
import com.huawei.hms.ads.nativead.NativeAdConfiguration;
import com.huawei.hms.ads.nativead.NativeAdLoader;
import com.huawei.hms.ads.nativead.NativeView;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.utils.AppConstants;
import com.theoffice.moneysaver.utils.MyFileUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GlobalRVAdapter extends RecyclerView.Adapter<GlobalRVAdapter.GoalViewHolder> {

    private ArrayList<Goal> goalList;
    private Context context;

    public GlobalRVAdapter(Context context, ArrayList<Goal> goalList) {
        this.context = context;
        this.goalList = goalList;
    }

    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onImageClick(String goalId);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    static class GoalViewHolder extends RecyclerView.ViewHolder{
        ImageView ivGoalPhoto;
        String goalId;

        public GoalViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            ivGoalPhoto = itemView.findViewById(R.id.iv_goal_photo);
            ivGoalPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getLayoutPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onImageClick(goalId);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_global_mini_goal, parent, false);
        return new GoalViewHolder(itemView, listener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final GoalViewHolder holder, int position) {
        Goal goal = goalList.get(position);
        holder.goalId = goal.getGoalId();

        if((25 - calculatePercentage(goal) / 4) > 0){
            MyFileUtils.blurImage(context, holder.ivGoalPhoto, goal);
        }else{
            Glide.with(context)
                    .load(goal.getGoalPhotoPath())
                    .placeholder(R.drawable.money_icon)
                    .error(R.drawable.error_icon)
                    .into(holder.ivGoalPhoto);
        }
    }

    private int calculatePercentage(Goal goal) {
        int cost = goal.getGoalCost();
        int actual = goal.getGoalActualMoney();
        return (actual * 100) / cost;
    }

    @Override
    public int getItemCount() {
        return goalList.size();
    }
}
