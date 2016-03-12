package com.example.jas.represent;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private EditText zipCode;
    private TextView title;
    private Button location;
    private Button go;

    private GoogleApiClient locGoogleApiClient;

    private static final String SUNLIGHT_API_Key = "fea5470fewf41d9123667ae";

    private Location mLastLocation;

    private String countyName = "NONE";
    private String stateAbrv = "N/A";
    private double obamaNum = -1;
    private double romneyNum = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (locGoogleApiClient == null) {
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            locGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(AppIndex.API).build();
        }



        zipCode = (EditText) findViewById(R.id.etxtZip);
        title = (TextView) findViewById(R.id.lblTitle);
        location = (Button) findViewById(R.id.btnLocation);
        go = (Button) findViewById(R.id.btnGo);

        zipCode.getLayoutParams().width = location.getLayoutParams().width;
        go.setVisibility(View.INVISIBLE);

        zipCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ViewGroup.LayoutParams params = zipCode.getLayoutParams();
                if (s.length() != 5) {
                    go.setVisibility(View.INVISIBLE);
                    params.width = location.getLayoutParams().width;
                } else {
                    go.setVisibility(View.VISIBLE);
                    params.width = location.getLayoutParams().width - go.getLayoutParams().width - 30;
                }
                zipCode.setLayoutParams(params);
            }
        });

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
//            Log.d("Phone listening", "Phone Listener works");
            String action = extras.getString("action");
            Log.d("Mobile main - 1", "key = " + action);

            if (action != null && action.equals("shake")) {
                Log.d("Mobile main - 2", "shake works");
                Random r = new Random();

                double lat =32.94 + (41.39 -32.94)*r.nextDouble();
                double lon =-117.1 + (-80.42 +117.1)*r.nextDouble();
                getPoliticians(0,lat,lon);
            }
        }

    }

    public void zipGo(View view){
        String zip = zipCode.getText().toString();
        if (zip.length() == 5){
            int z = Integer.parseInt(zip);


            //DO STUFF HERE

            String urlPath = "https://maps.googleapis.com/maps/api/geocode/json?address="+zip;
            Log.d("Mobile main zip->county", "Final URL=" + urlPath);

            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                DownloadWebpageTask dwt = new DownloadWebpageTask();
                dwt.mode = 1;
                dwt.execute(urlPath);
            } else {
                Log.d("Mobile Main", "No network connection available.");
            }


//            findPoliticians(view, z);
            getPoliticians(z, 0, 0);
        }
    }

    public void getLocation(View view){

        String urlPath = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+mLastLocation.getLatitude()+","+ mLastLocation.getLongitude();
        Log.d("Mobile main loc->county", "Final URL=" + urlPath);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            DownloadWebpageTask dwt = new DownloadWebpageTask();
            dwt.mode = 1;
            dwt.execute(urlPath);
        } else {
            Log.d("Mobile Main", "No network connection available.");
        }



        getPoliticians(0, mLastLocation.getLatitude(), mLastLocation.getLongitude());
