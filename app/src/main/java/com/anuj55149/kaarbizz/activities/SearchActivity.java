package com.anuj55149.kaarbizz.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.anuj55149.kaarbizz.R;
import com.anuj55149.kaarbizz.utilities.Constants;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private EditText etSearch;
    private ImageView ivBack, ivCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        ivBack = findViewById(R.id.ivBack);
        ivCancel = findViewById(R.id.ivCancel);

        Intent getIntent = getIntent();
        String etText = getIntent.getStringExtra(Constants.SEARCH_REULT_BACK_INTENT);
        if(etText != null && !etText.isEmpty()) {
            ivCancel.setVisibility(View.VISIBLE);
            etSearch.setText(etText);
            etSearch.setSelection(etText.length());
        }
        etSearch.addTextChangedListener(this);
        ivCancel.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.SEARCH_REULT_BACK_INTENT, etSearch.getText().toString());
                Log.d("tag", "sending "+etSearch.getText().toString());
                setResult(RESULT_OK, resultIntent);
                finish();
                overridePendingTransition(0, 0);
                break;

            case R.id.ivCancel:
                etSearch.setText("");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constants.SEARCH_REULT_BACK_INTENT, etSearch.getText().toString());
        Log.d("tag", "sending "+etSearch.getText().toString());
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.toString().isEmpty()) {
            ivCancel.setVisibility(View.GONE);
        } else {
            ivCancel.setVisibility(View.VISIBLE);
        }
    }
}
