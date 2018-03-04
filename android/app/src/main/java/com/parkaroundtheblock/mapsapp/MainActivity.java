package com.parkaroundtheblock.mapsapp;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parkaroundtheblock.mapsapp.network.Content;
import com.parkaroundtheblock.mapsapp.network.LocationItem;
import com.parkaroundtheblock.mapsapp.network.RemoteApi;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Content content = null;

    private TextView mTextMessage;
    private GoogleMap map;
    private View park;
    private View pay;
    private View xyo1;
    private View xyo2;
    private View splash;
    private BottomNavigationView bottomNavigationView;
    private Button parkButton;

    int contentIndex = -1;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_park:
                    park.setVisibility(View.VISIBLE);
                    pay.setVisibility(View.GONE);
                    mTextMessage.setText(R.string.title_park);
                    return true;
                case R.id.navigation_pay:
                    redirectToPay();
                    return true;
            }
            return false;
        }
    };

    private void redirectToPay() {
        park.setVisibility(View.GONE);
        pay.setVisibility(View.VISIBLE);
        mTextMessage.setText(R.string.title_pay);


        float difference = 17.22f;
        if (Math.abs((int) difference) > 1) {
            float peace = difference / Math.abs((int) difference);
            Observable.interval(50, TimeUnit.MILLISECONDS)
                    .take(Math.abs((int) difference))
                    .map(v -> v + 1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((v) -> {
                        float total = Math.abs(-179.52f);
                        total += peace;
                        updateTotalToday(total);
                    });
        }

        Maybe.empty()
                .delay(5000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        ActionBar actionBar = ((AppCompatActivity) MainActivity.this).getSupportActionBar();
                        if (actionBar != null) {
                            actionBar.setTitle("Transaction Completed. Mining started.");
                            invalidateOptionsMenu();
                        }
                    }
                })
                .subscribe();
    }

    public void updateTotalToday(float number) {
        try {
            float totalTripsDeductions = Math.abs(number);
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            changeTitle("0," +formatter.format(totalTripsDeductions) + " XYO PENDING...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeTitle(String text) {
        try {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                ActionBar actionBar = ((AppCompatActivity) this).getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(text);
                    invalidateOptionsMenu();
                    actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void move(LocationItem locationItem) {
        if (locationItem != null && locationItem.properties != null && !TextUtils.isEmpty(locationItem.properties.geoCoordinates)) {
            String[] coords = locationItem.properties.geoCoordinates.split(":");
            if (coords.length >= 2) {
                try {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(Integer.parseInt(coords[0]),Integer.parseInt(coords[1])))
                            .zoom(16)
                            .build();

                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }


    }

    private boolean shouldBeRedirectedToPay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        splash = findViewById(R.id.splash);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation);
        pay = findViewById(R.id.pay);
        xyo1 = findViewById(R.id.xyo1);
        xyo2 = findViewById(R.id.xyo2);

        parkButton = findViewById(R.id.park_button);
        parkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (content == null) {
                    if (shouldBeRedirectedToPay) {
                        redirectToPay();
                        return;
                    }

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(32.7172267,-117.1694746))
                            .zoom(18)
                            .build();

                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    Maybe.empty()
                            .delay(1000, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnComplete(new Action() {
                                @Override
                                public void run() throws Exception {
                                    parkButton.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_payment));
                                    shouldBeRedirectedToPay = true;
                                }
                            })
                            .subscribe();



                   /* RemoteApi.getInstance().getParkingSpots(MainActivity.this, "32.7172267", "-117.1694746", new Consumer<Response<Content>>() {
                        @Override
                        public void accept(Response<Content> contentResponse) throws Exception {
                            content = contentResponse.body();

                            if (content.content.size() > 0) {
                                contentIndex = 0;
                                moveNext();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });*/
                } else {
                    moveNext();
                }


            }
        });

        park = findViewById(R.id.park);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Maybe.empty()
                .delay(3000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        splash.setVisibility(View.GONE);
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        park.setVisibility(View.VISIBLE);

                        rotate(xyo1);
                    }
                })
                .subscribe();

        Maybe.empty()
                .delay(4000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        rotate(xyo2);
                    }
                })
                .subscribe();
    }

    private void moveNext() {
        move(content.content.get(contentIndex));
        ++contentIndex;
        if (contentIndex >= content.content.size()) {
            contentIndex = 0;
        }
    }

    private void setNavigationBarVisibility(boolean visibility){
        if(visibility){
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //32.7185975,-117.1594094
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(32.7185975, -117.1594094);
        //map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //map.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(32.7185975, -117.1594094))
                .zoom(16)
                .build();

        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        map.addMarker(new MarkerOptions().position(new LatLng(32.7185975, -117.1594094)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_me)));

        map.addMarker(new MarkerOptions().position(new LatLng(32.7172267,-117.1694746)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_green)));




        map.addMarker(new MarkerOptions().position(new LatLng(32.720997, -117.1684)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_red)));

        map.addMarker(new MarkerOptions().position(new LatLng( 32.715576, -117.163895)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_red)));

        map.addMarker(new MarkerOptions().position(new LatLng(32.71135, -117.17089)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_red)));

        map.addMarker(new MarkerOptions().position(new LatLng(32.715847, -117.16481)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_red)));
        map.addMarker(new MarkerOptions().position(new LatLng(32.705833, -117.159004)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_red)));

        map.addMarker(new MarkerOptions().position(new LatLng(32.705894, -117.163315)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_red)));

        map.addMarker(new MarkerOptions().position(new LatLng( 32.719986, -117.16469)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_red)));

        map.addMarker(new MarkerOptions().position(new LatLng(32.70773, -117.162094)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_red)));
        map.addMarker(new MarkerOptions().position(new LatLng(32.719982, -117.16303)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_red)));
        map.addMarker(new MarkerOptions().position(new LatLng(32.715603,-117.16119)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_red)));

        map.addMarker(new MarkerOptions().position(new LatLng(32.715824, -117.163025)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_red)));

        map.addMarker(new MarkerOptions().position(new LatLng(32.71555, -117.165504)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_red)));
        map.addMarker(new MarkerOptions().position(new LatLng(32.715588, -117.16458)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_red)));
        map.addMarker(new MarkerOptions().position(new LatLng(32.710796,-117.16817)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_red)));
        map.addMarker(new MarkerOptions().position(new LatLng(32.71789,-117.160995)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_red)));
        map.addMarker(new MarkerOptions().position(new LatLng(32.715836,-117.163956)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_red)));











        UiSettings settings = map.getUiSettings();
        settings.setAllGesturesEnabled(true);
    }

    private void rotate(View view) {
        // Create an animation instance
        Animation an = new RotateAnimation(0.0f, 360.0f, 180, 180);

        // Set the animation's parameters
        an.setDuration(10000);               // duration in ms
        an.setRepeatCount(-1);                // -1 = infinite repeated
        an.setRepeatMode(Animation.REVERSE); // reverses each repeat
        an.setFillAfter(true);               // keep rotation after animation

        // Aply animation to image view
        view.setAnimation(an);
    }

}
