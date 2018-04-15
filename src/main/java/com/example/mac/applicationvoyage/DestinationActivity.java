package com.example.mac.applicationvoyage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class DestinationActivity extends Activity implements AbsListView.OnScrollListener, View.OnClickListener, AdapterView.OnItemClickListener {

    private static final int REQUEST_CODE = 5748;

    ListView myListView;
    DestinationAdapter destinationAdapter;
    JSONArray jArray;
    Button filterBtn;
    byte[] byteArray;


    public DestinationActivity() {

    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_destination );
        myListView = (ListView) findViewById( R.id.list_view );

        filterBtn = (Button) findViewById( R.id.button );

        String url = "http://voyage2.corellis.eu/api/v2/homev2?lat=" + Synch.latitude + "&lon=" + Synch.longitude + "&offset=0";

        StringRequest stringRequest = new StringRequest( Request.Method.GET, url,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object o) {
                        // Cast the response as a String and parse it in JSONArray
                        String response = (String) o;
                        Log.v( "DestinationActivity", "Response is: " + response.substring( 0, 500 ) );
                        JSONArray jsonArray = ParseJSON( response );
                        SetupAdapter( jsonArray );
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v( "DestinationActivity", "That didn't work!" );
            }
        } );
        Synch.getInstance( this ).addToRequestQueue( stringRequest );

    }

    public JSONArray ParseJSON(String response) {
        JSONArray jsonArray = new JSONArray();
        try {

            JSONObject jsonObject = new JSONObject( response );
            jsonArray = jsonObject.getJSONArray( "data" );
            Log.v( "DestinationActivity", jsonArray.get( 0 ).toString() );
            Log.v( "DestinationActivity", jsonArray.get( 1 ).toString() );
            Log.v( "DestinationActivity", jsonArray.get( 2 ).toString() );
        } catch (JSONException e) {
            Log.v( "DestinationActivity", "JSON Exception" );
            e.printStackTrace();
            Log.v( "DestinationActivity", e.getMessage() );
        }

        return jsonArray;
    }

    private void SetupAdapter(JSONArray jsonList) {
        jArray = jsonList;
        destinationAdapter = new DestinationAdapter( this, jsonList );
        myListView.setAdapter( destinationAdapter );
        destinationAdapter.notifyDataSetChanged();
        myListView.setOnScrollListener( this );
        filterBtn.setOnClickListener(this);
        myListView.setOnItemClickListener( this );
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        Log.v( "DestinationActivity", "tata, i = " + i + ", i1 = " + i1 + ", i2 = " + i2 );
        if (i + i1 == i2 && !Synch.loading) {
            Synch.loading = true;
            Synch.offset += 1;
            String url = "http://voyage2.corellis.eu/api/v2/homev2?lat=" + Synch.latitude + "&lon=" + Synch.longitude + "&offset=" + Synch.offset;
            Log.v( "DestinationActivity", url );

            StringRequest stringRequest = new StringRequest( Request.Method.GET, url,
                    new Response.Listener() {
                        @Override
                        public void onResponse(Object o) {
                            // Cast the response as a String and parse it in JSONArray
                            String response = (String) o;
                            Log.v( "DestinationActivity", "Response is: " + response.substring( 0, 500 ) );
                            JSONArray jsonArray = ParseJSON( response );
                            AddToAdapter( jsonArray );
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v( "DestinationActivity", "That didn't work!" );
                }
            } );
            Synch.getInstance( this ).addToRequestQueue( stringRequest );
        }
    }

    public void AddToAdapter(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jArray.put( jsonArray.get( i ) );
            } catch (JSONException e) {
                e.printStackTrace();
                Log.v( "DestinationActivity", "Error in Adding New Element (nbr " + i + ") to array" );
            }
        }
        destinationAdapter.notifyDataSetChanged();
        Synch.loading = false;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent( DestinationActivity.this, FiltrerActivity.class );
        intent.putExtra( "POI", Synch.testPOI );
        intent.putExtra( "PARCOURS", Synch.testParcours );
        intent.putExtra( "CITY", Synch.testCity );

        startActivityForResult( intent, REQUEST_CODE );
    }

    String tvType = null;
    String tvDistance = null;
    public String tvDisplay = null;
    String tvDescription=null;
    double tvLongitude;
    double tvLatitude;
    String tvMedia= null;
    String tvId =null;


    final String EXTRA_TYPE = "tvType";
    final String EXTRA_DISTANCE = "tvDistance";
    final String EXTRA_DISPLAY = "tvDisplay";
    final String EXTRA_DESCRIPTION = "tvDescription";
    final String EXTRA_LONGITUDE = "tvLongitude";
    final String EXTRA_LATITUDE = "tvLatitude";
    final String EXTRA_IMAGE= "image";
    final String EXTRA_MEDIA= "tvMedia";
    final String EXTRA_ID= "tvId";


    @Override

    public void onItemClick(AdapterView parent, View view, int position, long id) {
        JSONObject jsonObject = (JSONObject) myListView.getItemAtPosition(position);
        Intent intent = new Intent( DestinationActivity.this, DetailsActivity.class );
      try {
          tvType= jsonObject.getString("type");
          tvId= jsonObject.getString( "id" );
          tvDistance =jsonObject.getString( "distance" );
          tvDisplay =jsonObject.getString( "display" );
          tvMedia = jsonObject.getString( "media" );
          ImageView preview = (ImageView) findViewById( R.id.ListImage );
          BitmapDrawable drawable = (BitmapDrawable) preview.getDrawable();
          Bitmap bmp = drawable.getBitmap();
          ByteArrayOutputStream stream = new ByteArrayOutputStream();
          bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
          byteArray = stream.toByteArray();
          } catch (JSONException e) {
            e.printStackTrace();
        }

        intent.putExtra(EXTRA_TYPE, tvType);
        intent.putExtra(EXTRA_ID,tvId );
        intent.putExtra(EXTRA_DISTANCE, tvDistance);
        intent.putExtra(EXTRA_DISPLAY, tvDisplay);
        intent.putExtra(EXTRA_DESCRIPTION, tvDescription);
        intent.putExtra(EXTRA_MEDIA, tvMedia);
        intent.putExtra("picture", byteArray);
        intent.putExtra(EXTRA_ID,tvId );

        startActivity( intent );

        Log.v( "DestinationActivity", "toto : " + position );
        Log.v( "DestinationActivity", jsonObject.optString( "id", "null" ) );
        Log.v( "DestinationActivity", jsonObject.optString( "type", "null" ) );
        Log.v( "DestinationActivity", jsonObject.optString( "media", "null" ) );
        Log.v( "DestinationActivity", jsonObject.optString( "display", "null" ) );
        Log.v( "DestinationActivity", jsonObject.optString( "lon", "null" ) );
        Log.v( "DestinationActivity", jsonObject.optString( "lat", "null" ) );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data.hasExtra( "POI" )) {
                Synch.testPOI = data.getBooleanExtra( "POI", true );
            }
            if (data.hasExtra( "PARCOURS" )) {
                Synch.testParcours = data.getBooleanExtra( "PARCOURS", true );
            }
            if (data.hasExtra( "CITY" )) {
                Synch.testCity = data.getBooleanExtra( "CITY", true );
            }
            destinationAdapter.notifyDataSetChanged();
        }
    }
}
