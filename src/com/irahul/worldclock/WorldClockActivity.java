/*
 * Copyright (C) 2012 iRahul.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.irahul.worldclock;

import java.util.TimeZone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

/**
 * Main world clock activity
 * 
 * @author rahul
 * 
 */
public class WorldClockActivity extends Activity {	
	private static final String TAG = WorldClockActivity.class.getName();
	//
	//Intent extras map keys
	//IN - sent to edit dialog
	//OUT - received in onActivityResult
	//
	public static final String INTENT_TZ_DISPLAYNAME_IN = "INTENT_TZ_DISPLAYNAME_IN";
	public static final String INTENT_TZ_ID_IN = "INTENT_TZ_ID_IN";
	public static final String INTENT_TZ_DISPLAYNAME_OUT = "INTENT_TZ_DISPLAYNAME_OUT";
	public static final String INTENT_TZ_ID_OUT = "INTENT_TZ_ID_OUT";
	
	//Request codes for intent 
	private static final int REQ_CODE_ADD_ZONE = 0;
	private static final int REQ_CODE_EDIT_ZONE = 1;
		
	private WorldClockData data;
	private TimeZoneListAdapter adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.worldclock_main);
		
		//init data
		data = new WorldClockData(getApplicationContext());						
		int listSize = refreshListView();
		
		// register to get context event to edit/delete
		ListView mainListView = (ListView)findViewById(R.id.main_list_view);
		registerForContextMenu(mainListView);	
		
		Button mainAddButton = (Button)findViewById(R.id.main_button_add);
		mainAddButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				invokeAddZoneActivity();
			}			
		});
		
		//if no data exists then prompt to add
		if(listSize==0){
			startActivityForResult(new Intent(Intent.ACTION_INSERT), REQ_CODE_ADD_ZONE);
		}
	}

	/**
	 * Context menu to allow edit/delete of timezone
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_timezone_edit, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		
		ListView mainListView = (ListView)findViewById(R.id.main_list_view);		
		WorldClockTimeZone selectedTimeZone = (WorldClockTimeZone) mainListView.getAdapter().getItem(info.position);

		switch (item.getItemId()) {
		case R.id.menu_edit:
			// edit
			Intent editIntent = new Intent(Intent.ACTION_EDIT);
			editIntent.putExtra(INTENT_TZ_ID_IN, selectedTimeZone.getId());
			editIntent.putExtra(INTENT_TZ_DISPLAYNAME_IN, selectedTimeZone.getDisplayName());						
			startActivityForResult(editIntent, REQ_CODE_EDIT_ZONE);
			return true;
		case R.id.menu_delete:
			// delete
			data.deleteZone(selectedTimeZone);
			refreshListView();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intentReceived) {
		Log.d(TAG, "activity requestcode="+requestCode+" resultCode="+resultCode+ "data="+intentReceived);
		//process response from add/edit activity
		if(resultCode==RESULT_OK){
			switch(requestCode){
			case REQ_CODE_ADD_ZONE:
				Log.d(TAG, "Add zone id"+intentReceived.getStringExtra(INTENT_TZ_ID_OUT)+ " name="+intentReceived.getStringExtra(INTENT_TZ_DISPLAYNAME_OUT));
				data.addZone(new WorldClockTimeZone(TimeZone.getTimeZone(intentReceived.getStringExtra(INTENT_TZ_ID_OUT)),intentReceived.getStringExtra(INTENT_TZ_DISPLAYNAME_OUT)));
				break;
			case REQ_CODE_EDIT_ZONE:
				Log.d(TAG, "EDIT - Remove zone id"+intentReceived.getStringExtra(INTENT_TZ_ID_IN)+ " name="+intentReceived.getStringExtra(INTENT_TZ_DISPLAYNAME_IN));
				Log.d(TAG, "EDIT - Add zone id"+intentReceived.getStringExtra(INTENT_TZ_ID_OUT)+ " name="+intentReceived.getStringExtra(INTENT_TZ_DISPLAYNAME_OUT));
				data.deleteZone(new WorldClockTimeZone(TimeZone.getTimeZone(intentReceived.getStringExtra(INTENT_TZ_ID_IN))));
				data.addZone(new WorldClockTimeZone(TimeZone.getTimeZone(intentReceived.getStringExtra(INTENT_TZ_ID_OUT)),intentReceived.getStringExtra(INTENT_TZ_DISPLAYNAME_OUT)));				
				break;
			default:
				throw new WorldClockException("Unsupported request code!");
			}
		}
		
		//refresh data 
		refreshListView();		
		super.onActivityResult(requestCode, resultCode, intentReceived);
	}

	/**
	 * Present options menu to add timezones
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}

	/**
	 * Handle menu item selects
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add:
			// add a new zone
			invokeAddZoneActivity();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void invokeAddZoneActivity() {
		startActivityForResult(new Intent(Intent.ACTION_INSERT), REQ_CODE_ADD_ZONE);
	}
	
	/**
	 * Refresh list of timezones
	 * @return number of items in list after refresh
	 */
	private int refreshListView() {
		WorldClockTimeZone[] values = data.getSavedTimeZones().toArray(
				new WorldClockTimeZone[] {});

		Log.d(TAG, "Loaded data size for refresh:" + values.length);

		adapter = new TimeZoneListAdapter(this, values);
		
		ListView mainListView = (ListView)findViewById(R.id.main_list_view);
		mainListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();		
		return values.length;
	}
}