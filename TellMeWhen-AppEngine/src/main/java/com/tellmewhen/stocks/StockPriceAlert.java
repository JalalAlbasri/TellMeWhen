package com.tellmewhen.stocks;

import javax.persistence.Entity;

import com.tellmewhen.stocks.StockService;

/*
 * Stock Alert
 * Alert for monitoring the price of a specific stock.
 */
@Entity
public class StockPriceAlert extends AbstractAlert {

	public StockPriceAlert(String deviceInfoId, String stockSymbol, double stockPrice, String conditional) {
		super(deviceInfoId);
		this.stockSymbol = stockSymbol;
		this.stockPrice = stockPrice;
		this.conditional = conditional;
	}

	//Default constructor used by endpoint client.
	public StockPriceAlert() {
		super();
	}

	private String stockSymbol;
	private double stockPrice;
	private String conditional;

	public String getStockSymbol() {
		return stockSymbol;
	}
	public void setStockSymbol(String stockSymbol) {
		this.stockSymbol = stockSymbol;
	}
	public double getStockPrice() {
		return stockPrice;
	}
	public void setStockPrice(double stockPrice) {
		this.stockPrice = stockPrice;
	}
	public String getConditional() {
		return conditional;
	}

	/*
	 * Validate the conditional can only be,
	 * < > = <= >= !=
	 */
	public void setConditional(String conditional) throws Exception {
		if (validateConditional(conditional)) {
			this.conditional = conditional;
		}
		else {
			/*
			 * TODO What to do here? Fail creating object, throw an exception.
			 */
			this.conditional = "=";
		}
	}

	protected boolean validateConditional(String conditional) {
		return (conditional.equalsIgnoreCase("<") ||
				conditional.equalsIgnoreCase(">") ||
				conditional.equalsIgnoreCase("=") ||
				conditional.equalsIgnoreCase("<=") ||
				conditional.equalsIgnoreCase(">=") ||
				conditional.equalsIgnoreCase("!=") );
	}

	/**
	 * Evaluates the alert for success criteria
	 * Called by the Alert Handler Polymorphically
	 */
	public boolean evaluateAlert() {
		//Call Stock Quote Service to determine alert satisfaction criteria.
		StockService stockService = new StockService();
		double stockMarketPrice = stockService.getStockPrice(this.stockSymbol);
		if (stockMarketPrice > 0) {
			if (conditional.equals("<")) {
                return (stockMarketPrice < this.stockPrice);
            } else if (conditional.equals("<=")) {
                return (stockMarketPrice <= this.stockPrice);
            } else if (conditional.equals(">")) {
                return (stockMarketPrice > this.stockPrice);
            } else if (conditional.equals(">=")) {
                return (stockMarketPrice >= this.stockPrice);
            } else if (conditional.equals("!=")) {
                return (stockMarketPrice != this.stockPrice);
            } else if (conditional.equals("=")) {
                return (stockMarketPrice == this.stockPrice);
            }
		}
		return false;
	}

	@Override
	public String toString(){
		return
				this.stockSymbol + " " + this.conditional + " " + this.stockPrice + "\n" +
				"Active: " + this.isActive() + " Satisfied: " + this.isSatisfied();
	}
}
