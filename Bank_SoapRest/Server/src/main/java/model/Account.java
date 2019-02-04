package model;


import mongoconverter.BigIntegerConverter;
import org.mongodb.morphia.annotations.Converters;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.math.BigInteger;
import java.util.List;

@Entity
@Converters(BigIntegerConverter.class)
public class Account {
    @Reference
    private User user;
    @Id
    private String number;
    private BigInteger balance;

    public Account() {}

    public Account(User user, String number, BigInteger balance) {
        this.user = user;
        this.number = number;
        this.balance = balance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public BigInteger getBalance() {
        return balance;
    }

    public void setBalance(BigInteger balance) {
        this.balance = balance;
    }
}
