package com.example.ken.checksams;

// TODO move sounds to sd card with generic tell-tale names
// like checksams_alarm_mp3 and checksams_chime_mp3
// try to load from those locations; catch with default sounds

// FIXME how about themes for "home" and "work" with different sound settings
// based on day of week and time of day

// FIXME figure out how to replace deprecated Http calls

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.webkit.WebView;

import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeMap;

public class MainActivity extends Activity  {

    private WebView  mWebViewKu;
    private TextView mTextViewResult;
    private TextView mTextViewDevices;

    public TextView mTextViewPrefs;

    //private Uri mChosenRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    private Uri mChosenRingtoneUri = Uri.parse("android.resource://com.example.ken.checksams/" + R.raw.quindar_push_rel_zing_mp3);
    private final int mREQUESTCODE_PICK_RINGTONE = 5;
    private final int mREQUESTCODE_PICK_AUDIO = 4;

    private int mAlarmCount;
    private AlarmManager mAlarmMgr;
    private Intent mAlarmRcvIntent;
    private PendingIntent mAlarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextClock tcClock = (TextClock) findViewById(R.id.textClock);
        mTextViewResult = (TextView) findViewById(R.id.resultRichTextView);
        mTextViewDevices = (TextView) findViewById(R.id.devicesRichTextView);

        mWebViewKu = (WebView) findViewById(R.id.kuAosWebView);

        tcClock.setFormat24Hour("H:mm");

        showToast("Get SAMS info from web...", Toast.LENGTH_SHORT);
        updateSensorTimesHtmlWebView();
        updateSensorTimesTextView();

        // FIXME set default pref values in MainActivity does not work
        // set default preference values
        //PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // make our ClickableSpans and URLSpans work
        mTextViewResult.setMovementMethod(LinkMovementMethod.getInstance());

        // pump our styled text into the TextView
        //mTextViewResult.setText(getSampleClunky(), TextView.BufferType.SPANNABLE);
        mTextViewResult.setText(getSampleSpannable(), TextView.BufferType.SPANNABLE);

        mTextViewPrefs = (TextView) findViewById(R.id.txtPrefs);

        displaySharedPreferences();

        loopNotifySound(2);

