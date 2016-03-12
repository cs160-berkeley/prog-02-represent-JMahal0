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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

//import com.twitter.sdk.android.Twitter;
//import com.twitter.sdk.android.core.*;
//import com.twitter.sdk.android.core.models.Tweet;
//import com.twitter.sdk.android.tweetui.*;
//import io.fabric.sdk.android.Fabric;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CongressionalActivity extends AppCompatActivity {

    private ImageButton polImgBtn1, polImgBtn2, polImgBtn3, polImgBtn4, polImgBtn5, polImgBtn6, polImgBtn7;
    private ImageView face;
    private TextView name, posNparty, tweet, email, website;
    private Button moreInfo;

    public static ArrayList<Politician> politicians = new ArrayList<Politician>();
    private int index = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congressional);

        polImgBtn1 = (ImageButton) findViewById(R.id.imgBtn1);
        polImgBtn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pol1Clicked(v);
            }
        });
        polImgBtn2 = (ImageButton) findViewById(R.id.imgBtn2);
        polImgBtn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pol2Clicked(v);
            }
        });
        polImgBtn3 = (ImageButton) findViewById(R.id.imgBtn3);
        polImgBtn3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                pol3Clicked(v);
            }
        });
        polImgBtn4 = (ImageButton) findViewById(R.id.imgBtn4);
        polImgBtn4.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                pol4Clicked(v);
            }
        });
        polImgBtn5 = (ImageButton) findViewById(R.id.imgBtn5);
        polImgBtn5.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                pol5Clicked(v);
            }
        });
        polImgBtn6 = (ImageButton) findViewById(R.id.imgBtn6);
        polImgBtn6.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                pol6Clicked(v);
            }
        });
        polImgBtn7 = (ImageButton) findViewById(R.id.imgBtn7);
        polImgBtn7.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                pol7Clicked(v);
            }
        });

        face = (ImageView) findViewById(R.id.picFace);
        name = (TextView) findViewById(R.id.lblName);
        posNparty = (TextView) findViewById(R.id.lblPosnParty);
        tweet = (TextView) findViewById(R.id.lblTweet);
        email = (TextView) findViewById(R.id.lblEmail);
        website = (TextView) findViewById(R.id.lblWebsite);
        moreInfo = (Button) findViewById(R.id.btnMore);
        moreInfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                toDetailedView(v);
            }
        });


        ArrayList<String> coms = new ArrayList<String >();
        coms.add("Committee for Veterans Health");
        coms.add("Committee for Women's Rights");
        ArrayList<String> rbills = new ArrayList<String >();
        rbills.add("Obamacare");
        rbills.add("Marriage Equality");

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
//            int i = extras.getInt("index",-1);
//            if (i != -1) {
//
//
//                Log.d("Congressional Init", "index = " + i);
//
//                index = i;
//                getDetailedInfo(null);
//            }

//            int polSet = extras.getInt("pSet");
//            Log.i("politician set", "set  = " + polSet);
//            if (polSet == 0){
//                politicians.add(new Politician("Barbara Boxer", "Senator", "D", "Donald Trump is not a leader.",
//                        "senator@boxer.senate.gov", "boxer.senate.gov", R.drawable.bboxer, coms, rbills));
//                politicians.add(new Politician("Dianne Feinstein", "Senator", "D", "Vote in the Democratic Primary.",
//                        "senator@feinstein.senate.gov", "feinstein.senate.gov", R.drawable.dfeinstein, coms, rbills));
//                politicians.add(new Politician("Barbara Lee", "Representative", "D", "Oakland is great.",
//                        "rep@lee.house.gov", "lee.house.gov", R.drawable.blee, coms, rbills));
//            }
//            if (polSet == 1) {
//                politicians.add(new Politician("Deb Fischer", "Senator", "R", "Donald Trump is not a leader.",
//                        "senator@Fischer.senate.gov", "Fischer.senate.gov", R.drawable.dfischer, coms, rbills));
//                politicians.add(new Politician("Ben Sasse", "Senator", "R", "Vote in the Republican Primary.",
//                        "senator@Sasse.senate.gov", "Sasse.senate.gov", R.drawable.bsasse, coms, rbills));
//                politicians.add(new Politician("Jeff Fortenbury", "Representative", "R", "Nebraska is great.",
//                        "rep@jeff.house.gov", "jeff.house.gov", R.drawable.jfortenberry, coms, rbills));
//            }

        }

