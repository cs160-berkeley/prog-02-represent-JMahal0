package com.example.jas.represent;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    private EditText zipCode;
    private TextView title;
    private Button location;
    private Button go;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                if (s.length() != 5){
                    go.setVisibility(View.INVISIBLE);
//                    zipCode.setWidth(250);
                    params.width = location.getLayoutParams().width;
                } else {
                    go.setVisibility(View.VISIBLE);
//                    zipCode.setWidth(160);
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
                Log.d("Mobile main - 2", "YAYYYYYYYYYYYYYYYYYYYYYYYYY");

                Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                sendIntent.putExtra("ZIP", 1);
                startService(sendIntent);

                Intent next = new Intent(this, CongressionalActivity.class); //temporary
                next.putExtra("pSet", 1);
                startActivity(next);
            }
        }

    }

    public void zipGo(View view){
        String zip = zipCode.getText().toString();
        if (zip.length() == 5){
            int z = Integer.parseInt(zip);
            z = 94720; // Delete this line for part 3
            findPoliticians(view, z);

        }
    }

    public void getLocation(View view){
        findPoliticians(view, 94720); // This needs to be changed when implementing APIS
    }

    // Is called to advance to the next Activity and load data using zipCode
    public void findPoliticians(View view, int zipCode){
        zipCode = 0;
        Log.d("Mobile main - 3", "ZIP: " + zipCode);

        //send thing to watch here
        Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        sendIntent.putExtra("ZIP", zipCode);
        startService(sendIntent);

        //next screen
        Intent next = new Intent(view.getContext(), CongressionalActivity.class); //temporary
        next.putExtra("pSet", 0);
        startActivity(next);
    }




}
