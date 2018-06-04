package com.example.john.userlocationdemo;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    static String currentAddress = "No Address";
    TextView textView;
    LocationManager locationManager;
    LocationListener locationListener;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button locBtn = findViewById(R.id.locationBtn);
        locBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
        textView = findViewById(R.id.addressTextView);
        textView.setText(currentAddress);
        //LocationManager -This class provides access to the system location services.
        //instances of this class must be obtained using Context.getSystemService(String)
        // with the argument Context.LOCATION_SERVICE.
        //Object getSystemService (String name) - Return the handle to a system-level service by name.
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //-LocationListener Used for receiving notifications from the LocationManager when the location has changed.
        //  These methods are called if the LocationListener has been registered with the location manager service
        //  using the requestLocationUpdates(String, long, float, LocationListener) method.
        locationListener = new LocationListener() {
            //onLocationChanged-Called when the location has changed.
            //Parameters: location - The new location, as a Location object.
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Location", "Lat: "+location.getLatitude()+" Long: "+location.getLongitude()+" LOCATION: "+location.toString());
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
        //Android versions before MarshMallow(23) would ask for permissions when downloading the app
        if(Build.VERSION.SDK_INT < 23){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }else {
            //-int checkSelfPermission (Context context, String permission)
            //-Determine whether you have been granted a particular permission.
            //-Returns - PERMISSION_GRANTED if you have the permission, or PERMISSION_DENIED if not.
            //PERMISSION_GRANTED is a constant from the PackageManager class
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //-if we don't have permission, ask for it
                //-ActivityCompat is a Helper class for accessing features in Activity.
                //-void requestPermissions (Activity activity, String[] permissions, int requestCode)
                //-Requests permissions(could be one or more, hence why it's an array) to be granted to this application.
                //-If your app does not have the requested permissions the user will be presented with UI for accepting them.
                //  After the user has accepted or rejected the requested permissions you will receive a callback reporting
                //  whether the permissions were granted or not. The results of permission requests will be delivered to
                //  onRequestPermissionsResult(int, String[], int[]) method from the nested interface class
                //  ActivityCompat.OnRequestPermissionsResultCallback
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                //-register the listener with the location manager service and ask for location update within minTime & minDistance
                //-register the current activity to be updated periodically by the named provider,
                //  -named provided is the name of the provider that will determine/give the GPS location
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        textView.setText(currentAddress);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //check if user has given us permission
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //permission was granted, so now we can start listening for user location using LocationManager
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }
}
