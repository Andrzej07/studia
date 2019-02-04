import generated.Account;
import generated.Transfer;
import swingstuff.AccountsListItem;
import swingstuff.OtherTableModel;
import swingstuff.TransferTableModel;
import util.AmountFormatter;

import javax.print.attribute.standard.MediaSize;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import javax.xml.ws.soap.SOAPFaultException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by macie on 16/01/2018.
 */
public class MainForm {

    public MainForm() {
        setupAmountInput(withdrawAmountInput);
        setupAmountInput(depositAmountInput);
        setupAmountInput(transferAmountInput);
        setupTransfersHistoryTable();
        setupOthersHistoryTable();
        Locale.setDefault(Locale.US);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginErrorLabel.setText("");
                String login = usernameField.getText();
                String password = passwordField.getText();
                if(!login.isEmpty() && !password.isEmpty()) {
                    if(!SoapWrapper.login(login, password)) {
                        loginErrorLabel.setText("Login attempt unsuccessful.");
                    } else {
                        // next screen..
                        populateAccountsCombo(false);
                        moveToPage("Card2");
                    }
                } else {
                    loginErrorLabel.setText("Form is incomplete.");
                }
            }
        });
        accountsComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAccountBalanceLabel();
            }
        });
        goWithdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (accountsComboBox.getSelectedItem() != null) {
                    withdrawStatusLabel.setText("");
                    moveToPage("withdrawCard");
                }
            }
        });
        goDepositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (accountsComboBox.getSelectedItem() != null) {
                    depositStatusLabel.setText("");
                    moveToPage("depositCard");
                }
            }
        });
        goTransferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (accountsComboBox.getSelectedItem() != null) {
                    transferStatusLabel.setText("");
                    moveToPage("transferCard");
                }
            }
        });
        goHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (accountsComboBox.getSelectedItem() != null) {
                    List<Transfer> transferList = SoapWrapper.getHistory(getSelectedAccount());
                    List<Transfer> transfers = new LinkedList<>();
                    List<Transfer> others = new LinkedList<>();
                    if(transferList != null) {
                        for(Transfer transfer : transferList) {
                            if(transfer.getSourceAccount() != null && !transfer.getSourceAccount().isEmpty()
                                    && transfer.getTargetAccount() != null && !transfer.getTargetAccount().isEmpty()) {
                                transfers.add(transfer);
                            } else {
                                others.add(transfer);
                            }
                        }
                        TransferTableModel transferTableModel = (TransferTableModel) historyTransfersTable.getModel();
                        OtherTableModel otherTableModel = (OtherTableModel) historyOthersTable.getModel();
                        transferTableModel.updateData(transfers);
                        otherTableModel.updateData(others);
                        historyTransfersTable.invalidate();
                        historyOthersTable.invalidate();
                    }
                    moveToPage("historyCard");
                }
            }
        });
        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Account account = getSelectedAccount();
                BigInteger amount = getAmountFromField(withdrawAmountInput);
                withdrawStatusLabel.setText("");
                if(amount != null) {
                    try {
                        account = SoapWrapper.withdraw(account, amount);
                        if (account != null) {
                            withdrawStatusLabel.setText("Success! Current balance: " + AmountFormatter.formatValue(account.getBalance()));
                            updateSelectedAccount(account);
                        }
                    } catch (SOAPFaultException exc) {
                        withdrawStatusLabel.setText(getFaultMessage(exc));
                    }
                }
            }
        });
        ActionListener returnListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToAccountScreen();
                populateAccountsCombo(true);
            }
        };
        withdrawReturnButton.addActionListener(returnListener);
        depositReturnButton.addActionListener(returnListener);
        transferReturnButton.addActionListener(returnListener);
        JButton returnButton1 = new JButton("Back");
        returnButton1.addActionListener(returnListener);
        historyTransfersPanel.add(returnButton1, BorderLayout.SOUTH);
        JButton returnButton2 = new JButton("Back");
        returnButton2.addActionListener(returnListener);
        historyOthersPanel.add(returnButton2, BorderLayout.SOUTH);
        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Account account = getSelectedAccount();
                BigInteger amount = getAmountFromField(depositAmountInput);
                depositStatusLabel.setText("");
                if(amount != null) {
                    try {
                        account = SoapWrapper.deposit(account, amount);
                        if (account != null) {
                            depositStatusLabel.setText("Success! Current balance: " + AmountFormatter.formatValue(account.getBalance()));
                            updateSelectedAccount(account);
                        }
                    } catch (SOAPFaultException exc) {
                        depositStatusLabel.setText(getFaultMessage(exc));
                    }
                }
            }
        });
        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transferStatusLabel.setText("");
                Account account = getSelectedAccount();
                String targetAccount = transferTargetAccountInput.getText();
                if(targetAccount == null || targetAccount.length() != 26) {
                    transferStatusLabel.setText("Invalid target account number");
                    return;
                }
                String title = transferTitleInput.getText();
                if(title == null || title.length() < 1) {
                    transferStatusLabel.setText("Invalid title");
                    return;
                }
                String name = transferNameInput.getText();
                if(name == null || name.length() < 1) {
                    transferStatusLabel.setText("Invalid name");
                    return;
                }
                BigInteger amount = getAmountFromField(transferAmountInput);
                if(amount != null) {
                    try {
                        account = SoapWrapper.transfer(account, targetAccount, amount, title, name);
                        if (account != null) {
                            populateAccountsCombo(true);
                            transferStatusLabel.setText("Success! Current balance: " + AmountFormatter.formatValue(account.getBalance()));
                        } else {
                            transferStatusLabel.setText("Operation failed");
                        }
                    } catch (SOAPFaultException exc) {
                        transferStatusLabel.setText(getFaultMessage(exc));
                    }
                } else {
                    transferStatusLabel.setText("Invalid amount");
                }
            }
        });
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Account newAccount = SoapWrapper.createAccount();
                if(newAccount != null) {
                    accountsComboBox.addItem(new AccountsListItem(newAccount));
                }
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginErrorLabel.setText("");
                String login = usernameField.getText();
                String password = passwordField.getText();
                if(!login.isEmpty() && !password.isEmpty()) {
                    if(!SoapWrapper.register(login, password)) {
                        loginErrorLabel.setText("Registration attempt unsuccessful.");
                    } else {
                        // next screen..
                        populateAccountsCombo(false);
                        moveToPage("Card2");
                    }
                } else {
                    loginErrorLabel.setText("Form is incomplete.");
                }
            }
        });
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoapWrapper.logout();
                moveToPage("Card1");
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainForm");
        frame.setContentPane(new MainForm().outerPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void returnToAccountScreen() {
        moveToPage("Card2");
    }

    private void moveToPage(String cardName) {
        ((CardLayout)outerPanel.getLayout()).show(outerPanel, cardName);
    }

    private void populateAccountsCombo(boolean rememberSelection) {
        Account selectedAccount = getSelectedAccount();
        accountsComboBox.removeAllItems();
        List<Account> accounts = SoapWrapper.getAccounts();
        AccountsListItem toSelect = null;
        if(accounts != null) {
            for (Account account : accounts) {
                AccountsListItem accountsListItem = new AccountsListItem(account);
                if(selectedAccount != null && selectedAccount.getNumber().equals(account.getNumber())) {
                    toSelect = accountsListItem;
                }
                accountsComboBox.addItem(accountsListItem);
            }
        }
        if(rememberSelection && toSelect != null) {
            accountsComboBox.setSelectedItem(toSelect);
        }
        updateAccountBalanceLabel();
    }

    private Account getSelectedAccount() {
        AccountsListItem accountsListItem = (AccountsListItem) accountsComboBox.getSelectedItem();
        if(accountsListItem != null) {
            return accountsListItem.getAccount();
        }
        return null;
    }

    private void updateSelectedAccount(Account account) {
        ((AccountsListItem) accountsComboBox.getSelectedItem()).setAccount(account);
        updateAccountBalanceLabel();
    }

    private void updateAccountBalanceLabel() {
        AccountsListItem item = (AccountsListItem) accountsComboBox.getSelectedItem();
        if(item != null) {
            accountBalanceLabel.setText(AmountFormatter.formatValue(item.getAccount().getBalance()));
        } else {
            accountBalanceLabel.setText("");
        }
    }

    private void setupAmountInput(JFormattedTextField field) {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);

        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setOverwriteMode(false);
        formatter.setCommitsOnValidEdit(true);
        formatter.setAllowsInvalid(true);

        //formatter.setMinimum(0);
        //formatter.setMaximum(10000000.0);
        //formatter.setAllowsInvalid(false);
        //formatter.setOverwriteMode(true);

        DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);
        field.setFormatterFactory(factory);
        field.setValue(50.0);
    }

    private BigInteger getAmountFromField(JFormattedTextField textField) {
        if(!textField.isValid()) {
            return null;
        }
        String text = textField.getText();
        if(text != null) {
            try {
                BigDecimal bigDecimal = new BigDecimal(text.substring(0)).multiply(BigDecimal.valueOf(100));
                return bigDecimal.toBigInteger();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private void setupTransfersHistoryTable() {
        historyTransfersPanel = new JPanel(new BorderLayout());
        historyTransfersTable = new JTable(new TransferTableModel());
        historyTransfersPanel.add(new JScrollPane(historyTransfersTable), BorderLayout.CENTER);
        transfersPane.addTab("Transfers", historyTransfersPanel);
    }

    private void setupOthersHistoryTable() {
        historyOthersPanel = new JPanel(new BorderLayout());
        historyOthersTable = new JTable(new OtherTableModel());
        historyOthersPanel.add(new JScrollPane(historyOthersTable), BorderLayout.CENTER);
        transfersPane.addTab("Others", historyOthersPanel);
    }

    private String getFaultMessage(SOAPFaultException exception) {
        return exception.getFault().getFaultString();
    }

    private JPanel outerPanel;
    private JPanel loginPanel;
    private JTextField usernameField;
    private JButton loginButton;
    private JPasswordField passwordField;
    private JLabel loginErrorLabel;
    private JPanel accountSelectionPanel;
    private JButton createAccountButton;
    private JComboBox accountsComboBox;
    private JButton goWithdrawButton;
    private JButton goDepositButton;
    private JButton goTransferButton;
    private JButton goHistoryButton;
    private JLabel accountBalanceLabel;
    private JPanel historyPanel;
    private JTabbedPane transfersPane;
    private JPanel withdrawPanel;
    private JFormattedTextField withdrawAmountInput;
    private JButton withdrawButton;
    private JButton withdrawReturnButton;
    private JLabel withdrawStatusLabel;
    private JPanel depositPanel;
    private JFormattedTextField depositAmountInput;
    private JButton depositButton;
    private JButton depositReturnButton;
    private JLabel depositStatusLabel;
    private JPanel transferPanel;
    private JTextField transferTitleInput;
    private JTextField transferNameInput;
    private JTextField transferTargetAccountInput;
    private JFormattedTextField transferAmountInput;
    private JButton transferButton;
    private JButton transferReturnButton;
    private JLabel transferStatusLabel;
    private JButton registerButton;
    private JButton logoutButton;
    private JPanel historyTransfersPanel;
    private JTable historyTransfersTable;
    private JPanel historyOthersPanel;
    private JTable historyOthersTable;
}
