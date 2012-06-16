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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

public class TimeZoneEditDialogListAdapter extends ArrayAdapter<WorldClockTimeZone> {
	private List<WorldClockTimeZone> originalDataValues;
	private List<WorldClockTimeZone> filteredDataValues;
	private Filter filter = null;

	public TimeZoneEditDialogListAdapter(Context context, List<WorldClockTimeZone> tzValues) {
		super(context, R.layout.timezone_edit_dialog_list, R.id.dialog_list_display_label, tzValues);

		this.originalDataValues = new ArrayList<WorldClockTimeZone>();
		this.originalDataValues.addAll(tzValues);
		
		this.filteredDataValues = new ArrayList<WorldClockTimeZone>();
		this.filteredDataValues.addAll(tzValues);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater li = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			convertView = li.inflate(R.layout.timezone_edit_dialog_list_item,
					null);
		}

		WorldClockTimeZone tz = filteredDataValues.get(position);

		// display label
		TextView displayLabel = (TextView) convertView
				.findViewById(R.id.dialog_list_display_label);
		displayLabel.setText(tz.getId());

		// offset
		TextView displayOffset = (TextView) convertView
				.findViewById(R.id.dialog_list_offset_label);
		displayOffset.setText(tz.getRawOffsetDisplay());

		// image icon
		ImageView displayIcon = (ImageView) convertView
				.findViewById(R.id.dialog_list_icon);
		// TODO
		// image.setImageResource(R.drawable.android);

		return convertView;
	}

	@Override
	public Filter getFilter() {
		if(filter==null){
			filter=new TimeZoneFilter();
		}
		return filter;
	}

	private class TimeZoneFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();
			
			if (prefix == null || prefix.length() == 0) {				
				List<WorldClockTimeZone> list = new ArrayList<WorldClockTimeZone>(originalDataValues);	                
	            results.values = list;
	            results.count = list.size();				
			} else {
				String prefixString = prefix.toString().toLowerCase();
				List<WorldClockTimeZone> values = new ArrayList<WorldClockTimeZone>(originalDataValues);				
				final int count = values.size();
				final List<WorldClockTimeZone> newValues = new ArrayList<WorldClockTimeZone>();

				for (int i = 0; i < count; i++) {
					final WorldClockTimeZone value = values.get(i);
					final String valueText = value.toString().toLowerCase();

					// First match against the whole, non-splitted value
					if (valueText.contains(prefixString)) {
						newValues.add(value);
					}
				}

				results.values = newValues;
				results.count = newValues.size();
			}

			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			filteredDataValues = (List<WorldClockTimeZone>) results.values;
			
			notifyDataSetChanged();
            clear();
            for(int i = 0; i < filteredDataValues.size(); i++){
                add(filteredDataValues.get(i));
            }
            notifyDataSetInvalidated();            
		}
	}

}
