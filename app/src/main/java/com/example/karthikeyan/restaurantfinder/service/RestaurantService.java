package com.example.karthikeyan.restaurantfinder.service;

import com.example.karthikeyan.restaurantfinder.model.Result;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by karthikeyan on 06/08/17.
 */

public interface RestaurantService {
    @GET("/api/v2.1//search")
    Call<Result> restaurantResult(@Header("user-key") String key, @Query("entity_id") int id, @Query("entity_type") String type);
}
