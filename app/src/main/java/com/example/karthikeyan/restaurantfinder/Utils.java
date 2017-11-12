package com.example.karthikeyan.restaurantfinder;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.example.karthikeyan.restaurantfinder.App.RestaurantApp;

/**
 * Created by karthikeyan on 11/11/17.
 */

public class Utils {

    private static final String TAG = "Utils";
    private static final String ZOMATO_KEY = "zomato-key";

    public static String getRestaurantApiKey() {
        try {
            Context appContext = RestaurantApp.getAppContext();
            ApplicationInfo ai = appContext.getPackageManager().getApplicationInfo(appContext.getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString(ZOMATO_KEY);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
        return null;
    }

    public static boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) RestaurantApp.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }


}
