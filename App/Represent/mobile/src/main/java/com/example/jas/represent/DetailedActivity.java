package com.example.jas.represent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailedActivity extends AppCompatActivity {

    private ImageView image;
    private TextView name;
    private TextView position_party;
    private Spinner committees;
    private Spinner bills;

//    //Should move these to Congress View
//    private ArrayList<Politician> politicians = new ArrayList<Politician>();
//    private int index = 0;


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

            Politician p = new Politician(extras.getString("name"), extras.getString("position"),
                    extras.getString("party"), extras.getString("tweet"), extras.getString("email"),
                    extras.getString("website"), extras.getInt("image"),
                    extras.getStringArrayList("committees"), extras.getStringArrayList("bills"));

            Log.i("DetailedView Init", "politician parsing");
            changeView(p);
        }


//        ArrayList<String> coms = new ArrayList<String >();
//        coms.add("Committee for Veterans Health");
//        coms.add("Committee for Women's Rights");
//        ArrayList<String> rbills = new ArrayList<String >();
//        rbills.add("Obamacare");
//        rbills.add("Marriage Equality");
//        changeView(new Politician("Barbara Boxer", "Senator", "D", "Donald Trump is not a leader.",
//                "senator@boxer.senate.gov", "boxer.senate.gov", R.drawable.bboxer, coms, rbills));
    }

//    //Should move this to Congress View
//    void addPolitician(String _name, String _position, String _party, String _tweet, String _email,
//                       String _website, int _image, ArrayList<String> _committees, ArrayList<String> _bills){
//        Politician pol = new Politician( _name,  _position,  _party,  _tweet,
//                _email,  _website, _image,  _committees,  _bills);
//        politicians.add(pol);
//    }

    void changeView(Politician pol){
        image.setImageResource(pol.getImage());
        name.setText(pol.getName());

        String subheader = pol.getPosition() + " - " + pol.getParty();
        position_party.setText(subheader);

        Log.i("sizes", "Coms " + pol.getCommittees().size() + " Bills " + pol.getBills().size());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pol.getCommittees());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        committees.setAdapter(adapter);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pol.getBills());
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bills.setAdapter(adapter2);

    }



}
