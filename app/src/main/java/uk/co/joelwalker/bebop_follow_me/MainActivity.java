package uk.co.joelwalker.bebop_follow_me;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import uk.co.joelwalker.bebop_follow_me.API.APIManager;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private TextView connection_txt, mobile_gps;

    private FrameLayout home_content, tour_content, create_content;

    private FloatingActionButton gps_fab;

    private View mainView;

    private APIManager api;

    public final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;

    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        connection_txt = (TextView) findViewById(R.id.connection_txt);
        mobile_gps = (TextView) findViewById(R.id.mob_gps_txt);

        mainView = findViewById(R.id.container);

        home_content = (FrameLayout) findViewById(R.id.home_content);
        tour_content = (FrameLayout) findViewById(R.id.tour_content);
        create_content =(FrameLayout) findViewById(R.id.create_content);

        api = new APIManager(this.getApplicationContext(), mainView, connection_txt);

        gps_fab = (FloatingActionButton) findViewById(R.id.gps_fab);

        //Get permission to use GPS Data
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }
        } else {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        gps_fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean b = api.getConnected();
                Log.d("MAIN FAB",  "connected"+ b);
                if(!b){
                    api.connectServer();
                }else{
                    api.disconnectServer();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        api.disconnectServer();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    //GPS Stuff
    @Override
    public void onLocationChanged(Location location) {
        if(api.getConnected()){
            mobile_gps.setText("Mobile GPS: " + location.getLatitude() + " "+ location.getLongitude() + " " + location.getAccuracy());
            api.sendGPS(location.getLongitude(), location.getLatitude(), location.getAccuracy());
        }else{
            mobile_gps.setText("Mobile GPS: - - ");
        }
    }
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }
    @Override
    public void onProviderEnabled(String s) {
    }
    @Override
    public void onProviderDisabled(String s) {
    }


    //Tabbed GUI Selector
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    create_content.setVisibility(View.INVISIBLE);
                    tour_content.setVisibility(View.INVISIBLE);
                    home_content.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_dashboard:
                    create_content.setVisibility(View.INVISIBLE);
                    home_content.setVisibility(View.INVISIBLE);
                    tour_content.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_notifications:
                    home_content.setVisibility(View.INVISIBLE);
                    tour_content.setVisibility(View.INVISIBLE);
                    create_content.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }
    };
}
