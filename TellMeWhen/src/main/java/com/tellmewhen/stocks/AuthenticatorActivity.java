package com.tellmewhen.stocks;

import com.google.android.gms.auth.GoogleAuthUtil;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.os.Bundle;
import android.util.Log;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {
	private static final String TAG = AuthenticatorActivity.class.getSimpleName();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "AuthenticatorActivity::onCreate()");
		AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        Account account = accounts[0];
        
        final Bundle bundle = new Bundle();
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        
        setAccountAuthenticatorResult(bundle);
        ContentResolver.setSyncAutomatically(account, "com.tellmewhen.stocks.alertprovider", true);
        
	}

}
