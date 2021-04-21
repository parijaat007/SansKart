package com.example.dubstep;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    public LatLng pos;
    private FusedLocationProviderClient fusedLocationClient;

    private DatabaseReference fromReference;
    private DatabaseReference toReference;
    private String PhoneNumber;
    private String uid;
    private String cartTotalAmt;
    private Button SendButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        PhoneNumber = getIntent().getStringExtra("PhoneNumber");
        uid = getIntent().getStringExtra("UID");
        cartTotalAmt = getIntent().getStringExtra("cartTotal");

        //Toast.makeText(MapsActivity.this,"Cart Total"+cartTotalAmt,Toast.LENGTH_SHORT).show();

        fromReference = FirebaseDatabase.getInstance().getReference("Cart").child(uid);
        toReference = FirebaseDatabase.getInstance().getReference("Orders");
        SendButton = findViewById(R.id.button_send);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerDragListener(this);
        getLastKnownLocation();
        Button button = (Button) findViewById(R.id.button_send);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click

                createOrder(pos.latitude, pos.longitude, toReference);

                //addUserLocation(pos.latitude,pos.longitude,toReference);
            }
        });
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        fusedLocationClient.getLastLocation().addOnCompleteListener((new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    pos = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(pos).draggable(true).title("Current Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }
            }
        }));
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        pos = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
    }

    private void createOrder(final double lat, final double longt, final DatabaseReference OrderNode){
        final HashMap<String,Object> order = new HashMap<>();

        order.put("CartTotalAmount",cartTotalAmt);
        order.put("Customer_UID",uid);
        order.put("Phone_Number",PhoneNumber);
        order.put("Latitude",String.valueOf(lat));
        order.put("Longitude",String.valueOf(longt));
        order.put("Status","0");
        order.put("Distance","NA");

        OrderNode.child(uid).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    //Toast.makeText(MapsActivity.this,"Amount:"+order.get("CartTotalAmount"),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MapsActivity.this,Order_placed.class);
                    intent.putExtra("TotalAmount",order.get("CartTotalAmount").toString());
                    intent.putExtra("UID",uid);
                    startActivity(intent);
                }else
                    Toast.makeText(MapsActivity.this,"Error.",Toast.LENGTH_SHORT).show();
            }
        });
    }
}