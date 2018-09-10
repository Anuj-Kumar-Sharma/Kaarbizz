package com.anuj55149.kaarbizz.viewHolders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.anuj55149.kaarbizz.R;

public class DealerRowVH extends RecyclerView.ViewHolder {

    public CardView cvDealerRow;
    public TextView tvDealerName, tvRatings, tvRateCount, tvDistance;

    public DealerRowVH(View itemView) {
        super(itemView);

        cvDealerRow = itemView.findViewById(R.id.cvDealerRow);
        tvDealerName = itemView.findViewById(R.id.tvSingleDealerName);
        tvRatings = itemView.findViewById(R.id.tvSingleRatings);
        tvRateCount = itemView.findViewById(R.id.ratingCount);
        tvDistance = itemView.findViewById(R.id.tvDistance);
    }
}
