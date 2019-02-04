package soapservice;

import dao.Dao;
import dao.MongoDao;
import model.Account;
import model.Transfer;
import model.User;
import service.AccountsService;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import java.math.BigInteger;
import java.util.List;

/**
 * Web Service for SOAP communication. Is responsible for credentials validation and creation. Delegates other tasks to AccountsService.
 */
@WebService(serviceName = "SoapService")
public class SoapService
{
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SoapService.class);
    private Dao dao = new MongoDao();
    private AccountsService accountsService = new AccountsService();

/*    @Resource(name="wsContext")
    WebServiceContext wsContext;*/

    @WebMethod(operationName = "login")
    public boolean login(@WebParam(name = "credentials") User user)
    {
        return validateUser(user);
    }

    @WebMethod(operationName = "register")
    public boolean register(@WebParam(name = "credentials") User user)
    {
        return dao.addUser(user);
    }

    @WebMethod(operationName = "getHistory")
    public List<Transfer> getHistory(@WebParam(name = "credentials") User user, @WebParam(name = "account") Account account)
    {
        if(!validateUser(user)) {
            throw new WebApplicationException(401);
        }
        return dao.getHistory(account);
    }

    @WebMethod(operationName = "getAccounts")
    public List<Account> getAccounts(@WebParam(name = "credentials") User user)
    {
        if(!validateUser(user)) {
            throw new WebApplicationException(401);
        }
        return dao.getAccounts(user);
    }

    @WebMethod(operationName = "createAccount")
    public Account createAccount(@WebParam(name = "credentials") User user)
    {
        if(!validateUser(user)) {
            throw new WebApplicationException(401);
        }
        Account account = new Account();
        account.setUser(user);
        account.setNumber(accountsService.getNextAccountNumber());
        account.setBalance(BigInteger.ZERO);
        account = dao.saveAccount(account);
        return account;
    }

    @WebMethod(operationName = "withdraw")
    public Account withdraw(@WebParam(name = "credentials") User user, @WebParam(name = "account") Account account, @WebParam(name = "amount") BigInteger amount) throws ServiceException {
        if(!validateUser(user)) {
            throw new WebApplicationException(401);
        }
        return accountsService.withdraw(user, account, amount);
    }

    @WebMethod(operationName = "deposit")
    public Account deposit(@WebParam(name = "credentials") User user, @WebParam(name = "account") Account account, @WebParam(name = "amount") BigInteger amount) throws ServiceException {
        if(!validateUser(user)) {
            throw new WebApplicationException(401);
        }
        return accountsService.deposit(user, account, amount);
    }

    @WebMethod(operationName = "makeTransfer")
    public Account transfer(@WebParam(name = "credentials") User user, @WebParam(name = "account") Account account, @WebParam(name="targetAccount") String targetAccount, @WebParam(name = "amount") BigInteger amount, @WebParam(name = "title") String title, @WebParam(name = "name") String name) throws ServiceException {
        if(!validateUser(user)) {
            throw new WebApplicationException(401);
        }
        Transfer transfer = new Transfer();
        transfer.setAmount(amount);
        transfer.setSource_account(account.getNumber());
        transfer.setTitle(title);
        transfer.setTarget_account(targetAccount);
        transfer.setName(name);
        return accountsService.transfer(user, account, transfer);
    }


    private boolean validateUser(User user) {
        if (user.getLogin() == null) {
            return false;
        }
        User dbUser = dao.getUser(user.getLogin());
        return dbUser != null && dbUser.getPassword().equals(user.getPassword());
    }


}