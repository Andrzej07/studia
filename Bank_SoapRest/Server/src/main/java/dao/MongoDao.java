package dao;

import com.mongodb.MongoClient;
import model.Account;
import model.Sequence;
import model.Transfer;
import model.User;
import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.Query;

import java.util.List;

/**
 * Created by macie on 17/01/2018.
 */
public class MongoDao implements Dao {
    private final static Morphia morphia = new Morphia();
    private static Datastore datastore;

    static {
        morphia.mapPackage("model");
        datastore = morphia.createDatastore(new MongoClient("localhost", 27017), "bank");
        datastore.ensureIndexes();
    }

    @Override
    public boolean addUser(User user) {
        User existingUser = getUser(user.getLogin());
        if(existingUser != null) {
            return false;
        } else {
            datastore.save(user);
            return true;
        }
    }

    @Override
    public User getUser(String login) {
        return datastore.find(User.class, "login", login).get();
    }

    @Override
    public Account getAccount(String number) {
        return datastore.find(Account.class, "number", number).get();
    }

    @Override
    public List<Account> getAccounts(User user) {
        return datastore.createQuery(Account.class).field("user").equal(user).asList();
    }

    @Override
    public Account saveAccount(Account account) {
        datastore.save(account);
        return account;
    }

    @Override
    public void saveHistory(Transfer transfer) {
        datastore.save(transfer);
    }

    @Override
    public List<Transfer> getHistory(Account account) {
        List<Transfer> transfers1 = datastore.createQuery(Transfer.class).field("source_account").equal(account.getNumber()).asList();
        List<Transfer> transfers2 = datastore.createQuery(Transfer.class).field("target_account").equal(account.getNumber()).asList();
        transfers1.addAll(transfers2);
        return transfers1;
    }

    @Override
    public String getNextAccountNumber() {
        Sequence sequence = datastore.find(Sequence.class, "name", "accountNumber").get();
        if(sequence == null) {
            sequence = new Sequence();
            sequence.setName("accountNumber");
        }
        String result = StringUtils.leftPad(Integer.toString(sequence.getNext()), 16, '0');
        datastore.save(sequence);
        return result;
    }
}
