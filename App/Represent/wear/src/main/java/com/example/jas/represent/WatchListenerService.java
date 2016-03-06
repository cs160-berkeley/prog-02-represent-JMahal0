package com.example.jas.represent;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

public class WatchListenerService extends WearableListenerService {
        // In PhoneToWatchService, we passed in a path, either "/FRED" or "/LEXY"
        // These paths serve to differentiate different phone-to-watch messages
//        private static final String FRED_FEED = "/Fred";
        private static final String ZIP_CODE = "/zip";

        @Override
        public void onMessageReceived(MessageEvent messageEvent) {
            Log.d("watch listener", "in WatchListenerService, got: " + messageEvent.getPath());
            //use the 'path' field in sendmessage to differentiate use cases
            //(here, fred vs lexy)
//
            if( messageEvent.getPath().equalsIgnoreCase( ZIP_CODE ) ) {
                String zip = new String(messageEvent.getData(), StandardCharsets.UTF_8);
                Intent intent = new Intent(this, MainActivity.class );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //you need to add this flag since you're starting a new activity from a service

                int pSet = Integer.parseInt(zip);
                Log.d("watch listener", "about to start watch MainActivity with ZIP: " + zip + " int pSet = " + pSet);

                intent.putExtra("ZIP", pSet);

                startActivity(intent);

            } else {
                Log.d("watch listener", "Not a ZIP Code");
                super.onMessageReceived( messageEvent );
            }

        }


}
