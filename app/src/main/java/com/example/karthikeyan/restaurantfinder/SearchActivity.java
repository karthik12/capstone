package com.example.karthikeyan.restaurantfinder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.karthikeyan.restaurantfinder.preference.ConfigurationManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by karthikeyan on 12/11/17.
 */

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.find_my_location)
    Button detectMe;

    @BindView(R.id.search_location)
    Button search;

    @BindView(R.id.place_query)
    EditText location;

    FusedLocationProviderClient mFusedLocationClient;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 12;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        ConfigurationManager instance = ConfigurationManager.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        ButterKnife.bind(this);

        detectMe.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                retrieveLocation();
            } else {
                permissionCheck();
            }
        });
        location.setOnEditorActionListener(((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_GO) {
                performSearch(instance);
                return true;
            }
            return false;
        }));
        search.setOnClickListener(view -> {
            performSearch(instance);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConfigurationManager instance = ConfigurationManager.getInstance();
        if (!instance.getLocation().isEmpty() || !(instance.getLatitude() == 0.0f && instance.getLongitude() == 0.0f)) {
            startMainActivity();
        }
    }

    private void performSearch(ConfigurationManager instance) {
        String locationString = location.getText().toString();
        if (!locationString.isEmpty()) {
            instance.putLocationQuery(locationString);
            startMainActivity();
        }
    }

    @SuppressLint("MissingPermission")
    private void retrieveLocation() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            //save lat, lon to preference
            ConfigurationManager instance = ConfigurationManager.getInstance();
            instance.putLatitude(location.getLatitude());
            instance.putLongitude(location.getLongitude());
            startMainActivity();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, R.string.unable_to_detect_location, Toast.LENGTH_LONG).show();
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void permissionCheck() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(findViewById(android.R.id.content), R.string.grant_location_permission, Snackbar.LENGTH_LONG).show();

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    retrieveLocation();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
