package com.tellmewhen.stocks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import android.content.ContentProviderOperation;

public class AlertOperations {

	private final ContentValues mValues;
    private final BatchOperation mBatchOperation;
    private final Context mContext;
    private boolean mIsSyncOperation;
    private long mRawAlertId;
    private int mBackReference;
    private boolean mIsNewAlert;
	private boolean mIsYieldAllowed;
	
	public AlertOperations(Context context, boolean isSyncOperation,
			BatchOperation batchOperation) {
		mValues = new ContentValues();
		mIsYieldAllowed = true;
		mIsSyncOperation = isSyncOperation;
		mContext = context;
		mBatchOperation = batchOperation;
	}
	
	public AlertOperations(Context context, String userId, String accountName, 
			boolean isSyncOperation, BatchOperation batchOperation) {
		this(context, isSyncOperation, batchOperation);
		mBackReference = mBatchOperation.size();
		mIsNewAlert = true;
		mValues.put(AlertContentProvider.KEY_SERVER_ID, userId);
		ContentProviderOperation.Builder builder = 
				newInsertCpo(AlertContentProvider.CONTENT_URI, mIsSyncOperation, true).withValues(mValues);
        mBatchOperation.add(builder.build());
	}
	
	public AlertOperations(Context context, long rawAlertId, boolean isSyncOperation,
			BatchOperation batchOperation) {
		this(context, isSyncOperation, batchOperation);
		mIsNewAlert = false;
		mRawAlertId = rawAlertId;
	}
	
	public static AlertOperations createNewAlert(Context context, String userId, 
			String accountName, boolean isSyncOperation, BatchOperation batchOperation) {
        return new AlertOperations(context, userId, accountName, isSyncOperation, batchOperation);
    }
	
	public static AlertOperations updateExistingAlert(Context context, long rawAlertId,
            boolean isSyncOperation, BatchOperation batchOperation) {
        return new AlertOperations(context, rawAlertId, isSyncOperation, batchOperation);
    }
	
	public AlertOperations addDeviceInfoId(String deviceInfoId) {
		mValues.clear();
		if (!TextUtils.isEmpty(deviceInfoId)) {
			mValues.put(AlertContentProvider.KEY_DEVICE_INFO_ID, deviceInfoId);
			addInsertOp();
		}
		return this;
	}
	
	public AlertOperations addCreatedTimestamp(Long createdTimestamp) {
		mValues.clear();
		if (createdTimestamp != 0) {
			mValues.put(AlertContentProvider.KEY_CREATED_TIMESTAMP, createdTimestamp);
			addInsertOp();
		}
		return this;
	}
	
	public AlertOperations addCheckedTimestamp(Long checkedTimestamp) {
		mValues.clear();
		if (checkedTimestamp != 0) {
			mValues.put(AlertContentProvider.KEY_CHECKED_TIMESTAMP, checkedTimestamp);
			addInsertOp();
		}
		return this;
	}
	
	public AlertOperations addActive(Boolean active) {
		mValues.clear();
		mValues.put(AlertContentProvider.KEY_ACTIVE, active);
		addInsertOp();
		return this;
	}
	
	public AlertOperations addSatisfied(Boolean satisfied) {
		mValues.clear();
		mValues.put(AlertContentProvider.KEY_SATISFIED, satisfied);
		addInsertOp();
		return this;
	}
	
	public AlertOperations addStockSymbol(String stockSymbol) {
		mValues.clear();
		if (!TextUtils.isEmpty(stockSymbol)) {
			mValues.put(AlertContentProvider.KEY_STOCK_SYMBOL, stockSymbol);
			addInsertOp();
		}
		return this;
	}
	
	public AlertOperations addStockPrice(String stockPrice) {
		mValues.clear();
		if (!TextUtils.isEmpty(stockPrice)) {
			mValues.put(AlertContentProvider.KEY_STOCK_PRICE, stockPrice);
			addInsertOp();
		}
		return this;
	}
	
	public AlertOperations addConditional(String conditional) {
		mValues.clear();
		if (!TextUtils.isEmpty(conditional)) {
			mValues.put(AlertContentProvider.KEY_CONDITIONAL, conditional);
			addInsertOp();
		}
		return this;
	}
	
	public AlertOperations updateServerId(String serverId, Uri uri) {
		mValues.clear();
		mValues.put(AlertContentProvider.KEY_SERVER_ID, serverId);
		addUpdateOp(uri);
		return this;
	}
	
	public AlertOperations updateDeviceInfoId(String deviceId, String existingDeivceId, Uri uri) {
		if (!TextUtils.equals(deviceId, existingDeivceId)) {
			mValues.clear();
			mValues.put(AlertContentProvider.KEY_DEVICE_INFO_ID, deviceId);
			addUpdateOp(uri);
		}
		return this;
	}
	
	public AlertOperations updateCreatedTimestamp(long createdTimestamp, 
			long existingCreatedTimestamp, Uri uri) {
		if (existingCreatedTimestamp != createdTimestamp) {
			mValues.clear();
			mValues.put(AlertContentProvider.KEY_CREATED_TIMESTAMP, createdTimestamp);
			addUpdateOp(uri);
		}
		return this;
	}
	
