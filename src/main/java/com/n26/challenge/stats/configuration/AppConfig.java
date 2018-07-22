package com.n26.challenge.stats.configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.n26.challenge.stats.service.StatsCalculationService;

@Configuration
public class AppConfig extends WebMvcConfigurerAdapter {
	
	
	
	@Autowired
	private StatsCalculationService calculations;
	
	@Bean
	@Primary
	public ScheduledExecutorService scheduler() {
		
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

		return executor;
	}
	
	
//	@Bean
//	public /*static*/ void statisticsFactory() {
////		if(stats == null) {
////			
////			synchronized (Statistics.class) {
//				if(stats == null) {
//					stats = new Statistics();
//				}
//				
//				
////			}
////		}
////		return stats;
//	}
	
	@PostConstruct
	public void startCalculations() {
		
		ScheduledExecutorService executor = scheduler();
//		statisticsFactory();
		
				
		Runnable task = () -> {
			//TODO: restore write!! 
//			calculations.writeStatistics(calculations.performCalculations());
			System.out.println("running");
			 
		};
		int initialDelay = 1;
		int period = 1;
		executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
	}
	
	

}
