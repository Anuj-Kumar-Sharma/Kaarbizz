package com.anuj55149.kaarbizz.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anuj55149.kaarbizz.R;
import com.anuj55149.kaarbizz.models.searchTypes.SearchTypeCarMake;
import com.anuj55149.kaarbizz.models.searchTypes.SearchTypeDealer;
import com.anuj55149.kaarbizz.models.searchTypes.SearchTypeMakeModel;
import com.anuj55149.kaarbizz.utilities.Constants;
import com.anuj55149.kaarbizz.viewHolders.SearchResultVH;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_MAKE_NAME = 1;
    private static final int TYPE_MAKE_MODEL = 2;
    private static final int TYPE_DEALER = 3;
    private static int TYPE_SHOW = Constants.TYPE_HISTORY;
    private Context context;
    private ArrayList<Object> list;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public SearchRecyclerViewAdapter(Context context, OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.context = context;
        list = new ArrayList<>();
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) != null) {
            switch (list.get(position).getClass().getName()) {
                case Constants.SEARCH_TYPE_CAR_MAKE_NAME:
                    return TYPE_MAKE_NAME;
                case Constants.SEARCH_TYPE_MAKE_MODEL_NAME:
                    return TYPE_MAKE_MODEL;
                case Constants.SEARCH_TYPE_DEALER_NAME:
                    return TYPE_DEALER;
            }
        }
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_search_result, parent, false);
        final SearchResultVH searchResultVH = new SearchResultVH(view);

        searchResultVH.rlEachSearchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (viewType) {
                    case TYPE_MAKE_NAME:
                        onRecyclerViewItemClickListener.onClick(v, searchResultVH.getAdapterPosition(), Constants.EACH_CAR_MAKE_CLICKED);
                        break;
                    case TYPE_MAKE_MODEL:
                        onRecyclerViewItemClickListener.onClick(v, searchResultVH.getAdapterPosition(), Constants.EACH_MAKE_MODEL_CLICKED);
                        break;
                    case TYPE_DEALER:
                        onRecyclerViewItemClickListener.onClick(v, searchResultVH.getAdapterPosition(), Constants.EACH_DEALER_CLICKED);
                        break;
                }
            }
        });
        return searchResultVH;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SearchResultVH searchResultVH = (SearchResultVH) holder;

        if (TYPE_SHOW == Constants.TYPE_HISTORY) {
            searchResultVH.ivEachHistory.setVisibility(View.VISIBLE);
            searchResultVH.vDivider.setVisibility(View.GONE);
        } else {
            searchResultVH.ivEachHistory.setVisibility(View.GONE);
            searchResultVH.vDivider.setVisibility(View.VISIBLE);
        }

        switch (holder.getItemViewType()) {
            case TYPE_MAKE_NAME:
                SearchTypeCarMake searchTypeCarMake = (SearchTypeCarMake) list.get(position);
                searchResultVH.tvEachSearchName.setText(searchTypeCarMake.getMakeName());
                searchResultVH.tvEachSearchAttribute.setText(R.string.brand);
                Glide.with(context).load(searchTypeCarMake.getBrandUrl()).apply(new RequestOptions().centerCrop()).into(searchResultVH.ivEachSearch);
                break;
            case TYPE_MAKE_MODEL:
                SearchTypeMakeModel searchTypeMakeModel = (SearchTypeMakeModel) list.get(position);
                searchResultVH.tvEachSearchName.setText(searchTypeMakeModel.getModelName());
                searchResultVH.tvEachSearchAttribute.setText(String.format("MODEL \u2022 %s", searchTypeMakeModel.getMakeName()));
                Glide.with(context).load(searchTypeMakeModel.getBrandUrl()).apply(new RequestOptions().centerCrop()).into(searchResultVH.ivEachSearch);
                break;
            case TYPE_DEALER:
                SearchTypeDealer searchTypeDealer = (SearchTypeDealer) list.get(position);
                searchResultVH.tvEachSearchName.setText(searchTypeDealer.getName());
                searchResultVH.tvEachSearchAttribute.setText(R.string.dealer);
                searchResultVH.ivEachSearch.setImageDrawable(context.getDrawable(R.drawable.ic_dealer));
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (list != null) return list.size();
        return 0;
    }

    public void updateSearchResultData(ArrayList<Object> list, int type) {
        this.list = list;
        TYPE_SHOW = type;
        notifyDataSetChanged();
    }

    public int getTypeShow() {
        return TYPE_SHOW;
    }
}
