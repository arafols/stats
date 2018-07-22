package com.n26.challenge.stats.service;

import com.n26.challenge.stats.domain.Statistics;
import com.n26.challenge.stats.domain.Transaction;

public interface StatsCalculationService {

	/**
	 * Stores a transaction
	 * @param t
	 * @throws Exception
	 */
	void putTransaction(Transaction t);
	
	/**
	 * Calculates the statistics to be served
	 * @return
	 */
	Statistics performCalculations();
	
	
	/**
	 * The internal thread run by the scheduled executor service uses this method to update the statistics attribute every second
	 * @param freshStats
	 */
	void writeStatistics(Statistics freshStats);

	/**
	 * Actual service method called when the statistics are requested, read the calculated result to keep O(1) for the endpoint
	 * @return
	 */
	Statistics readStatistics();
	
}
