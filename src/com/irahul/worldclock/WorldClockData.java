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
			Log.d(TAG, "Loading saved data from file");

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

		try {
			FileOutputStream fos = context.openFileOutput(FILENAME,
					Context.MODE_PRIVATE);
			OutputStreamWriter osw = new OutputStreamWriter(fos);

			// write empty array into file
			osw.write(new JSONArray().toString());

			osw.flush();
			osw.close();
			fos.close();
		} catch (FileNotFoundException e) {
			throw new WorldClockException(e);
		} catch (IOException e) {
			throw new WorldClockException(e);
		}
	}

	/**
	 * Writes set to file (assume not null)
	 */
	private void updateFile() {
		try {
			FileOutputStream fos = context.openFileOutput(FILENAME,
					Context.MODE_PRIVATE);
			OutputStreamWriter osw = new OutputStreamWriter(fos);

			JSONArray arr = serialize();
			Log.d(TAG, "Writing to file" + arr.toString());

			osw.write(arr.toString());
			osw.flush();
			osw.close();
			fos.close();

		} catch (FileNotFoundException e) {
			throw new WorldClockException(e);
		} catch (IOException e) {
			throw new WorldClockException(e);
		} catch (JSONException e) {
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
