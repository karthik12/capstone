package com.example.karthikeyan.restaurantfinder.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.karthikeyan.restaurantfinder.App.RestaurantApp;

/**
 * Created by karthikeyan on 12/11/17.
 */

public class ConfigurationManager {

    private static final String PREF_FILE = "com.ecample.karthikeyanp.restaurantfinder";
    private static ConfigurationManager sInstance;
    private SharedPreferences sharedPref;

    private ConfigurationManager() {
        //singleton
        sharedPref = RestaurantApp.getAppContext().getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
    }

    public ConfigurationManager getInstance() {
        if (sInstance == null) {
            sInstance = new ConfigurationManager();
        }
        return sInstance;
    }


}
