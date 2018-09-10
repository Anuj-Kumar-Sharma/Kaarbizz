package com.anuj55149.kaarbizz.models.searchTypes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchTypeCarMake implements Parcelable {

    public static final Creator<SearchTypeCarMake> CREATOR = new Creator<SearchTypeCarMake>() {
        @Override
        public SearchTypeCarMake createFromParcel(Parcel in) {
            return new SearchTypeCarMake(in);
        }

        @Override
        public SearchTypeCarMake[] newArray(int size) {
            return new SearchTypeCarMake[size];
        }
    };
    private int makeId;
    private String makeName, brandUrl;

    public SearchTypeCarMake(JSONObject obj) {
        try {
            this.makeId = obj.getInt("id");
            this.makeName = obj.getString("name");
            this.brandUrl = obj.getString("brandUrl");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected SearchTypeCarMake(Parcel in) {
        makeId = in.readInt();
        makeName = in.readString();
        brandUrl = in.readString();
    }

    public String getBrandUrl() {
        return brandUrl;
    }

    public int getMakeId() {
        return makeId;
    }

    public String getMakeName() {
        return makeName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(makeId);
        dest.writeString(makeName);
        dest.writeString(brandUrl);
    }
}
