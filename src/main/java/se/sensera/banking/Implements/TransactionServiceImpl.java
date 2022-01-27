package se.sensera.banking.Implements;

import se.sensera.banking.Transaction;
import se.sensera.banking.TransactionService;
import se.sensera.banking.exceptions.UseException;

import java.util.function.Consumer;

public class TransactionServiceImpl implements TransactionService {
    @Override
    public Transaction createTransaction(String created, String userId, String accountId, double amount) throws UseException {
        return null;
    }

    @Override
    public double sum(String created, String userId, String accountId) throws UseException {
        return 0;
    }

    @Override
    public void addMonitor(Consumer<Transaction> monitor) {

    }
}
