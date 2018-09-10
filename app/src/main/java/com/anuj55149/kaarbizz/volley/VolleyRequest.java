package com.anuj55149.kaarbizz.volley;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.anuj55149.kaarbizz.dao.DaoCallback;
import com.anuj55149.kaarbizz.utilities.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by anuj5 on 02-01-2018.
 */

public class VolleyRequest {

    private static final int MY_SOCKET_TIMEOUT_MS = 5000;
    private Context context;

    public VolleyRequest(Context context) {
        this.context = context;
    }

    public void callApiForArray(String url, final DaoCallback daoCallback, int method, JSONObject jsonObject) {
        switch (method) {
            case Constants.METHOD_GET:
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                        url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        daoCallback.response(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        daoCallback.errorResponse(error);
                    }
                });

                jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                        MY_SOCKET_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
                break;

        }
    }

    public void callApiForObject(String url, final DaoCallback daoCallback, int method, JSONObject jsonObject) {
        switch (method) {
            case Constants.METHOD_GET:
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                        url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        daoCallback.response(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        daoCallback.errorResponse(volleyError);

                        String message = null;
                        if (volleyError instanceof NetworkError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (volleyError instanceof ServerError) {
                            message = "The server could not be found. Please try again after some time!!";
                        } else if (volleyError instanceof AuthFailureError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (volleyError instanceof ParseError) {
                            message = "Parsing error! Please try again after some time!!";
                        } else if (volleyError instanceof TimeoutError) {
                            message = "Connection TimeOut! Please check your internet connection.";
                        }

                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                    }
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        MY_SOCKET_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
                break;
        }
    }


}
