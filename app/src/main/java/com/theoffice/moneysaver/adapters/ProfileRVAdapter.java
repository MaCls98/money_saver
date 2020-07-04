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

public class ProfileRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int LIST_AD_DELTA = 4;
    private static final int CARD = 0;
    private static final int AD = 1;

    private ArrayList<Goal> goalList;
    private Context context;
    private int rvLayout;

    public ProfileRVAdapter(Context context, ArrayList<Goal> goalList) {
        this.context = context;
        this.goalList = goalList;
        this.rvLayout = AppConstants.RV_LIST_VIEW;
    }

    private OnItemClickListener listener;

    public interface OnItemClickListener{
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

        private NativeAd globalNativeAd;
        private String adId;
        private View view;

        public AdsViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            view = itemView;
            adId = getAdId(context);
        }

        private String getAdId(Context context) {
            return context.getString(R.string.ad_id_native_small);
        }
    }

    static class GoalViewHolder extends RecyclerView.ViewHolder{
        TextView tvGoalName;
        TextView tvGoalActualMoney;
        TextView tvGoalLikes;
        ImageView ivGoalPhoto;
        ImageButton ibDeleteGoal;
        ConstraintLayout clMiniGoal;

        public GoalViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tvGoalName = itemView.findViewById(R.id.tv_goal_name);
            tvGoalActualMoney = itemView.findViewById(R.id.tv_goal_actual_money);
            tvGoalLikes = itemView.findViewById(R.id.tv_goal_likes);
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
        }
    }

    public int getRvLayout() {
        return rvLayout;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == CARD){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_mini_goal, parent, false);
            return new GoalViewHolder(itemView, listener);
        }else {
            View adsView = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_small_template, parent, false);
            return new AdsViewHolder(adsView, context);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GoalViewHolder){
            GoalViewHolder goalViewHolder = (GoalViewHolder) holder;
            Goal goal = goalList.get(getRealPosition(position));
            goalViewHolder.tvGoalName.setText(goal.getGoalName());
            //goalViewHolder.tvGoalActualMoney.setText("$" + goal.getGoalActualMoney());
            goalViewHolder.tvGoalActualMoney.setText(calculatePercentage(goal)  + "%");
            goalViewHolder.tvGoalLikes.setText(context.getString(R.string.likes, goal.getGoalLikes().length));

            if((25 - calculatePercentage(goal) / 4) > 0){
                MyFileUtils.blurImage(context, ((GoalViewHolder) holder).ivGoalPhoto, goal);
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
                    goalViewHolder.tvGoalName.setTextSize(14f);
                    goalViewHolder.tvGoalActualMoney.setTextSize(14f);
                    break;
            }
            set.applyTo(goalViewHolder.clMiniGoal);
        }else if (holder instanceof AdsViewHolder){

            NativeAdLoader.Builder builder = new NativeAdLoader.Builder(context, ((AdsViewHolder) holder).adId);

            builder.setNativeAdLoadedListener(new NativeAd.NativeAdLoadedListener() {
                @Override
                public void onNativeAdLoaded(NativeAd nativeAd) {
                    ((AdsViewHolder) holder).globalNativeAd = nativeAd;

                    // Obtain NativeView.
                    NativeView nativeView = (NativeView) ((AdsViewHolder) holder).view;

                    // Register and populate a native ad material view.
                    nativeView.setTitleView(nativeView.findViewById(R.id.ad_title));
                    nativeView.setMediaView((MediaView) nativeView.findViewById(R.id.ad_media));
                    nativeView.setAdSourceView(nativeView.findViewById(R.id.ad_source));
                    nativeView.setCallToActionView(nativeView.findViewById(R.id.ad_call_to_action));

                    // Populate a native ad material view.
                    ((TextView) nativeView.getTitleView()).setText(nativeAd.getTitle());
                    nativeView.getMediaView().setMediaContent(nativeAd.getMediaContent());

                    if (null != nativeAd.getAdSource()) {
                        ((TextView) nativeView.getAdSourceView()).setText(((AdsViewHolder) holder).globalNativeAd.getAdSource());
                    }
                    nativeView.getAdSourceView()
                            .setVisibility(null != ((AdsViewHolder) holder).globalNativeAd.getAdSource() ? View.VISIBLE : View.INVISIBLE);

                    if (null != ((AdsViewHolder) holder).globalNativeAd.getCallToAction()) {
                        ((Button) nativeView.getCallToActionView()).setText(((AdsViewHolder) holder).globalNativeAd.getCallToAction());
                    }
                    nativeView.getCallToActionView()
                            .setVisibility(null != ((AdsViewHolder) holder).globalNativeAd.getCallToAction() ? View.VISIBLE : View.INVISIBLE);

                    // Register a native ad object.
                    nativeView.setNativeAd(((AdsViewHolder) holder).globalNativeAd);
                }
            }).setAdListener(new AdListener(){
                @Override
                public void onAdFailed(int i) {
                    super.onAdFailed(i);
                }
            });

            NativeAdConfiguration adConfiguration = new NativeAdConfiguration.Builder()
                    .setChoicesPosition(NativeAdConfiguration.ChoicesPosition.BOTTOM_RIGHT)
                    .build();
            NativeAdLoader nativeAdLoader = builder.setNativeAdOptions(adConfiguration).build();
            nativeAdLoader.loadAd(new AdParam.Builder().build());
        }
    }

    private int calculatePercentage(Goal goal) {
        int cost = goal.getGoalCost();
        int actual = goal.getGoalActualMoney();
        return (actual * 100) / cost;
    }


    @Override
    public int getItemViewType(int position){
        if (position > 0 && position % LIST_AD_DELTA == 0){
            return AD;
        }
        return CARD;
    }

    public int getRealPosition(int position) {
        if (LIST_AD_DELTA == 0) {
            return position;
        } else {
            return position - position / LIST_AD_DELTA;
        }
    }

    @Override
    public int getItemCount() {
        int additionalContent = 0;
        if (goalList.size() > 0 && LIST_AD_DELTA > 0 && goalList.size() > LIST_AD_DELTA) {
            additionalContent = goalList.size() / LIST_AD_DELTA;
        }
        return goalList.size() + additionalContent;
    }
}
