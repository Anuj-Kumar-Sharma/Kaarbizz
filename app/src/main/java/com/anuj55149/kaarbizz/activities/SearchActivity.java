package com.anuj55149.kaarbizz.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anuj55149.kaarbizz.R;
import com.anuj55149.kaarbizz.adapters.OnRecyclerViewItemClickListener;
import com.anuj55149.kaarbizz.adapters.SearchRecyclerViewAdapter;
import com.anuj55149.kaarbizz.dao.SearchDao;
import com.anuj55149.kaarbizz.dao.ServerStateDao;
import com.anuj55149.kaarbizz.models.searchTypes.SearchTypeCarMake;
import com.anuj55149.kaarbizz.models.searchTypes.SearchTypeDealer;
import com.anuj55149.kaarbizz.models.searchTypes.SearchTypeMakeModel;
import com.anuj55149.kaarbizz.utilities.Constants;
import com.anuj55149.kaarbizz.utilities.DialogBoxes;
import com.anuj55149.kaarbizz.utilities.SharedPreference;
import com.anuj55149.kaarbizz.volley.RequestCallback;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, RequestCallback, OnRecyclerViewItemClickListener, View.OnTouchListener {

    private EditText etSearch;
    private ImageView ivBack, ivCancel;
    private SearchDao searchDao;
    private ServerStateDao serverStateDao;
    private Context context;
    private RecyclerView recyclerView;
    private SearchRecyclerViewAdapter searchRecyclerViewAdapter;
    private ProgressBar pbSearch;
    private RelativeLayout rlServerError;
    private TextView tvServerContinue;
    private SharedPreference pref;

    private ArrayList<Object> searchList;
    private ProgressDialog progressDialog;

    private boolean isDialogShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
        context = this;
        pref = new SharedPreference(context);
        etSearch = findViewById(R.id.etSearch);
        ivBack = findViewById(R.id.ivBack);
        ivCancel = findViewById(R.id.ivCancel);
        recyclerView = findViewById(R.id.rvSearcSuggestions);
        searchList = new ArrayList<>();
        pbSearch = findViewById(R.id.pbSearch);
        rlServerError = findViewById(R.id.rlServerError);
        tvServerContinue = rlServerError.findViewById(R.id.tvIPContinue);

        searchDao = new SearchDao(context, this);
        serverStateDao = new ServerStateDao(context, this);

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
        tvServerContinue.setOnClickListener(this);

        showHistory();
    }

    public void showServerError() {
        if (rlServerError.getVisibility() == View.GONE) {
            recyclerView.setVisibility(View.GONE);
            rlServerError.setVisibility(View.VISIBLE);
        }
    }

    public void hideServerError() {
        if (rlServerError.getVisibility() == View.VISIBLE) {
            recyclerView.setVisibility(View.VISIBLE);
            rlServerError.setVisibility(View.GONE);
        }
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

            case R.id.tvIPContinue:
                View view = getLayoutInflater().inflate(R.layout.dialog_change_ip_address, null);
                DialogBoxes.showChangeIPDialog(view, context, serverStateDao);
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
            if (s.length() > 1) {
                searchDao.getCarNamesWithQuery(s.toString());
            }
        }
    }

    private void showHistory() {
        searchList = pref.getHistoryList();
        searchRecyclerViewAdapter.updateSearchResultData(searchList, Constants.TYPE_HISTORY);
    }

    @Override
    public void onListRequestSuccessful(ArrayList list, int check, boolean status) {
        switch (check) {
            case Constants.GET_SEARCH_RESULT_WITH_QUERY:
                if (status) {
                    hideServerError();
                    searchList = list;
                    searchRecyclerViewAdapter.updateSearchResultData(list, Constants.TYPE_DATA);
                } else {
                    showServerError();
                }
        }
    }

    @Override
    public void onObjectRequestSuccessful(Object object, int check, boolean status) {
        switch (check) {
            case Constants.DAO_SERVER_STATE:
                DialogBoxes.dismissProgressDialog();
                if (status) {
                    Toast.makeText(context, "Connection is successful", Toast.LENGTH_SHORT).show();
                    hideServerError();
                } else {
                    Toast.makeText(context, "Server is not connected", Toast.LENGTH_SHORT).show();
                    showServerError();
                }
                break;
        }
    }

    @Override
    public void onClick(View view, int position, int check) {
        hideKeyboard(view);
        Intent intent = new Intent(SearchActivity.this, SearchResultsActivity.class);
        switch (check) {
            case Constants.EACH_CAR_MAKE_CLICKED:
                SearchTypeCarMake searchTypeCarMake = (SearchTypeCarMake) searchList.get(position);
                pref.updateHistoryList(searchTypeCarMake);

                intent.putExtra(Constants.INTENT_SEARCH_RESULT_ACTIVITY_SEARCH_TYPE, searchTypeCarMake);
                intent.putExtra(Constants.INTENT_SEARCH_RESULT_BRAND_URL, searchTypeCarMake.getBrandUrl());
                intent.putExtra(Constants.INTENT_SEARCH_RESULT_ACTIVITY_SHOW_TYPE, Constants.SHOW_CAR_MAKE_NAME);
                startActivity(intent);

                break;
            case Constants.EACH_MAKE_MODEL_CLICKED:
                SearchTypeMakeModel searchTypeMakeModel = (SearchTypeMakeModel) searchList.get(position);
                pref.updateHistoryList(searchTypeMakeModel);

                intent.putExtra(Constants.INTENT_SEARCH_RESULT_ACTIVITY_SHOW_TYPE, Constants.SHOW_MAKE_MODEL);
                intent.putExtra(Constants.INTENT_SEARCH_RESULT_BRAND_URL, searchTypeMakeModel.getBrandUrl());
                intent.putExtra(Constants.INTENT_SEARCH_RESULT_ACTIVITY_SEARCH_TYPE, searchTypeMakeModel);

                startActivity(intent);

                break;
            case Constants.EACH_DEALER_CLICKED:
                SearchTypeDealer searchTypeDealer = (SearchTypeDealer) searchList.get(position);
                pref.updateHistoryList(searchTypeDealer);
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
