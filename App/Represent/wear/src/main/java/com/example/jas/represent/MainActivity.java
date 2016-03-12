package com.example.jas.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends Activity implements SensorEventListener {

    private TextView header;
    private TextView subheader;
    private ImageView mainImage;
    private ImageButton arrowDown;
    private ImageButton arrowLeft;
    private ImageButton arrowRight;


    private SensorManager msensorManager;
    private Sensor msensor;

    //True means Politician and False means 2012 Results
    private boolean polMode;

    //Keeps track of the current politician page when on 2012 page. Range is [1, pages.size()-1]
    private int polIndex;

    //Index 0 is always a 2012 page and there can only be one 2012 page at a time.
    private ArrayList<WatchPage> pages = new ArrayList<WatchPage>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        header = (TextView) findViewById(R.id.lblHeader);
        subheader = (TextView) findViewById(R.id.lblSubheader);
        mainImage = (ImageView) findViewById(R.id.picMain);
        arrowDown = (ImageButton) findViewById(R.id.imgBtnDown);
        arrowLeft = (ImageButton) findViewById(R.id.imgBtnLeft);
        arrowRight = (ImageButton) findViewById(R.id.imgBtnRight);

        msensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE) ;
        msensor = msensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) ;

        arrowDown.setVisibility(View.INVISIBLE);

        setupBundle(getIntent());

        addPoliticianPage("Politician", "Position", "?");
//        addPoliticianPage("Pol2", "Senator", 'D', R.drawable.usflag);
//        addPoliticianPage("Barbara Lee", "Representative", 'D', R.drawable.blee);
        add2012Page("USA", "Overall", 51, 47);
//
        polIndex = 1;
        polMode = true;
        changeView(polIndex);

        Log.d("GUI Debugging", "PolIndex = " + polIndex + " PolMode = " + polMode);

    }

    @Override
    protected void onResume(){
        super.onResume();

        setupBundle(getIntent());

        msensorManager.registerListener(this, msensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void setupBundle(Intent in){
        Bundle extras = in.getExtras();

        if (extras != null) {
            Log.d("watch listening", "Watch Listener works");
//            String zip = extras.getString("ZIP");
            int pgs = extras.getInt("NUMPAGES");
            arrowDown.setVisibility(View.VISIBLE);
            resetAllViews(pgs);
        }
    }


    public void resetAllViews(int numPages){
        Log.d("Wear MAIN", "Num pages = " + numPages);

        pages.clear();
        numPages = WatchListenerService.pgs.size();
        for (int i = 0; i<numPages; i++) {
            pages.add(WatchListenerService.pgs.get(i));
        }

        polIndex = 1;
        polMode = true;
        changeView(polIndex);

        Log.d("GUI Debugging", "PolIndex = " + polIndex + " PolMode = " + polMode);
    }

    public void changeView(int index) {
        WatchPage pg = pages.get(index);

        if (polIndex == 1) {
            arrowLeft.setVisibility(View.INVISIBLE);
        }
        if (polIndex == pages.size()-1 ) {
            arrowRight.setVisibility(View.INVISIBLE);
        }

        header.setText(pg.getHeader());
        subheader.setText(pg.getSubheader());
        mainImage.setImageResource(pg.getImage());
//        polMode = pg.getIsPolitician();


    }

    void addPoliticianPage(String polName, String polPosition, String polParty) {
        int img = R.drawable.usflag;

        if (polParty.equals("D")){
            img = R.drawable.dem_logo;
        } else if (polParty.equals("R")){
            img = R.drawable.rep_logo;
        }

        WatchPage pg = new WatchPage(true, polName, polPosition + " - " + polParty, img);
        pages.add(pg);
    }

    void add2012Page(String county, String state, double obamaPercent, double romneyPercent) {
        String obama = "Obama: " + obamaPercent + "%\n";
        String romney = "Romney: " + romneyPercent + "%\n";
        String header;
        if (obamaPercent > romneyPercent) {
            header = obama + romney;
        } else {
            header = romney + obama;
        }
        WatchPage pg = new WatchPage(false, header, county + ", " + state, R.drawable.election2012);

        if (pages.size() == 0){
            pages.add(pg);
        } else {
            pages.add(0, pg);
        }
    }

    void clearPages(){
        pages.clear();
    }


    /** Switches between 2012 Results page and the Politician page at polIndex at returns the new mode.
     *  Is called when imgBtnDown is clicked */
    public boolean toggle2012View(View view) {
        if (polMode){
            arrowRight.setVisibility(View.INVISIBLE);
            arrowLeft.setVisibility(View.INVISIBLE);
            changeView(0);
        } else {
            arrowRight.setVisibility(View.VISIBLE);
            arrowLeft.setVisibility(View.VISIBLE);
            changeView(polIndex);
        }
        polMode = !polMode;
        return polMode;
    }

    /** Switches to the Politician page to the left, if there is one and returns the new polIndex.
     *  Is called when imgBtnLeft is clicked. */
    public int switchPoliticianLeft(View view) {
        if (polIndex != 1) {
            polIndex -= 1;
            changeView(polIndex);
            arrowRight.setVisibility(View.VISIBLE);

            Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
            sendIntent.putExtra("SWITCH", polIndex);
            startService(sendIntent);

            Log.d("Watch MAIN", "switch left");
        }
        return polIndex;
    }

    /** Switches to the Politician page to the right, if there is one and returns the new polIndex.
     *  Is called when imgBtnRight is clicked. */
    public int switchPoliticianRight(View view) {
        if (polIndex != pages.size()-1) {
            polIndex += 1;
            changeView(polIndex);
            arrowLeft.setVisibility(View.VISIBLE);

            Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
            sendIntent.putExtra("SWITCH", polIndex);
            startService(sendIntent);

            Log.d("Watch MAIN", "switch right");
        }
        return polIndex;
    }

    public void shake(View v) {
        Log.d("Shake", "Shake registered");
        Intent shakeIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
        shakeIntent.putExtra("ACTION", "shake");
        startService(shakeIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        msensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        Log.d("Wear main", "Shake Accuracy");
    }

    private static float lastPoll = 0;
    private static int stop = 0;
    @Override
    public void onSensorChanged(SensorEvent event) {
        final float threshold = 50;
        float poll = Math.abs(event.values[0]) + Math.abs(event.values[1]) + Math.abs(event.values[2]);


        if (Math.abs(poll - lastPoll) > threshold && stop <2){
            stop++;
            Log.d("Wear main", "Shake Sensor");
            shake(null);
        }

        lastPoll = poll;
    }
}
