package com.tellmewhen.stocks;

public class RawAlert {
	
	private static final String TAG = RawAlert.class.getSimpleName();
	
	//Stock Price alert members
	private final String mServerAlertId;
	private final long mRawAlertId;
	private final String mDeviceId;
	private final long mCreatedTimestamp;
	private final long mCheckedTimestamp;
	private final Boolean mActive;
	private final Boolean mSatisfied;
	private final String mStockSymbol;
	private final String mConditional;
	private final String mStockPrice;
	
	//Synchronization fields
	private final boolean mDeleted;
    private final boolean mDirty;
    private final long mSyncState;
	
    public String getServerAlertId() {
		return mServerAlertId;
	}
	public long getRawAlertId() {
		return mRawAlertId;
	}
	public String getDeviceId() {
		return mDeviceId;
	}
	public long getCreatedTimestamp() {
		return mCreatedTimestamp;
	}
	public long getCheckedTimestamp() {
		return mCheckedTimestamp;
	}
	public Boolean isActive() {
		return mActive;
	}
	public Boolean isSatisfied() {
		return mSatisfied;
	}
	public String getStockSymbol() {
		return mStockSymbol;
	}
	public String getConditional() {
		return mConditional;
	}
	public String getStockPrice() {
		return mStockPrice;
	}
	public long getSyncState() {
		return mSyncState;
	}
	public boolean isDeleted() {
		return mDeleted;
	}
	public boolean isDirty() {
		return mDirty;
	}

	public RawAlert(String mServerAlertId, long mRawAlertId,
			String mDeviceId, long mCreatedTimestamp, long mCheckedTimestamp,
			Boolean mActive, Boolean mSatisfied, String mStockSymbol,
			String mConditional, String mStockPrice, boolean mDeleted,
			boolean mDirty, long mSyncState) {
		this.mServerAlertId = mServerAlertId;
		this.mRawAlertId = mRawAlertId;
		this.mDeviceId = mDeviceId;
		this.mCreatedTimestamp = mCreatedTimestamp;
		this.mCheckedTimestamp = mCheckedTimestamp;
		this.mActive = mActive;
		this.mSatisfied = mSatisfied;
		this.mStockSymbol = mStockSymbol;
		this.mConditional = mConditional;
		this.mStockPrice = mStockPrice;
		this.mDeleted = mDeleted;
		this.mDirty = mDirty;
		this.mSyncState = mSyncState;
	}
	
	public static RawAlert create(String mServerAlertId, long mRawAlertId,
			String mDeviceId, long mCreatedTimestamp, long mCheckedTimestamp,
			Boolean mActive, Boolean mSatisfied, String mStockSymbol,
			String mConditional, String mStockPrice, boolean mDeleted,
			boolean mDirty, long mSyncState) {
		return new RawAlert(mServerAlertId, mRawAlertId, mDeviceId, mCreatedTimestamp,
				mCheckedTimestamp, mActive, mSatisfied, mStockSymbol, mConditional, mStockPrice,
				mDeleted, true, -1);
	}
	
	public static RawAlert createDeleted(String mServerAlertId, long mRawAlertId) {
		return new RawAlert(mServerAlertId, mRawAlertId, null, -1,
				-1, null, null, null, null, null, true, true, -1);
	}
	
}
