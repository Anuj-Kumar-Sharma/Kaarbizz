package com.anuj55149.kaarbizz.models.searchTypes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchTypeDealer implements Parcelable {

    public static final Creator<SearchTypeDealer> CREATOR = new Creator<SearchTypeDealer>() {
        @Override
        public SearchTypeDealer createFromParcel(Parcel in) {
            return new SearchTypeDealer(in);
        }

        @Override
        public SearchTypeDealer[] newArray(int size) {
            return new SearchTypeDealer[size];
        }
    };
    private int id;
    private String name;
    private double rating;

    public SearchTypeDealer(JSONObject obj) {
        try {
            this.id = obj.getInt("id");
            this.rating = obj.getDouble("avgRating");
            this.name = obj.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected SearchTypeDealer(Parcel in) {
        id = in.readInt();
        name = in.readString();
        rating = in.readDouble();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getRating() {
        return rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(rating);
    }
}
