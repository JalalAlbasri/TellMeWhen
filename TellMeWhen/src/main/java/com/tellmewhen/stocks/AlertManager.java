package com.tellmewhen.stocks;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.text.TextUtils;

public class AlertManager {
	private static final String TAG = AlertManager.class.getSimpleName();
	
	public static synchronized long updateAlerts(Context context, String account, 
			List<RawAlert> rawAlerts, long lastSyncMarker) 
	{
		long currentSyncMarker = lastSyncMarker;
        final ContentResolver resolver = context.getContentResolver();
        final BatchOperation batchOperation = new BatchOperation(context, resolver);
        final List<RawAlert> newAlerts = new ArrayList<RawAlert>();
		
        for (final RawAlert rawAlert : rawAlerts) {
        	
        	if(rawAlert.getSyncState() > currentSyncMarker) {
        		currentSyncMarker = rawAlert.getSyncState(); 
        	}
        	
        	final long rawAlertId;
        	final boolean updateServerId;
        	if(rawAlert.getRawAlertId() > 0) {
        		rawAlertId = rawAlert.getRawAlertId();
        		updateServerId = true;
        	} else {
        		String serverAlertId = rawAlert.getServerAlertId();
        		rawAlertId = lookupRawAlert(resolver, serverAlertId);
        		updateServerId = false;
        	} 
        	if (rawAlertId != 0) {
        		if (!rawAlert.isDeleted()) {
        			updateAlert(context, resolver, rawAlert, updateServerId, 
        					true, rawAlertId, batchOperation);
        		} else {
        			deleteAlert(context, rawAlertId, batchOperation);
        		}
        		
        	} else {
        		if (!rawAlert.isDeleted()){        	
        			newAlerts.add(rawAlert);
        			addAlert(context, account, rawAlert, true, batchOperation);
        		}
        	}
        	if (batchOperation.size() >= 50) {
        		batchOperation.execute();
        	}
        }
		batchOperation.execute();
		return currentSyncMarker;
	}

	private static long lookupRawAlert(ContentResolver resolver,
			String serverAlertId) {
		long rawAlertId = 0;
		final Cursor c = resolver.query(
				UserIdQuery.CONTENT_URI,
				UserIdQuery.PROJECTION,
				UserIdQuery.SELECTION,
				new String[] {serverAlertId},
				null);
		try {
			if ((c!= null) && c.moveToFirst()) {
				rawAlertId = c.getLong(UserIdQuery.COLUMN_RAW_CONTACT_ID);
			}
		} finally {
            if (c != null) {
                c.close();
            }
        }
        return rawAlertId;
	}

	private static void updateAlert(Context context, ContentResolver resolver,
			RawAlert rawAlert, boolean updateServerId, boolean inSync,
			long rawAlertId, BatchOperation batchOperation) {
		
		final Cursor c = resolver.query(DataQuery.CONTENT_URI, DataQuery.PROJECTION, DataQuery.SELECTION, 
				new String[] {String.valueOf(rawAlertId)}, null);
		
		final AlertOperations alertOp = AlertOperations.updateExistingAlert(context, rawAlertId, 
				inSync, batchOperation);
		
		try {
			while (c.moveToNext()) {
				final long id = c.getLong(DataQuery.COLUMN_ID);
				final Uri uri = ContentUris.withAppendedId(DataQuery.CONTENT_URI, id);
				alertOp.updateDeviceInfoId(c.getString(DataQuery.COLUMN_DEVICE_INFO_ID), 
						rawAlert.getDeviceId(), uri);
				alertOp.updateCreatedTimestamp(c.getLong(DataQuery.COLUMN_CREATED_TIMESTAMP), 
						rawAlert.getCreatedTimestamp(), uri);
				alertOp.updateCheckedTimestamp(c.getLong(DataQuery.COLUMN_CHECKED_TIMESTAMP), 
						rawAlert.getCheckedTimestamp(), uri);
				alertOp.updateActive("1".equals(c.getString(DataQuery.COLUMN_ACTIVE)), uri);
				alertOp.updateSatisfied("1".equals(c.getString(DataQuery.COLUMN_SATISFIED)), uri);
				alertOp.updateStockSymbol(c.getString(DataQuery.COLUMN_STOCK_SYMBOL), 
						rawAlert.getStockSymbol(), uri);
				alertOp.updateConditional(c.getString(DataQuery.COLUMN_CONDITIONAL),
						rawAlert.getConditional(), uri);
				alertOp.updateStockPrice(c.getString(DataQuery.COLUMN_STOCK_PRICE), 
						rawAlert.getStockPrice(), uri);
			}
		} finally {
			c.close();
		}
		
	}

