package com.example.karthikeyan.restaurantfinder;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.karthikeyan.restaurantfinder.model.Restaurant;
import com.example.karthikeyan.restaurantfinder.model.RestaurantInfo;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.karthikeyan.restaurantfinder.MainActivityFragment.RESTAURANT;

/**
 * Created by karthikeyan on 12/11/17.
 */

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DetailActivity";

    @BindView(R.id.backdrop)
    ImageView backDropView;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.fab_fav)
    FloatingActionButton favourite;

    Restaurant restaurant;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUi(savedInstanceState);
        loadUI();
        loadFragment(savedInstanceState);
    }

    private void loadUI() {
        if (restaurant != null) {
            loadBackDrop();
            initFav();

        }
    }

    private void loadFragment(Bundle savedInstanceState) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment;
        if (savedInstanceState == null) {
            fragment = new DetailActiviyFragment();
            Bundle args = new Bundle();
            args.putParcelable(RESTAURANT, restaurant);
            fragment.setArguments(args);
            fragmentTransaction.add(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }


    }


    private void initFav() {
        if (Utils.isFavorite(this, restaurant.getRestaurant())) {
            favourite.setImageResource(R.drawable.ic_bookmark);
        } else {
            favourite.setImageResource(R.drawable.ic_bookmark_border);
        }
    }

    private void loadBackDrop() {
        String featuredImage = restaurant.getRestaurant().getFeaturedImage();
        if (featuredImage.isEmpty()) {
            backDropView.setImageResource(R.drawable.recipe_icon_md);
            return;
        }
        Picasso.with(getBaseContext())
                .load(featuredImage)
                .error(R.drawable.recipe_icon_md)
                .placeholder(R.drawable.recipe_icon_md)
                .into(backDropView);
    }

    private void initUi(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
        setContentView(R.layout.activity_restaurant_detail);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar_details_activity);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        restaurant = getRestaurant();
        collapsingToolbar.setTitle(restaurant.getRestaurant().getName());

        favourite.setOnClickListener(this);
    }

    public Restaurant getRestaurant() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            throw new IllegalStateException();
        }
        if (!extras.containsKey(RESTAURANT)) {
            throw new IllegalStateException();
        }
        return (Restaurant) extras.get(RESTAURANT);
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick() called with: view = [" + view + "]");
        setFavorite();
    }

    private void setFavorite() {
        RestaurantInfo restaurantInfo = restaurant.getRestaurant();
        if (Utils.isFavorite(this, restaurantInfo)) {
            Utils.removeFromFavorites(this, restaurantInfo.getId());
            favourite.setImageResource(R.drawable.ic_bookmark_border);
        } else {
            Utils.addToFavorites(this, restaurantInfo);
            favourite.setImageResource(R.drawable.ic_bookmark);
        }
    }
}
