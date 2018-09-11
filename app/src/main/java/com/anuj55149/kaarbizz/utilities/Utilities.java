package com.anuj55149.kaarbizz.utilities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.anuj55149.kaarbizz.dao.ServerStateDao;
import com.anuj55149.kaarbizz.volley.Urls;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Utilities {

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "K");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String getCarQueryUrl(Context context, String query) {
        SharedPreference pref = new SharedPreference(context);
        String ipAddress = pref.getServerIPAddress();
        return "http://" + ipAddress + ":5000" + Urls.CARS_SEARCH_URL + "?q=" + query;
    }

    public static String getServerStateUrl(Context context) {
        SharedPreference pref = new SharedPreference(context);
        String ipAddress = pref.getServerIPAddress();
        return "http://" + ipAddress + ":5000/";
    }

    public static void startServerCheck(ServerStateDao serverStateDao) {
        serverStateDao.getServerState();
    }

    public static String encodeKeyword(String keyword) {
        keyword = keyword.replaceAll("[^\\.\\-\\w\\s]", " ");
        keyword = keyword.replaceAll("\\s+", " ");
        keyword = keyword.trim();
        try {
            keyword = (URLEncoder.encode(keyword, "UTF-8")).replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return keyword;
    }

    public static String getNearestDealersUrl(Context context, double lat, double lon) {
        SharedPreference pref = new SharedPreference(context);
        String ipAddress = pref.getServerIPAddress();
        return "http://" + ipAddress + ":5000" + Urls.NEAREST_DEALERS_URL + "?lat=" + lat + "&lon=" + lon;
    }

    public static int getColorFromRating(double x) {
        return android.graphics.Color.HSVToColor(new float[]{(float) x * 120f / 5f, 0.8f, 0.8f});
    }

    public static Address getAddressFromLatLng(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (null != listAddresses && listAddresses.size() > 0) {
                return listAddresses.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDealersUrlFromDType(Context context, String dType, int limit) {
        SharedPreference pref = new SharedPreference(context);
        String ipAddress = pref.getServerIPAddress();
        Address address = pref.getCurrentAddress();

        return "http://" + ipAddress + ":5000" + Urls.DEALERS_URL + "?lat=" + address.getLatitude() + "&lon=" + address.getLongitude() + "&dType=" + dType + "&limit=" + limit;
    }

    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static String getIndianCurrencyFormat(String amount) {
        StringBuilder stringBuilder = new StringBuilder();
        char amountArray[] = amount.toCharArray();
        int a = 0, b = 0;
        for (int i = amountArray.length - 1; i >= 0; i--) {
            if (a < 3) {
                stringBuilder.append(amountArray[i]);
                a++;
            } else if (b < 2) {
                if (b == 0) {
                    stringBuilder.append(",");
                    stringBuilder.append(amountArray[i]);
                    b++;
                } else {
                    stringBuilder.append(amountArray[i]);
                    b = 0;
                }
            }
        }
        return stringBuilder.reverse().toString();
    }
}