	public static void addAlert(Context context, String accountName,
			RawAlert rawAlert, boolean inSync, BatchOperation batchOperation) {
		
		final AlertOperations alertOp = AlertOperations.createNewAlert(
				context, rawAlert.getServerAlertId(), accountName, inSync, batchOperation);
		
		alertOp.addDeviceInfoId(rawAlert.getDeviceId())
		.addCreatedTimestamp(rawAlert.getCreatedTimestamp())
		.addCheckedTimestamp(rawAlert.getCheckedTimestamp())
		.addActive(rawAlert.isActive())
		.addSatisfied(rawAlert.isSatisfied())
		.addStockSymbol(rawAlert.getStockSymbol())
		.addStockPrice(rawAlert.getStockPrice())
		.addConditional(rawAlert.getConditional());
		
	}

	private static void deleteAlert(Context context, long rawAlertId,
			BatchOperation batchOperation) {
		batchOperation.add(AlertOperations.newDeleteCpo(ContentUris.withAppendedId(
				AlertContentProvider.CONTENT_URI, rawAlertId), true, true).build());
		
	}
	
	/**
     * Return a list of the local alerts that have been marked as
     * "dirty", and need syncing to the SampleSync server.
     *
     * @param context The context of Authenticator Activity
     * @param account The account that we're interested in syncing
     * @return a list of Users that are considered "dirty"
     */
    public static List<RawAlert> getDirtyAlerts(Context context, Account account) {
        Log.i(TAG, "*** Looking for local dirty alerts");
        List<RawAlert> dirtyAlerts = new ArrayList<RawAlert>();

        final ContentResolver resolver = context.getContentResolver();
        final Cursor c = resolver.query(
        		DirtyQuery.CONTENT_URI,
                DirtyQuery.PROJECTION,
                DirtyQuery.SELECTION,
                null,
                null);
        try {
            while (c.moveToNext()) {
                final long rawAlertId = c.getLong(DirtyQuery.COLUMN_RAW_ALERT_ID);
                final String serverAlertId = c.getString(DirtyQuery.COLUMN_SERVER_ID);
                final boolean isDirty = "1".equals(c.getString(DirtyQuery.COLUMN_DIRTY));
                final boolean isDeleted = "1".equals(c.getString(DirtyQuery.COLUMN_DELETED));
                final long version = c.getLong(DirtyQuery.COLUMN_VERSION);

                Log.i(TAG, "Dirty Alert: " + Long.toString(rawAlertId));
                Log.i(TAG, "Alert Version: " + Long.toString(version));

                if (isDeleted) {
                    Log.i(TAG, "Alert is marked for deletion");
                    RawAlert rawAlert = RawAlert.createDeleted(serverAlertId, rawAlertId);
                    dirtyAlerts.add(rawAlert);
                } else if (isDirty) {
                    RawAlert rawAlert = getRawAlert(context, rawAlertId);
                    dirtyAlerts.add(rawAlert);
                }
            }

        } finally {
            if (c != null) {
                c.close();
            }
        }
        return dirtyAlerts;
    }
	
	 private static RawAlert getRawAlert(Context context, long rawAlertId) {
		String serverId = null;
		String device_info_id = null;
		long created_timestamp = 0;
		long checked_timestamp = 0;
		boolean active = false;
		boolean satisfied  = false;
		String stockSymbol = null;
		String conditional = null;
		String stockPrice = null;
		
		final ContentResolver resolver = context.getContentResolver();
		final Cursor c =
	            resolver.query(DataQuery.CONTENT_URI, DataQuery.PROJECTION, DataQuery.SELECTION,
	                new String[] {String.valueOf(rawAlertId)}, null);

		try {
			while (c.moveToNext()) {
				final long id = c.getLong(DataQuery.COLUMN_ID);
				final String tempServerId = c.getString(DataQuery.COLUMN_SERVER_ID);
				if(!TextUtils.isEmpty(tempServerId)) {
					serverId = tempServerId;
				}
				final Uri uri = ContentUris.withAppendedId(AlertContentProvider.CONTENT_URI, id);
				device_info_id = c.getString(DataQuery.COLUMN_DEVICE_INFO_ID);
				created_timestamp = c.getLong(DataQuery.COLUMN_CREATED_TIMESTAMP);
				checked_timestamp = c.getLong(DataQuery.COLUMN_CHECKED_TIMESTAMP);
				active = (c.getInt(DataQuery.COLUMN_ACTIVE) == 1);
				satisfied = (c.getInt(DataQuery.COLUMN_SATISFIED) == 1);
				stockSymbol = c.getString(DataQuery.COLUMN_STOCK_SYMBOL);
				conditional = c.getString(DataQuery.COLUMN_CONDITIONAL);
				stockPrice = c.getString(DataQuery.COLUMN_STOCK_PRICE);	
			}
		} finally {
			c.close();
		}

		return null;
	 }

