package com.example.project;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import android.R.bool;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	// for intent message
	public final static String EXTRA_MESSAGE ="com.example.project.message";
	public final static String EXTRA_MESSAGE2 ="com.example.project.message2";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//addPreferencesFromResource(R.xml.preferences);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_main);
		setTitle("Home");
		// List Settings
		ArrayList<String> data = new ArrayList<String>();
		/*data.add("Computational Finance");
		data.add("Statistics Theory");
		data.add("Economics");*/
		
		Iterator<String> ite = pref.getStringSet("multi", new HashSet<String>()).iterator();
		while (ite.hasNext()) {
			data.add(ite.next());
		}
						
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1
				, data) {
			// ï∂éöÇÃëÂÇ´Ç≥ïœçX
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView view = (TextView)super.getView(position,  convertView,  parent);
				view.setTextSize(15);
				return view;
			}
		};
		ListView list = (ListView)findViewById(R.id.list);
		list.setAdapter(adapter);
		
		// List2 Settings
		ArrayList<String> data2 = new ArrayList<String>();
		data2.add("QMC");
		data2.add("MCMC");
		
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1
				, data2){
					// ï∂éöÇÃëÂÇ´Ç≥ïœçX
					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						TextView view = (TextView)super.getView(position,  convertView,  parent);
						view.setTextSize(15);
						return view;
					}
				};
		ListView list2 = (ListView)findViewById(R.id.list2);
		list2.setAdapter(adapter2);
		
		
		// List onClick
		list.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> av, View view, int postition, long id) {
					//	String test = String.valueOf(id); 
						
						// set intent and category
						CharSequence msg = ((TextView)view).getText();
						msg.toString();
//						Toast.makeText(MainActivity.this, msg.toString(), Toast.LENGTH_SHORT).show();
						// category choice
						if(msg.equals("Computational Finance")) {
							Toast.makeText(MainActivity.this, msg.toString(), Toast.LENGTH_SHORT).show();
							msg = "q-fin.CP";
						}
						
						if(msg.equals("Statistics Theory")) {
							Toast.makeText(MainActivity.this, msg.toString(), Toast.LENGTH_SHORT).show();
							msg = "stat.TH";
						}
						
						if(msg.equals("Economics")) {
							Toast.makeText(MainActivity.this, msg.toString(), Toast.LENGTH_SHORT).show();
							msg = "q-fin.EC";
						}
						
						if(msg.equals("Mathematical Finance")) {
							Toast.makeText(MainActivity.this, msg.toString(), Toast.LENGTH_SHORT).show();
							msg = "q-fin.MF";
						}
						
						// set intent and send msg(category)
						Intent intent = new Intent(MainActivity.this,NetworkActivity.class);
					    intent.putExtra(EXTRA_MESSAGE, msg);
						startActivity(intent);
					}
				}
				);	
		
		// List2 onClick
		list2.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> av, View view, int postition, long id) {
						//	String test = String.valueOf(id); 
								
						// set intent and word
						CharSequence msg = ((TextView)view).getText();
						msg.toString();
								
						// word choice
						if(msg == "MCMC") {
							Toast.makeText(MainActivity.this, msg.toString(), Toast.LENGTH_SHORT).show();
							msg = "Markov+AND+Chain+AND+Monte+AND+Carlo";
						}
								
						if(msg == "QMC") {
							Toast.makeText(MainActivity.this, msg.toString(), Toast.LENGTH_SHORT).show();
							msg = "Quasi+AND+Monte+AND+Carlo";
						}
								
						// set intent and send msg(word)
						Intent intent = new Intent(MainActivity.this,NetworkActivity2.class);
						intent.putExtra(EXTRA_MESSAGE2, msg);
						startActivity(intent);
					}
				}
				);	
						
	}
	
    
	
	/** Called when the user clicks the Send button */
	public void sendMessage(View view) {
	    Intent intent = new Intent(this,NetworkActivity.class);
	    //EditText editText = (EditText) findViewById(R.id.edit_message);
//	    String message = editText.getText().toString();
	//    intent.putExtra(EXTRA_MESSAGE, message);
	    startActivity(intent);
	}
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/
	
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.settings:
                Intent settingsActivity = new Intent(getBaseContext(), SettingsActivity2.class);
                startActivity(settingsActivity);
                return true;
        default:
                return super.onOptionsItemSelected(item);
        }
    }
	
/*
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
	}*/

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.activity_main, container,
					false);
			return rootView;
		}
	}

}
