package com.example.mac.applicationvoyage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_image );
        Bitmap bitmap;
        ImageView imageAgrandie = (ImageView) findViewById( R.id.imageA );
        final Intent intent = getIntent();
        if(intent!=null){
        String urlWelcome= intent.getStringExtra("url");
        Log.v("l'url est ",urlWelcome);
        URL url = null;
        try {
            url = new URL(urlWelcome);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStream inputStream = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            inputStream = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap = BitmapFactory.decodeStream( inputStream );
        imageAgrandie.setImageBitmap( bitmap );
    }}
}
