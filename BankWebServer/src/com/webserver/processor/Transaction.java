package com.webserver.processor;


/**
 * @author Madhuri Nagaraj Kaushik
 * 
 * This class captures the Balance Update transactions - accountNo, transaction type (CREDIT or DEBIT), amount (to be credited/debited).
 * CREDIT transaction updates the account balance to reflect the amount added.
 * DEBIT transaction updates the account balance to reflect the amount debited. 
 * Before doing DEBIT operation, the account balance is checked to verify if it is sufficient to complete the transaction.
 * 
 */
public class Transaction {
	private String accountNo;
	public enum  TransactionType {
		CREDIT, DEBIT;
	}
	private TransactionType type;
	private double amount;
	
	public Transaction(String accountNo, TransactionType type, double amount) {
		super();
		this.accountNo = accountNo;
		this.type = type;
		this.amount = amount;
	}
	
	public TransactionType getType() {
		return type;
	}
	public void setType(TransactionType type) {
		this.type = type;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	
	
}
