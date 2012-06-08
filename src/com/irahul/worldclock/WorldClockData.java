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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

/**
 * Data storage model - translate this to/from JSON
 * 
 * @author rahul
 * 
 */
public class WorldClockData {
	private static final String TAG = WorldClockData.class.getName();
	private static final String DISPLAY_NAME = "displayName";
	private static final String TIMEZONE_ID = "timezoneId";
	private static final String FILENAME = "WorldClockData";	
	private Set<WorldClockTimeZone> selectedTimeZones = null;
	private Context context;

	public WorldClockData(Context context) {
		this.context = context;

		// load timezones if not available
		if (this.selectedTimeZones == null) {
			try {
				FileInputStream fis = context.openFileInput(FILENAME);
				StringBuilder fileData = new StringBuilder();

				byte[] buffer = new byte[1];
				while (fis.read(buffer) != -1) {
					fileData.append(new String(buffer));
				}

				Log.d(TAG, "RAW - Loaded from file:" + fileData.toString());
				selectedTimeZones = deserialize(fileData.toString());

			} catch (FileNotFoundException e) {
				// no file exists - treat as empty list and create file
				createFile();
				this.selectedTimeZones = new HashSet<WorldClockTimeZone>();
				
			} catch (IOException e) {
				throw new WorldClockException(e);
			} catch (JSONException e) {
				throw new WorldClockException(e);
			}
		}
	}

	public Set<WorldClockTimeZone> getSavedTimeZones() {
		return selectedTimeZones;
	}

	public void deleteZone(WorldClockTimeZone wcTimeZone) {
		Log.d(TAG, "Removing zone: " + wcTimeZone);
		
		selectedTimeZones.remove(wcTimeZone);

		updateFile();
	}

	public void addZone(WorldClockTimeZone wcTimeZone) {
		Log.d(TAG, "Adding zone: " + wcTimeZone);
		
		selectedTimeZones.add(wcTimeZone);

		updateFile();
	}

	/**
	 * Creates a blank file
	 */
	private void createFile() {
		Log.d(TAG, "Creating new file");
		
		// write empty array into file
		writeToFile(new JSONArray().toString());
	}

	/**
	 * Writes set to file (assume not null)
	 */
	private void updateFile() {		
		try {
			JSONArray arr = serialize();
			writeToFile(arr.toString());
			
		} catch (JSONException e) {
			throw new WorldClockException(e);
		}
	}
	
	private void writeToFile(String jsonString) {
		Log.d(TAG, "Writing JSON to file: " + jsonString);
		try {
			FileOutputStream fos = context.openFileOutput(FILENAME,
					Context.MODE_PRIVATE);
			OutputStreamWriter osw = new OutputStreamWriter(fos);

			osw.write(jsonString);
			osw.flush();
			osw.close();
			fos.close();

		} catch (FileNotFoundException e) {
			throw new WorldClockException(e);
		} catch (IOException e) {
			throw new WorldClockException(e);
		}
	}

	private JSONArray serialize() throws JSONException {
		JSONArray jsonArr = new JSONArray();
		for (WorldClockTimeZone tz : selectedTimeZones) {
			JSONObject jsonTz = new JSONObject();
			jsonTz.put(TIMEZONE_ID, tz.getId());
			jsonTz.put(DISPLAY_NAME, tz.getDisplayName());
			jsonArr.put(jsonTz);
		}
		return jsonArr;
	}

	private Set<WorldClockTimeZone> deserialize(String jsonString)
			throws JSONException {
		JSONArray jsonArr = new JSONArray(jsonString);

		Set<WorldClockTimeZone> arr = new HashSet<WorldClockTimeZone>();
		for (int i = 0; i < jsonArr.length(); i++) {
			JSONObject jsonObj = jsonArr.getJSONObject(i);

			WorldClockTimeZone tz = new WorldClockTimeZone(
					TimeZone.getTimeZone(jsonObj.getString(TIMEZONE_ID)));			
			tz.setDisplayName(jsonObj.getString(DISPLAY_NAME));

			arr.add(tz);
		}

		return arr;
	}

}
