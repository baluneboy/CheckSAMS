package com.example.ken.checksams;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pims on 6/15/15.
 */
public class RegexMatches
{
    public static void main( String args[] ){

        // String to be scanned to find the pattern.
        String line = "2015:166:23:18:59 Ku_AOS GSE";
        String pattern = "(\\d{4}):(\\d+):(\\d{2}):(\\d{2}):(\\d{2})\\s(.*)\\s(.*)";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(line);
        if (m.find( )) {
            System.out.println("matcher count: " + m.groupCount());
            //System.out.println("m.group(0): " + m.group(0) );
            for(int i=1; i<m.groupCount()+1; i++){
                System.out.println("m.group(" + i + "): " + m.group(i) );
            }
        } else {
            System.out.println("NO MATCH");
        }
    }
}