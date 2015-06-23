package com.example.ken.checksams;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
    //private static final SimpleDateFormat YYYYDDD = new SimpleDateFormat("yyyy:DDD:");
    private static final SimpleDateFormat DOY = new SimpleDateFormat("DDD:");
    private static final SimpleDateFormat HHMMSS = new SimpleDateFormat("HH:mm:ss");

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

    private static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    private static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    private static void demoJsoup(String url) {

        // Create an array
        ArrayList arraylist = new ArrayList<HashMap<String, String>>();

        try {
            // Connect to the Website URL
            Document doc = Jsoup.connect(url).get();
            ArrayList<String> downServers = new ArrayList<>();

            // Identify Table Class "worldpopulation"
            Element table = doc.select("table").get(0); //select the first table.
            Elements rows = table.select("tr");

            for (int i = 1; i < rows.size(); i++) { //first row is the col names so skip it.
                Element row = rows.get(i);
                Elements cols = row.select("td");

                if (cols.get(2).text().equals("Titan")) {
/*                    if (cols.get(7).text().equals("down"))
                        downServers.add(cols.get(5).text());

                    do {
                        if(i < rows.size() - 1)
                            i++;
                        row = rows.get(i);
                        cols = row.select("td");
                        if (cols.get(7).text().equals("down") && cols.get(3).text().equals("")) {
                            downServers.add(cols.get(5).text());
                        }
                        if(i == rows.size() - 1)
                            break;
                    }
                    while (cols.get(3).text().equals(""));
                    i--; //if there is two Titan names consecutively.*/
                    System.out.println("you should not have got here");
                }
                else {
                    System.out.println(cols.get(1).text() + " " + cols.get(2).text());
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void main( String args[] ) throws Exception {

        // URL Address
        String url = "http://pims.grc.nasa.gov/plots/MAMS/active_sensors.html";

        // Parse HTML table
        demoJsoup(url);

/*        // Get device times mapping from result text (string)
        String result = "2015-06-17 butters host\n--------------------------------------\nbegin\n2015:168:20:09:00 butters HOST\n2015:168:20:09:15 Ku_AOS GSE\n2015:168:20:09:15 122-f02 EE\n2015:168:20:09:16 122-f03 EE";
        TreeMap<String,DeviceTimes> sorted_map = getSortedMap(result);

    	// Iterate to display sorted mapped values
        try {
            for (TreeMap.Entry<String, DeviceTimes> entry: sorted_map.entrySet()) {
                DeviceTimes dev = entry.getValue();
                System.out.println(dev);
                if (dev.device.equals("host")) System.out.println(new String(new char[80]).replace("\0", "-"));
            }
        } catch (Exception e) {
            //You'll need to add proper error handling here
            System.out.println("SOMETHING WRONG WITH ENTRIES IN MAP...GOT ku_aos AND host ENTRIES?");
        }

        System.out.println(padRight("Howto", 20) + "*");
        System.out.println(padLeft("Howto", 20) + "*");
        System.out.println(String.format("%6.1f", -999.89f));
        System.out.println(String.format("%6.1f", 999.89f));
        System.out.println(String.format("%6.1f", 1.23f));*/

    }

    public static TreeMap<String, DeviceTimes> getSortedMap( String result ){

        // Get device times mapping from result text (string)
        Map<String,DeviceTimes> map = getMapFromResultString(result);
        DeviceTimesComparator dtc =  new DeviceTimesComparator(map);
        TreeMap<String,DeviceTimes> sorted_map = new TreeMap<String,DeviceTimes>(dtc);

        // Sort by GMT
        sorted_map.putAll(map);

        // Iterate sorted_map entries to establish deltas
        try {
            for (Map.Entry<String, DeviceTimes> entry: sorted_map.entrySet()) {
                DeviceTimes dev = entry.getValue();
                dev.setDelta(map.get("host"));
                dev.setDelta(map.get("ku_aos"));
            }
        } catch (Exception e) {
            //You'll need to add proper error handling here
            //System.out.println("SOMETHING WRONG WITH ENTRIES IN MAP...GOT ku_aos AND host ENTRIES?");
            e.printStackTrace();
        }

        return sorted_map;
    }

    private static Map<String, DeviceTimes> getMapFromResultString(String result) {

        // Iterate over lines, put usable ones into map
        Map<String, DeviceTimes> map = new HashMap<String, DeviceTimes>();

        String[] lines = result.split("\\r?\\n");
        for (String line: lines) {
            System.out.println("LINE IS: " + line);
            // FIXME next if lines should be regular expression match
            //       or maybe try/catch around 2 lines below the if lines?
            if (line.startsWith("begin") || line.startsWith("end")) continue;
            if (line.startsWith("2015-") || line.startsWith("---")) continue;
            if (line.startsWith("yyyy") || line.startsWith("xxx")) continue;
            DeviceTimes dt = new DeviceTimes(line);
            map.put(dt.device, dt);
        }

        return map;

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

    public static SpannableString getSpannableFromMap(TreeMap<String, DeviceTimes> sorted_map) {

        int start;

        // now we have sorted map, so iterate to build sorted, formatted spannables
        SpannableStringBuilder deviceLines = new SpannableStringBuilder();
        try {

            // header line
            deviceLines.append("DOY:hh:mm:ss     dH     dK  device\n");
            deviceLines.append("----------------------------------\n");
            deviceLines.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, deviceLines.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            for (TreeMap.Entry<String, DeviceTimes> entry: sorted_map.entrySet()) {
                DeviceTimes dev = entry.getValue();
                Date t = dev.getTime();

                // FIXME when we are get to host entry, entire lines background gets distinct color
                // FIXME user config for dimmed devices

                // TODO make a home for snippet keeper for repeat character like this
                // the next line shows how to repeat char "-" 80 times
                //    Log.i("SEP", new String(new char[80]).replace("\0", "-"));


                // if host, then make this a distinctive line in the sand
                if (dev.getDevice().equals("host")) {

                    start = deviceLines.length();

                    deviceLines.append(DOY.format(t));
                    deviceLines.append(HHMMSS.format(t));
                    deviceLines.append(String.format(" %6.1f", dev.getDeltaHost()));
                    deviceLines.append(String.format(" %6.1f", dev.getDeltaKu()));
                    deviceLines.append("  " + dev.getDevice() + "      ");
                    deviceLines.setSpan(new ForegroundColorSpan(Color.WHITE), start, deviceLines.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    deviceLines.setSpan(new BackgroundColorSpan(Color.BLACK), start, deviceLines.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                }
                else {

                    // device time DOY: is plain
                    deviceLines.append(DOY.format(t));

                    // device time HH:MM:SS is orange
                    start = deviceLines.length();
                    deviceLines.append(HHMMSS.format(t));
                    deviceLines.setSpan(new ForegroundColorSpan(0xFFCC5500), start, deviceLines.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    // deltaHost is just a plain span
                    //start = deviceLines.length();
                    float dh = dev.getDeltaHost();
                    if (dh < -999.9) {
                        dh = -999.9f;
                    } else if (dh > 999.9) {
                        dh = 999.9f;
                    }
                    deviceLines.append(String.format(" %6.1f", dh));
                    //deviceLines.setSpan(new ForegroundColorSpan(0xFFCC5500), start, deviceLines.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //deviceLines.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, deviceLines.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    // deltaKu is just a plain span
                    //start = deviceLines.length();
                    float dk = dev.getDeltaKu();
                    boolean clipped = false;
                    if (dk < -999.9) {
                        dk = -999.9f; clipped = true;
                    } else if (dk > 999.9) {
                        dk = 999.9f;  clipped = true;
                    }
                    deviceLines.append(String.format(" %6.1f", dk));
                    if (clipped) {
                        deviceLines.setSpan(new ForegroundColorSpan(0x80ff0000), start, deviceLines.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        //deviceLines.setSpan(new ForegroundColorSpan(0xFFCC5500), start, deviceLines.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        //deviceLines.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, deviceLines.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    // device name is clickable linked to WHAT?
                    deviceLines.append("  ");
                    start = deviceLines.length();
                    deviceLines.append(padRight(dev.getDevice(), 12));
                    //deviceLines.setSpan(new URLSpan("http://pims.grc.nasa.gov/plots/sams/121f03/121f03.jpg"), start, deviceLines.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                }

                deviceLines.append("\n");

            }
        } catch (Exception e) {
            //You'll need to add proper error handling here
            Log.i("EXCEPT", "SOMETHING WRONG WITH ENTRIES IN MAP...GOT ku_aos AND host ENTRIES?");
        }

        return SpannableString.valueOf(deviceLines);

    }

}
