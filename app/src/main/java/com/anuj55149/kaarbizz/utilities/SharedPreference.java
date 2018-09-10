package com.anuj55149.kaarbizz.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;

import com.anuj55149.kaarbizz.R;
import com.anuj55149.kaarbizz.models.searchTypes.SearchTypeCarMake;
import com.anuj55149.kaarbizz.models.searchTypes.SearchTypeDealer;
import com.anuj55149.kaarbizz.models.searchTypes.SearchTypeMakeModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPreference {

    private android.content.SharedPreferences pref;

    public SharedPreference(Context context) {
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(context.getString(R.string.app_name), PRIVATE_MODE);
    }

    public boolean getIsFirstTime() {
        boolean isFirstTime = pref.getBoolean(Constants.SHARED_PREF_IS_FIRST_TIME, true);
        if (isFirstTime) {
            android.content.SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(Constants.SHARED_PREF_IS_FIRST_TIME, false);
            editor.apply();
            return true;
        } else {
            return false;
        }
    }

    public boolean getIsLaterClicked() {
        return pref.getBoolean(Constants.SHARED_PREF_IS_LATER_CLICKED, false);
    }

    public void setIsLaterClicked() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Constants.SHARED_PREF_IS_LATER_CLICKED, true);
        editor.apply();
    }

    public String getServerIPAddress() {
        return pref.getString(Constants.SHARED_PREF_SERVER_IP_ADDRESS, "192.168.0.2");
    }

    public void setServerIPAddress(String ipAddress) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constants.SHARED_PREF_SERVER_IP_ADDRESS, ipAddress);
        editor.apply();
    }

    public Address getCurrentAddress() {
        Gson gson = new Gson();
        String addressJson = pref.getString(Constants.SHARED_PREF_CURRENT_ADDRESS, "Not found");
        return gson.fromJson(addressJson, Address.class);
    }

    public void setCurrentAddress(Address address) {
        Gson gson = new Gson();
        String addressJson = gson.toJson(address);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constants.SHARED_PREF_CURRENT_ADDRESS, addressJson);
        editor.apply();
    }

    public void setCurrentLocationName(String locationName) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constants.SHARED_PREF_CURRENT_LOCATION_NAME, locationName);
        editor.apply();
    }

    public String getLocationName() {
        return pref.getString(Constants.SHARED_PREF_CURRENT_LOCATION_NAME, "");
    }

    public void updateHistoryList(Object object) {
        Type typeData = new TypeToken<ArrayList<String>>() {
        }.getType();
        Type typeType = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        Gson gson = new Gson();
        String historyListString = pref.getString(Constants.SHARED_PREF_HISTORY_LIST, "null");
        String historyListTypeString = pref.getString(Constants.SHARED_PREF_HISTORY_TYPE_LIST, "null");

        ArrayList<String> queueData = gson.fromJson(historyListString, typeData);
        ArrayList<Integer> queueType = gson.fromJson(historyListTypeString, typeType);

        if (queueData == null) {
            queueData = new ArrayList<>();
            queueType = new ArrayList<>();
        }

        if (queueData.size() == 6) {
            queueData.remove(queueData.size() - 1);
            queueType.remove(queueType.size() - 1);
        }


        switch (object.getClass().getName()) {
            case Constants.SEARCH_TYPE_CAR_MAKE_NAME:
                SearchTypeCarMake searchTypeCarMake = (SearchTypeCarMake) object;
                String searchCarMake = gson.toJson(searchTypeCarMake, SearchTypeCarMake.class);
                queueData.add(0, searchCarMake);
                queueType.add(0, 0);
                break;
            case Constants.SEARCH_TYPE_MAKE_MODEL_NAME:
                SearchTypeMakeModel searchTypeMakeModel = (SearchTypeMakeModel) object;
                String searchMakeModel = gson.toJson(searchTypeMakeModel, SearchTypeMakeModel.class);
                queueData.add(0, searchMakeModel);
                queueType.add(0, 1);
                break;
            case Constants.SEARCH_TYPE_DEALER_NAME:
                SearchTypeDealer searchTypeDealer = (SearchTypeDealer) object;
                String searchDealer = gson.toJson(searchTypeDealer, SearchTypeDealer.class);
                queueData.add(0, searchDealer);
                queueType.add(0, 2);
                break;
        }

        historyListString = gson.toJson(queueData, typeData);
        historyListTypeString = gson.toJson(queueType, typeType);

        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constants.SHARED_PREF_HISTORY_LIST, historyListString);
        editor.putString(Constants.SHARED_PREF_HISTORY_TYPE_LIST, historyListTypeString);
        editor.apply();
    }

    public ArrayList<Object> getHistoryList() {
        Type typeData = new TypeToken<ArrayList<String>>() {
        }.getType();
        Type typeType = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        Gson gson = new Gson();
        String historyListString = pref.getString(Constants.SHARED_PREF_HISTORY_LIST, "null");
        String historyListTypeString = pref.getString(Constants.SHARED_PREF_HISTORY_TYPE_LIST, "null");

        ArrayList<String> queueData = gson.fromJson(historyListString, typeData);
        ArrayList<Integer> queueType = gson.fromJson(historyListTypeString, typeType);

        if (queueData == null) {
            return null;
        }

        ArrayList<Object> ans = new ArrayList<>();
        for (int i = 0; i < queueType.size(); i++) {
            Integer e = queueType.get(i);
            switch (e) {
                case 0:
                    SearchTypeCarMake searchTypeCarMake = gson.fromJson(queueData.get(i), SearchTypeCarMake.class);
                    ans.add(searchTypeCarMake);
                    break;
                case 1:
                    SearchTypeMakeModel searchTypeMakeModel = gson.fromJson(queueData.get(i), SearchTypeMakeModel.class);
                    ans.add(searchTypeMakeModel);
                    break;
                case 2:
                    SearchTypeDealer searchTypeDealer = gson.fromJson(queueData.get(i), SearchTypeDealer.class);
                    ans.add(searchTypeDealer);
                    break;
            }
        }

        return ans;
    }

}
