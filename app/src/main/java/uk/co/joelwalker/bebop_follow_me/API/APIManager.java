package uk.co.joelwalker.bebop_follow_me.API;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joel on 22/03/2017.
 */

public class APIManager implements APIResponse {

    private final String TAG = "API";

    private Context context;
    private View mainView;
    private TextView statusTxt;

    private Boolean connected = false;

    public APIManager(Context context, View mainView, TextView statusTxt){
        this.context = context;
        this.mainView = mainView;
        this.statusTxt = statusTxt;
    }

    public void connectServer(){
        Log.d(TAG, "Connect");
        JSONObject json = new JSONObject();
        try {
            json.put("connected", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        APIPut put = new APIPut(context, mainView, this);
        put.execute(json);
    }

    public void disconnectServer(){
        Log.d(TAG, "disconnect");
        JSONObject json = new JSONObject();
        try {
            json.put("latitude", 500);
            json.put("longitude", 500);
            json.put("accuracy", 1000);
            json.put("connected", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        APIPut put = new APIPut(context, mainView, this);
        put.execute(json);
    }

    public void sendGPS(double longitude, double latitude, float accuracy){
        Log.d(TAG, "Send GPS");
        JSONObject json = new JSONObject();
        try {
            json.put("latitude", latitude);
            json.put("longitude", longitude);
            json.put("accuracy", accuracy);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        APIPut put = new APIPut(context, mainView, this);
        put.execute(json);
    }

    public void sendTest(double longitude, double latitude, float accuracy){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", "Test Name");
            jsonObject.put("info", "Test info");
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
            jsonObject.put("accuracy", accuracy);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonObject);

        JSONObject jsonObjectMain= new JSONObject();
        try {
            jsonObjectMain.put("tour", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        APIPut put = new APIPut(context, mainView, this);
        put.execute(jsonObjectMain);
    }

    public boolean getConnected(){
        return connected;
    }


    @Override
    public void apiResponse(boolean connected, JSONObject res) {
        this.connected = connected;
        if(connected){
            statusTxt.setText("Status: Connected");
        }else{
            statusTxt.setText("Status: Disconnected");
        }

    }
}
