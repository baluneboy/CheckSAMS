package com.example.ken.checksams;

import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.example.ken.checksams.DeviceTimes;

public class MySortedMap {

    public static void main(String[] args) {
        
        HashMap<String,DeviceTimes> map = new HashMap<String,DeviceTimes>();
        DeviceTimesComparator bvc =  new DeviceTimesComparator(map);
        TreeMap<String,DeviceTimes> sorted_map = new TreeMap<String,DeviceTimes>(bvc);        

        String line1 = "2015:159:21:22:03 es05rt CIR";
        DeviceTimes dt1 = new DeviceTimes(line1);        

        String line2 = "2015:159:21:22:00 es06rt DIR";
        DeviceTimes dt2 = new DeviceTimes(line2); 
        
        String line3 = "2015:159:21:22:09 es07rt WIR";
        DeviceTimes dt3 = new DeviceTimes(line3); 
        
        map.put("es05rt", dt1);
        map.put("es06rt", dt2);
        map.put("es07rt", dt3);       
        
    	// Iterate to display unsorted mapped values
        System.out.println("UNSORTED:");
        try {
            for (Map.Entry<String, DeviceTimes> entry: map.entrySet()) {
                //String key = entry.getKey();
                DeviceTimes dev = entry.getValue();
                System.out.println(dev);
            }
        } catch (Exception e) {
            //You'll need to add proper error handling here
            System.out.println("SOMETHING WRONG WITH ENTRIES IN MAP.");
        } 
        
        // Sort by GMT
        sorted_map.putAll(map);
       
    	// Iterate to display sorted mapped values
        System.out.println("SORTED BY GMT:");
        try {
            for (Map.Entry<String, DeviceTimes> entry: sorted_map.entrySet()) {
                //String key = entry.getKey();
                DeviceTimes dev = entry.getValue();
                System.out.println(dev);
            }
        } catch (Exception e) {
            //You'll need to add proper error handling here
            System.out.println("SOMETHING WRONG WITH ENTRIES IN MAP.");
        }           
        
    }
}

class DeviceTimesComparator implements Comparator<String> {

    Map<String, DeviceTimes> base;
    public DeviceTimesComparator(Map<String, DeviceTimes> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are NOT consistent with equals.    
    public int compare(String a, String b) {
        Calendar t1 = Calendar.getInstance();
        t1.setTime(base.get(a).getTime());
        Calendar t2 = Calendar.getInstance();
        t2.setTime(base.get(b).getTime());
    	Long aTime = t1.getTimeInMillis();
    	Long bTime = t2.getTimeInMillis(); 	
        if (aTime >= bTime) {
            return 1;
        } else {
            return -1;
        } // returning 0 would merge keys
    }
}