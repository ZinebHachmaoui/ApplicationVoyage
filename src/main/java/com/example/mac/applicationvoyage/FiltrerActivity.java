package com.example.mac.applicationvoyage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;


/**
 * Created by mac on 20/01/2017.
 */


public class FiltrerActivity extends Activity implements View.OnClickListener {

    CheckBox Poi;
    CheckBox Parcours;
    CheckBox City;
    Button appliquerButton;
    Button DestinationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_filtrer );
        Poi = (CheckBox) findViewById( R.id.checkBoxPOI );
        Parcours = (CheckBox) findViewById( R.id.checkBoxParcours );
        City = (CheckBox) findViewById( R.id.checkBoxCity );
        appliquerButton = (Button) findViewById( R.id.buttonAppliquer );
        DestinationButton = (Button) findViewById( R.id.buttonDestination );

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Boolean poi = extras.getBoolean( "POI", true );
            Boolean parcours = extras.getBoolean( "PARCOURS", true );
            Boolean city = extras.getBoolean( "CITY", true );


            Poi.setChecked( poi );
            Parcours.setChecked( parcours );
            City.setChecked( city );


        }

        appliquerButton.setOnClickListener( this );
        DestinationButton.setOnClickListener( myhandler );
    }
        View.OnClickListener myhandler = new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent( FiltrerActivity.this, DestinationActivity.class );
                startActivity( intent );
            }



    };

    @Override
    public void onClick(View v) {
        Intent data = new Intent();
        data.putExtra("POI", Poi.isChecked());
        data.putExtra("PARCOURS", Parcours.isChecked());
        data.putExtra("CITY", City.isChecked());
        setResult(RESULT_OK, data);
        finish();
    }
}
