package com.anuj55149.kaarbizz.utilities;
import android.Manifest;

public class Constants {

    public static final String API_KEY = "AIzaSyCebTpPAk9MwhS99SsS5yf5BcRbH08dyFQ";
    public static final String PERMISSION_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PERMISSION_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    public static final String SEARCH_RESULT_BACK_INTENT = "search_result_back_intent";
    public static final String SHARED_PREF_IS_FIRST_TIME = "shared_pref_is_first_time";
    public static final String SHARED_PREF_IS_LATER_CLICKED = "shared_pref_is_later_clicked";

    public static final int METHOD_GET = 2;
    public static final int METHOD_POST = 3;

    public static final int GET_CARS_NAME_WITH_QUERY = 4;
    public static final int SEARCH_RESULT_CLICKED = 5;
    public static final String SHARED_PREF_SERVER_IP_ADDRESS = "shared_pref_server_ip_address";

    public static final int DAO_SERVER_STATE = 6;
    public static final int DAO_GET_NEAREST_DEALERS = 7;
}
