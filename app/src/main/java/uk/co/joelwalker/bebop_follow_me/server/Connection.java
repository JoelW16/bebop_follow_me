package uk.co.joelwalker.bebop_follow_me.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class connects to server and provides Json
 */
public class Connection{

    private Context context;
    private final String TAG = "Connection";
    private View mView;

    public Connection(Context context, View mView){
        this.context = context;
        this.mView = mView;
    }


    public String postRequest(String urlIN, JSONObject json){

        final String TAGP = TAG + " Post";

        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        //Check network connection
        if(isNetworkAvailable()) {
            Log.i(TAGP, "Network Available");
            try {
                //Create URL object with http params
                URL url = new URL(urlIN);
                Log.i(TAGP, "URL:" + url);

                Log.i(TAGP, "JSON:" + json.toString());


                //Create connection object
                urlConnection = (HttpURLConnection) url.openConnection();

                //Connect
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                //Post Request
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("PUT");

                OutputStream os = urlConnection.getOutputStream();
                os.write(json.toString().getBytes("UTF-8"));
                os.flush();

                //Response code
                int statusCode = urlConnection.getResponseCode();;
                //200 represents HTTP OK
                if (statusCode == 200) {
                    Log.i(TAGP, "Status Code: " + statusCode);

                    //Gets input from stream
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                    urlConnection.disconnect();

                    //Returns Json from stream
                    return convertInputStreamToString(inputStream);

                } else {
                    Log.i(TAGP, "Status Code: " + statusCode);

                    urlConnection.disconnect();

                    //Returns null if error
                    return "Error";

                }
            } catch (Exception e) {
                Log.i(TAGP, "Connection Failed");
                Log.d(TAGP, e.getLocalizedMessage());
                displayNetworkError("Server Error");
                return "Server Error";
            }
        }else{
            //Returns if no network connection
            Log.i(TAGP, "Network Error");
            displayNetworkError("Network Error");
            return "Error: No Network";
        }
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        Log.i(TAG, "Stream to JSON");
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null){
            result += line;
        }

        /* Close Stream */
        if(null!=inputStream){
            inputStream.close();
        }
        return result;
    }

    private void displayNetworkError(String error){
        Log.i(TAG, "Display Error Snackbar");

    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}

//    public String getRequest(String urlIN){
//
//        InputStream inputStream = null;
//        HttpURLConnection urlConnection = null;
//
//        //Check network connection
//        if(isNetworkAvailable()) {
//            Log.i(TAG, "Network Available");
//            try {
//                //Create URL object with http params
//                URL url = new URL(urlIN);
//                Log.i(TAG, "URL:" + url);
//
//                //Create connection object
//                urlConnection = (HttpURLConnection) url.openConnection();
//
//                //Connect
//                urlConnection.connect();
//
//                //Get Request
//                urlConnection.setRequestMethod("GET");
//
//                //Response code
//                int statusCode = urlConnection.getResponseCode();
//
//                //200 represents HTTP OK
//                if (statusCode == 200) {
//                    Log.i(TAG, "Status Code: " + statusCode);
//
//                    //Gets input from stream
//                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
//
//                    //Returns Json from stream
//                    return convertInputStreamToString(inputStream);
//
//                } else {
//                    Log.i(TAG, "Status Code: " + statusCode);
//
//                    //Returns null if error
//                    return "Error";
//
//                }
//            } catch (Exception e) {
//                Log.i(TAG, "Connection Failed");
//                Log.d(TAG, e.getLocalizedMessage());
//                displayNetworkError("Server Error");
//                return "Server Error";
//            }
//        }else{
//            //Returns if no network connection
//            Log.i(TAG, "Network Error");
//            displayNetworkError("Network Error");
//            return "Error: No Network";
//        }
//    }
