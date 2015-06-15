package com.example.ken.checksams;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
//import java.util.logging.Logger;
import java.util.Map;
import java.io.BufferedReader;
import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;

import com.example.ken.checksams.Tuples.Tuple4;

public class SingleKeyMultipleValueUsingList {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Hello world!");
		
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1; // NOTE: zero based!
		int day = now.get(Calendar.DAY_OF_MONTH);
		int doy = now.get(Calendar.DAY_OF_YEAR);
		String result = String.format("%02d/%02d/%4d, Day %03d", month, day, year, doy);
		System.out.println(result);
		
		// create map to store
        //Map<String, List<String>> map = new HashMap<String, List<String>>();
        Map<String, Tuple4> map = new HashMap<String, Tuple4>();

		// read device times file
		String fname = "/home/pims/Documents/sensortimes.txt";
		String ftxt = getTextFromFile(fname);
		System.out.print(ftxt);

		// try parse date time from string
        String s1 = "12/30/2013 23:59:56";
        String s2 = "12/31/2013 23:59:56";
        //String s2 = "01/01/2014 00:00:03";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        try
        {
            Date date1 = simpleDateFormat.parse(s1);
            Date date2 = simpleDateFormat.parse(s2);

            // try tuple4
            Tuple4<Date, String, Float, Float> returnValue1 = Tuples.tuple4(date1, "GSE", 1.2f, 2.1f);
            Tuple4<Date, String, Float, Float> returnValue2 = Tuples.tuple4(date2, "SE", 3.4f, 4.3f);

            // put values into map
            map.put("Ku_AOS", returnValue1);
            map.put("121f03rt", returnValue2);

            // iterate to display values
            System.out.println("Fetching Keys and Corresponding, Multiple Values");
            for (Map.Entry<String, Tuple4> entry: map.entrySet()) {
                String key = entry.getKey();
                Tuple4 value = entry.getValue();
                System.out.print("Key = " + key + " >> ");
                System.out.print("Date = " + value.t1 + ", ");
                System.out.print("Label = " + value.t2 + ", ");
                System.out.print("Delta1 = " + value.t3 + ", ");
                System.out.println("Delta2 = " + value.t4 + ", ");
            }

            //showElapsedArr(simpleDateFormat, date1, date2);
            
        }
        catch (ParseException ex)
        {
            System.out.println("Exception " + ex);
        }		
		
	}

    public static void showElapsedArr(SimpleDateFormat sdf, Date d1, Date d2) {

        System.out.println("date1: " + sdf.format(d1));
        System.out.println("date2: " + sdf.format(d2));

        Calendar start = Calendar.getInstance();
        start.setTime(d1);
        Calendar end = Calendar.getInstance();
        end.setTime(d2);

        Float deltaSec = (float) (end.getTimeInMillis() - start.getTimeInMillis()) / 1000;
        System.out.println("deltaSec = " + deltaSec);

        Integer[] elapsed = new Integer[6];
        Calendar clone = (Calendar) start.clone(); // Otherwise changes are been reflected.
        elapsed[0] = elapsed(clone, end, Calendar.YEAR);
        clone.add(Calendar.YEAR, elapsed[0]);
        elapsed[1] = elapsed(clone, end, Calendar.MONTH);
        clone.add(Calendar.MONTH, elapsed[1]);
        elapsed[2] = elapsed(clone, end, Calendar.DATE);
        clone.add(Calendar.DATE, elapsed[2]);
        elapsed[3] = (int) (end.getTimeInMillis() - clone.getTimeInMillis()) / 3600000;
        clone.add(Calendar.HOUR, elapsed[3]);
        elapsed[4] = (int) (end.getTimeInMillis() - clone.getTimeInMillis()) / 60000;
        clone.add(Calendar.MINUTE, elapsed[4]);
        elapsed[5] = (int) (end.getTimeInMillis() - clone.getTimeInMillis()) / 1000;

        System.out.format("%d years, %d months, %d days, %d hours, %d minutes, %d seconds\n", elapsed);

    }

	public static int elapsed(Calendar before, Calendar after, int field) {
	    Calendar clone = (Calendar) before.clone(); // Otherwise changes are been reflected.
	    int elapsed = -1;
	    while (!clone.after(after)) {
	        clone.add(field, 1);
	        elapsed++;
	    }
	    return elapsed;
	}	
	
    private static String getTextFromFile(String fname) {
        //Get the text file
        File file = new File(fname);

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
            	if ( line.startsWith("begin") || line.startsWith("end") ) continue;
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
			System.out.println("IOException");

        }

        String result = text.toString();
        
        return result;

    }	
    
}