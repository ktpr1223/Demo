package com.example.project;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.project.R;
import com.example.project.ApiXmlParser.Entry;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class NetworkActivity extends Activity {
    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    private static  String URL = null;

    private static boolean wifiConnected = false;

    private static boolean mobileConnected = false;

    public static boolean refreshDisplay = true;

    public static String sPref = null;

    private NetworkReceiver receiver = new NetworkReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("åüçıåãâ ");           
        // Get the message from the intent
        Intent intent = getIntent();
        
        String category = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
      
        // URL setting 
        URL = "http://export.arxiv.org/api/query?search_query=cat:"
        		+ category
        		+ "&amp;start="
        		+ "0"
        		+ "&amp;max_results="
        		+ "10"
        		+ "&amp;sortBy=lastUpdatedDate&amp;sortOrder=descending";
        

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);
    }

  
    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        sPref = sharedPrefs.getString("listPref", "Wi-Fi");

        updateConnectedFlags();

        if (refreshDisplay) {
            loadPage();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

   
    private void updateConnectedFlags() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }

  
    private void loadPage() {
        if (((sPref.equals(ANY)) && (wifiConnected || mobileConnected))
                || ((sPref.equals(WIFI)) && (wifiConnected))) {
    
            new DownloadXmlTask().execute(URL);
        } else {
            showErrorPage();
        }
    }

  
    private void showErrorPage() {
        setContentView(R.layout.activity_network);
  
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.loadData(getResources().getString(R.string.connection_error),
                "text/html", null);
    }

  
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.network, menu);
        return true;
    }

 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.settings:
                Intent settingsActivity = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(settingsActivity);
                return true;
        case R.id.refresh:
                loadPage();
                return true;
        default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class DownloadXmlTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return getResources().getString(R.string.connection_error);
            } catch (XmlPullParserException e) {
                return getResources().getString(R.string.xml_error);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            setContentView(R.layout.activity_network);
   
            WebView myWebView = (WebView) findViewById(R.id.webview);
            // setWebViewClient!!
            myWebView.setWebViewClient(new WebViewClientSample());
            myWebView.loadData(result, "text/html", null);
        }
    }

   
    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        ApiXmlParser ApiXmlParser = new ApiXmlParser();
        List<Entry> entries = null;
        String title = null;
        String url = null;
        String summary = null;
        Calendar rightNow = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("MMM dd h:mmaa");


        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean pref = sharedPrefs.getBoolean("summaryPref", false);

        
        // ï\é¶htmlÇÃèÄîıÅiñ`ì™ïîï™Åj
        StringBuilder htmlString = new StringBuilder();
        htmlString.append("<h3>" + getResources().getString(R.string.page_title) + "</h3>");
      /*  htmlString.append("<em>" + getResources().getString(R.string.updated) + " " +
                formatter.format(rightNow.getTime()) + "</em>");*/

        try {
            stream = downloadUrl(urlString);
            entries = ApiXmlParser.parse(stream);
                      
  
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        for (Entry entry : entries) {
            htmlString.append("<p><a href='");
            htmlString.append(entry.link);
            htmlString.append("'>" + entry.title + "</a></p>");

            if (pref) {
                htmlString.append(entry.summary);
            }
        }
        return htmlString.toString();
    }


    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }

    public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();


            if (WIFI.equals(sPref) && networkInfo != null
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

                refreshDisplay = true;
                Toast.makeText(context, R.string.wifi_connected, Toast.LENGTH_SHORT).show();


            } else if (ANY.equals(sPref) && networkInfo != null) {
                refreshDisplay = true;


            } else {
                refreshDisplay = false;
                Toast.makeText(context, R.string.lost_connection, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