	final private static class DirtyQuery {

	        private DirtyQuery() {
	        }

	        public final static String[] PROJECTION = new String[] {
	            AlertContentProvider.KEY_ALERT_ID,
	            AlertContentProvider.KEY_SERVER_ID,
	            AlertContentProvider.KEY_DIRTY,
	            AlertContentProvider.KEY_DELETED,
	            AlertContentProvider.KEY_VERSION
	            };

	        public final static int COLUMN_RAW_ALERT_ID = 0;
	        public final static int COLUMN_SERVER_ID = 1;
	        public final static int COLUMN_DIRTY = 2;
	        public final static int COLUMN_DELETED = 3;
	        public final static int COLUMN_VERSION = 4;

	        public static final Uri CONTENT_URI = AlertContentProvider.CONTENT_URI.buildUpon()
	            .appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true")
	            .build();
	        
//	        public static final Uri CONTENT_URI = AlertContentProvider.CONTENT_URI;
	        
	        public static final String SELECTION =
	            AlertContentProvider.KEY_DIRTY + " = 1";

	    }
	
	/**
     * Constants for a query to get alert data for a given rawAlertId
     */
    final private static class DataQuery {

        private DataQuery() {
        }

        public static final String[] PROJECTION =
            new String[] {
        	AlertContentProvider.KEY_ALERT_ID, 
        	AlertContentProvider.KEY_SERVER_ID, 
        	AlertContentProvider.KEY_DIRTY,
            AlertContentProvider.KEY_DELETED,
            AlertContentProvider.KEY_VERSION,
            AlertContentProvider.KEY_DEVICE_INFO_ID,
            AlertContentProvider.KEY_CREATED_TIMESTAMP,
            AlertContentProvider.KEY_CHECKED_TIMESTAMP,
            AlertContentProvider.KEY_ACTIVE,
            AlertContentProvider.KEY_SATISFIED,
            AlertContentProvider.KEY_STOCK_SYMBOL,
            AlertContentProvider.KEY_STOCK_PRICE,
            AlertContentProvider.KEY_CONDITIONAL
            };

        public static final int COLUMN_ID = 0;
        public static final int COLUMN_SERVER_ID = 1;
        public static final int COLUMN_DIRTY = 2;
        public static final int COLUMN_DELETED = 3;
        public static final int COLUMN_VERSION = 4;
        public static final int COLUMN_DEVICE_INFO_ID = 5;
        public static final int COLUMN_CREATED_TIMESTAMP = 6;
        public static final int COLUMN_CHECKED_TIMESTAMP = 7;
        public static final int COLUMN_ACTIVE = 8;
        public static final int COLUMN_SATISFIED = 9;
        public static final int COLUMN_STOCK_SYMBOL = 10;
        public static final int COLUMN_STOCK_PRICE = 11;
        public static final int COLUMN_CONDITIONAL = 12;
        
        public static final Uri CONTENT_URI = AlertContentProvider.CONTENT_URI;

        public static final String SELECTION = AlertContentProvider.KEY_ALERT_ID + "=?";
    }
	
    /**
     * Constants for a query to find a alert given a sample SyncAdapter user
     * ID.
     */
    final private static class UserIdQuery {

        private UserIdQuery() {
        }

        public final static String[] PROJECTION = new String[] {
            AlertContentProvider.KEY_ALERT_ID,
            };

        public final static int COLUMN_RAW_CONTACT_ID = 0;

        public final static Uri CONTENT_URI = AlertContentProvider.CONTENT_URI;

        public static final String SELECTION =
                AlertContentProvider.KEY_SERVER_ID + "=?";
    }
    
}
