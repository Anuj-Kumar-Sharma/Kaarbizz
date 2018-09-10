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

    public static final int GET_SEARCH_RESULT_WITH_QUERY = 4;
    public static final int SEARCH_RESULT_CLICKED = 5;
    public static final String SHARED_PREF_SERVER_IP_ADDRESS = "shared_pref_server_ip_address";
    public static final String SHARED_PREF_CURRENT_ADDRESS = "shared_pref_current_locality";
    public static final String SHARED_PREF_HISTORY_LIST = "shared_pref_history_list";

    public static final int DAO_SERVER_STATE = 6;
    public static final int DAO_GET_NEAREST_DEALERS = 7;
    public static final int DAO_GET_CARS = 15;
    public static final int DAO_GET_DEALERS_FROM_DTYPE = 14;
    public static final int SINGLE_DEALER_CLICKED = 8;
    public static final String SHARED_PREF_CURRENT_LOCATION_NAME = "shared_pref_location_name";

    public static final String SEARCH_TYPE_CAR_MAKE_NAME = "com.anuj55149.kaarbizz.models.searchTypes.SearchTypeCarMake";
    public static final String SEARCH_TYPE_DEALER_NAME = "com.anuj55149.kaarbizz.models.searchTypes.SearchTypeDealer";
    public static final String SEARCH_TYPE_MAKE_MODEL_NAME = "com.anuj55149.kaarbizz.models.searchTypes.SearchTypeMakeModel";
    public static final int EACH_CAR_MAKE_CLICKED = 9;
    public static final int EACH_MAKE_MODEL_CLICKED = 10;
    public static final int EACH_DEALER_CLICKED = 11;

    public static final int TYPE_DATA = -1;
    public static final int TYPE_HISTORY = -2;
    public static final String SHARED_PREF_HISTORY_CAR_MAKE = "shared_pref_history_car_make";
    public static final String SHARED_PREF_HISTORY_TYPE_LIST = "shared_pref_history_type_list";

    public static final String INTENT_SEARCH_RESULT_ACTIVITY_SEARCH_TYPE = "intent_search_result_activity_search_type";
    public static final String INTENT_SEARCH_RESULT_ACTIVITY_SHOW_TYPE = "intent_search_result_activity_show_type";

    public static final int SHOW_CAR_MAKE_NAME = 12;
    public static final int SHOW_MAKE_MODEL = 13;
    public static final String D_TYPE_CAR_MAKE_NAME = "carMake";
    public static final String D_TYPE_MAKE_MODEL = "makeModel";
    public static final String D_TYPE_DEALER = "dealer";
    public static final int EACH_CAR_LAYOUT_CLICK = 16;
    public static final String INTENT_SEARCH_RESULT_BRAND_URL = "intent_search_result_brand_url";
}
