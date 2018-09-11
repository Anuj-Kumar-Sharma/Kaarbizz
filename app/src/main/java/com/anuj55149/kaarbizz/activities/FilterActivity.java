package com.anuj55149.kaarbizz.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.anuj55149.kaarbizz.R;
import com.anuj55149.kaarbizz.utilities.Constants;

import java.util.HashMap;
import java.util.Map;

public class FilterActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private ImageView ivBack, ivToggle;
    private SeekBar sbDistance, sbPrice;
    private TextView tvApply, tvReset, tvDistance, tvPrice;
    private RadioGroup rgTransmission, rgColor, rgRating;
    private Context context;
    private StringBuilder finalUrl;

    private Map<Integer, String> mapTransmission, mapColor;
    private Map<Integer, Integer> mapRating;
    private int[] filterArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        context = this;
        initVars();
        initViews();

        Intent intent = getIntent();
        filterArray = intent.getIntArrayExtra(Constants.INTENT_FILTER_ARRAY);
        updateViews();
    }

    private void updateViews() {
        if (filterArray[0] != -1) {
            RadioButton rbTransmission = (RadioButton) rgTransmission.getChildAt(filterArray[0]);
            rbTransmission.setChecked(true);
        }
        if (filterArray[1] != -1) {
            RadioButton rbColor = (RadioButton) rgColor.getChildAt(filterArray[1]);
            rbColor.setChecked(true);
        }
        if (filterArray[2] != -1) {
            sbDistance.setProgress(filterArray[2]);
        } else {
            sbDistance.setProgress(100);
        }
        if (filterArray[3] != -1) {
            sbPrice.setProgress(filterArray[3]);
        } else {
            sbPrice.setProgress(100);
        }
        if (filterArray[4] != -1) {
            RadioButton rbRating = (RadioButton) rgRating.getChildAt(filterArray[4]);
            rbRating.setChecked(true);
        }


    }

    private void initVars() {
        mapTransmission = new HashMap<>();
        mapColor = new HashMap<>();
        mapRating = new HashMap<>();
        finalUrl = new StringBuilder();

        mapTransmission.put(0, getString(R.string.manual));
        mapTransmission.put(1, getString(R.string.automatic));

        mapColor.put(0, getString(R.string.black));
        mapColor.put(1, getString(R.string.grey));
        mapColor.put(2, getString(R.string.metallic_white));
        mapColor.put(3, getString(R.string.silver));
        mapColor.put(4, getString(R.string.yellow));

        mapRating.put(0, 4);
        mapRating.put(1, 3);
        mapRating.put(2, 2);
        mapRating.put(3, 1);
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBackButton);
        ivToggle = findViewById(R.id.ivListMapToggle);
        ivToggle.setVisibility(View.GONE);
        tvApply = findViewById(R.id.tvApply);
        tvReset = findViewById(R.id.tvReset);
        sbDistance = findViewById(R.id.sbDistance);
        sbPrice = findViewById(R.id.sbPrice);
        sbDistance.setOnSeekBarChangeListener(this);
        sbPrice.setOnSeekBarChangeListener(this);
        tvDistance = findViewById(R.id.tvDistance);
        tvPrice = findViewById(R.id.tvPrice);
        rgColor = findViewById(R.id.rgColor);
        rgRating = findViewById(R.id.rgRating);
        rgTransmission = findViewById(R.id.rgTransmission);

        ivBack.setOnClickListener(this);
        tvApply.setOnClickListener(this);
        tvReset.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBackButton:
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.tvApply:
                int idTransmission = rgTransmission.getCheckedRadioButtonId();
                int idColor = rgColor.getCheckedRadioButtonId();
                int idRating = rgRating.getCheckedRadioButtonId();

                if (idTransmission != -1) {
                    int ind = rgTransmission.indexOfChild(rgTransmission.findViewById(idTransmission));
                    filterArray[0] = ind;
                    String transmission = mapTransmission.get(ind);
                    finalUrl.append("&transmission=").append(transmission);
                }
                if (idColor != -1) {
                    int ind = rgColor.indexOfChild(rgColor.findViewById(idColor));
                    filterArray[1] = ind;
                    String color = mapColor.get(ind);
                    finalUrl.append("&color=").append(color);
                }
                if (idRating != -1) {
                    int ind = rgRating.indexOfChild(rgRating.findViewById(idRating));
                    filterArray[4] = ind;
                    int rating = mapRating.get(ind);
                    finalUrl.append("&rating=").append(rating);
                }

                int distanceProgress = sbDistance.getProgress();
                int priceProgress = sbPrice.getProgress();

                if (distanceProgress < 100) {
                    filterArray[2] = distanceProgress;
                    int distance = (distanceProgress + 1) * 5000;
                    finalUrl.append("&distance=").append(distance);
                }

                if (priceProgress < 100) {
                    filterArray[3] = priceProgress;
                    int price = (priceProgress + 1) * 100000;
                    finalUrl.append("&priceHigh=").append(price);
                }

                Intent intent = new Intent();
                intent.putExtra(Constants.INTENT_FILTER_URL, finalUrl.toString());
                intent.putExtra(Constants.INTENT_FILTER_ARRAY, filterArray);
                setResult(Activity.RESULT_OK, intent);
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.tvReset:

                break;
        }
    }

    /*@Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
        super.onBackPressed();
    }*/

    @SuppressLint("DefaultLocale")
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sbDistance:
                if (progress == 100) {
                    tvDistance.setText(R.string.max_distance);
                } else {
                    tvDistance.setText(String.format("%dKm", progress * 5 + 5));
                }
                break;
            case R.id.sbPrice:
                if (progress == 100) {
                    tvPrice.setText(R.string.max_price);
                } else {
                    tvPrice.setText(String.format("₹%dL", progress + 1));
                }
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