        showToast("READY");

    }

    private SpannableString getSampleSpannable() {
        SpannableStringBuilder deviceTime = new SpannableStringBuilder();

        // year:doy:hour is plain
        deviceTime.append("2015:165:11:");

        // minute:second is bold
        int start = deviceTime.length();
        deviceTime.append("32:14 ");
        deviceTime.setSpan(new ForegroundColorSpan(Color.WHITE), start, deviceTime.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        deviceTime.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, deviceTime.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // device is linked to real-time plot
        start = deviceTime.length();
        deviceTime.append("Ku_AOS");
        deviceTime.setSpan(new URLSpan("http://pims.grc.nasa.gov/plots/sams/121f03/121f03.jpg"), start, deviceTime.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //deviceTime.setSpan(new ForegroundColorSpan(0xFFCC5500), start, deviceTime.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //deviceTime.setSpan(new BackgroundColorSpan(Color.BLUE), start, deviceTime.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // category is just a plain span
        deviceTime.append(" gse_packet");

        return SpannableString.valueOf(deviceTime);
    }

    private void updateSensorTimesTextView() {
        String url2 = "http://pims.grc.nasa.gov/plots/user/sams/status/sensortimes.txt";
        new ProcessSensorTimesTxtFromWeb().execute(url2);
        mTextViewResult.setText("One-liner for results will change via AsyncTask...");
        mTextViewResult.setTextColor(Color.YELLOW);
    }

    private void updateSensorTimesHtmlWebView() {
        String url = "http://pims.grc.nasa.gov/plots/user/sams/status/sensortimes.html";
        mWebViewKu.loadUrl(url);
    }

    private void showToast(String s, int duration) {
        Context context = getApplicationContext();
        //int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, s, duration);
        toast.show();
    }

    private void showToast(String s) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, s, duration);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Settings via MyPreferenceActivity
            // --------
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, MyPreferenceActivity.class);
                startActivity(intent);
                displaySharedPreferences();
                return true;

            // Refresh
            // -------
            case R.id.menu_action1:
                updateSensorTimesHtmlWebView();
                updateSensorTimesTextView();
                soundTheAlarm();
                return true;

            // Show prefs
            // ----
            case R.id.menu_action2:
                displaySharedPreferences();
                return true;

            // SD Card prefs
            // -------
            case R.id.menu_action3:
                Log.i("Kenfo", "SD Card select...");
                pickFile();
                return true;

            // Ringtone prefs
            // --------
            case R.id.menu_action4:
                Log.i("Kenfo", "Ringtone select...");
                pickRingtone();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // FIXME only sound the alarm under the right conditions, but for now just do it
    private void soundTheAlarm() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean alarmOnCheckBox = prefs.getBoolean("alarmOnCheckBox", false);
        if (alarmOnCheckBox) {
            String listPrefs = prefs.getString("alarmRepeatListPref", "Default list prefs");
            int count = Integer.parseInt(listPrefs);
            Log.i("soundTheAlarm", "loopNotifySound " + count + " times");
            loopNotifySound(count);
        }
    }

    private void pickRingtone() {
        Intent ringtoneIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        this.startActivityForResult(ringtoneIntent, this.mREQUESTCODE_PICK_RINGTONE);
    }

    private void pickFile() {
        Intent mediaIntent = new Intent(Intent.ACTION_GET_CONTENT);
        mediaIntent.setType("audio/*"); //set mime type as per requirement
        this.startActivityForResult(mediaIntent, this.mREQUESTCODE_PICK_AUDIO);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (resultCode == Activity.RESULT_OK && requestCode == this.mREQUESTCODE_PICK_RINGTONE) {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) { this.mChosenRingtoneUri = uri; }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == this.mREQUESTCODE_PICK_AUDIO) {
            Uri uri = intent.getData();
            if (uri != null) { this.mChosenRingtoneUri = uri; }
        }
    }

    public void loopNotifySound(int repeat) {
        final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), this.mChosenRingtoneUri);
        final int count = repeat - 1;
        new Thread(new Runnable() {
            public void run() {
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    int n = 0;
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (n < count) {
                            mp.start();
                            n++;
                        }
                    }
                });
                mp.start();
            }
        }).start();
        //Log.i("Kenfo", "done playing " + mChosenRingtoneUri.toString());
    }

    // TODO make prefs screen itself show ALL current values (in subtitles or via checks)
    // TODO get rid of this method when prefs screen's subtitles show current value
    private void displaySharedPreferences() {
        // get shared prefs
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String period = prefs.getString("period", "Default NickName");
        String nchk = prefs.getString("numChecks", "Default NumChecks");
        boolean alarmOnCheckBox = prefs.getBoolean("alarmOnCheckBox", false);
        String listPrefs = prefs.getString("alarmRepeatListPref", "Default list prefs");
        boolean es03rtCheckBox = prefs.getBoolean("es03rtCheckBox", false);
        boolean es05rtCheckBox = prefs.getBoolean("es05rtCheckBox", false);
        boolean es06rtCheckBox = prefs.getBoolean("es06rtCheckBox", false);

        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append("\n" + "Period (min): " + period + "\n");
        builder.append("Number of checks: " + nchk + "\n");
        builder.append("Alarm sound on: " + String.valueOf(alarmOnCheckBox) + "\n");
        builder.append("Alarm repeat count: " + listPrefs +"\n");
        builder.append("es03rtCheckBox: " + String.valueOf(es03rtCheckBox) + "\n");
        builder.append("es05rtCheckBox: " + String.valueOf(es05rtCheckBox) + "\n");
        builder.append("es06rtCheckBox: " + String.valueOf(es06rtCheckBox) + "\n");

        mTextViewPrefs.setText(builder.toString());
        Log.i("Ken Prefs are:", builder.toString());
    }

    private class ProcessSensorTimesTxtFromWeb extends AsyncTask<String, Void, String> {

        ProgressDialog dialog ;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // show progress dialog when downloading
            dialog = ProgressDialog.show(MainActivity.this, null, "Downloading...");
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                // FIXME with non-deprecated code
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(params[0]);
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();

                BufferedHttpEntity buf = new BufferedHttpEntity(entity);

                InputStream is = buf.getContent();

                BufferedReader r = new BufferedReader(new InputStreamReader(is));

                // FIXME should this be StringBuilder not SpannableStringBuilder?
                SpannableStringBuilder total = new SpannableStringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    if(line.startsWith("begin") || line.startsWith("end") || line.startsWith("yyyy")) continue;
                    total.append(line + "\n");
                }
                String result = total.toString();
                Log.i("Get URL", "Downloaded string: " + result);
                return result;
            } catch (Exception e) {
                Log.e("Get Url", "Error in downloading: " + e.toString());
            } finally {
                // FIXME do we need some equivalent of the following:
                // urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // show result in textView
            if (result == null) {
                mTextViewDevices.setText("Error in downloading. Please try again.");
            } else {

                // at this point, result is big string: several DeviceDeltas lines with newline characters
                TreeMap<String,DeviceDeltas> sorted_map = DeviceDeltas.getSortedMap(result);

                // FIXME with better way to handle these prefs
                List<String> ignore_devices = new ArrayList<String>();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                boolean es03rtCheckBox = prefs.getBoolean("es03rtCheckBox", false);
                boolean es05rtCheckBox = prefs.getBoolean("es05rtCheckBox", false);
                boolean es06rtCheckBox = prefs.getBoolean("es06rtCheckBox", false);
                if (!es03rtCheckBox) { ignore_devices.add("es03rt"); }
                if (!es05rtCheckBox) { ignore_devices.add("es05rt"); }
                if (!es06rtCheckBox) { ignore_devices.add("es06rt"); }

                // FIXME use different input profile in DigestDevices to get ranges from prefs...
                // ...AFTER adding XML for ranges FIRST.

                // now we have sorted map, so iterate over devices to digest info
                DigestDevices digestDevices = new DigestDevices(sorted_map, ignore_devices);
                digestDevices.processMap();

/*                Log.i("DIGEST", "bad ho count = " + digestDevices.getCountBadDeltaHosts());
                Log.i("DIGEST", "bad ku count = " + digestDevices.getCountBadDeltaKus());
                Log.i("DIGEST", "ho range = " + digestDevices.getDeltaHostRange().toString());
                Log.i("DIGEST", "ku range = " + digestDevices.getDeltaKuRange().toString());*/

                // make our ClickableSpans and URLSpans work
                mTextViewDevices.setMovementMethod(LinkMovementMethod.getInstance());

                // populate top result (one-liner) textview with alarm results in spannable text form
                mTextViewResult.setText(digestDevices.getResultOneLiner(), TextView.BufferType.SPANNABLE);

                // populate devices textview with device times info in spannable form
                mTextViewDevices.setText(digestDevices.getDeviceLines(), TextView.BufferType.SPANNABLE);

            }

            Typeface font = Typeface.createFromAsset(getAssets(), "fonts/UbuntuMono-R.ttf");
            mTextViewDevices.setTypeface(font);

            // close progresses dialog
            dialog.dismiss();

        }
    }

}