	public AlertOperations updateCheckedTimestamp(long checkedTimestamp, 
			long existingCheckedTimestamp, Uri uri) {
		if (existingCheckedTimestamp != checkedTimestamp) {
			mValues.clear();
			mValues.put(AlertContentProvider.KEY_CREATED_TIMESTAMP, checkedTimestamp);
			addUpdateOp(uri);
		}
		return this;
	}
	
	public AlertOperations updateActive(Boolean isActive, Uri uri) {
		int isActiveValue = isActive ? 1 : 0;
		mValues.clear();
		mValues.put(AlertContentProvider.KEY_ACTIVE, isActiveValue);
		addUpdateOp(uri);
		return this;
	}
	
	public AlertOperations updateSatisfied(Boolean isSatisfied, Uri uri) {
		int isSatisfiedValue = isSatisfied ? 1 : 0;
		mValues.clear();
		mValues.put(AlertContentProvider.KEY_ACTIVE, isSatisfiedValue);
		addUpdateOp(uri);
		return this;
	}
	
	public AlertOperations updateStockSymbol(String stockSymbol, String existingStockSymbol, Uri uri) {
		if (!TextUtils.equals(stockSymbol, existingStockSymbol)) {
			mValues.clear();
			mValues.put(AlertContentProvider.KEY_STOCK_SYMBOL, stockSymbol);
			addUpdateOp(uri);
		}
		return this;
	}
	
	public AlertOperations updateStockPrice(String stockPrice, String existingStockPrice, Uri uri) {
		if (!TextUtils.equals(stockPrice, existingStockPrice)) {
			mValues.clear();
			mValues.put(AlertContentProvider.KEY_STOCK_PRICE, stockPrice);
			addUpdateOp(uri);
		}
		return this;	
	}
	
	public AlertOperations updateConditional(String conditional, String existingConditional, Uri uri) {
		if (!TextUtils.equals(conditional, existingConditional)) {
			mValues.clear();
			mValues.put(AlertContentProvider.KEY_DEVICE_INFO_ID, conditional);
			addUpdateOp(uri);
		}
		return this;	
	}
	
    public AlertOperations updateDirtyFlag(boolean isDirty, Uri uri) {
        int isDirtyValue = isDirty ? 1 : 0;
        mValues.clear();
        mValues.put(AlertContentProvider.KEY_DIRTY, isDirtyValue);
        addUpdateOp(uri);
        return this;
    }
	
    /**
     * Adds an insert operation into the batch
     */
    private void addInsertOp() {

        if (!mIsNewAlert) {
            mValues.put(AlertContentProvider.KEY_ALERT_ID, mRawAlertId);
        }
        ContentProviderOperation.Builder builder =
                newInsertCpo(Data.CONTENT_URI, mIsSyncOperation, mIsYieldAllowed);
        builder.withValues(mValues);
        if (mIsNewAlert) {
            builder.withValueBackReference(AlertContentProvider.KEY_ALERT_ID, mBackReference);
        }
        mIsYieldAllowed = false;
        mBatchOperation.add(builder.build());
    }

    /**
     * Adds an update operation into the batch
     */
    private void addUpdateOp(Uri uri) {
        ContentProviderOperation.Builder builder =
                newUpdateCpo(uri, mIsSyncOperation, mIsYieldAllowed).withValues(mValues);
        mIsYieldAllowed = false;
        mBatchOperation.add(builder.build());
    }

    public static ContentProviderOperation.Builder newInsertCpo(Uri uri,
            boolean isSyncOperation, boolean isYieldAllowed) {
        return ContentProviderOperation
                .newInsert(addCallerIsSyncAdapterParameter(uri, isSyncOperation))
                .withYieldAllowed(isYieldAllowed);
    }

    public static ContentProviderOperation.Builder newUpdateCpo(Uri uri,
            boolean isSyncOperation, boolean isYieldAllowed) {
        return ContentProviderOperation
                .newUpdate(addCallerIsSyncAdapterParameter(uri, isSyncOperation))
                .withYieldAllowed(isYieldAllowed);
    }

    public static ContentProviderOperation.Builder newDeleteCpo(Uri uri,
            boolean isSyncOperation, boolean isYieldAllowed) {
        return ContentProviderOperation
                .newDelete(addCallerIsSyncAdapterParameter(uri, isSyncOperation))
                .withYieldAllowed(isYieldAllowed);
    }

    private static Uri addCallerIsSyncAdapterParameter(Uri uri, boolean isSyncOperation) {
        if (isSyncOperation) {
            // If we're in the middle of a real sync-adapter operation, then go ahead
            // and tell the Contacts provider that we're the sync adapter.  That
            // gives us some special permissions - like the ability to really
            // delete a contact, and the ability to clear the dirty flag.
            //
            // If we're not in the middle of a sync operation (for example, we just
            // locally created/edited a new contact), then we don't want to use
            // the special permissions, and the system will automagically mark
            // the contact as 'dirty' for us!
            return uri.buildUpon()
                    .appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true")
                    .build();
        }
        return uri;
    }
}
