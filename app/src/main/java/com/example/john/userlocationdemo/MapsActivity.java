package com.example.john.userlocationdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    //public final class GoogleMap extends Object
    //   -You cannot instantiate a GoogleMap object directly, rather, you must obtain one from the
    //    getMapAsync() method on a MapFragment or MapView that you have added to your application.
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);
        //set what the map will look like, normal view, satellite view, etc
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Add a marker in Sydney and move the camera
                setLocation(location);
                //Geocoder - A class for handling geocoding and reverse geocoding. Geocoding is the process of transforming a
                //   street address or other description of a location into a (latitude, longitude) coordinate. Reverse geocoding
                //   is the process of transforming a (latitude, longitude) coordinate into a (partial) address.
                //Locale class - A Locale object represents a specific geographical, political, or cultural region.
                //   - basically different locations(Countries) have different ways to display their addresses
                //   - Locale.getDefault() will get the locale of the current location
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());//try Locale.Canada after
                try {
                    //List<Address> getFromLocation (double latitude, double longitude, int maxResults)
                    //   -Returns an array of Addresses that are known to describe the area immediately
                    //   surrounding the given latitude and longitude.
                    List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if(addressList != null && addressList.size() > 0){//check if it's null first, then check its size
                        //the getter methods of Address class will contain a String or null. The String of what you're trying to get.
                        //feature name is street number & thoroughfare is street name
                        String address = addressList.get(0).getFeatureName() + " "+ addressList.get(0).getThoroughfare();
                        Toast.makeText(getApplicationContext(),"Address: "+address, Toast.LENGTH_LONG).show();
                        MainActivity.currentAddress = address;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if(Build.VERSION.SDK_INT < 23){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, locationListener);
        }else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                setLocation(lastKnownLocation);
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //permission was granted, so now we can start listening for user location using LocationManager
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, locationListener);
            }
        }
    }
    //public class Location extends Object implements Parcelable
    //    -A data class representing a geographic location.
    //    -A location can consist of a latitude, longitude, timestamp, and other
    //    information such as bearing, altitude and velocity.
    private void setLocation(Location location){
        //public final class LatLng - An immutable class representing a pair of latitude
        //    and longitude coordinates, stored as degrees.
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //remove all the Markers on the map
        mMap.clear();
        //Marker class - An icon placed at a particular point on the map's surface.
        //   -.addMarker() - add a new Marker object on the GoogleMap object map
        //   -.position(new LatLng(...)) - set the position of the marker with a LatLng obj
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker at where you are")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        //public final class CameraUpdateFactory extends Object
        //    -A class containing methods for creating CameraUpdate objects that change a map's camera.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,20));//zoom lvl between 1 to 20
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).title("Saved location!")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
    }
}
