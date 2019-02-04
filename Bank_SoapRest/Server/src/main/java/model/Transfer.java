package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import javax.xml.bind.annotation.XmlTransient;
import java.math.BigInteger;

/**
 * Created by macie on 11/01/2018.
 */
@Entity
public class Transfer {
    @Id
    @XmlTransient
    public ObjectId _id;
    private BigInteger amount;
    private String source_account;
    private String target_account;
    private String title; // 1 - 255 znakow
    private String name; // 1 - 255 znakow

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }

    public String getSource_account() {
        return source_account;
    }

    public void setSource_account(String source_account) {
        this.source_account = source_account;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTarget_account() {
        return target_account;
    }

    public void setTarget_account(String target_account) {
        this.target_account = target_account;
    }

}
