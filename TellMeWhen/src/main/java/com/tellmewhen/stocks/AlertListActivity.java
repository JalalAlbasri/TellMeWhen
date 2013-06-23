package com.tellmewhen.stocks;

import com.tellmewhen.stocks.AlertListFragment.OnAlertSelectedListener;
import com.tellmewhen.stocks.NewAlertFragment.OnNewAlertAddedListener;
import com.google.api.services.com.tellmewhen.stocks.stockpricealertendpoint.model.StockPriceAlert;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

public class AlertListActivity extends Activity implements OnAlertSelectedListener, OnNewAlertAddedListener {

	private static final String TAG = AlertListActivity.class.getSimpleName();
	private static final int MENU_REFRESH = Menu.FIRST;
	private static final int MENU_NEW = Menu.FIRST+1;
	private static final int MENU_RATE = Menu.FIRST+2;
	public static final String AUTHORITY = "com.tellmewhen.alertprovider";
	public static final String ACCOUNT_TYPE = "com.google";
	private AlertListFragment alertListFragment;
	private NewAlertFragment newAlertFragment;
	private FragmentManager fm;
	private ContentResolver mContentResolver;
	private AccountManager mAccountManager;
	private Account mAccount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fm = getFragmentManager();
		initActionBar();
		setContentView(R.layout.activity_alertlist);

		mAccountManager = AccountManager.get(this);
		Account[] accounts = mAccountManager.getAccountsByType(ACCOUNT_TYPE);
		mAccount = accounts[0];

		ContentResolver.setIsSyncable(mAccount, AUTHORITY, 1);
        //TODO: Discern which are actually required, or if both are needed together
		ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
		ContentResolver.addPeriodicSync(mAccount, AUTHORITY, new Bundle(), 15);
        mContentResolver = getContentResolver();


//		Bundle params = new Bundle();
//		params.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
//		params.putBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, true);
//		ContentResolver.requestSync(mAccount, AUTHORITY, params);




		//Account[] accounts = mAccountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
		//TODO Provide user with option to select if multiple accounts present
		//		Account[] accounts = mAccountManager.getAccountsByType("com.google");
		//		mAccount = accounts[0];
		//		Log.d(TAG, "Account Name:" + mAccount.name);
		//		ContentResolver.setIsSyncable(mAccount, AUTHORITY, 1);
		//		ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
		//		Bundle params = new Bundle();
		//		//Override if Global Sync is set to off.
		//		params.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		//		params.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		//		params.putBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, true);
		//		ContentResolver.requestSync(mAccount, AUTHORITY, params);
		//		mContentResolver = getContentResolver();
		////		Intent intent = new Intent(this, SyncService.class);
		//		this.startService(intent);


	}

	private void initActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		ArrayAdapter<CharSequence> dropdownAdapter = ArrayAdapter.createFromResource(this,
				R.array.alert_list_drop_down,
				android.R.layout.simple_list_item_1);

		actionBar.setListNavigationCallbacks(dropdownAdapter, onNavigationListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem refresh = menu.add(0, MENU_REFRESH, Menu.NONE, R.string.menu_refresh);
		refresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		refresh.setIcon(R.drawable.navigation_refresh);

		MenuItem newItem = menu.add(0, MENU_NEW, Menu.NONE, R.string.menu_new);
		newItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		newItem.setIcon(R.drawable.content_new);

        //TODO: Complete Rate Menubar option
		menu.add(0, MENU_RATE, Menu.NONE, R.string.menu_rate);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch(item.getItemId()) {

		case (MENU_REFRESH) : {
			Log.d(TAG, "MENU_REFRESH");
			alertListFragment = (AlertListFragment) fm.findFragmentById(R.id.alert_list_fragment);
			alertListFragment.onResume();
			if (mAccount != null)
				Log.d(TAG, "ContentResolver.requestSync();");

			//Request Forced Sync on Refresh.
			Bundle params = new Bundle();
			params.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			params.putBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, true);
			ContentResolver.requestSync(mAccount, AUTHORITY, params);

			/*
			TODO: Debug, Remove
			 * Test adding a dummy alert through the AlertManager
			 */
			RawAlert rawAlert = RawAlert.create("pending", System.currentTimeMillis(), "deviceid", 0, 0,
					true, false, "AAPL", ">", "455", false, true, 0);
            BatchOperation batchOperation = new BatchOperation(this, getContentResolver());
            AlertManager.addAlert(this, mAccount.name, rawAlert, false, batchOperation);
            batchOperation.execute();
			return true;
		}

		case (MENU_NEW): {
			Log.d(TAG, "MENU_NEW");
			((FrameLayout) findViewById(R.id.new_alert_container)).setVisibility(FrameLayout.VISIBLE);
			//			newAlertFragment = (NewAlertFragment) fm.findFragmentById(R.id.new_alert_container);
			//			if (newAlertFragment == null) {
			FragmentTransaction ft = fm.beginTransaction();
			ft.add(R.id.new_alert_container, new NewAlertFragment());
			ft.addToBackStack("new_alert");
			ft.commit();
			//			}
			//			else {
			//				newAlertFragment.onResume();
			//			}
			return true;
		}

		default : return true;
		}
	}

	/*
	 * Handle navigation item selection.
	 * Filter Alert List View.
	 * {All, Active, Inactive, Satisfied, Unsatisfied.
	 */
	private OnNavigationListener onNavigationListener = new OnNavigationListener() {
		public boolean onNavigationItemSelected(int itemPostion, long itemId) {

			return true;
		}
	};




	public boolean onNavigationItemSelected(int itemPostion, long itemId) {

		return true;
	}
    // Callback OnAlertSelectedListener
	public void onAlertSelected(StockPriceAlert alert) {

		//TODO Open a details fragment for the selected alert.

	}

	//TODO Detach the Fragments before destroy/pause.. find book section...

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onPause(){
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void onNewAlertAdded() {
		newAlertFragment = (NewAlertFragment) fm.findFragmentById(R.id.new_alert_container);
		FragmentTransaction ft = fm.beginTransaction();
		ft.remove(newAlertFragment);
		ft.commit();
		newAlertFragment = null;
		FrameLayout newItemContainer = (FrameLayout) findViewById(R.id.new_alert_container);
		newItemContainer.setVisibility(FrameLayout.GONE);
		//Refresh the alert list to show the newly added alert
        alertListFragment = (AlertListFragment) fm.findFragmentById(R.id.alert_list_fragment);
		alertListFragment.onResume();

	}

}
