package com.theoffice.moneysaver.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.data.model.Goal;

import java.io.File;
import java.util.ArrayList;

public class GoalsRVAdapter extends RecyclerView.Adapter<GoalsRVAdapter.GoalsViewHolder> {

    private ArrayList<Goal> goalList;

    static class GoalsViewHolder extends RecyclerView.ViewHolder{

        TextView tvGoalName;
        TextView tvGoalValue;
        TextView tvGoalDate;
        TextView tvGoalLikes;
        TextView tvGoalContribution;
        ImageView ivGoalPhoto;
        ImageButton ibLikeGoal;

        public GoalsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGoalName = itemView.findViewById(R.id.tv_goal_name);
            tvGoalValue = itemView.findViewById(R.id.tv_goal_value);
            tvGoalDate = itemView.findViewById(R.id.tv_goal_date);
            tvGoalLikes = itemView.findViewById(R.id.tv_goal_likes);
            tvGoalContribution = itemView.findViewById(R.id.tv_goal_contribution);
            ivGoalPhoto = itemView.findViewById(R.id.iv_goal_photo);
            ibLikeGoal = itemView.findViewById(R.id.ib_like_goal);
        }
    }

    public GoalsRVAdapter(ArrayList<Goal> goalList) {
        this.goalList = goalList;
    }

    @NonNull
    @Override
    public GoalsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_goal, parent, false);
        return new GoalsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalsViewHolder holder, int position) {
        Goal goal = goalList.get(position);
        holder.tvGoalName.setText(goal.getGoalName());
        holder.tvGoalValue.setText("Meta: " + goal.getGoalValue());
        holder.tvGoalDate.setText(goal.getGoalDate());
        holder.tvGoalContribution.setText("Contribuciones: " + goal.getContributionList().size());
        holder.ivGoalPhoto.setImageURI(Uri.fromFile(new File(goal.getGoalPhotoPath())));
        holder.tvGoalLikes.setText("Likes: " + goal.getGoalLikes());
    }

    @Override
    public int getItemCount() {
        return goalList.size();
    }
}
