package com.theoffice.moneysaver.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.data.model.Contribution;

import java.util.ArrayList;

public class ContributionRVAdapter extends RecyclerView.Adapter<ContributionRVAdapter.ContributionViewHolder> {

    private ArrayList<Contribution> contributions;

    public ContributionRVAdapter(ArrayList<Contribution> contributions){
        this.contributions = contributions;
    }

    static class ContributionViewHolder extends RecyclerView.ViewHolder{

        TextView tvContrValue;
        TextView tvContrDate;

        public ContributionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContrValue = itemView.findViewById(R.id.tv_contr_value);
            tvContrDate = itemView.findViewById(R.id.tv_contr_date);
        }
    }

    @NonNull
    @Override
    public ContributionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_contribution, parent, false);
        return new ContributionViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ContributionViewHolder holder, int position) {
        Contribution contribution = contributions.get(position);
        holder.tvContrValue.setText("$" + contribution.getContributionValue());
        holder.tvContrDate.setText(contribution.getContributionDate());
    }

    @Override
    public int getItemCount() {
        return contributions.size();
    }

}
