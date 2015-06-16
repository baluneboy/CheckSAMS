package com.example.ken.checksams;


import java.awt.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.ken.checksams.Tuples.Tuple3;
import com.example.ken.checksams.Tuples.Tuple4;


/**
 * Created by pims on 6/15/15.
 */
public class ParseTime {
	
    public static void main( String args[] ){

        // String to be scanned to find the pattern.
        String line = "2015:166:23:18:59 Ku_AOS GSE";
        Tuple3<Date, String, String> out = getMatch(line);
        System.out.println(out.t1);
        System.out.println(out.t2);
        System.out.println(out.t3);
        
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
    
    public static Tuple3<Date, String, String> getMatch(String line) {
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
        
    }
    
}
