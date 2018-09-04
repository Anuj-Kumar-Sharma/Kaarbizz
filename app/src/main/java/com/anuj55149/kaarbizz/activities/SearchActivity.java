package com.anuj55149.kaarbizz.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.anuj55149.kaarbizz.R;
import com.anuj55149.kaarbizz.adapters.OnRecyclerViewItemClickListener;
import com.anuj55149.kaarbizz.adapters.SearchRecyclerViewAdapter;
import com.anuj55149.kaarbizz.dao.SearchDao;
import com.anuj55149.kaarbizz.utilities.Constants;
import com.anuj55149.kaarbizz.volley.RequestCallback;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, RequestCallback, OnRecyclerViewItemClickListener, View.OnTouchListener {

    private EditText etSearch;
    private ImageView ivBack, ivCancel;
    private SearchDao searchDao;
    private Context context;
    private RecyclerView recyclerView;
    private SearchRecyclerViewAdapter searchRecyclerViewAdapter;

    private ArrayList<String> searchCarsNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
    }

    private void initViews() {
        context = this;
        etSearch = findViewById(R.id.etSearch);
        ivBack = findViewById(R.id.ivBack);
        ivCancel = findViewById(R.id.ivCancel);
        recyclerView = findViewById(R.id.rvSearchResult);
        searchCarsNameList = new ArrayList<>();
        searchDao = new SearchDao(context, this);


        Intent getIntent = getIntent();
        String etText = getIntent.getStringExtra(Constants.SEARCH_RESULT_BACK_INTENT);
        if(etText != null && !etText.isEmpty()) {
            ivCancel.setVisibility(View.VISIBLE);
            etSearch.setText(etText);
            etSearch.setSelection(etText.length());
            searchDao.getCarNamesWithQuery(etText);
        }


        searchRecyclerViewAdapter = new SearchRecyclerViewAdapter(context, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(searchRecyclerViewAdapter);

        etSearch.addTextChangedListener(this);
        ivCancel.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        recyclerView.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.SEARCH_RESULT_BACK_INTENT, etSearch.getText().toString());
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
        resultIntent.putExtra(Constants.SEARCH_RESULT_BACK_INTENT, etSearch.getText().toString());
        setResult(RESULT_OK, resultIntent);
        finish();
        overridePendingTransition(0, 0);
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
            showHistory();
        } else {
            ivCancel.setVisibility(View.VISIBLE);
            searchDao.getCarNamesWithQuery(s.toString());
        }

    }

    private void showHistory() {
        Toast.makeText(context, "History", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListRequestSuccessful(ArrayList list, int check, boolean status) {
        switch (check) {
            case Constants.GET_CARS_NAME_WITH_QUERY:
                if (status) {
                    searchCarsNameList = list;
                    searchRecyclerViewAdapter.updateSearchResultData(list);
                } else {
                    Toast.makeText(context, "some error occurred", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onObjectRequestSuccessful(Object object, int check, boolean status) {

    }

    @Override
    public void onClick(View view, int position, int check) {
        switch (check) {
            case Constants.SEARCH_RESULT_CLICKED:
                hideKeyboard(view);
                Toast.makeText(context, "clicked " + searchCarsNameList.get(position), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        hideKeyboard(v);
        return false;
    }
}
