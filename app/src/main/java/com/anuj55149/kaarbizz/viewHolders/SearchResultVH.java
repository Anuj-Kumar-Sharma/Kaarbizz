package com.anuj55149.kaarbizz.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anuj55149.kaarbizz.R;

public class SearchResultVH extends RecyclerView.ViewHolder {

    public TextView tvEachSearchName, tvEachSearchAttribute;
    public RelativeLayout rlEachSearchLayout;
    public ImageView ivEachSearch, ivEachHistory;
    public View vDivider;

    public SearchResultVH(View itemView) {
        super(itemView);

        tvEachSearchName = itemView.findViewById(R.id.tvEachSearchName);
        rlEachSearchLayout = itemView.findViewById(R.id.rlEachSearchResult);
        tvEachSearchAttribute = itemView.findViewById(R.id.tvEachSearchAttribute);
        ivEachSearch = itemView.findViewById(R.id.ivEachSearch);
        ivEachHistory = itemView.findViewById(R.id.ivHistory);
        vDivider = itemView.findViewById(R.id.vDivider);
    }
}
