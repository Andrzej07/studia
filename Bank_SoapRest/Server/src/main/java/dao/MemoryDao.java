package dao;

import model.Account;
import model.Transfer;
import model.User;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by macie on 14/12/2017.
 */
public class MemoryDao implements Dao {
    private static List<Account> accountList = new LinkedList<>();
    private static List<Transfer> transferList = new LinkedList<>();
    private static List<User> userList = new LinkedList<>();

    private static int accountSequence;

    public MemoryDao() {
    }

    @Override
    public boolean addUser(User user) {
        if(userList.stream().anyMatch(usr -> usr.getLogin().equals(user.getLogin()))) {
            return false;
        } else {
            userList.add(user);
            return true;
        }
    }

    @Override
    public User getUser(String login) {
        for(User user : userList) {
            if(user.getLogin().equals(login)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public Account getAccount(String number) {
        for(Account account : accountList) {
            if(account.getNumber().equals(number)) {
                return account;
            }
        }
        return null;
    }

    @Override
    public List<Account> getAccounts(User user) {
        return accountList.stream().filter(acc -> acc.getUser().getLogin().equals(user.getLogin())).collect(Collectors.toList());
    }

    @Override
    public Account saveAccount(Account account) {
        List<Account> accounts = accountList.stream().filter(acc -> acc.getNumber().equals(account.getNumber())).collect(Collectors.toList());
        if(accounts.size() == 0) {
            accountList.add(account);
            return account;
        } else {
            Account existing = accounts.get(0);
            existing.setBalance(account.getBalance());
            return existing;
        }
    }

    @Override
    public void saveHistory(Transfer transfer) {
        transferList.add(transfer);
    }

    @Override
    public List<Transfer> getHistory(Account account) {
        return transferList.stream().filter(trans ->
                (trans.getSource_account() != null && trans.getSource_account().equals(account.getNumber()))
                        || (trans.getTarget_account() != null && trans.getTarget_account().equals(account.getNumber()))).collect(Collectors.toList());
    }

    @Override
    public String getNextAccountNumber() {
        return StringUtils.leftPad(Integer.toString(accountSequence++), 16, '0');
    }
}
