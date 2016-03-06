package com.example.jas.represent;

import java.util.ArrayList;


public class Politician {

    private String name, position, party, tweet, email, website;
    private int image;
    private ArrayList<String> committees, bills;


    public Politician(String _name, String _position, String _party, String _tweet, String _email, String _website,
                      int _image, ArrayList<String> _committees, ArrayList<String> _bills) {
        name =_name;
        position = _position;
        party =_party;
        tweet = _tweet;
        email = _email;
        website = _website;
        image = _image;
        committees = _committees;
        bills = _bills;
    }

    public int getImage(){return image;}

    public String getName(){return name;}

    public String getPosition(){return position;}

    public String getParty(){return party;}

    public String getTweet(){return tweet;}

    public String getEmail() {return email;}

    public String getWebsite() {return website;}

    public ArrayList<String> getCommittees(){return committees;}

    public ArrayList<String> getBills(){return bills;}
}
