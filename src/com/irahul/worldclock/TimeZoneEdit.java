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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Activity to add a timezone or edit an existing one
 * 
 * @author rahul
 * 
 */
public class TimeZoneEdit extends Activity {
	private static final String TAG = TimeZoneEdit.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timezone_edit);

		// setup spinner
		Spinner spinner = (Spinner) findViewById(R.id.timezone_edit_spinner);
		ArrayAdapter<WorldClockTimeZone> adapter = new ArrayAdapter<WorldClockTimeZone>(
				this, android.R.layout.simple_spinner_item, getTimeZones());

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		// pick mode - add or edit
		final Intent intent = getIntent();

		final String action = intent.getAction();
		if (Intent.ACTION_EDIT.equals(action)) {
			TextView title = (TextView) findViewById(R.id.timezone_edit_title);
			title.setText(getString(R.string.title_timezone_edit));

			// editing zone info
			Log.d(TAG, "EDIT tz="+ intent.getStringExtra(WorldClockActivity.INTENT_TZ_ID_IN));
			Log.d(TAG, "EDTI display="+ intent.getStringExtra(WorldClockActivity.INTENT_TZ_DISPLAYNAME_IN));

			// pre-select in spinner
			spinner.setSelection(getPositionForZone(intent
					.getStringExtra(WorldClockActivity.INTENT_TZ_ID_IN)));

			// pre-select displayname
			EditText displayName = (EditText) findViewById(R.id.timezone_edit_displayname);
			displayName.setText(intent.getStringExtra(WorldClockActivity.INTENT_TZ_DISPLAYNAME_IN));

		} else if (Intent.ACTION_INSERT.equals(action)) {
			TextView title = (TextView) findViewById(R.id.timezone_edit_title);
			title.setText(getString(R.string.title_timezone_add));
			
			//spinner select action - update default display name
			//on edit intent we don't update custom display name
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
						int pos, long id) {				
					// pre-populate display name box with default
					WorldClockTimeZone selectedItem = (WorldClockTimeZone) parent.getItemAtPosition(pos);
					EditText displayName = (EditText) findViewById(R.id.timezone_edit_displayname);
					displayName.setText(selectedItem.getDisplayName());
				}

				public void onNothingSelected(AdapterView<?> arg0) {
					// do nothing
				}
			});

		} else {
			// unrecognized action - should never get here
			throw new WorldClockException("Unexpected intent received" + intent);
		}

		Button saveButton = (Button) findViewById(R.id.timezone_edit_save);
		saveButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Spinner spinner = (Spinner) findViewById(R.id.timezone_edit_spinner);
				WorldClockTimeZone selectedZone = (WorldClockTimeZone) spinner
						.getSelectedItem();

				EditText displayName = (EditText) findViewById(R.id.timezone_edit_displayname);

				intent.putExtra(WorldClockActivity.INTENT_TZ_ID_OUT, selectedZone.getId());
				intent.putExtra(WorldClockActivity.INTENT_TZ_DISPLAYNAME_OUT, displayName.getText().toString());
				setResult(RESULT_OK, intent);

				finish();
			}
		});

		Button buttonCancel = (Button) findViewById(R.id.timezone_edit_cancel);
		buttonCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});

	}

	private int getPositionForZone(String timeZoneId) {
		WorldClockTimeZone[] allZones = getTimeZones();

		for (int i = 0; i < allZones.length; i++) {
			if (allZones[i].getId().equals(timeZoneId))
				return i;
		}

		// not found
		return 0;
	}

	private WorldClockTimeZone[] getTimeZones() {
		String[] timezoneIds = TimeZone.getAvailableIDs();

		WorldClockTimeZone[] tzArray = new WorldClockTimeZone[timezoneIds.length];
		int count = 0;
		for (String id : timezoneIds) {
			tzArray[count++] = new WorldClockTimeZone(TimeZone.getTimeZone(id));
		}

		return tzArray;
	}
}
