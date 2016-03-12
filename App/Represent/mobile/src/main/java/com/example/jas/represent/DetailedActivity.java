package com.example.jas.represent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DetailedActivity extends AppCompatActivity {

    private ImageView image;
    private TextView name;
    private TextView position_party;
    private Spinner committees;
    private Spinner bills;

//    private int index = 0;
    private Politician pol;

    private static final String SUNLIGHT_API_Key = "4e92253595470cdabd641d91236674d3";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        image = (ImageView) findViewById(R.id.picFace);
        name = (TextView) findViewById(R.id.lblName);
        position_party = (TextView) findViewById(R.id.lblPosnParty);
        committees = (Spinner) findViewById(R.id.spinCommittees);
        bills = (Spinner) findViewById(R.id.spinRecentBills);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            int i = extras.getInt("index");
            Log.d("DetailedView Init", "index = "+i);
            changeView(i);
        }

    }


    void changeView(int index){
        pol = CongressionalActivity.politicians.get(index);

        Bitmap img = pol.getImage();
        if (img != null) {
            image.setImageBitmap(img);
        } else {
            image.setImageResource(R.drawable.blank);
        }


        name.setText(pol.getName());

        String subheader = pol.getPosition() + " - " + pol.getParty();
        position_party.setText(subheader);

//        Log.i("sizes", "Coms " + pol.getCommittees().size() + " Bills " + pol.getBills().size());
        getDetails();
//        fillSpinners();
    }

    public void fillSpinners(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pol.getCommittees());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        committees.setAdapter(adapter);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pol.getBills());
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bills.setAdapter(adapter2);

    }

    public void getDetails(){
        if (pol.getBills().size() != 0 && pol.getCommittees().size() != 0){
            fillSpinners();
            return;
        }

        if (pol.getCommittees().size() == 0) {
            // Get from SunLight
            prepURL("/committees?member_ids="+pol.getId(), true);
        }
        if (pol.getBills().size() == 0) {
            // get from SunLight
            prepURL("/bills/search?sponsor_id="+pol.getId(), false);
        }
    }

    void prepURL(String path, boolean getCommitteesURL) {
        String url = "http://congress.api.sunlightfoundation.com" + path + "&apikey=" + SUNLIGHT_API_Key;
        Log.d("Congress Sunlight", "Final URL=" + url);

        if (getCommitteesURL){
            Log.d("Congress Sunlight", "Getting Committees");
        } else {
            Log.d("Congress Sunlight", "Getting Bills");
        }

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            DownloadWebpageTask dwTask = new DownloadWebpageTask();
//            new DownloadWebpageTask().execute(url);
            dwTask.isCommittee = getCommitteesURL;
            dwTask.execute(url);

        } else {
            Log.d("Congressional", "No network connection available.");
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        // If true getting Poltician committees, if false getting Politician sponsored bills
        public boolean isCommittee;

        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
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
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }


        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("Congressional Sunlight", "" + result);
//            Politician pol = politicians.get(index);

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jArray = jsonObject.getJSONArray("results");

                for (int i=0; i < jArray.length(); i++) {
                    JSONObject curr = jArray.getJSONObject(i);

                    if (isCommittee) {
                        pol.getCommittees().add(curr.getString("name"));
                    } else {
                        String billName = curr.getString("official_title");
                        if (!curr.getString("popular_title").equals("null")){
                            billName = curr.getString("popular_title");
                        } else if (!curr.getString("short_title").equals("null")) {
                            billName = curr.getString("short_title");
                        }
                        pol.getBills().add(curr.getString("last_version_on")+  " - " +billName);
                    }

                }

            } catch (JSONException e) {
                Log.d("Congress Sunlight Ex", e.getMessage() + "");
            }
            if (isCommittee) {
                Log.d("Congress Sunlight", "Committee done, size = " + pol.getCommittees().size());
            } else {
                Log.d("Congress Sunlight", "Bills done, size = " + pol.getBills().size());
            }
            if (pol.getBills().size()!=0) { // Might need to add && pol.getCommittees().size()!=0
                fillSpinners();
            }

        }


    }





}
