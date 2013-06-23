package com.tellmewhen.stocks;
import java.sql.Date;
import java.text.SimpleDateFormat;

import com.tellmewhen.stocks.R;
import com.google.api.services.com.tellmewhen.stocks.stockpricealertendpoint.model.StockPriceAlert;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class AlertListSimpleCursorAdapter extends SimpleCursorAdapter {

	Context context;
	int layout;

	public AlertListSimpleCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.context = context;
		this.layout = layout;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout alertItemView;

		Cursor cursor = getCursor();

		String stockSymbol = cursor.getString(cursor.getColumnIndex(AlertContentProvider.KEY_STOCK_SYMBOL));
		String conditional = cursor.getString(cursor.getColumnIndex(AlertContentProvider.KEY_CONDITIONAL));
		String stockPrice = cursor.getString(cursor.getColumnIndex(AlertContentProvider.KEY_STOCK_SYMBOL));
		Long createdDateLong = cursor.getLong(cursor.getColumnIndex(AlertContentProvider.KEY_CREATED_TIMESTAMP));
		Date createdDate = new Date(createdDateLong);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		String createdDateString = sdf.format(createdDate);

		if (convertView == null) {
			alertItemView = new LinearLayout(context);
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater li = (LayoutInflater) context.getSystemService(inflater);
			li.inflate(layout, alertItemView, true);
		} else {
			alertItemView = (LinearLayout) convertView;
		}

		TextView alertStockSymbolView = (TextView) alertItemView.findViewById(R.id.alert_item_stock_symbol);
		TextView alertConditionalView = (TextView) alertItemView.findViewById(R.id.alert_item_conditional);
		TextView alertStockPriceView = (TextView) alertItemView.findViewById(R.id.alert_item_stock_price);
		TextView alertDateView = (TextView) alertItemView.findViewById(R.id.alert_item_date);

		alertStockSymbolView.setText(stockSymbol);
		alertConditionalView.setText(conditional);
		alertStockPriceView.setText(stockPrice);
		alertDateView.setText(createdDateString);

		return alertItemView;

	}
}
