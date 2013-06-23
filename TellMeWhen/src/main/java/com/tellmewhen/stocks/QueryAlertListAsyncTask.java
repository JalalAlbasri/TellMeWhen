package com.tellmewhen.stocks;
import java.io.IOException;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.com.tellmewhen.stocks.stockpricealertendpoint.Stockpricealertendpoint;
import com.google.api.services.com.tellmewhen.stocks.stockpricealertendpoint.model.CollectionResponseStockPriceAlert;

import android.app.FragmentManager;
import android.os.AsyncTask;
import android.util.Log;


public class QueryAlertListAsyncTask extends
	AsyncTask<String, Void, CollectionResponseStockPriceAlert> {

	private static String TAG = QueryAlertListAsyncTask.class.getSimpleName();

	private Stockpricealertendpoint stockpricealertendpoint;
	private AlertListActivity activity;


	public QueryAlertListAsyncTask(AlertListActivity activity) {
		this.activity = activity;
	}

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
	protected CollectionResponseStockPriceAlert doInBackground(String... deviceInfoId) {
		CollectionResponseStockPriceAlert result = null;
		Log.d(TAG, "deviceInfoId: " + deviceInfoId[0]);
		if (!deviceInfoId[0].equals("")){
			try {
				result = stockpricealertendpoint.listStockPriceAlertbyDeviceInfoId(deviceInfoId[0]).execute();
			} catch (IOException e) {
				Log.e(TAG, "IOException received trying to retrieve alert list" + stockpricealertendpoint.getRootUrl(), e);
			}
		}
		return result;
	}

	@Override
	protected void onPostExecute(CollectionResponseStockPriceAlert result) {
		if (result != null) {
			FragmentManager fm = activity.getFragmentManager();
			AlertListFragment alertListFragment =  (AlertListFragment) fm.findFragmentById(R.id.alert_list_fragment);
			if (alertListFragment != null)
				alertListFragment.updateListView(result);
		}
	}

}