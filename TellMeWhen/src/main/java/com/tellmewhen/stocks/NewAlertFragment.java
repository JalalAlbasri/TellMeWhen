package com.tellmewhen.stocks;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class NewAlertFragment extends Fragment {
	private static final String TAG = NewAlertFragment.class.getSimpleName();

	private AlertListActivity activity;
	private OnNewAlertAddedListener onNewAlertAddedListener;
	private InsertNewAlertAsyncTask insertNewAlertAsyncTask;
	private String deviceInfoId;
	private EditText stockSymbolEditText;
	private Spinner conditionalSpinner;
	private EditText stockPriceEditText;
	private TextView stockSymbolHelpText;
	private TextView stockPriceHelpText;
	private Button save;
	private Button cancel;
	private String conditional;
	private String stockName;
	private String currentPrice;

	private SharedPreferences prefs;
	private ContentResolver cr;

	public interface OnNewAlertAddedListener {
		public void onNewAlertAdded();
	}

	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "onAttach()");
		super.onAttach(activity);
		this.activity = (AlertListActivity) activity;
		try {
			onNewAlertAddedListener = (OnNewAlertAddedListener) activity;
		} catch (ClassCastException e) {
			Log.e(TAG, "Activity must implement OnNewAlertAddedListener interface");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView()");
		return inflater.inflate(R.layout.fragment_newalert, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated()");
		super.onActivityCreated(savedInstanceState);
		cr = activity.getContentResolver();

		prefs = activity.getSharedPreferences(MainActivity.SHARED_PREFERENCES, MainActivity.MODE_PRIVATE);
		deviceInfoId = prefs.getString(MainActivity.DEVICE_ID_KEY, MainActivity.DEFAULT_DEVICE_ID);

        //Get References to UI Widgets
		stockSymbolEditText = (EditText)  activity.findViewById(R.id.stock_symbol_edit_text);
		conditionalSpinner = (Spinner) activity.findViewById(R.id.conditional_spinner);
        stockPriceEditText = (EditText) activity.findViewById(R.id.stock_price_edit_text);
		stockSymbolHelpText = (TextView) activity.findViewById(R.id.stock_symbol_help_text);
		stockPriceHelpText = (TextView) activity.findViewById(R.id.stock_price_help_text);
        save = (Button) activity.findViewById(R.id.save_button);
        cancel = (Button) activity.findViewById(R.id.cancel_button);
        //Set Listeners
        stockSymbolEditText.setOnFocusChangeListener(stockSymbolOnFocusChangeListener);
        conditionalSpinner.setOnItemSelectedListener(conditionalSpinnerListener);
        save.setOnClickListener(saveOnClickListener);
        cancel.setOnClickListener(cancelOnClickListener);

	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	private View.OnClickListener saveOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			String stockSymbol = stockSymbolEditText.getText().toString();
			String stockPrice = stockPriceEditText.getText().toString();
			if (!stockSymbol.equals("") && !stockPrice.equals("")) {
				QueryStockQuoteAsyncTask queryStockQuoteAsyncTask = new QueryStockQuoteAsyncTask(getActivity());
				queryStockQuoteAsyncTask.execute(stockSymbol);
				if (!stockName.equals("")) {
//					insertNewAlertAsyncTask = new InsertNewAlertAsyncTask(activity);
//					insertNewAlertAsyncTask.execute(deviceInfoId, stockSymbol, conditional, stockPrice);
					ContentValues values = new ContentValues();
					values.put(AlertContentProvider.KEY_DEVICE_INFO_ID, deviceInfoId);
					values.put(AlertContentProvider.KEY_STOCK_SYMBOL, stockSymbol);
					values.put(AlertContentProvider.KEY_CONDITIONAL, conditional);
					values.put(AlertContentProvider.KEY_STOCK_PRICE, stockPrice);
					values.put(AlertContentProvider.KEY_ACTIVE, true);
					values.put(AlertContentProvider.KEY_SATISFIED, false);
					values.put(AlertContentProvider.KEY_CREATED_TIMESTAMP, System.currentTimeMillis());

					cr.insert(AlertContentProvider.CONTENT_URI, values);

					onNewAlertAddedListener.onNewAlertAdded();
				}
			}
			else {
				if (stockSymbol.equals("")){
					stockSymbolHelpText.setText(R.string.stock_symbol_empty_error);
					stockSymbolHelpText.setVisibility(TextView.VISIBLE);
				}
				if (stockPrice.equals("")) {
					stockPriceHelpText.setText(R.string.stock_price_empty_error);
					stockPriceHelpText.setVisibility(View.VISIBLE);
				}
			}
		}
	};

	private View.OnClickListener cancelOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//detach fragment, call onDestroy onDetach etc..
			onNewAlertAddedListener.onNewAlertAdded();
		}
	};

	private View.OnFocusChangeListener stockSymbolOnFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus) {
				String stockSymbol = stockSymbolEditText.getText().toString();
				if(!stockSymbol.equals("")) {
					stockSymbolEditText.setText(stockSymbol.toUpperCase());
					QueryStockQuoteAsyncTask queryStockQuoteAsyncTask =
							new QueryStockQuoteAsyncTask(getActivity());
					queryStockQuoteAsyncTask.execute(stockSymbol);
				}
				else {
					stockSymbolHelpText.setVisibility(View.GONE);
					stockPriceHelpText.setVisibility(View.GONE);
				}
			}
		}
	};

	private OnItemSelectedListener conditionalSpinnerListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			conditional = (String) parent.getItemAtPosition(pos);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};

    //Used by QueryStockQuoteAsyncTask
	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public void setCurrentPrice(String currentPrice) {
		this.currentPrice = currentPrice;
	}

	public void updateHelpText() {
		if (stockName != null) {
			stockSymbolHelpText.setText(stockName);
			stockPriceHelpText.setText(currentPrice);
			stockSymbolHelpText.setVisibility(View.VISIBLE);
			stockPriceHelpText.setVisibility(View.VISIBLE);
		}
		else {
			stockSymbolHelpText.setText(R.string.stock_symbol_lookup_error);
			stockSymbolHelpText.setVisibility(View.VISIBLE);
			stockPriceHelpText.setVisibility(View.GONE);
		}
	}
}
