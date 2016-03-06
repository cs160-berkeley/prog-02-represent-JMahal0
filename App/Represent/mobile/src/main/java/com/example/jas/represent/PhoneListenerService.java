package com.example.jas.represent;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

public class PhoneListenerService extends WearableListenerService {
    //   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    private static final String TOAST = "/send_toast";
    private static final String SHAKE = "/shake";
    private static final String SWITCH = "/switch";


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if( messageEvent.getPath().equalsIgnoreCase(SHAKE) ) {

            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra("action", "shake"); //Makeshift random variable.

            Log.d("Phone Listener", "About to start new Activity");

            startActivity(intent);


        } else if (messageEvent.getPath().equalsIgnoreCase(SWITCH)) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            int index = Integer.parseInt(value);

            Intent intent = new Intent(this, CongressionalActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra("switch", index); //Makeshift random variable.

            Log.d("Phone Listener", "switch index = "+ index);

            startActivity(intent);

        } else {
            super.onMessageReceived( messageEvent );
        }

    }
}
