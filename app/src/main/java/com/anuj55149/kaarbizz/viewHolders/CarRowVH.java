package com.anuj55149.kaarbizz.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anuj55149.kaarbizz.R;

public class CarRowVH extends RecyclerView.ViewHolder {

    public ImageView ivCarLogo;
    public TextView tvCarMakeName, tvMakeModelName, tvCarPrice;
    public RelativeLayout rlEachCar;

    public TextView tvDealerName, tvDealerRating, tvDistance;

    public CarRowVH(View itemView) {
        super(itemView);

        ivCarLogo = itemView.findViewById(R.id.ivEachCarLogo);
        tvCarMakeName = itemView.findViewById(R.id.tvCarMakeName);
        tvMakeModelName = itemView.findViewById(R.id.tvCarModelName);
        rlEachCar = itemView.findViewById(R.id.rlCarRow);
        tvCarPrice = itemView.findViewById(R.id.tvCarPrice);

        tvDealerName = itemView.findViewById(R.id.tvSingleDealerName);
        tvDealerRating = itemView.findViewById(R.id.tvSingleRatings);
        tvDistance = itemView.findViewById(R.id.tvDistance);
    }
}
