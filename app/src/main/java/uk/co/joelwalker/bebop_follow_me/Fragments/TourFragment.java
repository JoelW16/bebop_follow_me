package layout;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.co.joelwalker.bebop_follow_me.*;
import uk.co.joelwalker.bebop_follow_me.API.APIManager;


public class TourFragment extends Fragment implements LocationListener {

    private FloatingActionButton gps_fab;
    private APIManager api;

    private View mainView;
    private TextView connection_txt, mobile_gps;


    public final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;

    LocationManager locationManager;

    public TourFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tour, container, false);

        mainView = getActivity().findViewById(R.id.main_container);

        connection_txt = (TextView) view.findViewById(R.id.connection_txt);
        mobile_gps = (TextView) view.findViewById(R.id.mob_gps_txt);

        api = new APIManager(getActivity(), mainView);


        gps_fab = (FloatingActionButton) view.findViewById(R.id.gps_fab);

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
}
