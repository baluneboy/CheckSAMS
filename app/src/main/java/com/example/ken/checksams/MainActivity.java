package com.example.ken.checksams;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Environment;
import android.text.Html;
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
                showToast("Time for a smart ass remark...");
                tvResult.setText(String.format("Insert 25%s for details!", cent));
                tvResult.setTextColor(Color.RED);
            }
        });

        // this is the text we'll be operating on
        SpannableString text = new SpannableString("Lorem ipsum dolor sit amet");

        // make "Lorem" (characters 0 to 5) red
        text.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, 0);

        // make "ipsum" (characters 6 to 11) one and a half time bigger than the textbox
        text.setSpan(new RelativeSizeSpan(1.5f), 6, 11, 0);

        // make "dolor" (characters 12 to 17) display a toast message when touched
        final Context context = this;
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "dolor", Toast.LENGTH_LONG).show();
            }
        };
        text.setSpan(clickableSpan, 12, 17, 0);

        // make "sit" (characters 18 to 21) struck through
        text.setSpan(new StrikethroughSpan(), 18, 21, 0);

        // make "amet" (characters 22 to 26) twice as big, green and a link to this site.
        // it's important to set the color after the URLSpan or the standard
        // link color will override it.
        text.setSpan(new RelativeSizeSpan(2f), 22, 26, 0);
        text.setSpan(new URLSpan("http://www.chrisumbel.com"), 22, 26, 0);
        text.setSpan(new ForegroundColorSpan(Color.GREEN), 22, 26, 0);
        text.setSpan(new BackgroundColorSpan(Color.BLUE), 22, 26, 0);

        SpannableString text2 = new SpannableString(" Ciao!");
        SpannableString text3 = new SpannableString( TextUtils.concat(text, text2) );

        text3.setSpan(new ForegroundColorSpan(Color.RED), 27, 31, 0);
        text3.setSpan(new BackgroundColorSpan(Color.BLACK), 27, 31, 0);

        // make our ClickableSpans and URLSpans work
        tvResult.setMovementMethod(LinkMovementMethod.getInstance());

        // shove our styled text into the TextView
        tvResult.setText(text3, TextView.BufferType.SPANNABLE);

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

//        @Override
//        protected String doInBackground(String... params) {
//
//            try {
//                DefaultHttpClient httpClient = new DefaultHttpClient();
//                HttpGet httpGet = new HttpGet(params[0]);
//                HttpResponse response = httpClient.execute(httpGet);
//                HttpEntity entity = response.getEntity();
//
//                BufferedHttpEntity buf = new BufferedHttpEntity(entity);
//
//                InputStream is = buf.getContent();
//
//                BufferedReader r = new BufferedReader(new InputStreamReader(is));
//
//                StringBuilder total = new StringBuilder();
//                String line;
//                while ((line = r.readLine()) != null) {
//                    total.append(line + "\n");
//                }
//                String result = total.toString();
//                Log.i("Get URL", "Downloaded string: " + result);
//                return result;
//            } catch (Exception e) {
//                Log.e("Get Url", "Error in downloading: " + e.toString());
//            }
//            return null;
//        }

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

                SpannableStringBuilder total = new SpannableStringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
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
                tvDevices.setText(result);
            }

            // close progresses dialog
            dialog.dismiss();
        }
    }

}