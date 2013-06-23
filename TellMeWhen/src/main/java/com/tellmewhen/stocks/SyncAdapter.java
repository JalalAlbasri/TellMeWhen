package com.tellmewhen.stocks;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableNotifiedException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.net.ParseException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
	private static final String TAG = SyncAdapter.class.getSimpleName();
	private static final String SYNC_MARKER_KEY = "com.tellmewhen.stocks.marker";
	private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
	private static final boolean NOTIFY_AUTH_FAILURE = true;
	private final AccountManager mAccountManager;
	private final Context mContext;
	private Account mAccount;
	private static Bundle mSyncMarkers;
	
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context,  autoInitialize);
		Log.d(TAG, "****SyncAdaper()");
		mContext = context;
		mAccountManager = AccountManager.get(context);
		mSyncMarkers = new Bundle();
	}
	
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		String authtoken = null;
		Log.d(TAG, "***onPerformSync()! Account: " + account.name);
		try {
			mAccount = account;
			long lastSyncMarker = getServerSyncMarker(account);
			
			List<RawAlert> dirtyAlerts;
            List<RawAlert> updatedAlerts;
			
			authtoken = fetchToken();
			Log.d(TAG, "AuthToken:" + authtoken);
			
			dirtyAlerts = AlertManager.getDirtyAlerts(mContext, account);
			//Get dirty alerts from contentprovider
			//Get updated alerts from server
			//Update Local contants and get new syncstate.
			//Save off new sync state.
			
			
		} catch (Exception e) {
			handleException(authtoken, e, syncResult);
		}
		
	}
	
	//SyncResult, used to inform SyncManager of any errors.
	private void handleException(String authtoken, Exception e, SyncResult syncResult) {
		 if (e instanceof AuthenticatorException) {
	          syncResult.stats.numParseExceptions++;
	          Log.e(TAG, "AuthenticatorException", e);
	      } else if (e instanceof OperationCanceledException) {
	          Log.e(TAG, "OperationCanceledExcepion", e);
	      } else if (e instanceof IOException) {
	    	  Log.e(TAG, "IOException", e);
	    	  syncResult.stats.numIoExceptions++;
	      
//	      } else if (e instanceof AuthenticationException) {
//	    	  //	          mAccountManager.invalidateAuthToken(
//	    	  //	          AuthenticatorActivity.PARAM_ACCOUNT_TYPE, authtoken);
//	    	  //	          // The numAuthExceptions require user intervention and are
//	    	  //	          // considered hard errors.
//	    	  //	          // We automatically get a new hash, so let's make SyncManager retry
//	    	  //	          // automatically.
//	    	  //	          syncResult.stats.numIoExceptions++;
//	    	  //	          Log.e(TAG, "AuthenticationException", e);
	      } else if (e instanceof ParseException) {
	    	  syncResult.stats.numParseExceptions++;
	    	  Log.e(TAG, "ParseException", e);
	      } else if (e instanceof JsonParseException) {
	    	  syncResult.stats.numParseExceptions++;
	          Log.e(TAG, "JSONException", e);
	      } else {
	    	  Log.e(TAG, "Exception: " + e.getMessage(), e);
	      }
	}
	
	  protected String fetchToken() throws IOException {
	      Log.d(TAG, "fetchToken()");
		  try {
	          return GoogleAuthUtil.getTokenWithNotification(
	                  mContext, mAccount.name, SCOPE, null, AlertListActivity.AUTHORITY, null);
	      } catch (UserRecoverableNotifiedException userRecoverableException) {
	          // Unable to authenticate, but the user can fix this.
	          Log.e(TAG, "Could not fetch token.", userRecoverableException);
	      } catch (GoogleAuthException fatalException) {
	          Log.e(TAG, "Unrecoverable error " + fatalException.getMessage(), fatalException);
	      }
	      return null;
	  }
	
	/**
     * This helper function fetches the last known high-water-mark
     * we received from the server - or 0 if we've never synced.
     * @param account the account we're syncing
     * @return the change high-water-mark
     */
    private long getServerSyncMarker(Account account) {
    	//Check bundle for account and return last sync marker if present.
    	return mSyncMarkers.getLong(account.name); 
    }

    /**
     * Save off the high-water-mark we receive back from the server.
     * @param account The account we're syncing
     * @param marker The high-water-mark we want to save.
     */
    private void setServerSyncMarker(Account account, long marker) {
        mSyncMarkers.putLong(account.name, marker);
    }

}
