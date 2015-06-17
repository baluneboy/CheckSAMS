package com.example.ken.checksams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.ken.checksams.Tuples.Tuple3;
//import com.example.ken.checksams.Tuples.Tuple4;


/**
 * Created by pims on 6/15/15.
 */
public class DeviceTimes {

    // member variables
    private Date time;          // device time
    private String device;      // device like "121f03rt"
    private String tag;         // device tag like "SE"
    private Float deltaHost;    // device time minus host time in seconds
    private Float deltaKu;      // device time minus Ku time in seconds
    private Boolean found;      // true if pattern found

    // constants
    private static final String pattern = "(\\d{4}:\\d+:\\d{2}:\\d{2}:\\d{2})\\s(.*)\\s(.*)";
    private static final Pattern rx = Pattern.compile(pattern);

    /**********************************************************
     Method:         Default Constructor
     Purpose:        Create a new DeviceTimes object and initialize it
                     with invalid deltas
     Parameters:     line string to be parsed like "2015:166:23:18:59 Ku_AOS GSE"
     Preconditions:  None
     Postconditions: a new DeviceTimes object is created with null deltas
     ***********************************************************/
    public DeviceTimes(String line) {

        // Create matcher object
        Matcher m = rx.matcher(line);
        found = m.find();

        // convert string to datetime
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:DDD:HH:mm:ss");
        try {
            time = simpleDateFormat.parse(m.group(1));
            // handle the reverse ordering for special case of host
            if (m.group(3).equals("HOST")) {
                device = m.group(3).toLowerCase();
                tag = m.group(2).toUpperCase();
            }
            else {
                device = m.group(2).toLowerCase();
                tag = m.group(3).toUpperCase();
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**********************************************************
     Method:         toString
     Purpose:        Convert the internal representation of DeviceTimes,
                     to a String which could then be printed to the screen
     Parameters:     None
     Preconditions:  None
     Postconditions: The value of the "this" object will be converted
                     to a String
     Returns:        A String representation of the "this" object
     ***********************************************************/
    public String toString()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd,DDD/HH:mm:ss");
        String t = dateFormat.format(time);
        String buffer = String.format("%s %-9s %-9s", t, device, tag);
        buffer += String.format("  dHo = %9.1fs,", deltaHost);
        buffer += String.format("  dKu = %9.1fs", deltaKu);
        return buffer;
    }

    /********************************************/
    /* public accessor methods for private data */
    /********************************************/
    // getters
    public Date getTime() { return time; }
    public String getDevice() { return device; }
    public String getTag() { return tag; }
    public float getDeltaHost() { return deltaHost; }
    public float getDeltaKu() { return deltaKu; }

    // setters
    private void setDelta(DeviceTimes dt) {
        if (isHost(dt)) {
            deltaHost = getDeltaSec(dt);
        } else if (isKu(dt)) {
            deltaKu = getDeltaSec(dt);
        } else {
            throw new IllegalArgumentException("invalid argument for device, not in (Ku_AOS, HOST)");
        }
    }

    private Float getDeltaSec(DeviceTimes dt) {
        Calendar otherTime = Calendar.getInstance();
        otherTime.setTime(dt.time);
        Calendar myTime = Calendar.getInstance();
        myTime.setTime(time);
        return (float) (myTime.getTimeInMillis() - otherTime.getTimeInMillis()) / 1000;
    }

    private static Boolean isHost(DeviceTimes dt) { return dt.device.equals("host"); }

    private static Boolean isKu(DeviceTimes dt) { return dt.device.equals("ku_aos"); }

    /******************************************************/
    /* public action methods for manipulating DeviceTimes */
    /******************************************************/

    /**********************************************************
     Method:         subtract
     Purpose:        subtract two DeviceTimes, a minus b, where a is the "this"
                     object, and b is passed as the input parameter
     Parameters:     b, the fraction to subtract from "this"
     Preconditions:  both DeviceTimes a and b must contain valid times
     Postconditions: None
     ***********************************************************/
    private float subtract(DeviceTimes b) {
        // check preconditions
        if (time == b.time)
            throw new IllegalArgumentException("FIXME the times were the same!");
        // create new fraction to return as difference
        float diff = 123.456f;
        return diff;
    }

    public static void main( String args[] ){
        
    	// Get device times mapping from text file
        Map<String,DeviceTimes> map = getMapFromFile("/misc/yoda/www/plots/user/sams/status/sensortimes.txt");
        DeviceTimesComparator dtc =  new DeviceTimesComparator(map);
        TreeMap<String,DeviceTimes> sorted_map = new TreeMap<String,DeviceTimes>(dtc);
        
        // Sort by GMT
        sorted_map.putAll(map); 
    	
    	// Iterate to display mapped values
    	// Iterate to display sorted mapped values
        try {
            for (Map.Entry<String, DeviceTimes> entry: sorted_map.entrySet()) {
                //String key = entry.getKey();
                DeviceTimes dev = entry.getValue();
                dev.setDelta(map.get("host"));
                dev.setDelta(map.get("ku_aos"));
                System.out.println(dev);
                if (dev.device.equals("host")) System.out.println(new String(new char[80]).replace("\0", "-"));
            }
        } catch (Exception e) {
            //You'll need to add proper error handling here
            System.out.println("SOMETHING WRONG WITH ENTRIES IN MAP...GOT ku_aos AND host ENTRIES?");
        }     	
    }
    
    private static Map<String, DeviceTimes> getMapFromFile(String fname) {
        //Get the text file
        File file = new File(fname);

        //Read text from file, put into map
        Map<String, DeviceTimes> map = new HashMap<String, DeviceTimes>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
            	// FIXME next if lines should be regular expression match
            	//       or maybe try/catch around 2 lines below the if lines?
            	if ( line.startsWith("begin") || line.startsWith("end") ) continue;
            	if ( line.startsWith("2015-") || line.startsWith("---") ) continue;            	
            	if ( line.startsWith("yyyy")  || line.startsWith("xxx") ) continue;            	
                DeviceTimes dt = new DeviceTimes(line);
                map.put(dt.device, dt);
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
			System.out.println("IOException");
        }
        
        return map;

    }	
    
    public static void showMatch(Boolean bFound, Matcher m) {
        if (bFound) {
            System.out.println("matcher count: " + m.groupCount());
            for(int i=1; i<m.groupCount()+1; i++) {
                System.out.println("m.group(" + i + "): " + m.group(i) );
            }
        } else {
            System.out.println("NO MATCH");
        }
        
    }
    
/*    public static Tuple3<Date, String, String> getMatch(String line) {
    	//String pattern = "(\\d{4}):(\\d+):(\\d{2}):(\\d{2}):(\\d{2})\\s(.*)\\s(.*)";
    	String pattern = "(\\d{4}:\\d+:\\d{2}:\\d{2}:\\d{2})\\s(.*)\\s(.*)";
    	
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(line);
        Boolean bFound = m.find();
        
        // Show match
        //showMatch(bFound, m);

        // convert string to datetime
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:DDD:HH:mm:ss");
		try {
            Date date1 = simpleDateFormat.parse(m.group(1));        	
	        Tuple3<Date, String, String> returnValue3 = Tuples.tuple3(date1, m.group(2), m.group(3));
	        return returnValue3;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	        return null;			
		}        
        
    }*/
    
}
