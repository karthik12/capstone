package com.example.karthikeyan.restaurantfinder.App;

import android.app.Application;
import android.content.Context;

/**
 * Created by karthikeyan on 11/11/17.
 */

public class RestaurantApp extends Application {

    private static RestaurantApp context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = (RestaurantApp) getApplicationContext();
    }

    public static Context getAppContext() {
        return context;
    }
}
