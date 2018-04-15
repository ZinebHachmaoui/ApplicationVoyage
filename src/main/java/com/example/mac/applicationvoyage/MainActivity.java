package com.example.mac.applicationvoyage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by mac on 18/02/2017.
 */


/**
 * La premiere activité
 *Il detecte la position de l'utilisateur a l'aide du GPS*/

public class MainActivity extends Activity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private double locationLatitude;
    private double locationLongitude;
    private TextView succée;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        succée = (TextView) findViewById( R.id.ChargementTextView );

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.v( "MainActivity", "GPS, location changed" );
                locationLatitude = location.getLatitude();
                locationLongitude = location.getLongitude();
                Log.v( "MainActivity", "Longitude : " + locationLongitude );
                Log.v( "MainActivity", "Latitude : " + locationLatitude );
                succée.setText( R.string.position_trouvée );
                locationManager.removeUpdates( locationListener );
                searchJSON( locationLatitude, locationLongitude );
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.v( "MainActivity", "GPS status changed" );
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.v( "MainActivity", "GPS enabled" );

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
                startActivity( intent );
                Log.v( "MainActivity", "GPS disabled" );
            }
        };
        /* GPSRequest();*/
        //to test without using GPS position
        locationLatitude = 43.3;
        locationLongitude = 5.4;
        searchJSON( locationLatitude, locationLongitude );
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                    locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
                    Log.v("MainActivity", "onRequestPermissionResult");
                }
                break;
        }
    }

    public void GPSRequest() {
        Log.v("MainActivity", "Starting");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);

                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        } else {
            locationManager.requestLocationUpdates("gps", 0, 0, locationListener);
        }
        Log.v("MainActivity", "Ending");
    }
    public void searchJSON(double locationLatitude, double locationLongitude) {
        // Define the URL associated with the location
        String url = "http://voyage2.corellis.eu/api/v2/homev2?lat=" + locationLatitude + "&lon=" + locationLongitude + "&offset=0";
        Log.v("MainActivity", url);

        Synch.latitude = locationLatitude;
        Synch.longitude = locationLongitude;
        Synch.offset = 0;

        // Instantiate the RequestQueue.
        RequestQueue queue = Synch.getInstance(this.getApplicationContext()).
                getRequestQueue();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object o) {
                        Intent intent = new Intent(MainActivity.this, DestinationActivity.class);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("MainActivity", "That didn't work!");
                succée.setText(R.string.volley_exception);
            }
        });
        // Add the request to the RequestQueue.
        Synch.getInstance(this).addToRequestQueue(stringRequest);
    }
}
