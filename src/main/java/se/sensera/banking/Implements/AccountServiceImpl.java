package se.sensera.banking.Implements;

import lombok.AllArgsConstructor;
import se.sensera.banking.*;
import se.sensera.banking.exceptions.Activity;
import se.sensera.banking.exceptions.UseException;
import se.sensera.banking.exceptions.UseExceptionType;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Stream;
@AllArgsConstructor

public class AccountServiceImpl implements AccountService {
    private final UsersRepository usersRepository;
    private final AccountsRepository accountsRepository;


    @Override
    public Account createAccount(String userId, String accountName) throws UseException {

        User user = getUserFromRepository(userId, Activity.CREATE_ACCOUNT);
        Account account = new AccountImpl(UUID.randomUUID().toString(), user, accountName, true);
        isAccountNameUnique(accountName);

        return accountsRepository.save(account);
    }

    @Override
    public Account changeAccount(String userId, String accountId, Consumer<ChangeAccount> changeAccountConsumer) throws UseException {
        Account account = getAccountFromRepository(accountId, Activity.UPDATE_ACCOUNT, UseExceptionType.ACCOUNT_NOT_FOUND);
        AtomicBoolean saveAccount = new AtomicBoolean(true);

        doesAccountBelongToOwner(userId, account, Activity.UPDATE_ACCOUNT);
        isAccountActive(account, Activity.UPDATE_ACCOUNT, UseExceptionType.NOT_ACTIVE);
        setAccountName(changeAccountConsumer, account, saveAccount);
        if (!saveAccount.get()) return account;
        return accountsRepository.save(account);
    }

    @Override
    public Account addUserToAccount(String userId, String accountId, String newUserId) throws UseException {
        User newUser = getUserFromRepository(newUserId, Activity.UPDATE_ACCOUNT);
        Account account = getAccountFromRepository(accountId, Activity.UPDATE_ACCOUNT, UseExceptionType.NOT_FOUND);

        isAccountActive(account, Activity.UPDATE_ACCOUNT, UseExceptionType.ACCOUNT_NOT_ACTIVE);
        isNewAssignedUserOwner(userId, newUser.getId());
        isUserAssignedToAccount(newUser, account);
        doesAccountBelongToOwner(userId, account, Activity.UPDATE_ACCOUNT);

        account.addUser(newUser);
        return accountsRepository.save(account);
    }

    @Override
    public Account removeUserFromAccount(String userId, String accountId, String userIdToBeAssigned) throws UseException {
        Account account = getAccountFromRepository(accountId, Activity.UPDATE_ACCOUNT, UseExceptionType.NOT_FOUND);
        User user = getUserFromRepository(userIdToBeAssigned, Activity.UPDATE_ACCOUNT);

        doesAccountBelongToOwner(userId, account, Activity.UPDATE_ACCOUNT);
        ifUserNotAssignedToAccount(userIdToBeAssigned, account);

        account.removeUser(user);
        return accountsRepository.save(account);
    }

    @Override
    public Account inactivateAccount(String userId, String accountId) throws UseException {
        User user = getUserFromRepository(userId, Activity.INACTIVATE_ACCOUNT);
        Account account = getAccountFromRepository(accountId, Activity.INACTIVATE_ACCOUNT, UseExceptionType.NOT_FOUND);

        isAccountActive(account, Activity.INACTIVATE_ACCOUNT, UseExceptionType.NOT_ACTIVE);
        doesAccountBelongToOwner(user.getId(), account, Activity.INACTIVATE_ACCOUNT);

        account.setActive(false);
        return accountsRepository.save(account);
    }

    @Override
    public Stream<Account> findAccounts(String searchValue, String userId, Integer pageNumber, Integer pageSize, SortOrder sortOrder) throws UseException {
        return null;
    }

    private User getUserFromRepository(String userId, Activity activity) throws UseException {
        return usersRepository.getEntityById(userId).
                orElseThrow(() -> new UseException(activity, UseExceptionType.USER_NOT_FOUND));
    }

    private Account getAccountFromRepository(String accountId, Activity activity, UseExceptionType useExceptionType) throws UseException {
        return accountsRepository.getEntityById(accountId).
                orElseThrow(() -> new UseException(activity, useExceptionType));
    }


    private void doesAccountBelongToOwner(String userId, Account account, Activity activity) throws UseException {
        if (!account.getOwner().getId().equals(userId)) {
            throw new UseException(activity, UseExceptionType.NOT_OWNER);
        }
    }
    private void isNewAssignedUserOwner(String userId, String newUserId) throws UseException {
        if (userId.equals(newUserId)) {
            throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.CANNOT_ADD_OWNER_AS_USER);
        }
    }

    private void isUserAssignedToAccount(User newUser, Account account) throws UseException {
        if(account.getUsers()
                .anyMatch(user1 -> user1.getId().equals(newUser.getId()))){
            throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.USER_ALREADY_ASSIGNED_TO_THIS_ACCOUNT);
        }
    }

    private void ifUserNotAssignedToAccount(String newUserId, Account account) throws UseException {
        if(account.getUsers()
                .noneMatch(user1 -> user1.getId().equals(newUserId))){
            throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.USER_NOT_ASSIGNED_TO_THIS_ACCOUNT);
        }
    }

    public boolean accountNameEq(String name){
        return accountsRepository.all()
                .anyMatch(account -> account.getName().equals(name));
    }

    private void setAccountName(Consumer<ChangeAccount> changeAccountConsumer, Account account, AtomicBoolean save) {
        changeAccountConsumer.accept(name -> {
            if (accountNameEq(name)) {
                save.set(false);
                throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.ACCOUNT_NAME_NOT_UNIQUE);
            }
            if (account.getName().equals(name)) {
                save.set(false);
            } else {
                account.setName(name);
            }
        });
    }

    private void isAccountNameUnique(String accountName) throws UseException {
        if (accountsRepository.all()
                .anyMatch(account1 -> account1.getName().equals(accountName)))
            throw new UseException(Activity.CREATE_ACCOUNT, UseExceptionType.ACCOUNT_NAME_NOT_UNIQUE);
    }

    private void isAccountActive(Account account, Activity activity, UseExceptionType useExceptionType) throws UseException {
        if (!account.isActive()) {
            throw new UseException(activity, useExceptionType);
        }
    }

}
