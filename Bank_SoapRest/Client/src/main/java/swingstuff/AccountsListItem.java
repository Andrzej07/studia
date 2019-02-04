package swingstuff;

import generated.Account;


public class AccountsListItem {
    private Account account;

    public AccountsListItem(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return account.getNumber();
    }
}
