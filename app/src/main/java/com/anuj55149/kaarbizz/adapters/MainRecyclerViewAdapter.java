package com.anuj55149.kaarbizz.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anuj55149.kaarbizz.R;
import com.anuj55149.kaarbizz.models.Dealer;
import com.anuj55149.kaarbizz.utilities.Constants;
import com.anuj55149.kaarbizz.utilities.Utilities;
import com.anuj55149.kaarbizz.viewHolders.DealerRowVH;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Dealer> dealers;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public MainRecyclerViewAdapter(Context context, OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.context = context;
        this.dealers = new ArrayList<>();
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final DealerRowVH dealerRowVH = new DealerRowVH(LayoutInflater.from(context).inflate(R.layout.single_dealer_row, parent, false));
        dealerRowVH.cvDealerRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecyclerViewItemClickListener.onClick(v, dealerRowVH.getAdapterPosition(), Constants.SINGLE_DEALER_CLICKED);
            }
        });
        return dealerRowVH;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DealerRowVH dealerRowVH = (DealerRowVH) holder;
        dealerRowVH.tvDealerName.setText(dealers.get(position).getName());
        DecimalFormat df = new DecimalFormat("#.0");
        dealerRowVH.tvRatings.setText(df.format(dealers.get(position).getRating()));
        dealerRowVH.tvRateCount.setText(String.format("(%d)", dealers.get(position).getRateCount()));

        GradientDrawable bgShape = (GradientDrawable) dealerRowVH.tvRatings.getBackground();
        bgShape.setColor(Utilities.getColorFromRating(dealers.get(position).getRating()));

        String distance = Utilities.format((long) dealers.get(position).getDistanceFromCurrentLocation() / 1000);
        dealerRowVH.tvDistance.setText(String.format("%s km away", distance));
    }

    @Override
    public int getItemCount() {
        return dealers.size();
    }

    public void updateNearestDealersData(ArrayList<Dealer> dealers) {
        this.dealers = dealers;
        notifyDataSetChanged();
    }
}
