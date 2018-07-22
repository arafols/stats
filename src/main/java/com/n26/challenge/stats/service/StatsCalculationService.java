package com.n26.challenge.stats.service;

import com.n26.challenge.stats.domain.Statistics;
import com.n26.challenge.stats.domain.Transaction;

public interface StatsCalculationService {

	/**
	 * @param t
	 * @throws Exception
	 */
	void putTransaction(Transaction t);
	
	/**
	 * @return
	 */
	Statistics performCalculations();
	
	void writeStatistics(Statistics freshStats);

	Statistics readStatistics();
	
}
