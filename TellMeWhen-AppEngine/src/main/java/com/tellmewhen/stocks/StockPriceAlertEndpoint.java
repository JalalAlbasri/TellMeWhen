package com.tellmewhen.stocks;

import com.tellmewhen.stocks.EMF;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JPACursorHelper;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import java.util.List;
import com.google.appengine.api.datastore.KeyFactory;
import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Api(name = "stockpricealertendpoint")
public class StockPriceAlertEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
//	@SuppressWarnings({ "unchecked", "unused" })
//	public CollectionResponse<StockPriceAlert> listStockPriceAlert(
//			@Nullable @Named("cursor") String cursorString,
//			@Nullable @Named("limit") Integer limit) {
//
//		EntityManager mgr = null;
//		Cursor cursor = null;
//		List<StockPriceAlert> execute = null;
//
//		try {
//			mgr = getEntityManager();
//			Query query = mgr
//					.createQuery("select from StockPriceAlert as StockPriceAlert");
//			if (cursorString != null && cursorString != "") {
//				cursor = Cursor.fromWebSafeString(cursorString);
//				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
//			}
//
//			if (limit != null) {
//				query.setFirstResult(0);
//				query.setMaxResults(limit);
//			}
//
//			execute = (List<StockPriceAlert>) query.getResultList();
//			cursor = JPACursorHelper.getCursor(execute);
//			if (cursor != null)
//				cursorString = cursor.toWebSafeString();
//
//			// Tight loop for fetching all entities from datastore and accomodate
//			// for lazy fetch.
//			for (StockPriceAlert obj : execute)
//				;
//		} finally {
//			mgr.close();
//		}
//
//		return CollectionResponse.<StockPriceAlert> builder().setItems(execute)
//				.setNextPageToken(cursorString).build();
//	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	public StockPriceAlert getStockPriceAlert(@Named("id") Long id) {
		EntityManager mgr = getEntityManager();
		StockPriceAlert stockpricealert = null;
		try {
			stockpricealert = mgr.find(StockPriceAlert.class, id);
		} finally {
			mgr.close();
		}
		return stockpricealert;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param stockpricealert the entity to be inserted.
	 * @return The inserted entity.
	 */
	public StockPriceAlert insertStockPriceAlert(StockPriceAlert stockpricealert) {
		System.out.println("insertStockPRiceAlert()");
		EntityManager mgr = getEntityManager();
		try {
			if (stockpricealert.getAlertId() != null) {
				if (containsStockPriceAlert(stockpricealert)) {
					throw new EntityExistsException("Object already exists");
				}
			}
			System.out.println("Persisting Stock Price Alert: " + stockpricealert.toString() + "...........");
			mgr.persist(stockpricealert);
			System.out.println("Persist Successful.");
		} finally {
			mgr.close();
		}
		/**
		 * Edited by @author jalal
		 */
		//Push the alert onto the AlertQueue
		Queue alertQueue = QueueFactory.getQueue("AlertQueue");
		alertQueue.add(withUrl("/CheckAlert").param("alertId", 
				KeyFactory.keyToString(stockpricealert.getAlertId())));
		
		System.out.println("Alert Added to AlertQueue.");
		return stockpricealert;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param stockpricealert the entity to be updated.
	 * @return The updated entity.
	 */
	public StockPriceAlert updateStockPriceAlert(StockPriceAlert stockpricealert) {
		EntityManager mgr = getEntityManager();
		try {
			if (!containsStockPriceAlert(stockpricealert)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.persist(stockpricealert);
		} finally {
			mgr.close();
		}
		return stockpricealert;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 * @return The deleted entity.
	 */
	public StockPriceAlert removeStockPriceAlert(@Named("id") Long id) {
		EntityManager mgr = getEntityManager();
		StockPriceAlert stockpricealert = null;
		try {
			stockpricealert = mgr.find(StockPriceAlert.class, id);
			mgr.remove(stockpricealert);
		} finally {
			mgr.close();
		}
		return stockpricealert;
	}

	private boolean containsStockPriceAlert(StockPriceAlert stockpricealert) {
		EntityManager mgr = getEntityManager();
		boolean contains = true;
		try {
			StockPriceAlert item = mgr.find(StockPriceAlert.class,
					stockpricealert.getAlertId());
			if (item == null) {
				contains = false;
			}
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}

	@ApiMethod(
		      path = "stockpricealert/device/{id}"
			)
			
	public CollectionResponse<StockPriceAlert> listStockPriceAlertbyDeviceInfoId(
			@Named("deviceInfoId") String deviceInfoId,
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {
		
		System.out.println("deviceInfoId: " + deviceInfoId);
		
		EntityManager mgr = null;
		Cursor cursor = null;
		List<StockPriceAlert> execute = null;

		try {
			mgr = getEntityManager();
			Query query = mgr
					.createQuery("SELECT p FROM StockPriceAlert p " +
							"WHERE p.deviceInfoId = :deviceInfoId " +
							"ORDER BY p.createdTimestamp DESCENDING");
			query.setParameter("deviceInfoId", deviceInfoId);
//					.createQuery("select from StockPriceAlert as StockPriceAlert" +
//					" WHERE StockPriceAlert.deviceInfoId EQUALS \"" + deviceInfoId + "\"" +
//					" ORDER BY StockPriceAlert.createdTimestamp DESCENDING");
//					.createQuery("SELECT a FROM StockPriceAlert as a " +
//							"WHERE a.deviceInfoId EQUALS :deviceInfoId " +
//							"ORDER BY a.createdTimestamp DESCENDING");
//			query.setParameter(1, deviceInfoId);
			System.out.println(query.toString());
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}

			if (limit != null) {
				query.setFirstResult(0);
				query.setMaxResults(limit);
			}

			execute = (List<StockPriceAlert>) query.getResultList();
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (AbstractAlert obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<StockPriceAlert> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}
	
}
