package com.example.jas.represent;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class WatchListenerService extends WearableListenerService {
        // In PhoneToWatchService, we passed in a path, either "/FRED" or "/LEXY"
        // These paths serve to differentiate different phone-to-watch messages
//        private static final String FRED_FEED = "/Fred";
        private static final String ZIP_CODE = "/zip";

        private String message;

        public static ArrayList<WatchPage> pgs = new ArrayList<WatchPage>();

        public String getNext() {
            int colon = message.indexOf(':');

            if (colon == -1){
                return null;
            }

            String res = message.substring(0, colon);
            if (colon+1 < message.length()){
                message = message.substring(colon+1);
            } else {
                message = null;
            }
            return res;
        }

        @Override
        public void onMessageReceived(MessageEvent messageEvent) {
            Log.d("watch listener", "in WatchListenerService, got: " + messageEvent.getPath());
            //use the 'path' field in sendmessage to differentiate use cases
            //(here, fred vs lexy)
//
            if( messageEvent.getPath().equalsIgnoreCase( ZIP_CODE ) ) {
                String msg = new String(messageEvent.getData(), StandardCharsets.UTF_8);
                Intent intent = new Intent(this, MainActivity.class );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //you need to add this flag since you're starting a new activity from a service

// Alameda County:CA:78.5:18.7:3:Barbara Lee:Representative:D:Barbara Boxer:Senator:D:Dianne Feinstein:Senator:D:
                Log.d("watch Listener", "msg= "+ msg);

                pgs.clear();

                message = msg;

                String county = getNext();
                String state = getNext();
                String obama = getNext();
                String romney = getNext();

                if (obama.equals("-1") || romney.equals("-1") || state.equals("N/A") || county.equals("NONE")) {
                    add2012Page("USA", "Overall", 51.1, 47.2);
                } else {
                    add2012Page(county, state, Double.parseDouble(obama), Double.parseDouble(romney));
                }

                Log.d("watchListen", "Tokenized = " + county + state + obama + romney);


                int numPols = Integer.parseInt(getNext());

                for (int i = 0; i < numPols; i++) {
                    String name = getNext();
                    String pos = getNext();
                    String party = getNext();

                    Log.d("watchListen", "Tokenized = " + name + pos + party);

                    addPoliticianPage(name, pos, party);

                }


                intent.putExtra("NUMPAGES", pgs.size());

                startActivity(intent);

            } else {
                Log.d("watch listener", "Not a ZIP Code");
                super.onMessageReceived( messageEvent );
            }

        }

        void addPoliticianPage(String polName, String polPosition, String polParty) {
            int img = R.drawable.usflag;

            if (polParty.equals("D")){
                img = R.drawable.dem_logo;
            } else if (polParty.equals("R")){
                img = R.drawable.rep_logo;
            }

            WatchPage pg = new WatchPage(true, polName, polPosition + " - " + polParty, img);
            pgs.add(pg);
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

            if (pgs.size() == 0){
                pgs.add(pg);
            } else {
                pgs.add(0, pg);
            }
        }


}
