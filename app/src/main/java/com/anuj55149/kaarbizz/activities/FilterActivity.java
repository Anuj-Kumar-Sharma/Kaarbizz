package com.anuj55149.kaarbizz.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.anuj55149.kaarbizz.R;

public class FilterActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivBack, ivToggle;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        context = this;
        initViews();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBackButton);
        ivToggle = findViewById(R.id.ivListMapToggle);
        ivToggle.setVisibility(View.GONE);

        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBackButton:
                finish();
                overridePendingTransition(0, 0);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
        super.onBackPressed();
    }
}
