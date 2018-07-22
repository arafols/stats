package com.n26.challenge.stats;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

public class TimeStampTest {

	@Test
	public void RoundToSecondLocalDateTimetest() {
//		fail("Not yet implemented");
		LocalDateTime ts = LocalDateTime.of(2013, 12, 18, 14, 30, 40);
		LocalDateTime roundTs = 
				ts.truncatedTo(ChronoUnit.MINUTES);
		LocalDateTime tsRounded = 
				LocalDateTime.of(2013, 12, 18, 14, 30, 00);
//		
//		ts.get
//		System.out.println(roundTs.getSecond());
		System.out.println(roundTs.toInstant(ZoneOffset.UTC).toEpochMilli());
//		System.out.println(tsRounded.getSecond());
		System.out.println(tsRounded.toInstant(ZoneOffset.UTC).toEpochMilli());//long
		assertEquals(roundTs, tsRounded);	
	
	}
	
	@Test
	public void RoundToSecondInstantest() {
		long millis = System.currentTimeMillis() ;
		
		Instant instant = Instant.ofEpochSecond(System.currentTimeMillis());
		Instant instantTrunc = instant.truncatedTo( ChronoUnit.SECONDS ) ;
		System.out.println(millis);
		System.out.println(instantTrunc.getEpochSecond());
//		assertEquals(0, instantTrunc.get(ChronoField.SECOND_OF_DAY));	
	}
	
	@Test
	public void calculate60SecondsBackwards() {
		
		Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());
//		Instant instantTrunc = instant.truncatedTo( ChronoUnit.SECONDS ) ;

		System.out.println(instant.truncatedTo( ChronoUnit.SECONDS ).with(ChronoField.NANO_OF_SECOND, 0L).getEpochSecond());
		
		for (int i = 0; i < 60; i++) {
			System.out.println((instant.minus(1L*i,ChronoUnit.SECONDS )).truncatedTo( ChronoUnit.SECONDS ).with(ChronoField.NANO_OF_SECOND, 0L).getEpochSecond() );
		}
		
		System.out.println(Instant.ofEpochSecond(1532258314L));
		System.out.println(Instant.ofEpochMilli(1532258351615L));
		System.out.println(Instant.ofEpochMilli(1532258351316L));
		
	}

}
