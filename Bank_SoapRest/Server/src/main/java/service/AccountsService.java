package service;

import dao.Dao;
import dao.MongoDao;
import model.Account;
import model.Transfer;
import model.User;
import org.apache.commons.lang3.StringUtils;
import soapservice.ServiceException;

import java.math.BigInteger;

/**
 * Handles operations related to bank account.
 */
public class AccountsService {
    private Dao dao = new MongoDao();
    private ExternalTransferService externalTransferService = new ExternalTransferService();

    /**
     * Generates next account number.
     * @return
     */
    public String getNextAccountNumber() {
        String bankAccount = StringUtils.leftPad("117256", 8, '0') +dao.getNextAccountNumber();
        BigInteger bigNumber = new BigInteger(bankAccount);
        int number = 98 - bigNumber.mod(BigInteger.valueOf(97)).intValue();

        if(number < 10) {
            return "0" + Integer.toString(number) + bankAccount;
        }
        return Integer.toString(number) + bankAccount;
    }

    /**
     * Handles withdrawal operation.
     * @param user credentials for validation.
     * @param account account to withdraw from.
     * @param amount amount to withdraw.
     * @return modified Account object.
     * @throws ServiceException when operation is impossible. States reason in text.
     */
    public Account withdraw(User user, Account account, BigInteger amount) throws ServiceException {
        Account acc = dao.getAccount(account.getNumber());
        validateUserAccount(user, acc);
        validateAmount(amount);
        if(acc.getBalance().compareTo(amount) < 0) {
            throw new ServiceException("Not enough moneyss!!");
        }
        
        Transfer transfer = new Transfer();
        transfer.setAmount(amount);
        transfer.setSource_account(acc.getNumber());
        acc.setBalance(acc.getBalance().subtract(amount));
        acc = dao.saveAccount(acc);
        dao.saveHistory(transfer);
        return acc;
    }

    /**
     * Handles deposit operation.
     * @param user credentials for validation.
     * @param account account to deposit to.
     * @param amount amount to deposit.
     * @return modified Account object.
     * @throws ServiceException when operation is impossible. States reason in text.
     */
    public Account deposit(User user, Account account, BigInteger amount) throws ServiceException {
        Account acc = dao.getAccount(account.getNumber());
        validateUserAccount(user, acc);
        validateAmount(amount);
        Transfer transfer = new Transfer();
        transfer.setAmount(amount);
        transfer.setTarget_account(acc.getNumber());
        acc.setBalance(acc.getBalance().add(amount));
        acc = dao.saveAccount(acc);
        dao.saveHistory(transfer);
        return acc;
    }

    /**
     * Handles both internal and external transfers.
     * @param user credentials for validation.
     * @param account source account.
     * @param transfer object containing transfer information.
     * @return modified Account object.
     * @throws ServiceException when operation is impossible. States reason in text.
     */
    public Account transfer(User user, Account account, Transfer transfer) throws ServiceException {
        Account acc = dao.getAccount(account.getNumber());
        validateUserAccount(user, acc);
        validateAmount(transfer.getAmount());
        if(acc.getBalance().compareTo(transfer.getAmount()) < 0) {
            throw new ServiceException("Not enough funds!");
        }
        if(account.getNumber().equals(transfer.getTarget_account())) {
            throw new ServiceException("Cannot transfer to the same account!");
        }
        if(externalTransferService.isExternalAccount(transfer.getTarget_account())) {
            if(externalTransferService.sendExternalTransfer(transfer)) {
                acc.setBalance(acc.getBalance().subtract(transfer.getAmount()));
                acc = dao.saveAccount(acc);
                dao.saveHistory(transfer);
                return acc;
            }
        } else {
            Account acc2 = dao.getAccount(transfer.getTarget_account());
            if(acc2 != null) {
                acc.setBalance(acc.getBalance().subtract(transfer.getAmount()));
                acc2.setBalance(acc2.getBalance().add(transfer.getAmount()));
                acc = dao.saveAccount(acc);
                dao.saveAccount(acc2);
                dao.saveHistory(transfer);
                return acc;
            } else {
                throw new ServiceException("Target account doesn't exist!");
            }
        }

        return null;
    }


    private void validateAmount(BigInteger amount) throws ServiceException {
        if (amount.compareTo(BigInteger.valueOf(0)) <= 0) {
            throw new ServiceException("Amount must be positive!");
        }
    }

    private void validateUserAccount(User user, Account account) throws ServiceException {
        if(account == null) {
            throw new ServiceException("Account doesn't exist!");
        }
        if(!account.getUser().getLogin().equals(user.getLogin())) {
            throw new ServiceException("Account doesn't belong to this user!");
        }
    }
}
