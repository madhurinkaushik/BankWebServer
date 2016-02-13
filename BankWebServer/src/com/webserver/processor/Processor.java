package com.webserver.processor;
import java.util.List;

import com.webserver.dao.AccountDAO;
import com.webserver.dao.AccountDAOImpl;
import com.webserver.processor.Transaction.TransactionType;
import com.webserver.server.HttpRequest;
import com.webserver.server.HttpResponse;



/**
 * @author Madhuri Nagaraj Kaushik
 * 
 * This class is a singleton. Provides methods to process the body of HttpRequest and parse business data such as accountNo, Transaction, etc.
 * Handles 4 different business operations - Create account (PUT), Balance update (POST), Balance check (GET), Delete account (DELETE).
 * Error handling for the business operations
 * Invokes DAO to fetch, store, update or delete data.
 *
 */
public class Processor {
	private static Processor processor = null;
	private AccountDAO accountDao = new AccountDAOImpl();

	private Processor() {}
	public static synchronized Processor getInstance() {
		if(processor == null ) {
			processor = new Processor();
		}
		return processor;
	}

	public HttpResponse process(HttpRequest request) {
		HttpResponse response = null;
		List<String> body = request.getBody();
		String httpMethod = request.getMethod();
		String responseMsg = null;

		if(httpMethod.equals(HttpRequest.HttpMethod.PUT)) {
			if(body != null && body.size() > 0) {
				String line = body.get(0);
				String accountNo = line.split(" ")[0];
				boolean createStatus = accountDao.createAccount(accountNo);
				
				
				if(createStatus) {
					responseMsg = OperationType.CREATE+" successful. Account "+accountNo+" created.";
				} else {
					responseMsg = OperationType.CREATE+" failed. Account "+accountNo+" already exists.";
				}
				response = new HttpResponse(HttpResponse.StatusCode.OK).frameHttpResponse(responseMsg);
			}
		} else if(httpMethod.equals(HttpRequest.HttpMethod.POST)) {
			if(body != null && body.size() > 0) {
				String line = body.get(0);
				String[] txnParams = line.split(" ");
				if(txnParams.length == 3) {
					TransactionType type = null;
					if(txnParams[1].equalsIgnoreCase("CREDIT")) {
						type = TransactionType.CREDIT;
					} else if(txnParams[1].equalsIgnoreCase("DEBIT")) {
						type = TransactionType.DEBIT;
					}
					double amount = 0.0;
					try {
						amount = Double.parseDouble(txnParams[2]);
					} catch(NumberFormatException e) {
						System.out.println(e.getMessage());
						throw e;
					}
					Transaction transaction = new Transaction(txnParams[0], type, amount);
					boolean updateStatus = accountDao.updateBalance(transaction);
					
					if(updateStatus) {
						responseMsg = OperationType.UPDATE+" successful for account "+txnParams[0]+". Amount $"+amount+" "+type;
					} else {
						responseMsg = OperationType.UPDATE+" failed. Account "+txnParams[0]+" does not exist, or not enough balance in the account.";
					}
					response = new HttpResponse(HttpResponse.StatusCode.OK).frameHttpResponse(responseMsg);
				}
			}
		} else if(httpMethod.equals(HttpRequest.HttpMethod.GET)) {
			String line = request.getUrl();
			if(line != null) {
				String[] accountData = line.split("/");
				String accountNo = null;
				if(accountData.length == 3) {
					accountNo = accountData[2];
				} else {
					responseMsg = OperationType.BALANCE_CHECK+" failed. Please check the URL. The expected format of the URL is <Server IP address>:<port>/account/<accountNo>";
					response = new HttpResponse(HttpResponse.StatusCode.OK).frameHttpResponse(responseMsg);
					return response;
				}
				double balance = accountDao.checkBalance(accountNo);
				
				if(balance >= 0) {
					responseMsg = OperationType.BALANCE_CHECK+" successful. Account balance = "+balance;
				} else {
					responseMsg = OperationType.BALANCE_CHECK+" failed. Account "+accountNo+" does not exist.";
				}
				
				response = new HttpResponse(HttpResponse.StatusCode.OK).frameHttpResponse(responseMsg);
			} else {
				response = new HttpResponse(HttpResponse.StatusCode.OK).frameHttpResponse(responseMsg);
			}
		} else if(httpMethod.equals(HttpRequest.HttpMethod.DELETE)) {
			if(body != null && body.size() > 0) {
				String line = body.get(0);
				String accountNo = line.split(" ")[0];
				boolean deleteStatus = accountDao.deleteAccount(accountNo);
				
				if(deleteStatus) {
					responseMsg = OperationType.DELETE+" successful. Account "+accountNo+" deleted.";
				} else {
					responseMsg = OperationType.DELETE+" failed. Account "+accountNo+" does not exist.";
				}
				
				response = new HttpResponse(HttpResponse.StatusCode.OK).frameHttpResponse(responseMsg);
			}
		}
		return response;
	}
	
	public static class OperationType {
		public static final String CREATE = "Create operation";
		public static final String UPDATE = "Balance update operation";
		public static final String BALANCE_CHECK = "Balance check operation";
		public static final String DELETE = "Delete operation";
	}
}


