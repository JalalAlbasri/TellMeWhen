package com.tellmewhen.stocks;

import java.util.List;

import com.google.api.services.com.tellmewhen.stocks.stockpricealertendpoint.model.CollectionResponseStockPriceAlert;
import com.google.api.services.com.tellmewhen.stocks.stockpricealertendpoint.model.StockPriceAlert;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;

public class AlertListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final String TAG = AlertListFragment.class.getSimpleName();

	private static final int ALERT_LOADER = 0;

	private AlertListActivity activity;
	private OnAlertSelectedListener onAlertSelectedListener;
	private QueryAlertListAsyncTask queryAlertAsyncTask;
	private AlertListArrayAdapter adapter;
	private SimpleCursorAdapter cursorAdapter;
	private SharedPreferences prefs;
	private List<StockPriceAlert> alertList;
	private String deviceInfoId;

	public interface OnAlertSelectedListener {
		public void onAlertSelected(StockPriceAlert alert);
	}

	/* Override for custom list implementation.
	@Override
	public void onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.)
	}
	*/

	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "onAttach()");
		super.onAttach(activity);
		try {
			this.activity = (AlertListActivity) activity;
			onAlertSelectedListener = (OnAlertSelectedListener) activity;
		} catch (ClassCastException e) {
			Log.e(TAG, "ClassCastException" + activity.toString() +
					" must implement OnAlertSelectedListener");
			throw new ClassCastException (activity.toString() +
					" must implement OnAlertSelectedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated()");
		super.onActivityCreated(savedInstanceState);
		prefs = activity.getSharedPreferences(MainActivity.SHARED_PREFERENCES, MainActivity.MODE_PRIVATE);
		deviceInfoId = prefs.getString(MainActivity.DEVICE_ID_KEY, MainActivity.DEFAULT_DEVICE_ID);
//		setListAdapter(adapter);
		getLoaderManager().initLoader(ALERT_LOADER, null, this);

		String fromColumns[] = new String[] {
				AlertContentProvider.KEY_ALERT_ID,
				AlertContentProvider.KEY_STOCK_SYMBOL,
				AlertContentProvider.KEY_CONDITIONAL,
				AlertContentProvider.KEY_STOCK_PRICE,
				AlertContentProvider.KEY_CREATED_TIMESTAMP
		};

		int toLayoutIds[] = new int[] {
				R.id.alert_item_id,
				R.id.alert_item_stock_symbol,
				R.id.alert_item_conditional,
				R.id.alert_item_stock_price,
				R.id.alert_item_date
		};

		cursorAdapter = new SimpleCursorAdapter(
				activity,
				R.layout.alert_listitem_cursor,
				null,
				fromColumns,
				toLayoutIds
		);

		cursorAdapter.setViewBinder(new AlertListViewBinder());
		setListAdapter(cursorAdapter);
	}


	@Override
	public void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();
//		queryAlertAsyncTask = new QueryAlertListAsyncTask(activity);
//		queryAlertAsyncTask.execute(deviceInfoId);
        getLoaderManager().restartLoader(ALERT_LOADER, null, this);

	}

	public void updateListView(CollectionResponseStockPriceAlert result) {
		if (result != null) {
//			adapter = new ArrayAdapter<StockPriceAlert>(this.getActivity(),
//					android.R.layout.simple_list_item_1, result.getItems());
//			alertList = result.getItems();
//			adapter = new AlertListArrayAdapter(this.getActivity(),
//					R.layout.alertlist_item, result.getItems());
//			setListAdapter(adapter);
		}
	}

	public void updateListView(StockPriceAlert result) {

//		if (result != null) {
//			alertList.add(result);
//			adapter.notifyDataSetChanged();
//		}
	}

	@Override
	public void onPause() {
		Log.d(TAG, "onPause()");
		super.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		Log.d(TAG, "onSaveInstanceState()");
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

//		if (args != null) {
//
//		}

		String projection[] = new String[] {
				AlertContentProvider.KEY_ALERT_ID,
				AlertContentProvider.KEY_STOCK_SYMBOL,
				AlertContentProvider.KEY_CONDITIONAL,
				AlertContentProvider.KEY_STOCK_PRICE,
				AlertContentProvider.KEY_CREATED_TIMESTAMP
				};

		CursorLoader loader = new CursorLoader(activity,
				AlertContentProvider.CONTENT_URI, projection, null, null, null);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		cursorAdapter.changeCursor(cursor);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		cursorAdapter.changeCursor(null);

	}
}
