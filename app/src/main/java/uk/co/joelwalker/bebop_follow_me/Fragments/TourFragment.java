package uk.co.joelwalker.bebop_follow_me.Fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import uk.co.joelwalker.bebop_follow_me.API.APIManager;
import uk.co.joelwalker.bebop_follow_me.API.APIResponse;
import uk.co.joelwalker.bebop_follow_me.R;


public class TourFragment extends Fragment implements LocationListener, APIResponse {

    private FloatingActionButton gps_fab;
    private APIManager api;

    private Button btn_takeoff, btn_land, btn_emg;

    private View mainView;
    private TextView connection_txt, mobile_gps, currentPos_txt;

    private boolean connected = false;

    public final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;

    LocationManager locationManager;

    public TourFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("Tour", "On Create View");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tour, container, false);

        mainView = getActivity().findViewById(R.id.main_container);

        btn_takeoff = (Button) view.findViewById(R.id.btn_takeoff);
        btn_land = (Button) view.findViewById(R.id.btn_land);
        btn_emg = (Button)  view.findViewById(R.id.btn_EMG);

        connection_txt = (TextView) view.findViewById(R.id.connection_txt);
        mobile_gps = (TextView) view.findViewById(R.id.mob_gps_txt);
        currentPos_txt = (TextView) view.findViewById(R.id.pos_txt);

        api = new APIManager(getActivity(), mainView, this);


        gps_fab = (FloatingActionButton) view.findViewById(R.id.gps_fab);

        gps_fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean b = api.getConnected();
                if(!b){
                    api.connectServer();
                }else{
                    connected = false;
                    api.disconnectServer();
                }

            }
        });

        btn_takeoff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                api.droneTakeoff();
            }
        });

        btn_land.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                api.droneLand();
            }
        });

        btn_emg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                api.droneEmgLand();
            }
        });


        //Get permission to use GPS Data
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }
        } else {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        return view;
    }


    //GPS Stuff
    @Override
    public void onLocationChanged(Location location) {
        if(connected){
            mobile_gps.setText("Mobile GPS: " + location.getLatitude() + " "+ location.getLongitude() + " " + location.getAccuracy());

            if(location.getAccuracy() <= 300){
                api.sendGPS(location.getLongitude(), location.getLatitude(), location.getAccuracy());
                mobile_gps.setTextColor(Color.GREEN);
            }else{
                mobile_gps.setTextColor(Color.RED);
            }
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

    @Override
    public void apiResponse(JSONObject res) {
        if(api.getConnected()){
            connection_txt.setText("Status: Connected");
            connected= true;

        }else{
            connected = false;
            connection_txt.setText("Status: disconnected");
        }


//        try {
//            String pos = res.getString("pos");
//            currentPos_txt.setText("Current Node: " + pos);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }
}
