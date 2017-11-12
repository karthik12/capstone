package com.example.karthikeyan.restaurantfinder;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.karthikeyan.restaurantfinder.model.Restaurant;
import com.example.karthikeyan.restaurantfinder.model.Result;
import com.example.karthikeyan.restaurantfinder.service.RestaurantService;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by karthikeyan on 11/11/17.
 */

public class RestaurantViewModel extends AndroidViewModel {

    private static final String TAG = "RestaurantViewModel";

    private static final String BASE_URL = "https://developers.zomato.com/api/v2.1/";

    private Context context;


    public MutableLiveData<List<Restaurant>> restaurants;

    public RestaurantViewModel(@NonNull Application application) {
        super(application);
        context = application;
    }

    public LiveData<List<Restaurant>> getRestaurants() {
        if (restaurants == null) {
            restaurants = new MutableLiveData<>();
            loadRestaurants(); //async
        }
        return restaurants;
    }

    private void loadRestaurants() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().build())
                .build();

        RestaurantService recipeService = retrofit.create(RestaurantService.class);
        Call<Result> resultCall = recipeService.restaurantResult(Utils.getRestaurantApiKey(), 1, "city");
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {
                Result body = response.body();
                if (body == null) {
                    Log.e(TAG, "onResponse: null");
                    return;
                }
                restaurants.postValue(body.getRestaurants());
            }

            @Override
            public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {
                if (!call.isCanceled()) {
                    Toast.makeText(context, R.string.result_retrieve_error,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
