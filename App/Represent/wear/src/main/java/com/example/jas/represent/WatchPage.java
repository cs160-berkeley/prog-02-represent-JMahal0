package com.example.jas.represent;


public class WatchPage {

    private boolean isPolitician; // If false, this is a 2012 Results Page
    private String header; // Either Politician name or 2012 results
    private String subheader; // Either Politician position&party or 2012 County,State
    private int image;

    public WatchPage(boolean _isPolitician, String _header, String _subheader, int _image){
        isPolitician = _isPolitician;
        header = _header;
        subheader = _subheader;
        image = _image;
    }

//    //Constructor for a politician page
//    public WatchPage(String polName, String polPosition, char polParty, int polImage) {
//        isPolitician = true;
//        header = polName;
//        subheader = polPosition + " - " + polParty;
//        image = polImage;
//    }
//
//    //Constructor for a 2012 Results page
//    public WatchPage(String county, String state, int obamaPercent, int romneyPercent) {
//        isPolitician = false;
//        String obama = "Obama: " + obamaPercent + "%\n";
//        String romney = "Romney: " + romneyPercent + "%\n";
//        if (obamaPercent > romneyPercent) {
//            header = obama + romney;
//        } else {
//            header = romney + obama;
//        }
//        subheader = county + ", " + state;
//        image = R.drawable.election2012;
//    }

    // Accessors
    public String getHeader(){
        return header;
    }

    public String getSubheader() {
        return subheader;
    }

    public boolean getIsPolitician(){
        return isPolitician;
    }

    public int getImage(){
        return image;
    }



}
