package com.theoffice.moneysaver.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.data.model.User;
import com.theoffice.moneysaver.utils.AppConstants;
import com.theoffice.moneysaver.views.activities.PlayGround;

import java.io.File;
import java.util.ArrayList;

public class ProfileRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private User user;
    private ArrayList<Goal> goalList;
    private Context context;

    static class GoalViewHolder extends RecyclerView.ViewHolder{
        TextView tvGoalName;
        TextView tvGoalValue;
        TextView tvGoalDate;
        TextView tvGoalLikes;
        TextView tvGoalContribution;
        ImageView ivGoalPhoto;
        ImageButton ibLikeGoal;

        public GoalViewHolder(@NonNull View itemView) {
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

    static class HeaderViewHolder extends RecyclerView.ViewHolder{
        TextView tvUsername;
        TextView tvUserGoals;
        Button btnScannProduct;

        public HeaderViewHolder(@NonNull View itemView, final Context context) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvUserGoals = itemView.findViewById(R.id.tv_user_goals);
            btnScannProduct = itemView.findViewById(R.id.btn_scann_product);
            btnScannProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PlayGround.class);
                    context.startActivity(intent);
                }
            });
        }
    }

    public ProfileRVAdapter(Context context, User user) {
        this.user = user;
        this.context = context;
        this.goalList = user.getGoalList();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == AppConstants.TYPE_HEADER){
            View headerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_goals_profile, parent, false);
            return new HeaderViewHolder(headerView, context);
        }else if (viewType == AppConstants.TYPE_ITEM){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_goal, parent, false);
            return new GoalViewHolder(itemView);
        }
        else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder){
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.tvUsername.setText(user.getUserName());
            headerViewHolder.tvUserGoals.setText("Total de metas: " + user.getGoalList().size());

        }else if (holder instanceof GoalViewHolder){
            GoalViewHolder goalViewHolder = (GoalViewHolder) holder;
            Goal goal = goalList.get(position);
            goalViewHolder.tvGoalName.setText(goal.getGoalName());
            goalViewHolder.tvGoalValue.setText("Meta: " + goal.getGoalValue());
            goalViewHolder.tvGoalDate.setText(goal.getGoalDate());
            goalViewHolder.tvGoalContribution.setText("Contribuciones: " + goal.getContributionCount());
            goalViewHolder.ivGoalPhoto.setImageURI(Uri.fromFile(new File(goal.getGoalPhotoPath())));
            goalViewHolder.tvGoalLikes.setText("Likes: " + goal.getGoalLikes());
        }
    }

    public int getItemViewType(int position){
        if (position == 0){
            return AppConstants.TYPE_HEADER;
        }
        return AppConstants.TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return goalList.size();
    }
}
