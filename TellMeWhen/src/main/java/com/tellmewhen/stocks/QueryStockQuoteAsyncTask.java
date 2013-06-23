package com.tellmewhen.stocks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

public class QueryStockQuoteAsyncTask extends AsyncTask<String, Void, String[]> {
	private static String TAG = QueryStockQuoteAsyncTask.class.getSimpleName();
	private AlertListActivity activity;
	private String stockSymbol;

	public QueryStockQuoteAsyncTask(Activity activity) {
		this.activity = (AlertListActivity) activity;
	}

	@Override
	protected void onPreExecute() {

	}

	@Override
	protected String[] doInBackground(String... stockSymbol) {
		this.stockSymbol = stockSymbol[0];
		String result[] = new String[2];
		String YQLResponse = getYQLResponse();
		Log.d(TAG, YQLResponse);
		try {

			JSONObject quote = new JSONObject(YQLResponse)
			.getJSONObject("query")
			.getJSONObject("results")
			.getJSONObject("quote");

			if (!quote.getString("StockExchange").equals("null")){
				result[0] = quote.getString("Name");
				result[1] = quote.getString("LastTradePriceOnly");
			}

		} catch (JSONException e) {
			Log.d(TAG, "JSON Failed: " + e);
		}
		return result;
	}

	@Override
	protected void onPostExecute(String[] result) {
		FragmentManager fm = activity.getFragmentManager();
		NewAlertFragment newAlertFragment = (NewAlertFragment) fm.findFragmentById(R.id.new_alert_container);
		newAlertFragment.setStockName(result[0]);
		newAlertFragment.setCurrentPrice(result[1]);
		newAlertFragment.updateHelpText();
	}

	private String getYQLResponse() {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(YQLRequestString());
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e(TAG, "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}


	private String YQLRequestString() {
        //TODO Move Url to an external String resource
		return "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20(%22" +
				stockSymbol +
				"%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

	}
}
