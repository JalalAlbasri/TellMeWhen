package com.tellmewhen.stocks;

import java.net.*;
import java.io.*;
import java.util.StringTokenizer;

public class StockService {
	String urlString;
	String csvString;
	URL url;
	URLConnection urlConn;
	InputStreamReader  inStream;
	BufferedReader buff;

	public StockService() {

	}

	/**
	 * Queries server for stock symbols current market price. 
	 * Return -1 on failure.
	 */
	public double getStockPrice(String stockSymbol) {
			
		try{
			urlString = "http://finance.yahoo.com/d/quotes.csv?s=" 
					+ stockSymbol + "&f=l1";
			url = new URL(urlString);

			urlConn = url.openConnection();
			inStream = new
					InputStreamReader(urlConn.getInputStream());
			buff= new BufferedReader(inStream);

			// get the quote as a csv string
			csvString =buff.readLine();  

			// parse the csv string
			StringTokenizer tokenizer = new
					StringTokenizer(csvString, ",");
			String price = tokenizer.nextToken();

			return Double.parseDouble(price);

		} catch(MalformedURLException e){
			System.out.println("Please check the spelling of the URL:" 
					+ e.toString() );
			return -1;
		} catch(IOException  e1){
			System.out.println("Can't read from the Internet: " + 
					e1.toString());
			return -1;
		}
		finally{
			try{
				inStream.close();
				buff.close();   
			}catch(Exception e){
				e.printStackTrace();
			}
		}  
	}
}
