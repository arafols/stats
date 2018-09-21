package com.banking.challenge.stats.service;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import com.banking.challenge.stats.domain.Statistics;
import com.banking.challenge.stats.domain.Transaction;
import com.banking.challenge.stats.exception.OldTransactionException;
import com.banking.challenge.stats.service.StatsCalculationService;
import com.banking.challenge.stats.service.StatsCalculationServiceImpl;


/**
 * Stats Service tests 
 * @author agusti
 *
 */
@RunWith(SpringRunner.class)
public class StatsCalculationServiceTests {

	Transaction t1, t2, t3, t4;
	
	@InjectMocks
	private StatsCalculationService statsCalculationService = new StatsCalculationServiceImpl();

	@Before
    public void setup() {
		setupTransactions();
		MockitoAnnotations.initMocks(this);
	}

	
	/**
	 * Insert some transactions and see they are stored
	 * @throws Exception
	 */
	@Test
	public void putTransactionTest() throws Exception{
		
		sanitize();
	
		statsCalculationService.putTransaction(t1);
		statsCalculationService.putTransaction(t2);
		statsCalculationService.putTransaction(t3);
		
		Field transactions = StatsCalculationServiceImpl.class.getDeclaredField("transactions");
		transactions.setAccessible(true);
		
		ConcurrentHashMap<Long, List<Transaction>> savedTransactions = (ConcurrentHashMap<Long, List<Transaction>>)transactions.get(null);

		List<Transaction> bucket = savedTransactions.elements().nextElement();
		
		assertTrue(t1.equals(bucket.get(0)));
		assertTrue(t2.equals(bucket.get(1)));
		assertTrue(t3.equals(bucket.get(2)));
		
	}
	
	/**
	 * Insert 2 transactions belonging to different second, check they are stored under different keys
	 * @throws Exception
	 */
	@Test
	public void transactionKeysOccupationTest() throws Exception{
		
		sanitize();
		
		statsCalculationService.putTransaction(t1);
		statsCalculationService.putTransaction(t4);
		
		Field transactions = StatsCalculationServiceImpl.class.getDeclaredField("transactions");
		transactions.setAccessible(true);
		
		ConcurrentHashMap<Long, List<Transaction>> savedTransactions = (ConcurrentHashMap<Long, List<Transaction>>)transactions.get(null);
		
		assertTrue(savedTransactions.keySet().size() == 2);
		
	}
	
	/**
	 * Insert a few elements and verify the calculations are fine for the stats
	 * @throws Exception
	 */
	@Test
	public void performCalculationsTest() throws Exception{
		
		sanitize();
		
		statsCalculationService.putTransaction(t1);
		statsCalculationService.putTransaction(t2);
		statsCalculationService.putTransaction(t3);
		
		final Statistics current = statsCalculationService.performCalculations();
		
		assertTrue(current.getCount() == 3);
		assertTrue(current.getSum().equals(BigDecimal.valueOf(21.0)));
		assertTrue(current.getMax().equals(BigDecimal.valueOf(12.3)));
		assertTrue(current.getMin().equals(BigDecimal.valueOf(2.3)));
		assertTrue(current.getAvg().equals(BigDecimal.valueOf(7.0)));
	}
	
	/**
	 * Throw 204 when timestamp belongs to the past beyond the statistics range
	 * @throws OldTransactionException
	 */
	@Test(expected = OldTransactionException.class)
	public void handlePastTimestampTest() throws OldTransactionException {
		
		final long pastTimestamp = 1478192204000L;
		
		statsCalculationService.putTransaction(new Transaction(pastTimestamp, BigDecimal.ONE));
		
	}
	
	/**
	 * Verify the Transactions inserted are recovered for the calculation of the relevant period
	 * @throws Exception
	 */
	@Test
	public void getStatisticTransactionsTest() throws Exception{
		
		sanitize();
		
		statsCalculationService.putTransaction(t1);
		statsCalculationService.putTransaction(t2);
		

		Method method = StatsCalculationServiceImpl.class.getDeclaredMethod("getStatisticTransactions", Long.class);
        method.setAccessible(true);
        
        List<Transaction> currentStatTransactions = (List<Transaction>)method.invoke(statsCalculationService, System.currentTimeMillis());
        
        assertTrue(currentStatTransactions.size() == 2);
        
        assertTrue(currentStatTransactions.contains(t1));
        assertTrue(currentStatTransactions.contains(t2));
	}

	/**
	 * set some data for the tests
	 */
	private void setupTransactions(){

		long timestamp = System.currentTimeMillis();
		
		t1 = new Transaction( timestamp, BigDecimal.valueOf(12.3));
		t2 = new Transaction( System.currentTimeMillis(), BigDecimal.valueOf(2.3));
		t3 = new Transaction( System.currentTimeMillis(), BigDecimal.valueOf(6.4));
		
		long timestamp2 = System.currentTimeMillis()+3000L;
		t4 = new Transaction( timestamp2, BigDecimal.ZERO);
	}
	
	/**
	 * clean the shared data structure after every test to make sure the tests done fail because of dirty data
	 * @throws Exception
	 */
	private void sanitize() throws Exception{
		
		Field transactions = StatsCalculationServiceImpl.class.getDeclaredField("transactions");
		transactions.setAccessible(true);
		
		ConcurrentHashMap<Long, List<Transaction>> savedTransactions = (ConcurrentHashMap<Long, List<Transaction>>)transactions.get(null);
		
		transactions.set(null, new ConcurrentHashMap<>());
	}
	
}
