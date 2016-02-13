package com.webserver.dao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.webserver.processor.Transaction;
import com.webserver.processor.Transaction.TransactionType;


/**
 * @author Madhuri Nagaraj Kaushik
 * 
 * This class implements the business operations.
 * Maintains the account balance of user accounts in a Map.
 * ConcurrentHashMap is used for this purpose to improve performance while keeping the data structure thread-safe.
 * Error handling for business operations.
 *
 */

public class AccountDAOImpl implements AccountDAO {

	private static Map<String, Double> accountBalanceMap = new ConcurrentHashMap<String, Double>();

	@Override
	public boolean createAccount(String accountNo) {
		boolean createStatus = false;
		if(accountBalanceMap.get(accountNo) == null) {
			accountBalanceMap.put(accountNo, 0.0);
			createStatus = true;
		} 
		return createStatus;
	}

	@Override
	public boolean updateBalance(Transaction transaction) {
		boolean updateStatus = true;
		String accountNo = transaction.getAccountNo();
		Double currentBalance = accountBalanceMap.get(accountNo);
		if(currentBalance != null) {
			if(transaction.getType().equals(TransactionType.CREDIT)) {
				accountBalanceMap.put(accountNo, currentBalance+transaction.getAmount());
			} else if(transaction.getType().equals(TransactionType.DEBIT)) {
				if(currentBalance >= transaction.getAmount()) {
					accountBalanceMap.put(accountNo, currentBalance-transaction.getAmount());
				} else {
					updateStatus = false;
				}
			}
		} else {
			updateStatus = false;
		}
		return updateStatus;
	}

	@Override
	public double checkBalance(String accountNo) {
		double balance = Double.NEGATIVE_INFINITY;
		if(accountBalanceMap.get(accountNo) != null) {
			balance = accountBalanceMap.get(accountNo);
		}
		return balance;
	}

	@Override
	public boolean deleteAccount(String accountNo) {
		boolean deleteStatus = false;
		if(accountBalanceMap.get(accountNo) != null) {
			accountBalanceMap.remove(accountNo);
			deleteStatus = true;
		}
		return deleteStatus;
	}

}
