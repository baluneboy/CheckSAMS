package com.example.ken.checksams;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/*import java.text.SimpleDateFormat;
import java.util.Date;*/

/**
 * Created by pims on 7/4/15.
 */
public class MyAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // toast the alarm being triggered
        //Log.e("onReceive", "The abstract onReceive alarm message.");
        Toast.makeText(context, "The abstract onReceive alarm message.", Toast.LENGTH_SHORT).show();

    }

}