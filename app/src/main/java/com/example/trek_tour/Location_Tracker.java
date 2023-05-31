package com.example.trek_tour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Location_Tracker extends AppCompatActivity {

    FusedLocationProviderClient client;
    GoogleMap mMap;
    double currentLat=0,currentLang=0;
    public double Latitude,Longitude;
    private static final int REQUEST_CODE = 101;
    Button update;
    SupportMapFragment mapFragment;
    MarkerOptions markerOptions;
    TextView textView1,textView2,textView3;
    String fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_tracker);

        //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        client = LocationServices.getFusedLocationProviderClient(this);
        textView1=findViewById(R.id.getLoc);
        textView2=findViewById(R.id.getLoc2);
        textView3=findViewById(R.id.getLoc3);
        fullname=UserProfile.name;

        Log.d("s22","s1"+fullname);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
        else {
            getMyLocation();
            getCurrentLocation();
        }
        update=findViewById(R.id.updateLoc);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //currentLatLang(Latitude,Longitude);
                SmsManager smsManager = SmsManager.getDefault();
                //send message
                Log.d("sms","Successful"+fullname+currentLat+currentLang);
                smsManager.sendTextMessage("7738593611", null,"My Location :"+fullname+"\n"+currentLat+"\n"+currentLang, null, null);
                smsManager.sendTextMessage("8552073386", null, "My Location :"+fullname+"\n"+currentLat+"\n"+currentLang, null, null);
                Toast.makeText(Location_Tracker.this,"Sent Successfully",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void getCurrentLocation() {
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
        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location=task.getResult();
                if(location !=null){
                    try {
                        Geocoder geocoder=new Geocoder(Location_Tracker.this, Locale.getDefault());
                        List<Address> addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                        textView1.setText(Html.fromHtml(
                                "<font-color='#06200EE'<b>Latitude : </b><br></font>"+
                                        addresses.get(0).getLatitude()
                        ));
                        textView2.setText(Html.fromHtml(
                                "<font-color='#06200EE'<b>Longitude : </b><br></font>"+
                                        addresses.get(0).getLongitude()
                        ));
                        textView3.setText(Html.fromHtml(
                                "<font-color='#06200EE'<b>Address : </b><br></font>"+
                                        addresses.get(0).getAddressLine(0)
                        ));
                        Latitude=addresses.get(0).getLatitude();
                        Longitude=addresses.get(0).getLongitude();
                        Log.d("loc","Latitude+Longitude");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void getMyLocation() {
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
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    currentLat=location.getLatitude();
                    currentLang=location.getLongitude();
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            mMap=googleMap;
                            LatLng latLng=new LatLng(currentLat,currentLang);
                            markerOptions=new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(latLng).title("Current Location");
                            mMap.addMarker(markerOptions);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat,currentLang),15));
                        }
                    });
                }

            }
        });
    }
    /*private void currentLatLang(double Latitude,double Longitude) {
         FirebaseAuth auth = FirebaseAuth.getInstance();
         FirebaseUser firebaseUser = auth.getCurrentUser();
         //Enter user data into the firebase realtime database
         CurrentLocation currentLocation = new CurrentLocation();

         //Extracting user reference from database for registered users
         DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
         referenceProfile.child(firebaseUser.getUid()).push().setValue(currentLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if (task.isSuccessful()) {
                     Log.d("ss2","successful"+Latitude+Longitude);
                     Toast.makeText(Location_Tracker.this,"Successful",Toast.LENGTH_LONG).show();
                     //progressBar.setVisibility(View.GONE);
                 }
             }
         });
     }*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (REQUEST_CODE) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getMyLocation();
                }
                break;
        }

    }
}
