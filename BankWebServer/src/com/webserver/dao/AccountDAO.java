package com.webserver.dao;

import com.webserver.processor.Transaction;


/**
 * @author Madhuri Nagaraj Kaushik
 * 
 * This interface defines the methods to handle the business operations.
 *
 */
public interface AccountDAO {
	public boolean createAccount(String accountNo);
	public boolean updateBalance(Transaction transaction);
	public double checkBalance(String accountNo);
	public boolean deleteAccount(String accountNo);
}
