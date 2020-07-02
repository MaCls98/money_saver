package com.theoffice.moneysaver.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.bumptech.glide.request.RequestOptions;
import com.huawei.hms.ads.nativead.NativeAd;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.data.model.User;
import com.theoffice.moneysaver.utils.AppConstants;
import com.theoffice.moneysaver.views.activities.PlayGround;

import java.util.ArrayList;
import java.util.Random;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class ProfileRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private User user;
    private ArrayList<Goal> goalList;
    private Context context;
    private int rvLayout;

    private Random random;

    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onLikeClick(int position);
        void onImageClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void setRvLayout(int rvLayout) {
        this.rvLayout = rvLayout;
    }

    static class AdsViewHolder extends RecyclerView.ViewHolder{

        private int layoutId;
        private NativeAd globalNativeAd;

        public AdsViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class GoalViewHolder extends RecyclerView.ViewHolder{
        TextView tvGoalName;
        TextView tvGoalActualMoney;
        //TextView tvGoalDate;
        TextView tvGoalLikes;
        //TextView tvGoalContribution;
        ImageView ivGoalPhoto;
        ImageButton ibDeleteGoal;
        ConstraintLayout clMiniGoal;

        public GoalViewHolder(@NonNull View itemView, final OnItemClickListener listener,
                              final Context context) {
            super(itemView);
            tvGoalName = itemView.findViewById(R.id.tv_goal_name);
            tvGoalActualMoney = itemView.findViewById(R.id.tv_goal_actual_money);
            //tvGoalDate = itemView.findViewById(R.id.tv_goal_date);
            tvGoalLikes = itemView.findViewById(R.id.tv_goal_likes);
            //tvGoalContribution = itemView.findViewById(R.id.tv_goal_contribution);
            ibDeleteGoal = itemView.findViewById(R.id.ib_delete_goal);
            ivGoalPhoto = itemView.findViewById(R.id.iv_goal_photo);
            clMiniGoal = itemView.findViewById(R.id.cl_mini_goal);
            ivGoalPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getLayoutPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onImageClick(position);
                        }
                    }
                }
            });
            ibDeleteGoal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getLayoutPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
            ivGoalPhoto.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (ibDeleteGoal.getVisibility() == View.GONE){
                        //ibDeleteGoal.setVisibility(View.VISIBLE);
                    }else {
                        //ibDeleteGoal.setVisibility(View.GONE);
                    }
                    return false;
                }
            });
        }
    }

    /*
    static class HeaderViewHolder extends RecyclerView.ViewHolder{
        ImageView ivUserPhoto;
        TextView tvUsername;
        TextView tvUserGoals;
        Button btnScannProduct;

        public HeaderViewHolder(@NonNull View itemView, final Context context) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvUserGoals = itemView.findViewById(R.id.tv_user_goals);
            ivUserPhoto = itemView.findViewById(R.id.iv_user_photo);
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
     */



    public ProfileRVAdapter(Context context, ArrayList<Goal> goalList) {
        //this.user = user;
        this.context = context;
        this.goalList = goalList;
        this.rvLayout = AppConstants.RV_LIST_VIEW;
        random = new Random();
    }

    public int getRvLayout() {
        return rvLayout;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*
        if (viewType == AppConstants.TYPE_HEADER){
            View headerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_profile, parent, false);
            return new HeaderViewHolder(headerView, context);
        }else


        if (viewType == AppConstants.TYPE_ITEM){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_mini_goal, parent, false);
            return new GoalViewHolder(itemView, listener, context);
        }
        throw new RuntimeException("There is no type that matches");
        */
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_mini_goal, parent, false);
        return new GoalViewHolder(itemView, listener, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        /*
        if (holder instanceof HeaderViewHolder){
            final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.tvUsername.setText(user.getUserName());
            headerViewHolder.tvUserGoals.setText(context.getString(R.string.goals, user.getGoalList().size()));

            Glide.with(context)
                    .load(user.getUserPhotoUrl())
                    .placeholder(R.drawable.user_icon)
                    .into(headerViewHolder.ivUserPhoto);

        }else
            */
        if (holder instanceof GoalViewHolder){
            GoalViewHolder goalViewHolder = (GoalViewHolder) holder;
            Goal goal = goalList.get(position);
            goalViewHolder.tvGoalName.setText(goal.getGoalName());
            //goalViewHolder.tvGoalActualMoney.setText("$" + goal.getGoalActualMoney());
            goalViewHolder.tvGoalActualMoney.setText(calculatePercentage(goal)  + "%");
            goalViewHolder.tvGoalLikes.setText(context.getString(R.string.likes, goal.getGoalLikes().length));
            //TODO Trabajando en transformar la imagen
            if((25 - calculatePercentage(goal) / 4) > 0){
                Glide.with(context)
                        .load(goal.getGoalPhotoPath()).apply(RequestOptions.bitmapTransform(new BlurTransformation(25 - (calculatePercentage(goal) / 4), 1)))
                        .placeholder(R.drawable.money_icon)
                        .error(R.drawable.error_icon)
                        .into(goalViewHolder.ivGoalPhoto);
            }else{
                Glide.with(context)
                        .load(goal.getGoalPhotoPath())
                        .placeholder(R.drawable.money_icon)
                        .error(R.drawable.error_icon)
                        .into(goalViewHolder.ivGoalPhoto);
            }

            ConstraintSet set = new ConstraintSet();
            set.clone(goalViewHolder.clMiniGoal);

            switch (rvLayout){
                case AppConstants.RV_LIST_VIEW:
                    set.setDimensionRatio(goalViewHolder.ivGoalPhoto.getId(), "12:5");
                    break;
                case AppConstants.RV_GRID_VIEW:
                    set.setDimensionRatio(goalViewHolder.ivGoalPhoto.getId(), "1:1");
                    break;
            }
            set.applyTo(goalViewHolder.clMiniGoal);
        }
    }

    private int calculatePercentage(Goal goal) {
        int cost = goal.getGoalCost();
        int actual = goal.getGoalActualMoney();
        int percentage = (actual * 100) / cost;
        return percentage;
    }

    /*
    @Override
    public int getItemViewType(int position){
        if (position == 0){
            return AppConstants.TYPE_HEADER;
        }else if(position > 0){
            int r = random.nextInt(2);
        }
        return AppConstants.TYPE_ITEM;
    }

     */

    @Override
    public int getItemCount() {
        return goalList.size();
    }
}
