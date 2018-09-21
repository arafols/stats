package com.banking.challenge.stats.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Basic exception to hold old transactions received by the service
 * 
 * @author agusti
 *
 */
@ResponseStatus(HttpStatus.NO_CONTENT)
public class OldTransactionException extends RuntimeException {

	private static final long serialVersionUID = -8291034455762269791L;
	
}
