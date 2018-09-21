package com.banking.challenge.stats.configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.banking.challenge.stats.service.StatsCalculationService;

/**
 * Setup for the application, adds a thread to calculate the stats every second, so that the actual endpoint just reads the result with cost O(1).
 * ScheduledExecutorService makes it possible by running the calculation every second in the background
 * @author agusti
 *
 */
@Configuration
public class AppConfig extends WebMvcConfigurerAdapter {
	
	@Autowired
	private StatsCalculationService calculations;
	
	@Bean
	@Primary
	public ScheduledExecutorService scheduler() {
		
		return Executors.newScheduledThreadPool(1);

	}
	
	@PostConstruct
	public void startCalculations() {
		
		ScheduledExecutorService executor = scheduler();
		
		//TODO: See how to replace Runnable with a Callable so that when reading the stats the result is ready and no past calculation is read
		Runnable task = () -> {

			calculations.writeStatistics(calculations.performCalculations());
			 
		};

		executor.scheduleAtFixedRate(task, 1, 1, TimeUnit.SECONDS);
	}
	
	

}
