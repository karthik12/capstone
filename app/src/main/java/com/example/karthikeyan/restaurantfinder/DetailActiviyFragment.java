package com.example.karthikeyan.restaurantfinder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by karthikeyan on 12/11/17.
 */

public class DetailActiviyFragment extends AbstractRestaurantFragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.restaurant_detail_fragment, container, false);
        ButterKnife.bind(this, view);
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getRestaurantFromBundle();
        loadContent();
    }

    private void loadContent() {

    }
}
