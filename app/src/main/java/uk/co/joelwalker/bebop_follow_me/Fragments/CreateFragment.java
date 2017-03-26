package uk.co.joelwalker.bebop_follow_me.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.joelwalker.bebop_follow_me.API.APIManager;
import uk.co.joelwalker.bebop_follow_me.API.APIResponse;
import uk.co.joelwalker.bebop_follow_me.Point;
import uk.co.joelwalker.bebop_follow_me.R;
import uk.co.joelwalker.bebop_follow_me.ViewTourMap;

public class CreateFragment extends Fragment  implements LocationListener, APIResponse{

    private String TAG = "Create Tour";

    private Button poi_btn, waypoint_btn, view_btn;

    private EditText name_Etxt, info_Etxt;

    private TextView gps_txt;

    private CheckBox poi_swt;

    private double lng, lat;

    private APIManager api;

    private float accuracy;

    private ArrayList<Point> points;

    public final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;

    LocationManager locationManager;


    public CreateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_create, container, false);

        poi_btn = (Button) view.findViewById(R.id.poi_btn);
        waypoint_btn = (Button) view.findViewById(R.id.way_btn);
        //view_btn = (Button) view.findViewById(R.id.view_btn);

        name_Etxt = (EditText) view.findViewById(R.id.name_Etxt);
        info_Etxt = (EditText) view.findViewById(R.id.Info_Etxt);

        poi_swt = (CheckBox) view.findViewById(R.id.poit_cbx);

        gps_txt = (TextView) view.findViewById(R.id.gps_txt);

        lat = 500;
        lng = 500;
        accuracy = 1000;

        points = new ArrayList<Point>();

        View mainView = getActivity().findViewById(R.id.main_container);

        api = new APIManager(getActivity(), mainView, this);

        poi_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                if(validate()){
                    String name = name_Etxt.getText().toString();
                    String info = info_Etxt.getText().toString();

                    boolean poi = poi_swt.isChecked();

                    Point point = new Point(name, info,lng,lat,accuracy, poi);

                    points.add(point);

                    name_Etxt.setText("");
                    info_Etxt.setText("");
                    Toast toast = Toast.makeText(getActivity(), "POI added", Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });

        waypoint_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                Intent intent = new Intent(getActivity().getApplicationContext(), ViewTourMap.class);
                intent.putExtra("points", points);
                startActivity(intent);

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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
        }

        return view;
    }

    private boolean validate(){
        boolean valid = true;

        if(lng == 500 || lat == 500 ){
            Log.d(TAG, "No GPS");
            valid = false;
        }else if(accuracy > 1000.0){
            Log.d(TAG, "GPS Not Accurate");
            valid = false;
        }else if(name_Etxt.getText().toString().equals("")){
            Log.d(TAG, "No Name");
            valid = false;
        }



        return valid;
    }

    @Override
    public void onLocationChanged(Location location) {
        lng = location.getLongitude();
        lat = location.getLatitude();
        accuracy = location.getAccuracy();
        gps_txt.setText("GPS: " + location.getLatitude() + ", "+ location.getLongitude() + "  Accuracy: " + location.getAccuracy() + "m");

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

    }
}
