package com.tellmewhen.stocks;


import java.io.IOException;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

/**
 * This class is an implementation of AbstractAccountAuthenticator for
 * authenticating accounts in the com.example.android.samplesync domain. The
 * interesting thing that this class demonstrates is the use of authTokens as
 * part of the authentication process. In the account setup UI, the user enters
 * their username and password. But for our subsequent calls off to the service
 * for syncing, we want to use an authtoken instead - so we're not continually
 * sending the password over the wire. getAuthToken() will be called when
 * SyncAdapter calls AccountManager.blockingGetAuthToken(). When we get called,
 * we need to return the appropriate authToken for the specified account. If we
 * already have an authToken stored in the account, we return that authToken. If
 * we don't, but we do have a username and password, then we'll attempt to talk
 * to the sample service to fetch an authToken. If that fails (or we didn't have
 * a username/password), then we need to prompt the user - so we create an
 * AuthenticatorActivity intent and return that. That will display the dialog
 * that prompts the user for their login information.
 */
class Authenticator extends AbstractAccountAuthenticator {

    /** The tag used to log to adb console. **/
    private static final String TAG = "Authenticator";
    
    private static final String AUTHTOKEN_TYPE = "com.google";
    private static final String ACCOUNT_TYPE = "com.google";
    
    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    
    // Authentication Service context
    private final Context mContext;
    private String mEmail;

    public Authenticator(Context context) {
        super(context);
        mContext = context;
    }
    /*
     * Adds an account of the specified accountType.
     */
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
            String authTokenType, String[] requiredFeatures, Bundle options) {
        Log.v(TAG, "Authenticator::addAccount()");
//        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
//        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
//        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        Log.d(TAG, "addAccount()");
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
        
    }

    @Override
    public Bundle confirmCredentials(
            AccountAuthenticatorResponse response, Account account, Bundle options) {
        Log.v(TAG, "confirmCredentials()");
        return null;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        Log.v(TAG, "editProperties()");
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
            String authTokenType, Bundle loginOptions) throws NetworkErrorException {
        Log.v(TAG, "getAuthToken()");

        // If the caller requested an authToken type we don't support, then
        // return an error
        if (!authTokenType.equals(AUTHTOKEN_TYPE)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return result;
        }

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);
        //final String password = am.getPassword(account);
        //if (password != null) {
            //final String authToken = NetworkUtilities.authenticate(account.name, password);
        mEmail = account.name;
        try {
        	final String authToken = fetchToken(); 
        	if (!TextUtils.isEmpty(authToken)) {
        		final Bundle result = new Bundle();
        		result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        		result.putString(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
        		result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        		return result;
        	}
        } catch (IOException e) {
        	Log.e(TAG, "IOException Retriving Auth Token: " + e.getMessage());
        }
        
        //}

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity panel.
//        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
//        intent.putExtra(AuthenticatorActivity.PARAM_USERNAME, account.name);
//        intent.putExtra(AuthenticatorActivity.PARAM_AUTHTOKEN_TYPE, authTokenType);
//        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putString(AccountManager.KEY_ERROR_MESSAGE, "Retriving authToken failed");
//        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        // null means we don't support multiple authToken types
        Log.v(TAG, "getAuthTokenLabel()");
        return null;
    }

    @Override
    public Bundle hasFeatures(
            AccountAuthenticatorResponse response, Account account, String[] features) {
        // This call is used to query whether the Authenticator supports
        // specific features. We don't expect to get called, so we always
        // return false (no) for any queries.
        Log.v(TAG, "hasFeatures()");
        final Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
            String authTokenType, Bundle loginOptions) {
        Log.v(TAG, "updateCredentials()");
        return null;
    }
    
    protected String fetchToken() throws IOException {
        try {
            return GoogleAuthUtil.getToken(mContext, mEmail, SCOPE);
        } catch (GooglePlayServicesAvailabilityException playEx) {
            // GooglePlayServices.apk is either old, disabled, or not present.
            Log.e(TAG, "Error Retrieving AuthToken" + playEx.getConnectionStatusCode());
        } catch (UserRecoverableAuthException userRecoverableException) {
            // Unable to authenticate, but the user can fix this.
            // Forward the user to the appropriate activity.
            //mActivity.startActivityForResult(userRecoverableException.getIntent(), mRequestCode);
        } catch (GoogleAuthException fatalException) {
            Log.e(TAG, "Unrecoverable error " + fatalException.getMessage(), fatalException);
        }
        return null;
    }
}