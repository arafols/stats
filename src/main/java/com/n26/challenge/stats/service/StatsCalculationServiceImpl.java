package com.n26.challenge.stats.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.n26.challenge.stats.domain.Statistics;
import com.n26.challenge.stats.domain.Transaction;
import com.n26.challenge.stats.exception.OldTransactionException;

@Service
public class StatsCalculationServiceImpl implements StatsCalculationService {

	//concurrent and thread-safe, no need to use synchronized
	private static ConcurrentHashMap<Long, List<Transaction>> transactions = new ConcurrentHashMap<>();

	private static Statistics stats = null;
	
	/* (non-Javadoc)
	 * @see com.n26.challenge.stats.service.StatsCalculationService#performCalculations()
	 */
	@Override
	public Statistics performCalculations() {
		List<Transaction> transactionsHappened = getStatisticTransactions(System.currentTimeMillis());
		
		Statistics freshStats = new Statistics();
		
		int count = transactionsHappened.size();
		freshStats.setCount(transactionsHappened.size());
		
		Optional<BigDecimal> sum = transactionsHappened.parallelStream().filter(Objects::nonNull).map(Transaction::getAmount).reduce(BigDecimal::add);
		if(sum.isPresent()) {
			freshStats.setSum(sum.get());
			
			BigDecimal avg = sum.get().divide(BigDecimal.valueOf(count));
			freshStats.setAvg(avg);
		}
		
		Optional<BigDecimal> max = transactionsHappened.parallelStream().filter(Objects::nonNull).map(Transaction::getAmount).reduce(BigDecimal::max);
		if(max.isPresent()) {
			freshStats.setMax(max.get());
		}
		
		Optional<BigDecimal> min = transactionsHappened.parallelStream().filter(Objects::nonNull).map(Transaction::getAmount).reduce(BigDecimal::min);
		if(min.isPresent()) {
			freshStats.setMin(min.get());
		}
		
		return freshStats;
	}

	/* (non-Javadoc)
	 * @see com.n26.challenge.stats.service.StatsCalculationService#putTransaction(com.n26.challenge.stats.domain.Transaction)
	 */
	@Override
	public void putTransaction(final Transaction t){

		final Instant lowTimeBoundary = Instant.now().minus(1, ChronoUnit.MINUTES);
		final Instant transactionTimestamp = Instant.ofEpochMilli(t.getTimestamp());

		if(transactionTimestamp.isBefore(lowTimeBoundary)){
			throw new OldTransactionException();
		}
		
		//keys in the map are the seconds, so the other service can retrieve last 60 seconds  
		Instant timeKey = transactionTimestamp.truncatedTo( ChronoUnit.SECONDS ).with(ChronoField.NANO_OF_SECOND, 0L);
		
		if(transactions.get(timeKey.getEpochSecond()) == null) {
			List<Transaction> bucket = new ArrayList<>();
			bucket.add(t);
			transactions.put(timeKey.getEpochSecond(), bucket);
		
		} else {
			
			transactions.get(timeKey.getEpochSecond()).add(t);			
		}	
	}
	
	/* (non-Javadoc)
	 * @see com.n26.challenge.stats.service.StatsCalculationService#writeStatistics(com.n26.challenge.stats.domain.Statistics)
	 */
	@Override
	public  void writeStatistics(Statistics freshStats) {
		synchronized (Statistics.class){
			
			stats = freshStats;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.n26.challenge.stats.service.StatsCalculationService#readStatistics()
	 */
	@Override
	public Statistics readStatistics() {
		return stats;
	}

	/**
	 * @param timestamp
	 * @return
	 */
	private List<Transaction> getStatisticTransactions(Long timestamp){
		Instant timeKey = Instant.ofEpochMilli(timestamp).truncatedTo(ChronoUnit.SECONDS);
		
		List<Transaction> lastMinTransactions = new ArrayList<>();
		
		//generate keys for last 60 seconds
		List<Transaction> temp;

		for (int i = 0; i < 60; i++) {
//			System.out.println(timeKey.minus(1L*i,ChronoUnit.SECONDS ).getEpochSecond());
			temp = transactions.get((timeKey.minus(1L*i,ChronoUnit.SECONDS )).getEpochSecond());
			
			if(temp != null && !temp.isEmpty()) {
				
				lastMinTransactions.addAll(temp);
			}
		}
		
		return lastMinTransactions;
	}

}