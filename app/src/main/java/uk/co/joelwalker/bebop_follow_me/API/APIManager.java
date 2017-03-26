package uk.co.joelwalker.bebop_follow_me.API;

import android.content.Context;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import uk.co.joelwalker.bebop_follow_me.Point;
import uk.co.joelwalker.bebop_follow_me.server.Constants;

/**
 * Created by Joel on 22/03/2017.
 */

public class APIManager implements APIResponse {

    private final String TAG = "API";

    private Context context;
    private View mainView;

    private APIResponse apires;
    private Boolean connected = false;

    public APIManager(Context context, View mainView, APIResponse apires){
        this.context = context;
        this.mainView = mainView;

        this.apires = apires;
    }

    public void connectServer(){
        Log.d(TAG, "Connect");
        JSONObject json = new JSONObject();
        try {
            json.put("connected", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        APIPut put = new APIPut(context, mainView, this, Constants.Android_API);
        put.execute(json);
    }

    public Boolean disconnectServer(){
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
        APIPut put = new APIPut(context, mainView, this, Constants.Android_API);
        String res = null;
        try {
            res =  put.execute(json).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return res != null;
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

        APIPut put = new APIPut(context, mainView, this, Constants.Android_API);
        put.execute(json);
    }

    public void sendPoint(double longitude, double latitude, float accuracy){
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

        APIPut put = new APIPut(context, mainView, this, Constants.Android_TOUR);
        put.execute(jsonObjectMain);
    }

    public void sendPoints(ArrayList<Point> points){

        JSONArray jsonArray = new JSONArray();
        for (Point p :points){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("locationRef", points.indexOf(p));
                jsonObject.put("name", p.getName());
                jsonObject.put("info", p.getInfo());
                jsonObject.put("latitude", p.getLat());
                jsonObject.put("longitude", p.getLng());
                jsonObject.put("accuracy", p.getAccuracy());
                jsonObject.put("poi", p.isPoi());

                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject jsonObjectMain= new JSONObject();
        try {
            jsonObjectMain.put("tour", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        APIPut put = new APIPut(context, mainView, this, Constants.Android_TOUR);
        put.execute(jsonObjectMain);
    }

    public boolean getConnected(){
        return connected;
    }


    @Override
    public void apiResponse(JSONObject res) {
        if(res.has("connected")){
            try {
                connected = (Boolean) res.get("connected");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        apires.apiResponse(res);
    }
}
