package com.example.mac.applicationvoyage;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DetailsActivity extends FragmentActivity implements OnMapReadyCallback {
    URL urlWelcome;
    String Url;
    private GoogleMap GMap;
    private LatLng yaounde = new LatLng( 3.881501, 11.50818 );
    final String EXTRA_DISTANCE = "tvDistance";
    final String EXTRA_TYPE = "tvType";
    String type;
    String id;
    JSONObject jsonObject;
    JSONObject jsonO;
    TextView Title;
    TextView Distance;
    String result = null;
    ImageView selectedImage;
    Gallery gallery;
    public URL[] url;
    public JSONObject[] json = null;
    public Bitmap[] image;
    JSONArray jArray;
    RatingBar starss;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_details );
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy( policy );
        try {
            Title = (TextView) findViewById( R.id.Display );
            Distance = (TextView) findViewById( R.id.Distance );
            gallery = (Gallery) findViewById( R.id.gallery );
            selectedImage = (ImageView) findViewById( R.id.image );
            starss= (RatingBar)findViewById( R.id.starss);
            final TextView description = (TextView) findViewById( R.id.Description );
            final Intent intent = getIntent();
            URLConnection connection;
            if (intent != null) {
                id = intent.getStringExtra( "tvId" );
                type = intent.getStringExtra( EXTRA_TYPE );
                if (type.equals( "POI" )) {  //http://voyage2.corellis.eu/api/v2/poi?id=FRA13006A8
                    Url = "http://voyage2.corellis.eu/api/v2/poi?id=" + id;
                } else if (type.equals( "PARCOURS" )) {
                    Url = "http://voyage2.corellis.eu/api/v2/parcours?id=" + id;
                } else if (type.equals( "CITY" )) {
                    Url = "http://voyage2.corellis.eu/api/v2/destination?id=" + id;
                }
                URL urll = null;
                try {
                    urll = new URL( Url );
                    Log.v( "DetailsActivity", "l'url est " + urll.toString() );
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                connection = urll.openConnection();
                connection.setDoInput( true );

                InputStream is = null;
                try {
                    is = connection.getInputStream();
                } catch (IOException ioe) {
                    if (connection instanceof HttpURLConnection) {
                        HttpURLConnection httpConn = (HttpURLConnection) connection;
                        int statusCode = httpConn.getResponseCode();
                        if (statusCode != 200) {
                            is = httpConn.getErrorStream();
                        }
                    }
                }
                result = InputStreamOperations.InputStreamToString( is );

                jsonObject = new JSONObject( result );
                try {
                    if (type.equals( "POI" )) {
                        JSONArray jsonArray = jsonObject.getJSONArray( "data" );
                        jsonO = (JSONObject) jsonArray.get( 0 );
                        jArray = jsonO.getJSONArray( "medias" );
                        Log.v( "DetailsActivity", "Le jArray est " + jArray.getJSONObject( 0 ).getString( "url" ) );
                        Log.v( "DetailsActivity", "La longueur du jArray est " + jArray.length() );


                        Title.setText( jsonO.getString( "name" ) );
                        starss.setRating( jsonO.getInt( "stars" ) );

                        description.setText( jsonO.getString( "description" ) );
                    } else if (type.equals( "PARCOURS" )) {
                        JSONArray jsonArray = jsonObject.getJSONArray( "data" );
                        jsonO = (JSONObject) jsonArray.get( 0 );
                        jArray = jsonO.getJSONArray( "medias" );
                        starss.setRating( jsonO.getInt( "stars" ) );

                        Title.setText( jsonO.getString( "title" ) );
                        description.setText( jsonO.getString( "description" ) );
                    } else if (type.equals( "CITY" )) {
                        JSONObject o = jsonObject.getJSONObject( "data" );
                        Title.setText( o.getString( "name" ) );
                        description.setText( o.getString( "description" ) );
                        jArray = o.getJSONArray( "medias" );
                        starss.setRating( o.getInt( "stars" ) );


                    }

                    Distance.setText( "A " + intent.getStringExtra( EXTRA_DISTANCE ) + " Km" );
                    gallery.setSpacing( 1 );


                    Log.v( "DetailsActivity", "La longueur du jArray est " + jArray.length() );
                    try {
                        image = new Bitmap[jArray.length()];
                        url = new URL[jArray.length()];
                        json = new JSONObject[jArray.length()];


                        for (int j = 0; j < jArray.length(); j++) {

                            json[j] = (JSONObject) jArray.get( j );
                            Log.v( "DetailsActivity", "l'object " + j + " est : " + json[j].toString() );

                            url[j] = new URL( json[j].getString( "url" ) );
                            Log.v( "DetailsActivity", "l'url de l'image est " + url[j].toString() );

                            image[j] = BitmapFactory.decodeStream( url[j].openConnection().getInputStream() );
                            Log.v( "DetailsActivity", "l'image est bien concue" );
                        }

                        //on s'assure que l'objet map n'est pas vide
                        if (GMap != null) {
                            //mode d'affichage de la carte
                            GMap.setTrafficEnabled( true );
                            //on autorise l'api à afficher le bouton pour accéder à notre position courante
                            if (ActivityCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            GMap.setMyLocationEnabled( true );

                            //définition du marqueur qui va se positionner sur le point qu'on désire afficher
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.title("YAOUNDE");
                            markerOptions.visible(true);
                            markerOptions.position(yaounde);
                            markerOptions.icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

                            //ajout du marqueur sur la carte
                            GMap.addMarker(markerOptions);
                            //zoom de la caméra sur la position qu'on désire afficher
                            GMap.moveCamera( CameraUpdateFactory.newLatLngZoom(yaounde, 16));
                            //animation le zoom toute les 2000ms
                            GMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    final GalleryImageAdapter galleryImageAdapter = new GalleryImageAdapter( this, image );
                    Log.v( "DetailsActivity", "ca va s'afficher " );
                    gallery.setAdapter( galleryImageAdapter );

                    Log.v( "DetailsActivity", "ca va s'afficher " );

                    gallery.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                            // show the selected Image
                            Intent intent = new Intent( DetailsActivity.this, ImageActivity.class );
                            intent.putExtra( "url",url[position].toString() );
                            startActivity( intent );
                        }
                    } );

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if ((firstVisibleItem + visibleItemCount) >= totalItemCount) {

                //here start next 10 items request
            }


        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}






