package com.anuj55149.kaarbizz.dao;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.anuj55149.kaarbizz.models.Car;
import com.anuj55149.kaarbizz.utilities.Constants;
import com.anuj55149.kaarbizz.volley.RequestCallback;
import com.anuj55149.kaarbizz.volley.VolleyRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class CarDao extends VolleyRequest {

    private RequestCallback requestCallback;
    private Context context;

    public CarDao(Context context, RequestCallback requestCallback) {
        super(context);
        this.context = context;
        this.requestCallback = requestCallback;
    }

    public void getCars(String url) {
        Log.d("carDaoTag", url);
        callApiForArray(url, new DaoCallback() {
            @Override
            public void response(Object response) {
                JSONArray jsonArray = (JSONArray) response;
                ArrayList<Car> cars = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        cars.add(new Car(jsonArray.getJSONObject(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                requestCallback.onListRequestSuccessful(cars, Constants.DAO_GET_CARS, true);
            }

            @Override
            public void stringResponse(String response) {

            }

            @Override
            public void errorResponse(VolleyError error) {
                requestCallback.onListRequestSuccessful(null, Constants.DAO_GET_CARS, false);
            }
        }, Constants.METHOD_GET, null);
    }
}
