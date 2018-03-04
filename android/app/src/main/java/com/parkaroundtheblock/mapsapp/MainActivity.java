package com.parkaroundtheblock.mapsapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
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

import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView mTextMessage;
    private GoogleMap map;
    private View park;
    private View pay;
    private View xyo1;
    private View xyo2;
    private View splash;
    private BottomNavigationView bottomNavigationView;
    private Button parkButton;

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
                    park.setVisibility(View.GONE);
                    pay.setVisibility(View.VISIBLE);
                    mTextMessage.setText(R.string.title_pay);
                    return true;
            }
            return false;
        }
    };

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
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(32.7172267,-117.1694746))
                        .zoom(16)
                        .build();

                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
                .zoom(15)
                .build();

        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        map.addMarker(new MarkerOptions().position(new LatLng(32.7185975, -117.1594094)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_green)));

        map.addMarker(new MarkerOptions().position(new LatLng(32.7172267,-117.1694746)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_green)));


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
