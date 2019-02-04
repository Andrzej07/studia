package dao;

import model.Account;
import model.Transfer;
import model.User;

import java.util.List;

/**
 * Database access interface
 */
public interface Dao {

    /**
     * Saves user to database.
     * @param user user to save
     * @return true if saved, false if user already existed
     */
    boolean addUser(User user);

    /**
     * Retrieve user from DB.
     * @param login requested user login
     * @return user with the same login, null otherwise.
     */
    User getUser(String login);

    /**
     * Retrieve account from DB.
     * @param number account number to search for.
     * @return account with specified number, null if not found.
     */
    Account getAccount(String number);

    /**
     * Retrieve all accounts for given user
     * @param user
     * @return list of accounts for user
     */
    List<Account> getAccounts(User user);

    /**
     * Create or update account.
     * @param account
     * @return saved account.
     */
    Account saveAccount(Account account);

    /**
     * Save operation information.
     * @param transfer
     */
    void saveHistory(Transfer transfer);

    /**
     * Retrieve all operations for account.
     * @param account
     * @return
     */
    List<Transfer> getHistory(Account account);

    /**
     * Generate last 16 digits of next account number.
     * @return
     */
    String getNextAccountNumber();
}
