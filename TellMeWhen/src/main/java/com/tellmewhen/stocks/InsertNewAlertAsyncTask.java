package com.tellmewhen.stocks;

import java.io.IOException;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.com.tellmewhen.stocks.stockpricealertendpoint.Stockpricealertendpoint;
import com.google.api.services.com.tellmewhen.stocks.stockpricealertendpoint.model.StockPriceAlert;

import android.os.AsyncTask;
import android.util.Log;

public class InsertNewAlertAsyncTask extends 
	AsyncTask<String, Void, StockPriceAlert>{
	private static String TAG = InsertNewAlertAsyncTask.class.getSimpleName();
	
	private AlertListActivity activity;
	private Stockpricealertendpoint stockpricealertendpoint;
	private StockPriceAlert newStockPriceAlert;
	private String deviceInfoId;
	private String stockSymbol;
	private String conditional;
	private String stockPrice;
	
	public InsertNewAlertAsyncTask(AlertListActivity activity) {
		this.activity = activity;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		Stockpricealertendpoint.Builder stockpricealertendpointBuilder = new Stockpricealertendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				new HttpRequestInitializer() {
					public void initialize(HttpRequest httpRequest) {
					}
				});
		stockpricealertendpoint = CloudEndpointUtils.updateBuilder(stockpricealertendpointBuilder).build();
	}
	
	@Override
	protected StockPriceAlert doInBackground(String... params) {
		Log.d(TAG, "doInBackgournd()");
		StockPriceAlert result = null;
		deviceInfoId = params[0];
		stockSymbol = params[1];
		conditional = params[2];
		stockPrice = params[3];
		
		
		if (!deviceInfoId.equals("")) {
			newStockPriceAlert = new StockPriceAlert();
			try {
				result = stockpricealertendpoint.insertStockPriceAlert(
						newStockPriceAlert
						.setDeviceInfoId(deviceInfoId)
						.setStockSymbol(stockSymbol)
						.setConditional(conditional)
						.setStockPrice(Double.parseDouble(stockPrice))
						.setCreatedTimestamp(System.currentTimeMillis())
						.setCheckedTimestamp(null)
						.setActive(true)
						.setSatisfied(false)
						).execute();

			} catch (IOException e) {
				Log.e(TAG, "insertStockPriceAlert failed.");
			}
		}
		return result;
	}
	
	@Override
	protected void onPostExecute(StockPriceAlert result) {
		
	}

}