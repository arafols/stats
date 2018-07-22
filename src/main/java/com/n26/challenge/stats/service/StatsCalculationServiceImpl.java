package com.n26.challenge.stats.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
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
	 * @see com.n26.challenge.stats.service.StatsCalculationService#putTransaction(com.n26.challenge.stats.domain.Transaction)
	 */
	@Override
	public void putTransaction(final Transaction t){
		
		final LocalDateTime transactionTimestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(t.getTimestamp()), TimeZone.getDefault().toZoneId());
		final LocalDateTime lowTimeBoundary = LocalDateTime.now(ZoneOffset.systemDefault().normalized()).minusMinutes(1);

		if(transactionTimestamp.isBefore(lowTimeBoundary)){
			throw new OldTransactionException();
		}
		
		//keys in the map are the seconds, so the other service can retrieve last 60 seconds  
		long timeKey = Instant.ofEpochSecond(t.getTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli();
		
		
		if(transactions.get(timeKey) == null) {
			List<Transaction> bucket = new ArrayList<>();
			bucket.add(t);
			transactions.put(timeKey, bucket);
		
		} else {
			
			transactions.get(timeKey).add(t);			
		}	
	}
	
	@Override
	public  void writeStatistics(Statistics freshStats) {
		synchronized (Statistics.class){
			
			stats = freshStats;
		}
	}
	
	@Override
	public Statistics readStatistics() {
		return stats;
	}

	private List<Transaction> getStatisticTransactions(Long timestamp){
		long timeKey = Instant.ofEpochSecond(timestamp).truncatedTo(ChronoUnit.SECONDS).toEpochMilli();
		
		
		List<Transaction> keys = new ArrayList<>();
		
		//generate keys for last 60 seconds
		for (int i = 0; i < 600; i++) {
			System.out.println(timeKey - 1000L*i);
			List<Transaction> temp = transactions.get(timeKey - 1000L*i);
			if(temp != null && !temp.isEmpty()) {
				
				keys.addAll(temp);
			}
		}
		
		return keys;
	} 
	
//	/**
//	 * @param timestamp
//	 * @return
//	 */
//	private List<Transaction> getTransactions(Long timestamp) {
//		
////		long timeKey = Instant.ofEpochSecond(timestamp).truncatedTo(ChronoUnit.SECONDS).toEpochMilli();
//		
//		if(transactions.get(timestamp) != null) {
//			return transactions.get(timestamp);
//		}
//		
//		return new ArrayList<>();
//	}
	
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

}
