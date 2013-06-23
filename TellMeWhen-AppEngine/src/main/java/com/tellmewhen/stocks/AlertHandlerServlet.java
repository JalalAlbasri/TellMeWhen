package com.tellmewhen.stocks;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import com.tellmewhen.stocks.EMF;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public class AlertHandlerServlet extends HttpServlet{

	  private static final Logger logger = 
			  Logger.getLogger(AlertHandlerServlet.class.getCanonicalName());
	
	@Override
	  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	      throws ServletException, IOException {
	    checkAlert(req, resp);
	  }

	  @Override
	  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	      throws ServletException, IOException {
	    checkAlert(req, resp);
	  }
	  
	  /**
	   * This method get a task from queue with an alertID.
	   * It will get the Alert from the DataStore. 
	   * Check that the alert is still active and unsatisfied, if not active remove it from queue.
	   * Otherwise, call the alert's evaluate alert method.
	   * if it is successful then queue a message to be sent at the message queue
	   * return 200 so that the alert is removed from the queue. 
	   * If unsuccessful return an error response so that the message is retried.
	   * 
	   */
	  
	  private void checkAlert(HttpServletRequest req, HttpServletResponse resp)
			  throws IOException {
		  //Get the alertId stored in the task
		  String alertId = req.getParameter("alertId");
		  //@DEBUG
		  System.out.println("AlertId: " + alertId);
		  
		  //retrieve the alert from the data store
		  EntityManager mgr = EMF.get().createEntityManager();		  		  
		  StockPriceAlert alert = null;
		  try {
			  	alert = mgr.find(StockPriceAlert.class, alertId);
			  	if (alert != null && alert.isActive() && !alert.isSatisfied()) {
			  		//Evaluate the alert see if success criteria has been satisfied
			  		if (alert.evaluateAlert()) {
			  			//Deactivate the Alert and Persist to Data Store.
			  			alert.setSatisfied(true);
			  			alert.setActive(false);
			  			mgr.persist(alert);
			  			//Add the alert to the message queue.
			  			Queue alertQueue = QueueFactory.getQueue("MessageQueue");
						alertQueue.add(withUrl("/SendMessage").param("alertId", 
								KeyFactory.keyToString(alert.getAlertId())));
			  		} else {
				  		//Alert not satisfied, return response so queue tries again.
			  			resp.sendError(resp.SC_SERVICE_UNAVAILABLE);
				  	}
			  	}
			} finally {
				mgr.close();
			} 
	  }
}
