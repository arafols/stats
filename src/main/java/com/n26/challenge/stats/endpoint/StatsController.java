package com.n26.challenge.stats.endpoint;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.n26.challenge.stats.domain.Statistics;
import com.n26.challenge.stats.domain.Transaction;
import com.n26.challenge.stats.service.StatsCalculationService;

/**
 * API for the statistics published endpoints
 * @author agusti
 *
 */
@RestController
public class StatsController {

	@Autowired
	private StatsCalculationService calculationService; 
	
	@RequestMapping(method = RequestMethod.GET, path = "/statistics", produces = APPLICATION_JSON_UTF8_VALUE)
	public Statistics statistics() {
		
		return calculationService.readStatistics();
	}
	
	@RequestMapping(method = RequestMethod.POST, path = "/transactions", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public ResponseEntity<String> transactions(@RequestBody Transaction t) {
			
		calculationService.putTransaction(t);
		return new ResponseEntity<>(HttpStatus.CREATED);
	
	}
}
