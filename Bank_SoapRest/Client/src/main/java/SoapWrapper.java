import generated.*;

import java.math.BigInteger;
import java.util.List;



public class SoapWrapper {

    private static User logged_user;
    private static SoapService soapService;

    static {
        soapService = (new SoapService_Service()).getSoapServicePort();
    }

    public static boolean isLogged() {
        return logged_user != null;
    }

    public static void logout() {
        logged_user = null;
    }

    public static boolean login(String login, String password) {
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        boolean success = soapService.login(user);
        if (success) {
            logged_user = user;
        }
        return success;
    }

    public static boolean register(String login, String password) {
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        boolean success = soapService.register(user);
        if (success) {
            logged_user = user;
        }
        return success;
    }

    public static Account createAccount() {
        if(!isLogged()) {
            return null;
        }
        return soapService.createAccount(logged_user);
    }

    public static List<Account> getAccounts() {
        if(!isLogged()) {
            return null;
        }
        return soapService.getAccounts(logged_user);
    }

    public static Account withdraw(Account account, BigInteger amount) {
        if(!isLogged()) {
            return null;
        }
        return soapService.withdraw(logged_user, account, amount);
    }

    public static Account deposit(Account account, BigInteger amount) {
        if(!isLogged()) {
            return null;
        }
        return soapService.deposit(logged_user, account, amount);
    }

    public static Account transfer(Account account, String targetAccount, BigInteger amount, String title, String name) {
        if(!isLogged()) {
            return null;
        }
        return soapService.makeTransfer(logged_user, account, targetAccount, amount, title, name);
    }

    public static List<Transfer> getHistory(Account account) {
        if(!isLogged()) {
            return null;
        }
        return soapService.getHistory(logged_user, account);
    }
}
