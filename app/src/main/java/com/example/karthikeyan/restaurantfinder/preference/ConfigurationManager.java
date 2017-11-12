package com.example.karthikeyan.restaurantfinder.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.karthikeyan.restaurantfinder.App.RestaurantApp;

/**
 * Created by karthikeyan on 12/11/17.
 */

public class ConfigurationManager {

    private static final String PREF_FILE = "com.ecample.karthikeyanp.restaurantfinder";
    private static final String LATITUDE = "LATITUDE";
    private static final String LONGITUDE = "LONGITUDE";
    private static final String LOCATION = "LOCATION";
    private static ConfigurationManager sInstance;
    private SharedPreferences sharedPref;

    private ConfigurationManager() {
        //singleton
        sharedPref = RestaurantApp.getAppContext().getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
    }

    public static ConfigurationManager getInstance() {
        if (sInstance == null) {
            sInstance = new ConfigurationManager();
        }
        return sInstance;
    }


    public void putLatitude(double latitude) {
        sharedPref.edit().putFloat(LATITUDE, (float) latitude).apply();
    }

    public void putLongitude(double longitude) {
        sharedPref.edit().putFloat(LONGITUDE, (float) longitude).apply();
    }

    public double getLatitude() {
        return sharedPref.getFloat(LATITUDE, 0.0f);
    }

    public double getLongitude() {
        return sharedPref.getFloat(LONGITUDE, 0.0f);
    }

    public void putLocationQuery(String location) {
        sharedPref.edit().putString(LOCATION, location).apply();
    }

    public String getLocation() {
        return sharedPref.getString(LOCATION, "");
    }

    public void clear() {
        sharedPref.edit().clear().apply();
    }
}
