package com.banking.challenge.stats.domain;

import java.io.Serializable;
import java.math.BigDecimal;


public class Transaction implements Serializable {

	private static final long serialVersionUID = 3509239046300172378L;

	private long timestamp;
	private BigDecimal amount;
	
	public Transaction(long timestamp, BigDecimal amount) {
		super();
		this.timestamp = timestamp;
		this.amount = amount;
	}

	public Transaction() {}



	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	
}
