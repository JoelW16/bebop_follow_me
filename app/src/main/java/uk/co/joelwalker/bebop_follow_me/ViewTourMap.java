package uk.co.joelwalker.bebop_follow_me;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.joelwalker.bebop_follow_me.API.APIManager;
import uk.co.joelwalker.bebop_follow_me.API.APIResponse;

public class ViewTourMap extends AppCompatActivity implements APIResponse, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    private Context context;
    private ArrayList<Point> points;

    private APIManager api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tour_map);

        View mainview = findViewById(R.id.submit_tour);

        api = new APIManager(this, mainview, this);

        points =  (ArrayList<Point>) getIntent().getSerializableExtra("points");

        context = this.getApplicationContext();

        Button submitt = (Button) findViewById(R.id.submit_tour);

        submitt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                api.sendPoints(points);

                Toast toast = Toast.makeText(context, "Uploaded", Toast.LENGTH_LONG);
                toast.show();


            }
        });
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        PolylineOptions line = new PolylineOptions();
        line.width(5).color(Color.RED);


        for(Point p : points){
            LatLng ll = new LatLng(p.getLat(), p.getLng());
            line.add(ll);
            Log.d("MAPS", ""+p.getLng());
            if(!p.isPoi()){
                mMap.addMarker(new MarkerOptions()
                        .position(ll)
                        .snippet(p.getInfo())
                        .title(p.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                );
            }else{
                mMap.addMarker(new MarkerOptions()
                        .position(ll)
                        .snippet(p.getInfo())
                        .title(p.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                );
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 18));
        }


        mMap.setOnMarkerClickListener(this);
        mMap.addPolyline(line);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }


    @Override
    public void apiResponse(JSONObject res) {

    }
}
