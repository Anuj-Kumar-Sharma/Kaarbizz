package com.anuj55149.kaarbizz.models.searchTypes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchTypeMakeModel implements Parcelable {

    public static final Creator<SearchTypeMakeModel> CREATOR = new Creator<SearchTypeMakeModel>() {
        @Override
        public SearchTypeMakeModel createFromParcel(Parcel in) {
            return new SearchTypeMakeModel(in);
        }

        @Override
        public SearchTypeMakeModel[] newArray(int size) {
            return new SearchTypeMakeModel[size];
        }
    };
    private int makeModelId;
    private String makeName, modelName, brandUrl;

    public SearchTypeMakeModel(JSONObject obj) {
        try {
            this.makeModelId = obj.getInt("id");
            this.makeName = obj.getString("makeName");
            this.modelName = obj.getString("modelName");
            this.brandUrl = obj.getString("brandUrl");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected SearchTypeMakeModel(Parcel in) {
        makeModelId = in.readInt();
        makeName = in.readString();
        modelName = in.readString();
        brandUrl = in.readString();
    }

    public String getBrandUrl() {
        return brandUrl;
    }

    public int getMakeModelId() {
        return makeModelId;
    }

    public String getMakeName() {
        return makeName;
    }

    public String getModelName() {
        return modelName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(makeModelId);
        dest.writeString(makeName);
        dest.writeString(modelName);
        dest.writeString(brandUrl);
    }
}
