package howdy;

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

import howdy.Tuples;
import howdy.Tuples.Tuple2;

public class SingleKeyMultipleValueUsingList {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello world!");
		
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1; // NOTE: zero based!
		int day = now.get(Calendar.DAY_OF_MONTH);
		int doy = now.get(Calendar.DAY_OF_YEAR);
		String result = String.format("%02d/%02d/%4d, Day %03d", month, day, year, doy);
		System.out.println(result);
		
		// create map to store
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		
		// create list #1 and store values
		List<String> valSetOne = new ArrayList<String>();
		valSetOne.add("Apple");
		valSetOne.add("Airplane");

		// create list #2 and store values
		List<String> valSetTwo = new ArrayList<String>();
		valSetTwo.add("Bat");
		valSetTwo.add("Banana");
		valSetTwo.add("Beep");

		// put values into map
		map.put("A", valSetOne);
		map.put("B", valSetTwo);
		
		// iterate to display values
		System.out.println("Fetching Keys and Corresponding, Multiple Values");
		for (Map.Entry<String, List<String>> entry: map.entrySet()) {
			String key = entry.getKey();
			List<String> values = entry.getValue();
			System.out.print("Key = " + key + " >> ");
			System.out.println("Values = " + values);
		}
		
		// read device times file
		String fname = "c:/temp/sensortimes.txt";
		String ftxt = getTextFromFile(fname);
		System.out.print(ftxt);

		// try tuple2
		Tuple2<Integer, Boolean> returnValue = Tuples.tuple2(1, true);
		System.out.println(returnValue.t1);
		System.out.println(returnValue.t2);
		
		// try parse date time from string
        String s1 = "12/31/2013 23:59:56";
        String s2 = "01/01/2014 00:00:03";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        try
        {
            Date date1 = simpleDateFormat.parse(s1);
            Date date2 = simpleDateFormat.parse(s2);

            System.out.println("date1: " + simpleDateFormat.format(date1));
            System.out.println("date1: " + simpleDateFormat.format(date2));            
            
            Calendar start = Calendar.getInstance();
            start.setTime(date1);
            Calendar end = Calendar.getInstance();
            end.setTime(date2);            
            
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
        catch (ParseException ex)
        {
            System.out.println("Exception " + ex);
        }		
		
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