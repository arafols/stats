package com.n26.challenge.stats;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
	
//	@Test
//	public void RoundToSecondInstantest() {
//		
//		Instant instant = Instant.ofEpochMilli( 1_384_393_612_958L ) ;
//		Instant instantTrunc = instant.truncatedTo( ChronoUnit.SECONDS ) ;
//		long millis = instantTrunc.toEpochMilli() ;
//		System.out.println(millis);
//		System.out.println(instantTrunc.get(ChronoField.SECOND_OF_MINUTE));
////		assertEquals(0, instantTrunc.get(ChronoField.SECOND_OF_DAY));	
//	}

}
