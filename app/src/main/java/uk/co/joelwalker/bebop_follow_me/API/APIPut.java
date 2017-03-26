package uk.co.joelwalker.bebop_follow_me.API;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.joelwalker.bebop_follow_me.server.Connection;
import uk.co.joelwalker.bebop_follow_me.server.Constants;

/**
 * Created by Joel on 22/03/2017.
 */

public class APIPut extends AsyncTask<JSONObject, Void, String> {


    private APIResponse responce;

    final String TAG = "API Put";

    private String apiAdd;

    Boolean connected = false;

    Connection connection;

    APIPut(Context context, View mainView, APIResponse apiResponse, String apiAdd) {
        connection = new Connection(context, mainView);
        responce = apiResponse;
        this.apiAdd = apiAdd;
    }

    @Override
    protected String doInBackground(JSONObject... params) {
        JSONObject json = params[0];
        return connection.postRequest(Constants.SERVER_URL + apiAdd, json);
    }

    @Override
    protected void onPostExecute(String s) {
        Log.i(TAG, "JSON RES: " + s);

        try {
            JSONObject jsonRes = new JSONObject(s);
            responce.apiResponse(jsonRes);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
