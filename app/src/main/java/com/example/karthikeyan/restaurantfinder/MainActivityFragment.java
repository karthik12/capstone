package com.example.karthikeyan.restaurantfinder;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.karthikeyan.restaurantfinder.adapter.RestaurantsAdapter;
import com.example.karthikeyan.restaurantfinder.database.RestaurantContract;
import com.example.karthikeyan.restaurantfinder.model.Location;
import com.example.karthikeyan.restaurantfinder.model.Restaurant;
import com.example.karthikeyan.restaurantfinder.model.RestaurantInfo;
import com.example.karthikeyan.restaurantfinder.model.UserRating;

import java.util.ArrayList;
import java.util.List;

import static com.example.karthikeyan.restaurantfinder.Utils.isOnline;

/**
 * Created by karthikeyan on 11/11/17.
 */

public class MainActivityFragment extends Fragment implements RestaurantsAdapter.ClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MainActivityFragment";
    public static final String RESTAURANT = "RESTAURANT";
    private static final int LOADER_ID = 123;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    RestaurantViewModel viewModel;
    RestaurantsAdapter adapter;
    private boolean favDisplaying;
    private List<RestaurantInfo> restaurants = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container);
        recyclerView = view.findViewById(R.id.recycler_view_main);
        progressBar = view.findViewById(R.id.progress_bar);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_fav:
                favDisplaying = true;
                fetchFavRestaurants();
                return true;
            case R.id.menu_all:
                favDisplaying = false;
                observe();
                return true;
            case R.id.menu_search:
                startSearchActivity();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void startSearchActivity() {
        Intent intent = new Intent(getContext(),SearchActivity.class);
        getContext().startActivity(intent);
    }

    private void fetchFavRestaurants() {
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    private void observe() {
        if (favDisplaying) {
            return;
        }
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


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                RestaurantContract.RestaurantEntry.CONTENT_URI,
                null,
                null,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Restaurant> restaurantList = new ArrayList<>();
        restaurants.clear();
        if (!favDisplaying) {
            return;
        }
        if (data != null && data.moveToFirst()) {
            do {
                String restaurantId = data.getString(data.getColumnIndex(RestaurantContract.RestaurantEntry.RESTAURANT_ID));
                String restaurantName = data.getString(data.getColumnIndex(RestaurantContract.RestaurantEntry.RESTAURANT_NAME));
                String restaurantBackDrop = data.getString(data.getColumnIndex(RestaurantContract.RestaurantEntry.RESTAURANT_BACKDROP_URI));
                String cuisines = data.getString(data.getColumnIndex(RestaurantContract.RestaurantEntry.RESTAURANT_CUISINES));
                Integer cost = data.getInt(data.getColumnIndex(RestaurantContract.RestaurantEntry.RESTAURANT_COST));
                String currency = data.getString(data.getColumnIndex(RestaurantContract.RestaurantEntry.RESTAURANT_CURRENCY));
                String rating = data.getString(data.getColumnIndex(RestaurantContract.RestaurantEntry.RESTAURANT_RATING));
                Integer online = data.getInt(data.getColumnIndex(RestaurantContract.RestaurantEntry.RESTAURANT_ONLINE));
                String address = data.getString(data.getColumnIndex(RestaurantContract.RestaurantEntry.RESTAURANT_ADDRESS));
                String city = data.getString(data.getColumnIndex(RestaurantContract.RestaurantEntry.RESTAURANT_CITY));
                String lat = data.getString(data.getColumnIndex(RestaurantContract.RestaurantEntry.RESTAURANT_LAT));
                String lon = data.getString(data.getColumnIndex(RestaurantContract.RestaurantEntry.RESTAURANT_LON));
                RestaurantInfo restaurantInfo = new RestaurantInfo(
                        restaurantId, restaurantName, new Location(address, city, lat, lon)
                        , cuisines, cost, currency, online, restaurantBackDrop, new UserRating(rating));

                restaurants.add(restaurantInfo);
            } while (data.moveToNext());
            for (RestaurantInfo restaurant : restaurants) {
                Restaurant rest = new Restaurant();
                rest.setRestaurant(restaurant);
                restaurantList.add(rest);
            }
            adapter.updateRestaurants(restaurantList);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
