package com.example.karthikeyan.restaurantfinder;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.karthikeyan.restaurantfinder.adapter.RestaurantsAdapter;
import com.example.karthikeyan.restaurantfinder.model.Restaurant;

import java.util.List;

import static com.example.karthikeyan.restaurantfinder.Utils.isOnline;

/**
 * Created by karthikeyan on 11/11/17.
 */

public class MainActivityFragment extends Fragment implements RestaurantsAdapter.ClickListener {

    private static final String TAG = "MainActivityFragment";
    public static final String RESTAURANT = "RESTAURANT";
    RecyclerView recyclerView;
    ProgressBar progressBar;
    RestaurantViewModel viewModel;
    RestaurantsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container);
        recyclerView = view.findViewById(R.id.recycler_view_main);
        progressBar = view.findViewById(R.id.progress_bar);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getContext().registerReceiver(connectivityChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        int gridColumn = getResources().getInteger(R.integer.grid_column_number);
        viewModel = ViewModelProviders.of(getActivity()).get(RestaurantViewModel.class);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), gridColumn));

        adapter = new RestaurantsAdapter(viewModel.restaurants, this);
        recyclerView.setAdapter(adapter);
        if (!isOnline()) {
            Snackbar.make(getView(), R.string.no_connection, Snackbar.LENGTH_LONG).show();
            return;
        }
        observe();
    }

    private void observe() {

        viewModel.getRestaurants().observe(this, result -> {
            if (result == null) {
                return;
            }
            onlineViewChanges();
            adapter.updateRestaurants(result);

        });
    }

    private BroadcastReceiver connectivityChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isOnline()) {
                observe();
            } else if (viewModel == null || viewModel.restaurants == null) {
                offlineViewChanges();
            }
        }
    };

    private void offlineViewChanges() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void onlineViewChanges() {
        if (viewModel != null && viewModel.restaurants != null) {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(connectivityChangeReceiver);
        } catch (Exception e) {
            Log.i(TAG, "Exception occurred during un-registering network receiver");
        }
    }

    @Override
    public void accept(int position) {
        List<Restaurant> value = viewModel.getRestaurants().getValue();
        if (value == null) {
            Log.i(TAG, "onRecipeClicked: No recipe");
            return;
        }
        Restaurant restaurant = value.get(position);
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra(RESTAURANT, restaurant);
        startActivity(intent);

        Log.d(TAG, "onRecipeClicked() called with: recipe = [" + position + "]");
    }
}
