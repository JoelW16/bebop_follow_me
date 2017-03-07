package uk.co.joelwalker.bebop_follow_me;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.joelwalker.bebop_follow_me.server.Connection;
import uk.co.joelwalker.bebop_follow_me.server.Constants;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private View mainView;
    TextView mobile_GPS_Text, other_text;
    public final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    LocationManager locationManager;
    boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainView = findViewById(R.id.content_view);

        Log.i("GPS", "INIT");
        mobile_GPS_Text = (TextView) findViewById(R.id.mob_gps_text);
        other_text = (TextView) findViewById(R.id.drone_gps_text);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
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
    }

    @Override
    protected void onStart() {
        super.onStop();
        connectServer();
    }

    @Override
    protected void onDestroy() {
        super.onStop();
        disconnectServer();
    }

    @Override
    protected void onPause() {
        super.onStop();
        disconnectServer();
    }

    @Override
    protected void onResume() {
        super.onStop();
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

        DroneServer droneServer = new DroneServer(this.getApplicationContext());
        droneServer.execute(json);
        connected = false;
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
        mobile_GPS_Text.setText(location.getLatitude() + " "+ location.getLongitude() + " " + location.getAccuracy());
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
                other_text.setText("Error");
            }else{
                other_text.setText("Connected");
            }

        }

    }


}
