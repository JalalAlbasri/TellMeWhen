package com.tellmewhen.stocks;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class AlertListViewBinder implements ViewBinder {
	private static final String TAG = AlertListViewBinder.class.getSimpleName();
	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		if (columnIndex == cursor.getColumnIndex(AlertContentProvider.KEY_CREATED_TIMESTAMP)) {
			
			Long createdDateLong = cursor.getLong(columnIndex);
			Date createdDate = new Date(createdDateLong);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
			String createdDateString = sdf.format(createdDate);
			
			TextView textView = (TextView) view;
			textView.setText(createdDateString);
			return true;
		}
		return false;
	}
	
}
