package com.example.ken.checksams;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Environment;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

import static java.lang.String.*;

import com.example.ken.checksams.DeviceTimes;

public class MainActivity extends Activity  {

    Button bUpdate;
    Button bDetails;
    private WebView wvKuAos;
    private TextView tvResult;
    private TextView tvDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = (TextView) findViewById(R.id.resultRichTextView);
        tvDevices = (TextView) findViewById(R.id.devicesRichTextView);

        bUpdate = (Button) findViewById(R.id.updateButton);
        bDetails = (Button) findViewById(R.id.detailsButton);

        wvKuAos = (WebView) findViewById(R.id.kuAosWebView);
        wvKuAos.setWebViewClient(new MyBrowser());

        showToast("Initial async update of both WebViews...", Toast.LENGTH_LONG);
        updateKuClip();
        updateSensorTimes();

        bUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateKuClip();
                updateSensorTimes();
            }
        });

        bDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spanned tmp = Html.fromHtml("&cent;");
                String cent = tmp.toString();
                showToast("Time for a wise crack...");
                tvResult.setText(format("Insert 25%s for details!", cent));
                tvResult.setTextColor(Color.RED);
            }
        });

        // make our ClickableSpans and URLSpans work
        tvResult.setMovementMethod(LinkMovementMethod.getInstance());

        // shove our styled text into the TextView
        //tvResult.setText(getSampleClunky(), TextView.BufferType.SPANNABLE);
        tvResult.setText(getSampleSpannable(), TextView.BufferType.SPANNABLE);

    }

    private SpannableString getSampleSpannable() {
        SpannableStringBuilder deviceTime = new SpannableStringBuilder();

        // year:doy:hour is plain
        deviceTime.append("2015:165:11:");

        // minute:second is bold
        int start = deviceTime.length();
        deviceTime.append("32:14 ");
        deviceTime.setSpan(new ForegroundColorSpan(0xFFCC5500), start, deviceTime.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        deviceTime.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, deviceTime.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // device is linked to real-time plot
        start = deviceTime.length();
        deviceTime.append("Ku_AOS");
        deviceTime.setSpan(new URLSpan("http://pims.grc.nasa.gov/plots/sams/121f03/121f03.jpg"), start, deviceTime.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //deviceTime.setSpan(new ForegroundColorSpan(Color.GREEN), start, deviceTime.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //deviceTime.setSpan(new BackgroundColorSpan(Color.BLUE), start, deviceTime.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // category is just a plain span
        deviceTime.append(" gse_packet");

        return SpannableString.valueOf(deviceTime);
    }

    private void updateSensorTimes() {
        String url2 = "http://pims.grc.nasa.gov/plots/user/sams/status/sensortimes.txt";
        new GetStringFromUrl().execute(url2);
        tvResult.setText("The money line might change here.");
        tvResult.setTextColor(Color.BLACK);
    }

    private void updateKuClip() {
        //Get the text file
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "Documents/getweb.txt");

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

        wvKuAos.loadUrl(url);

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

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetStringFromUrl extends AsyncTask<String, Void, String> {

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
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(params[0]);
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();

                BufferedHttpEntity buf = new BufferedHttpEntity(entity);

                InputStream is = buf.getContent();

                BufferedReader r = new BufferedReader(new InputStreamReader(is));

                // FIXME this should be StringBuilder not SpannableStringBuilder
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
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // show result in textView
            if (result == null) {
                tvDevices.setText("Error in downloading. Please try again.");
            } else {

                // at this point, result is big string with newline characters
                TreeMap<String,DeviceTimes> sorted_map = DeviceTimes.getSortedMap(result);

                // now we have sorted map, so iterate to build sorted, formatted spannables
                SpannableString resultSpannable = DeviceTimes.getSpannableFromMap(sorted_map);

                // make our ClickableSpans and URLSpans work
                tvDevices.setMovementMethod(LinkMovementMethod.getInstance());

                // populate textview with device times info in spannable form
                tvDevices.setText(resultSpannable, TextView.BufferType.SPANNABLE);

            }

            // close progresses dialog
            dialog.dismiss();
        }
    }

}