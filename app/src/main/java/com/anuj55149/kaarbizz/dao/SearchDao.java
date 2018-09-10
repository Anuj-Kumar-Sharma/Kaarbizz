package com.anuj55149.kaarbizz.dao;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.anuj55149.kaarbizz.models.searchTypes.SearchTypeCarMake;
import com.anuj55149.kaarbizz.models.searchTypes.SearchTypeDealer;
import com.anuj55149.kaarbizz.models.searchTypes.SearchTypeMakeModel;
import com.anuj55149.kaarbizz.utilities.Constants;
import com.anuj55149.kaarbizz.utilities.Utilities;
import com.anuj55149.kaarbizz.volley.RequestCallback;
import com.anuj55149.kaarbizz.volley.VolleyRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class SearchDao extends VolleyRequest {

    private RequestCallback requestCallback;
    private Context context;

    public SearchDao(Context context, RequestCallback requestCallback) {
        super(context);
        this.context = context;
        this.requestCallback = requestCallback;
    }

    public void getCarNamesWithQuery(String query) {
        query = Utilities.encodeKeyword(query);
        String url = Utilities.getCarQueryUrl(context, query);
        Log.d("tag", url);
        callApiForArray(url, new DaoCallback() {
            @Override
            public void response(Object response) {
                JSONArray jsonArray = (JSONArray) response;
                ArrayList<Object> searchList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        String type = jsonArray.getJSONObject(i).getString("resultType");
                        switch (type) {
                            case "carMake":
                                searchList.add(new SearchTypeCarMake(jsonArray.getJSONObject(i)));
                                break;
                            case "makeModel":
                                searchList.add(new SearchTypeMakeModel(jsonArray.getJSONObject(i)));
                                break;
                            case "dealer":
                                searchList.add(new SearchTypeDealer(jsonArray.getJSONObject(i)));
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                requestCallback.onListRequestSuccessful(searchList, Constants.GET_SEARCH_RESULT_WITH_QUERY, true);
            }

            @Override
            public void stringResponse(String response) {

            }

            @Override
            public void errorResponse(VolleyError error) {
                Log.d("volleytag", error.toString());
                requestCallback.onListRequestSuccessful(null, Constants.GET_SEARCH_RESULT_WITH_QUERY, false);
            }
        }, Constants.METHOD_GET, null);
    }
}
