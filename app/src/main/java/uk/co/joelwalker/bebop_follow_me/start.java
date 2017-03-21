package uk.co.joelwalker.bebop_follow_me;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
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

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.joelwalker.bebop_follow_me.server.Connection;
import uk.co.joelwalker.bebop_follow_me.server.Constants;

public class start extends AppCompatActivity implements LocationListener {

    private TextView connection_txt, mobile_gps;

    private FrameLayout home_content, tour_content, create_content;

    private FloatingActionButton gps_fab;

    private View mainView;

    public final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;

    LocationManager locationManager;
    boolean connected = false;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        connection_txt = (TextView) findViewById(R.id.connection_txt);
        mobile_gps = (TextView) findViewById(R.id.mob_gps_txt);


        home_content = (FrameLayout) findViewById(R.id.home_content);
        tour_content = (FrameLayout) findViewById(R.id.tour_content);
        create_content =(FrameLayout) findViewById(R.id.create_content);

        gps_fab = (FloatingActionButton) findViewById(R.id.gps_fab);

        mainView = findViewById(R.id.container);


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
                if(!connected){
                    connectServer();
                }else{
                    disconnectServer();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectServer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disconnectServer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectServer();
    }

    private void connectServer(){
        JSONObject json = new JSONObject();
        try {
            json.put("connected", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        DroneServer droneServer = new DroneServer(this.getApplicationContext());
        droneServer.execute(json);
        connected = true;
    }

    private void disconnectServer(){

        JSONObject json = new JSONObject();
        try {
            json.put("latitude", 500);
            json.put("longitude", 500);
            json.put("accuracy", 1000);
            json.put("connected", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        connected = false;
        DroneServer droneServer = new DroneServer(this.getApplicationContext());
        droneServer.execute(json);
    }

    private void sendGPS(double longitude, double latitude, float accuracy){
        JSONObject json = new JSONObject();
        try {
            json.put("latitude", latitude);
            json.put("longitude", longitude);
            json.put("accuracy", accuracy);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        DroneServer droneServer = new DroneServer(this.getApplicationContext());
        droneServer.execute(json);
    }

    @Override
    public void onLocationChanged(Location location) {
        mobile_gps.setText(location.getLatitude() + " "+ location.getLongitude() + " " + location.getAccuracy());
        location.getAccuracy();
        if(connected){
            sendGPS(location.getLongitude(), location.getLatitude(), location.getAccuracy());
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

    public class DroneServer extends AsyncTask<JSONObject, Void, String> {

        final String TAG = "API";
        Connection connection;

        DroneServer(Context context) {
            connection = new Connection(context, mainView);
        }

        @Override
        protected String doInBackground(JSONObject... params) {
            JSONObject json = params[0];
            return connection.postRequest(Constants.SERVER_URL + Constants.Android_API, json);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.i(TAG, "JSON: " + s);
            if(s.contains("Error")){
                connection_txt.setText("Status: Sever Unavailable ");
            }else{
                if(connected){
                    connection_txt.setText("Status: Connected");
                }else{
                    connection_txt.setText("Status: Disconnected");
                }
            }

        }

    }
}
