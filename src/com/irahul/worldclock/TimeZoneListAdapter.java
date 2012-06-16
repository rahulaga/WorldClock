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

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
/**
 * Custom view for list
 * 
 * Note: get view method renders each row in list
 * @author rahul
 *
 */
public class TimeZoneListAdapter extends ArrayAdapter<WorldClockTimeZone> {	
	//TODO - externalize into preferences in the future
	private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEEE hh:mm a");
	private WorldClockTimeZone[] displayTimeZones;
	
	public TimeZoneListAdapter(Context context, WorldClockTimeZone[] tzValues) {
		super(context, R.layout.list_itemview, R.id.list_display_label, tzValues);		
		this.displayTimeZones=tzValues;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		if(convertView==null){
			LayoutInflater li = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = li.inflate(R.layout.list_itemview, null);
		}
		
		WorldClockTimeZone tz = displayTimeZones[position];			
		TextView time = (TextView)convertView.findViewById(R.id.list_time_label);		
		DATE_FORMAT.setTimeZone(tz.getTimeZone());
		time.setText(DATE_FORMAT.format(new Date()));
		
		TextView displayName = (TextView)convertView.findViewById(R.id.list_display_label);
		displayName.setText(tz.getDisplayName());
		
		return convertView;
	}
	
}
