package com.example.project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

public class SendDropBox extends Activity {

	// for URL
	String url = null;
	String url2 = null;
	
	// for DropBox
	final static private String APP_KEY = "a36u5kxmooacbwb";
	final static private String APP_SECRET = "epqu5cswri3bd5z";
	final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;

  
	// In the class declaration section:
	private DropboxAPI<AndroidAuthSession> mDBApi;


    final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private static final boolean USE_OAUTH1 = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_send_drop_box);
		
		// Get the message from the intent
        Intent intent = getIntent();        
        url = intent.getStringExtra(WebViewClientSample.EXTRA_MESSAGE);
        url2 = url;
        url = url.replace("abs", "pdf");
               	
		

        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        mDBApi.getSession().startOAuth2Authentication(SendDropBox.this);
        
        // view
        setContentView(R.layout.activity_send_drop_box);
        checkAppKeySetup();
        setTitle("åüçıåãâ ");   
        
        WebView myview2 = (WebView)findViewById(R.id.webView2);
		myview2.loadUrl(url);
        
	}

	protected void onResume() {
	    super.onResume();

	    if (mDBApi.getSession().authenticationSuccessful()) {
	        try {
	            mDBApi.getSession().finishAuthentication();
	            String accessToken = mDBApi.getSession().getOAuth2AccessToken();
	        } catch (IllegalStateException e) {
	            Log.i("DbAuthLog", "Error authenticating", e);
	        }
	    }	    
					
	}
	
	// pdf upload
	public void onClick(View view) {
	  String f = null;
	
	  try {
		  URL urls = new URL(url);
		  f = urls.getFile();
		  
	  } catch (MalformedURLException e){}
	  f = f.replace("/pdf/","");
	  	  
	  String filePath= Environment.getExternalStorageDirectory() + 
	    		"/Download/"
	    		+ f + ".pdf";
	
  	  File file = new File(filePath);
  	  String DIR = "/test/";
  	  Upload upload = new Upload(this, mDBApi, DIR, file);
  	  upload.execute();

  }
	// memo upload
	public void onClick2(View view) {
		EditText txtMemo = (EditText)findViewById(R.id.txtMemo);
		Date Date = new Date();
		try {
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(
							openFileOutput(Date.toString() + ".txt", Context.MODE_PRIVATE)));
			writer.write(txtMemo.getText().toString());
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String filePath= "data/data/com.example.project/files/"
		    		+ Date.toString()
		    		+".txt";
		File file = new File(filePath);
	  	String DIR = "/test/";
	  	Upload upload = new Upload(this, mDBApi, DIR, file);
	  	upload.execute();
		
	}
	
	// view pdf on app
	public void onClick3(View view) {
		WebView myview = (WebView)findViewById(R.id.webView1);
		myview.getSettings().setJavaScriptEnabled(true);
		String pdfLink = url;
		myview.loadUrl("http://docs.google.com/gview?embedded=true&url=" + pdfLink);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.activity_send_drop_box, container,
					false);
			return rootView;
		}
	}
	
	// aaaaaa
	
	private void checkAppKeySetup() {

        if (APP_KEY.startsWith("CHANGE") ||
                APP_SECRET.startsWith("CHANGE")) {
            showToast("You must apply for an app key and secret from developers.dropbox.com, and add them to the DBRoulette ap before trying it.");
            finish();
            return;
        }

        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        String scheme = "db-" + APP_KEY;
        String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
        testIntent.setData(Uri.parse(uri));
        PackageManager pm = getPackageManager();
        if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
            showToast("URL scheme in your app's " +
                    "manifest is not set up correctly. You should have a " +
                    "com.dropbox.client2.android.AuthActivity with the " +
                    "scheme: " + scheme);
            finish();
        }
    }

	private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }

	
    private void loadAuth(AndroidAuthSession session) {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0) return;

        if (key.equals("oauth2:")) {
    
            session.setOAuth2AccessToken(secret);
        } else {
    
            session.setAccessTokenPair(new AccessTokenPair(key, secret));
        }
    }


    private void storeAuth(AndroidAuthSession session) {

        String oauth2AccessToken = session.getOAuth2AccessToken();
        if (oauth2AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, "oauth2:");
            edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.commit();
            return;
        }

        AccessTokenPair oauth1AccessToken = session.getAccessTokenPair();
        if (oauth1AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, oauth1AccessToken.key);
            edit.putString(ACCESS_SECRET_NAME, oauth1AccessToken.secret);
            edit.commit();
            return;
        }
    }

    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);

        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }
}
