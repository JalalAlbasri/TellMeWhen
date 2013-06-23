package com.tellmewhen.stocks;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.api.services.com.tellmewhen.stocks.stockpricealertendpoint.model.StockPriceAlert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlertListArrayAdapter extends ArrayAdapter<StockPriceAlert> {

    @Override
    public StockPriceAlert getItem(int position) {
        return super.getItem(position);
    }

    int resource;

	public AlertListArrayAdapter(Context context, int resource,
			List<StockPriceAlert> alerts) {
		super(context, resource, alerts);

		this.resource = resource;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout alertItemView;
		StockPriceAlert alert = getItem(position);
		String alertDetails = alert.getStockSymbol() + " " + alert.getConditional() + " " + alert.getStockPrice();
		Long createdDateLong = alert.getCreatedTimestamp();
		Date createdDate = new Date(createdDateLong);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		String createdDateString = sdf.format(createdDate);

		if (convertView == null) {
			alertItemView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater li = (LayoutInflater) getContext().getSystemService(inflater);
			li.inflate(resource, alertItemView, true);
		} else {
			alertItemView = (LinearLayout) convertView;
		}

		TextView alertDetailsView = (TextView) alertItemView.findViewById(R.id.alert_item_details);
		TextView alertDateView = (TextView) alertItemView.findViewById(R.id.alert_item_date);

		alertDetailsView.setText(alertDetails);
		alertDateView.setText(createdDateString);

		return alertItemView;
	}

}