//        findPoliticians(view, 94720); // This needs to be changed when implementing APIS
    }

    // Is called to advance to the next Activity and send data to Watch
    public void advance(){

        if (CongressionalActivity.politicians.size() == 0) {
            zipCode.setText("");
            zipCode.setHint("Enter Another Zip Code");
            return;
        }

        //send Politician data to watch here
        Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        sendIntent.putExtra("ZIP", CongressionalActivity.politicians.size());
        sendIntent.putExtra("COUNTY", countyName);
        sendIntent.putExtra("STATE", stateAbrv);

        get2012Results();

        sendIntent.putExtra("OBAMA", obamaNum);
        sendIntent.putExtra("ROMNEY", romneyNum);

        startService(sendIntent);

        //next screen
        Intent next = new Intent(getBaseContext(), CongressionalActivity.class); //temporary
        next.putExtra("pSet", 0);
        startActivity(next);
    }

    void get2012Results(){
        try {
            InputStream stream = getAssets().open("newelectioncounty2012.json");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            String str = new String(buffer, "UTF-8");

            JSONObject data = new JSONObject(str);
            String k = countyName + ", " + stateAbrv;
            JSONObject res = data.getJSONObject(k);

            if (res == null) {
                obamaNum = -1;
                romneyNum = -1;
            } else {
                romneyNum = res.getDouble("romney");
                obamaNum = res.getDouble("obama");
            }

        } catch (IOException ex) {
            Log.d("Mobile MAIN 2012", "IOErr =" + ex.getMessage());
        } catch (JSONException ex) {
            Log.d("Mobile MAIN 2012", "JSONErr =" + ex.getMessage());
        }
    }

    void addPolitician(String _id, String _name, String _position, String _party, String _tweetID, String _email,
                       String _website) {
        Politician pol = new Politician(_id, _name,  _position,  _party,  _tweetID,
                _email,  _website);
        CongressionalActivity.politicians.add(pol);
    }

    // For the Sunlight API

    // If using lat and lon, zip = 0, and if using zip, lat and lon = 0
    private void getPoliticians(int zip, double lat, double lon) {
        CongressionalActivity.politicians.clear();

        String geodata = "";
        if (lat == 0 && lon == 0) {
            geodata = "zip=" + zip;
        } else {
            geodata = "latitude=" + lat + "&longitude=" + lon;
        }
        Log.d("Mobile main Sunlight", "locate string = " + geodata);

        String urlPath = "http://congress.api.sunlightfoundation.com/legislators/locate?" + geodata + "&apikey=" + SUNLIGHT_API_Key;
//        Log.d("Mobile main Sunlight", "Final URL=" + urlPath);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            DownloadWebpageTask dwt = new DownloadWebpageTask();
            dwt.mode = 0;
            dwt.execute(urlPath);
        } else {
            Log.d("Mobile Main", "No network connection available.");
        }

    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        // 0 means get Politicians from Sunlight, 1 means
        public int mode;

        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }


        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("Mobile main URL", "" + result);

            try {

                if (mode == 0) {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jArray = jsonObject.getJSONArray("results");

                    for (int i=0; i < jArray.length(); i++) {
                        JSONObject currPol = jArray.getJSONObject(i);

                        // Str _id, Str _name, Str _position, Str _party, Str _tweetID, Str _email, Str _website

                        String name =  currPol.getString("first_name") + " " + currPol.getString("last_name");

                        Log.d("Mobile main Sunlight","Making a politician " + name + " title " + currPol.getString("title"));

                        addPolitician(currPol.getString("bioguide_id"), name, currPol.getString("title"), currPol.getString("party"),
                                currPol.getString("twitter_id"), currPol.getString("oc_email"), currPol.getString("website"));
                    }

                } else if(mode ==1) {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jArray = jsonObject.getJSONArray("results");
                    JSONObject resObj = jArray.getJSONObject(0);
                    JSONArray comps = resObj.getJSONArray("address_components");

                    countyName = "NONE";
                    stateAbrv = "N/A";

                    for (int i=0; i<comps.length(); i++){
                        JSONObject curr = comps.getJSONObject(i);

                        JSONArray typesArray = curr.getJSONArray("types");

                        String areaLevel = typesArray.getString(0);

                        if (areaLevel.equals("administrative_area_level_2")) {
                            countyName = curr.getString("long_name");
                        }
                        if (areaLevel.equals("administrative_area_level_1")) {
                            stateAbrv = curr.getString("short_name");
                        }

                    }

                    Log.d("Mobile Main County", "county= " + countyName + ", state= " + stateAbrv);


                }

            } catch (JSONException e) {
                Log.d("Mobile main URL Ex", "County Error"+ e.getMessage()+"");
            }

            if (mode == 0) {
                advance();
            } else if(mode ==1) {

            }

//            textView.setText(result);
        }

        private String downloadUrl(String myurl) throws IOException {

            try {
                URL url = new URL(myurl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }


        }
        //Bitmap bitmap = BitmapFactory.decodeStream(is); imageView.setImageBitmap(bitmap); // use this for images
    }




    // For the Google Location APIs


    @Override //we need this to implement GoogleApiClient.ConnectionsCallback
    public void onConnectionSuspended(int i) {}

    @Override //alternate method to connecting: no longer create this in a new thread, but as a callback
    public void onConnected(Bundle bundle) {
        Log.d("Mobile main MapAPI", "in onconnected");

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(locGoogleApiClient);
        if (mLastLocation != null) {
            Log.d("Mobile main MapAPI", "Got a Location, lat = " + mLastLocation.getLatitude() + "  lon = " + mLastLocation.getLongitude());

//            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        } else {
            Log.d("Mobile main MapAPI", "Location = null");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult res) {Log.d("Mobile main MapAPI", "MAP API connection failed");}


    @Override
    protected void onResume() {
        super.onResume();
        locGoogleApiClient.connect();
    }


    @Override
    protected void onPause() {
        super.onPause();
        locGoogleApiClient.disconnect();
    }

//    protected void startLocationUpdates() {
//        LocationServices.FusedLocationApi.requestLocationUpdates(
//                mGoogleApiClient, mLocationRequest, this);
//    }

    @Override
    public void onLocationChanged(Location location){
        Log.d("\"Mobile main MapAPI", "Location changed");
    }

    @Override
    public void onProviderEnabled(String s){

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
