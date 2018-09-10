package com.anuj55149.kaarbizz.dao;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.anuj55149.kaarbizz.models.Dealer;
import com.anuj55149.kaarbizz.utilities.Constants;
import com.anuj55149.kaarbizz.utilities.Utilities;
import com.anuj55149.kaarbizz.volley.RequestCallback;
import com.anuj55149.kaarbizz.volley.VolleyRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class DealerDao extends VolleyRequest {

    private Context context;
    private RequestCallback requestCallback;

    public DealerDao(Context context, RequestCallback requestCallback) {
        super(context);
        this.context = context;
        this.requestCallback = requestCallback;
    }

    public void getNearestDealers(double lat, double lon) {
        String url = Utilities.getNearestDealersUrl(context, lat, lon);
        Log.d("dealertag", url);
        callApiForArray(url, new DaoCallback() {
            @Override
            public void response(Object response) {
                JSONArray jsonArray = (JSONArray) response;
                ArrayList<Dealer> dealers = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        dealers.add(new Dealer(jsonArray.getJSONObject(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                requestCallback.onListRequestSuccessful(dealers, Constants.DAO_GET_NEAREST_DEALERS, true);
            }

            @Override
            public void stringResponse(String response) {

            }

            @Override
            public void errorResponse(VolleyError error) {
                Log.d("dealertag", error.toString());
                requestCallback.onListRequestSuccessful(null, Constants.DAO_GET_NEAREST_DEALERS, false);
            }
        }, Constants.METHOD_GET, null);
    }

    public void getDealersForSearchResult(String dType, int limit) {
        String url = Utilities.getDealersUrlFromDType(context, dType, limit);
        Log.d("dealersTag", url);
        callApiForArray(url, new DaoCallback() {
            @Override
            public void response(Object response) {
                JSONArray jsonArray = (JSONArray) response;
                ArrayList<Dealer> dealers = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        dealers.add(new Dealer(jsonArray.getJSONObject(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                requestCallback.onListRequestSuccessful(dealers, Constants.DAO_GET_DEALERS_FROM_DTYPE, true);
            }

            @Override
            public void stringResponse(String response) {

            }

            @Override
            public void errorResponse(VolleyError error) {
                requestCallback.onListRequestSuccessful(null, Constants.DAO_GET_DEALERS_FROM_DTYPE, false);
            }
        }, Constants.METHOD_GET, null);
    }
}
