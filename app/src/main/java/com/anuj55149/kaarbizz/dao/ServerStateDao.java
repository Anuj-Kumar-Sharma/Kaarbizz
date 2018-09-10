package com.anuj55149.kaarbizz.dao;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.anuj55149.kaarbizz.utilities.Constants;
import com.anuj55149.kaarbizz.utilities.Utilities;
import com.anuj55149.kaarbizz.volley.RequestCallback;
import com.anuj55149.kaarbizz.volley.VolleyRequest;

public class ServerStateDao extends VolleyRequest {

    private RequestCallback requestCallback;
    private Context context;

    public ServerStateDao(Context context, RequestCallback requestCallback) {
        super(context);

        this.requestCallback = requestCallback;
        this.context = context;
    }

    public void getServerState() {
        String url = Utilities.getServerStateUrl(context);
        Log.d("servertag", url);
        callApiForObject(url, new DaoCallback() {
            @Override
            public void response(Object response) {
                Log.d("servertag", "success");
                requestCallback.onObjectRequestSuccessful(response, Constants.DAO_SERVER_STATE, true);
            }

            @Override
            public void stringResponse(String response) {

            }

            @Override
            public void errorResponse(VolleyError volleyError) {
                Log.d("servertag", volleyError.toString());
                requestCallback.onObjectRequestSuccessful(null, Constants.DAO_SERVER_STATE, false);
            }
        }, Constants.METHOD_GET, null);
    }
}
