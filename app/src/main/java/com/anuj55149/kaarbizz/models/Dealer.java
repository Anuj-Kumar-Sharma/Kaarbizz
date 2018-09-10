package com.anuj55149.kaarbizz.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Dealer implements Parcelable {

    public static final Creator<Dealer> CREATOR = new Creator<Dealer>() {
        @Override
        public Dealer createFromParcel(Parcel in) {
            return new Dealer(in);
        }

        @Override
        public Dealer[] newArray(int size) {
            return new Dealer[size];
        }
    };
    private int id, rateCount;
    private double lat, lon, rating, distanceFromCurrentLocation;
    private String name, email;

    private Dealer(Parcel in) {
        id = in.readInt();
        rateCount = in.readInt();
        lat = in.readDouble();
        lon = in.readDouble();
        rating = in.readDouble();
        distanceFromCurrentLocation = in.readDouble();
        name = in.readString();
        email = in.readString();
    }

    public Dealer(JSONObject obj) {
        try {
            this.id = obj.getInt("id");
            this.rateCount = obj.getInt("peopleRated");
            this.lat = obj.getDouble("lat");
            this.lon = obj.getDouble("lon");
            this.rating = obj.getDouble("avgRating");
            this.name = obj.getString("name");
            this.email = obj.getString("mail");
            this.distanceFromCurrentLocation = obj.getDouble("disCurLocation");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public Dealer(int id, int rateCount, double lat, double lon, double rating, double distanceFromCurrentLocation, String name, String email) {
        this.id = id;
        this.rateCount = rateCount;
        this.lat = lat;
        this.lon = lon;
        this.rating = rating;
        this.distanceFromCurrentLocation = distanceFromCurrentLocation;
        this.name = name;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRateCount() {
        return rateCount;
    }

    public void setRateCount(int rateCount) {
        this.rateCount = rateCount;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getDistanceFromCurrentLocation() {
        return distanceFromCurrentLocation;
    }

    public void setDistanceFromCurrentLocation(double distanceFromCurrentLocation) {
        this.distanceFromCurrentLocation = distanceFromCurrentLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(rateCount);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeDouble(rating);
        dest.writeDouble(distanceFromCurrentLocation);
        dest.writeString(name);
        dest.writeString(email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dealer dealer = (Dealer) o;

        return id == dealer.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