//        // TODO: Use a more specific parent
//        final ViewGroup parentView = (ViewGroup) getWindow().getDecorView().getRootView();
//        // TODO: Base this Tweet ID on some data from elsewhere in your app
//        long tweetId = 631879971628183552L;
//        TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
//            @Override
//            public void success(Result<Tweet> result) {
//                TweetView tweetView = new TweetView(CongressionalActivity.this, result.data);
//                parentView.addView(tweetView);
//            }
//            @Override
//            public void failure(TwitterException exception) {
//                Log.d("TwitterKit", "Load Tweet failure", exception);
//            }
//        });



        configurePolBtns();
        Log.d("CongressionalView", "Number of pols " + politicians.size());
        changeView(politicians.get(0));
    }

    void addPolitician(String _id, String _name, String _position, String _party, String _tweetID, String _email,
                       String _website) {
        Politician pol = new Politician(_id, _name,  _position,  _party,  _tweetID,
                _email,  _website);
        politicians.add(pol);
    }

    void changeView(Politician pol) {
        Bitmap img = pol.getImage();
        if (img != null) {
            face.setImageBitmap(pol.getImage());
        } else {
            face.setImageResource(R.drawable.blank);
        }
        name.setText(pol.getName());
        String subheader = pol.getPosition() + " - " + pol.getParty();
        posNparty.setText(subheader);

        tweet.setText(pol.getTweetID()); // This is currently the Twitter handle, not the tweet

        email.setText(pol.getEmail());
        website.setText(pol.getWebsite());

    }

    void configurePolBtns(){

        ImageButton polBtns[] ={polImgBtn1, polImgBtn2, polImgBtn3, polImgBtn4, polImgBtn5, polImgBtn6, polImgBtn7};
        for (int i = 0; i < 7; i++){
            if (i < politicians.size()) {
                polBtns[i].setVisibility(View.VISIBLE);

                Bitmap img = politicians.get(i).getImage();
                if (img != null) {
                    polBtns[i].setImageBitmap(img);
                } else {
                    polBtns[i].setImageResource(R.drawable.blank);
                }
            } else {
                polBtns[i].setVisibility(View.INVISIBLE);
            }
        }

    }

    // Called when polImgBtn1 is clicked
    void pol1Clicked(View view) {
        index = 0;
        changeView(politicians.get(0));
    }

    // Called when polImgBtn2 is clicked
    void pol2Clicked(View view) {
        index = 1;
        changeView(politicians.get(1));
    }

    // Called when polImgBtn3 is clicked
    void pol3Clicked(View view) {
        index = 2;
        changeView(politicians.get(2));
    }

    // Called when polImgBtn4 is clicked
    void pol4Clicked(View view) {
        index = 3;
        changeView(politicians.get(3));
    }

    // Called when polImgBtn5 is clicked
    void pol5Clicked(View view) {
        index = 4;
        changeView(politicians.get(4));
    }

    // Called when polImgBtn6 is clicked
    void pol6Clicked(View view) {
        index = 5;
        changeView(politicians.get(5));
    }

    // Called when polImgBtn7 is clicked
    void pol7Clicked(View view) {
        index = 6;
        changeView(politicians.get(6));
    }

    // Called when moreInfo is Clicked
    void toDetailedView(View v) {

        //next screen
        Intent next = new Intent(getBaseContext(), DetailedActivity.class);
        next.putExtra("index", index);
//        next = bundlePolitician(next, politicians.get(index));

        startActivity(next);
    }




// Shouldn't need this anymore
//    // Puts a politician in an intent to be sent to a different Activity
//    Intent bundlePolitician(Intent intent, Politician pol) {
//        intent.putExtra("name", pol.getName());
//        intent.putExtra("position", pol.getPosition());
//        intent.putExtra("party", pol.getParty());
//        intent.putExtra("tweet", pol.getTweet());
//        intent.putExtra("email", pol.getEmail());
//        intent.putExtra("website", pol.getWebsite());
//        intent.putExtra("image", pol.getImage());
//
//        intent.putStringArrayListExtra("committees", pol.getCommittees());
//        intent.putStringArrayListExtra("bills", pol.getBills());
//
//        return intent;
//    }

}
