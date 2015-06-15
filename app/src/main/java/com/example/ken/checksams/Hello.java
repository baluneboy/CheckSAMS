import android.util.Log;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Hello {

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
		
	}
	
    private void getTextFromFile(String fname) {
        //Get the text file
        File file = new File(fname);

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
            Log.w("Unhandled IOException", e.getMessage());
            Log.v("Hello", "There");

        }

        Log.w("HERE IS text:", text.toString());
        String url = text.toString();

    }	

}
