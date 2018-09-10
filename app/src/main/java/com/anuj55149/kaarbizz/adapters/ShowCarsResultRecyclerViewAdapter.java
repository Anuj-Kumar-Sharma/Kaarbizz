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
import com.anuj55149.kaarbizz.models.Car;
import com.anuj55149.kaarbizz.models.Dealer;
import com.anuj55149.kaarbizz.utilities.Constants;
import com.anuj55149.kaarbizz.utilities.Utilities;
import com.anuj55149.kaarbizz.viewHolders.CarRowVH;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ShowCarsResultRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Car> carsList;
    private Context context;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    private String brandUrl;

    public ShowCarsResultRecyclerViewAdapter(Context context, String brandUrl, OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.context = context;
        this.brandUrl = brandUrl;
        carsList = new ArrayList<>();
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_car_row, parent, false);
        final CarRowVH carRowVH = new CarRowVH(view);
        carRowVH.rlEachCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecyclerViewItemClickListener.onClick(v, carRowVH.getAdapterPosition(), Constants.EACH_CAR_LAYOUT_CLICK);
            }
        });
        return carRowVH;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CarRowVH carRowVH = (CarRowVH) holder;
        carRowVH.tvCarMakeName.setText(String.format("%s • %s • %s", carsList.get(position).getMake(), carsList.get(position).getColor(), carsList.get(position).getTrim()));
        carRowVH.tvMakeModelName.setText(carsList.get(position).getModel());
        carRowVH.tvCarPrice.setText(String.format("₹%s", Utilities.getIndianCurrencyFormat(carsList.get(position).getPrice() + "")));
        Glide.with(context).load(brandUrl).apply(new RequestOptions().centerCrop()).into(carRowVH.ivCarLogo);

        Dealer dealer = carsList.get(position).getDealer();
        carRowVH.tvDealerName.setText(dealer.getName());
        DecimalFormat df = new DecimalFormat("#.0");
        carRowVH.tvDealerRating.setText(df.format(dealer.getRating()));

        GradientDrawable bgShape = (GradientDrawable) carRowVH.tvDealerRating.getBackground();
        bgShape.setColor(Utilities.getColorFromRating(dealer.getRating()));

        String distance = Utilities.format((long) dealer.getDistanceFromCurrentLocation() / 1000);
        carRowVH.tvDistance.setText(String.format("%s km away", distance));
    }

    @Override
    public int getItemCount() {
        if (carsList != null) return carsList.size();
        return 0;
    }

    public void updateCarsList(ArrayList<Car> carList) {
        this.carsList = carList;
        notifyDataSetChanged();
    }
}
