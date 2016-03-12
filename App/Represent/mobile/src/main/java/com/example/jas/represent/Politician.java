package com.example.jas.represent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;


public class Politician {

    private String id, name, position, party, tweetID, email, website;
    private Bitmap image;
    private ArrayList<String> committees, bills;


    public Politician(String _id, String _name, String _position, String _party, String _tweetID, String _email, String _website) {
        id = _id;
        name =_name;

        position = "Senator";
        if(_position.equals("Rep")) {
            position = "Representative";
        }

        party =_party;
        tweetID = _tweetID;
        email = _email;
        website = _website;

        committees = new ArrayList<String>();
        bills = new ArrayList<String>();
        image = null;
    }

    public void setImage(Bitmap _image){
        image = _image;
    }

    public String getId(){return id;}

    public Bitmap getImage(){return image;}

    public String getName(){return name;}

    public String getPosition(){return position;}

    public String getParty(){return party;}

    public String getTweetID(){return tweetID;}

    public String getEmail() {return email;}

    public String getWebsite() {return website;}

    public ArrayList<String> getCommittees(){return committees;}

    public ArrayList<String> getBills(){return bills;}
}
