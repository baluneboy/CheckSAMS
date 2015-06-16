package com.example.ken.checksams;

import java.awt.List;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.ken.checksams.Tuples.Tuple3;
import com.example.ken.checksams.Tuples.Tuple4;

/**
 * Created by pims on 6/15/15.
 */
public class RegexMatches
{
    public static void main( String args[] ){

        // String to be scanned to find the pattern.
        String line = "2015:166:23:18:59 Ku_AOS GSE";
        Tuple3<String, String, String> out = getMatch(line);
        System.out.println(out.t2);
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
    
    public static Tuple3<String, String, String> getMatch(String line) {
    	String pattern = "(\\d{4}):(\\d+):(\\d{2}):(\\d{2}):(\\d{2})\\s(.*)\\s(.*)";
    	
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(line);
        Boolean bFound = m.find();
        
        // Show match
        //showMatch(bFound, m);
        
        Tuple3<String, String, String> returnValue3 = Tuples.tuple3(m.group(5), m.group(6), m.group(7));
        
        return returnValue3;
    }
}