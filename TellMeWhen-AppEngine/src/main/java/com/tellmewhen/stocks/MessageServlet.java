package com.tellmewhen.stocks;

import java.io.IOException;

import com.tellmewhen.stocks.EMF;
import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class MessageServlet extends HttpServlet {
	private static final String API_KEY = "AIzaSyBNQ-v8-NQE23iI-JYG5AO9dwlWfVxGt7A";

	private static final DeviceInfoEndpoint endpoint = new DeviceInfoEndpoint();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		sendMessage(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		sendMessage(req, resp);
	}



	private void sendMessage(HttpServletRequest req, HttpServletResponse resp) 
			throws IOException {
		Sender sender = new Sender(API_KEY);		
		//Get the alertId stored in the task
		String alertId = req.getParameter("alertId");
		//retrieve the alert from the data store
		EntityManager mgr = EMF.get().createEntityManager();		  		  
		StockPriceAlert alert = null;
		try {
			alert = mgr.find(StockPriceAlert.class, alertId);
			if (alert != null) {
				DeviceInfo deviceInfo = mgr.find(DeviceInfo.class, alert.getDeviceInfoId());
				if (deviceInfo != null) {
					doSendViaGcm("alert: " + alertId + ", has been satisfied", sender, deviceInfo);
				}
			}
		} finally {
			mgr.close();
		}
	}

		/**
		 * Sends the message using the Sender object to the registered device.
		 * 
		 * @param message
		 *            the message to be sent in the GCM ping to the device.
		 * @param sender
		 *            the Sender object to be used for ping,
		 * @param deviceInfo
		 *            the registration id of the device.
		 * @return Result the result of the ping.
		 */
		private static Result doSendViaGcm(String message, Sender sender,
				DeviceInfo deviceInfo) throws IOException {
			//DEBUG
			System.out.println("DoSendViaGCM: " + message);
			// Trim message if needed.
			if (message.length() > 1000) {
				message = message.substring(0, 1000) + "[...]";
			}

			Message msg = new Message.Builder().addData("message", message).build();
			Result result = sender.send(msg, deviceInfo.getDeviceRegistrationID(),
					5);
			if (result.getMessageId() != null) {
				String canonicalRegId = result.getCanonicalRegistrationId();
				if (canonicalRegId != null) {
					//@jalal why remove then readd the device?
					//If the message was created but the result returned a canonical registration ID,
					//it's necessary to replace the current registration ID with the canonical one.
					endpoint.removeDeviceInfo(deviceInfo.getDeviceRegistrationID());
					deviceInfo.setDeviceRegistrationID(canonicalRegId);
					endpoint.insertDeviceInfo(deviceInfo);
				}
			} else {
				String error = result.getErrorCodeName();
				if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
					//If the returned error is NotRegistered, it's necessary to remove that registration ID,
					//because the application was uninstalled from the device.
					endpoint.removeDeviceInfo(deviceInfo.getDeviceRegistrationID());
				}
			}

			return result;
		}

	}