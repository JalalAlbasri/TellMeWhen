package com.tellmewhen.stocks;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.google.appengine.api.datastore.Key;

/**
 * An entity for storing Alert information.
 * 
 * Used to automatically generate Alert Endpoint used for CRUD Alerts
 * from mobile device. 
 * 
 * @author jalal
 *
 */
@Entity
@MappedSuperclass
public abstract class AbstractAlert {
	public AbstractAlert() {
		
	}
	
	public AbstractAlert(String deviceInfoId) {
		super();
		this.deviceInfoId = deviceInfoId;
		this.createdTimestamp = System.currentTimeMillis();
		this.active = true;
		this.satisfied = false;
	}

	/*
	 * An alertID to uniquely identify this Alert.
	 * Generated automatically the first time Alert is persisted to the Data Store.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key alertId;
	
	/*
	 * The Registration ID of the Device Registering this Alert.
	 */
	private String deviceInfoId;
	
	/*
	 * Timestamp Created indicating when this alert was registered with the application.
	 */
	private long createdTimestamp;
	
	/*
	 * Last checked
	 */
	private long checkedTimestamp;
	
	/*
	 * Indicates whether this is an active Alert.
	 */
	private boolean active;
	
	/*
	 * Indicates whether or not the Alert conditional is satisfied.
	 */
	private boolean satisfied;

	public Key getAlertId() {
		return alertId;
	}
	
	public String getDeviceInfoId() {
		return deviceInfoId;
	}

	public long getCreatedTimestamp() {
		return createdTimestamp;
	}

	public long getCheckedTimestamp() {
		return checkedTimestamp;
	}

	public void setCheckedTimestamp(long checkedTimestamp) {
		this.checkedTimestamp = checkedTimestamp;
	}

	/*
	 * TODO: Add method to reset the alert by setting 
	 * active = true and satisfied = false.
	 * and seperate methods, deactivate task and 
	 * task satisfied...
	 */
	
	public void setCreatedTimestamp(long createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public void setDeviceInfoId(String deviceInfoId) {
		this.deviceInfoId = deviceInfoId;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isSatisfied() {
		return satisfied;
	}

	public void setSatisfied(boolean satisfied) {
		this.satisfied = satisfied;
	}
	
	protected abstract boolean validateConditional(String conditional);
	protected abstract boolean evaluateAlert();
}