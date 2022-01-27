package se.sensera.banking.Implements;

import lombok.AllArgsConstructor;
import lombok.Data;
import se.sensera.banking.Account;
import se.sensera.banking.Transaction;
import se.sensera.banking.User;

import java.util.Date;


@AllArgsConstructor
@Data
public class TransactionImpl implements Transaction {
    String id;
    Date created;
    User user;
    Account account;
    double amount;


}
