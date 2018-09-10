package com.anuj55149.kaarbizz.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Car {

    private int id, year, price, dealerId;
    private String vin, make, model, trim, engine, body, color, transmission;
    private Dealer dealer;

    public Car(JSONObject obj) {
        try {
            id = obj.getInt("id");
            year = obj.getInt("year");
            price = obj.getInt("price");
            dealerId = obj.getInt("dealerId");
            vin = obj.getString("vin");
            make = obj.getString("make");
            model = obj.getString("model");
            trim = obj.getString("trim");
            engine = obj.getString("engine");
            body = obj.getString("body");
            color = obj.getString("color");
            transmission = obj.getString("transmission");

            int rateCount = obj.getInt("peopleRated");
            double lat = obj.getDouble("lat");
            double lon = obj.getDouble("lon");
            double rating = obj.getDouble("avgRating");
            String name = obj.getString("dealerName");
            String email = obj.getString("mail");
            int distanceFromCurrentLocation = obj.getInt("disCurLocation");

            dealer = new Dealer(dealerId, rateCount, lat, lon, rating, distanceFromCurrentLocation, name, email);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public int getPrice() {
        return price;
    }

    public int getDealerId() {
        return dealerId;
    }

    public String getVin() {
        return vin;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getTrim() {
        return trim;
    }

    public String getEngine() {
        return engine;
    }

    public String getBody() {
        return body;
    }

    public String getColor() {
        return color;
    }

    public String getTransmission() {
        return transmission;
    }

    public Dealer getDealer() {
        return dealer;
    }
}
