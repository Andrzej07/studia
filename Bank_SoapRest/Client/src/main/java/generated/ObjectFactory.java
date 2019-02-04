
package generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Register_QNAME = new QName("http://soapservice/", "register");
    private final static QName _Withdraw_QNAME = new QName("http://soapservice/", "withdraw");
    private final static QName _WithdrawResponse_QNAME = new QName("http://soapservice/", "withdrawResponse");
    private final static QName _Deposit_QNAME = new QName("http://soapservice/", "deposit");
    private final static QName _RegisterResponse_QNAME = new QName("http://soapservice/", "registerResponse");
    private final static QName _MakeTransferResponse_QNAME = new QName("http://soapservice/", "makeTransferResponse");
    private final static QName _GetHistory_QNAME = new QName("http://soapservice/", "getHistory");
    private final static QName _Login_QNAME = new QName("http://soapservice/", "login");
    private final static QName _CreateAccount_QNAME = new QName("http://soapservice/", "createAccount");
    private final static QName _GetAccounts_QNAME = new QName("http://soapservice/", "getAccounts");
    private final static QName _MakeTransfer_QNAME = new QName("http://soapservice/", "makeTransfer");
    private final static QName _GetAccountsResponse_QNAME = new QName("http://soapservice/", "getAccountsResponse");
    private final static QName _LoginResponse_QNAME = new QName("http://soapservice/", "loginResponse");
    private final static QName _CreateAccountResponse_QNAME = new QName("http://soapservice/", "createAccountResponse");
    private final static QName _DepositResponse_QNAME = new QName("http://soapservice/", "depositResponse");
    private final static QName _GetHistoryResponse_QNAME = new QName("http://soapservice/", "getHistoryResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CreateAccountResponse }
     * 
     */
    public CreateAccountResponse createCreateAccountResponse() {
        return new CreateAccountResponse();
    }

    /**
     * Create an instance of {@link DepositResponse }
     * 
     */
    public DepositResponse createDepositResponse() {
        return new DepositResponse();
    }

    /**
     * Create an instance of {@link GetHistoryResponse }
     * 
     */
    public GetHistoryResponse createGetHistoryResponse() {
        return new GetHistoryResponse();
    }

    /**
     * Create an instance of {@link LoginResponse }
     * 
     */
    public LoginResponse createLoginResponse() {
        return new LoginResponse();
    }

    /**
     * Create an instance of {@link GetAccountsResponse }
     * 
     */
    public GetAccountsResponse createGetAccountsResponse() {
        return new GetAccountsResponse();
    }

    /**
     * Create an instance of {@link MakeTransfer }
     * 
     */
    public MakeTransfer createMakeTransfer() {
        return new MakeTransfer();
    }

    /**
     * Create an instance of {@link CreateAccount }
     * 
     */
    public CreateAccount createCreateAccount() {
        return new CreateAccount();
    }

    /**
     * Create an instance of {@link GetAccounts }
     * 
     */
    public GetAccounts createGetAccounts() {
        return new GetAccounts();
    }

    /**
     * Create an instance of {@link Login }
     * 
     */
    public Login createLogin() {
        return new Login();
    }

    /**
     * Create an instance of {@link GetHistory }
     * 
     */
    public GetHistory createGetHistory() {
        return new GetHistory();
    }

    /**
     * Create an instance of {@link MakeTransferResponse }
     * 
     */
    public MakeTransferResponse createMakeTransferResponse() {
        return new MakeTransferResponse();
    }

    /**
     * Create an instance of {@link RegisterResponse }
     * 
     */
    public RegisterResponse createRegisterResponse() {
        return new RegisterResponse();
    }

    /**
     * Create an instance of {@link Deposit }
     * 
     */
    public Deposit createDeposit() {
        return new Deposit();
    }

    /**
     * Create an instance of {@link WithdrawResponse }
     * 
     */
    public WithdrawResponse createWithdrawResponse() {
        return new WithdrawResponse();
    }

    /**
     * Create an instance of {@link Register }
     * 
     */
    public Register createRegister() {
        return new Register();
    }

    /**
     * Create an instance of {@link Withdraw }
     * 
     */
    public Withdraw createWithdraw() {
        return new Withdraw();
    }

    /**
     * Create an instance of {@link Transfer }
     * 
     */
    public Transfer createTransfer() {
        return new Transfer();
    }

    /**
     * Create an instance of {@link User }
     * 
     */
    public User createUser() {
        return new User();
    }

    /**
     * Create an instance of {@link Account }
     * 
     */
    public Account createAccount() {
        return new Account();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Register }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soapservice/", name = "register")
    public JAXBElement<Register> createRegister(Register value) {
        return new JAXBElement<Register>(_Register_QNAME, Register.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Withdraw }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soapservice/", name = "withdraw")
    public JAXBElement<Withdraw> createWithdraw(Withdraw value) {
        return new JAXBElement<Withdraw>(_Withdraw_QNAME, Withdraw.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WithdrawResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soapservice/", name = "withdrawResponse")
    public JAXBElement<WithdrawResponse> createWithdrawResponse(WithdrawResponse value) {
        return new JAXBElement<WithdrawResponse>(_WithdrawResponse_QNAME, WithdrawResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Deposit }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soapservice/", name = "deposit")
    public JAXBElement<Deposit> createDeposit(Deposit value) {
        return new JAXBElement<Deposit>(_Deposit_QNAME, Deposit.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soapservice/", name = "registerResponse")
    public JAXBElement<RegisterResponse> createRegisterResponse(RegisterResponse value) {
        return new JAXBElement<RegisterResponse>(_RegisterResponse_QNAME, RegisterResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MakeTransferResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soapservice/", name = "makeTransferResponse")
    public JAXBElement<MakeTransferResponse> createMakeTransferResponse(MakeTransferResponse value) {
        return new JAXBElement<MakeTransferResponse>(_MakeTransferResponse_QNAME, MakeTransferResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetHistory }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soapservice/", name = "getHistory")
    public JAXBElement<GetHistory> createGetHistory(GetHistory value) {
        return new JAXBElement<GetHistory>(_GetHistory_QNAME, GetHistory.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Login }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soapservice/", name = "login")
    public JAXBElement<Login> createLogin(Login value) {
        return new JAXBElement<Login>(_Login_QNAME, Login.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateAccount }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soapservice/", name = "createAccount")
    public JAXBElement<CreateAccount> createCreateAccount(CreateAccount value) {
        return new JAXBElement<CreateAccount>(_CreateAccount_QNAME, CreateAccount.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAccounts }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soapservice/", name = "getAccounts")
    public JAXBElement<GetAccounts> createGetAccounts(GetAccounts value) {
        return new JAXBElement<GetAccounts>(_GetAccounts_QNAME, GetAccounts.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MakeTransfer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soapservice/", name = "makeTransfer")
    public JAXBElement<MakeTransfer> createMakeTransfer(MakeTransfer value) {
        return new JAXBElement<MakeTransfer>(_MakeTransfer_QNAME, MakeTransfer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAccountsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soapservice/", name = "getAccountsResponse")
    public JAXBElement<GetAccountsResponse> createGetAccountsResponse(GetAccountsResponse value) {
        return new JAXBElement<GetAccountsResponse>(_GetAccountsResponse_QNAME, GetAccountsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LoginResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soapservice/", name = "loginResponse")
    public JAXBElement<LoginResponse> createLoginResponse(LoginResponse value) {
        return new JAXBElement<LoginResponse>(_LoginResponse_QNAME, LoginResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateAccountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soapservice/", name = "createAccountResponse")
    public JAXBElement<CreateAccountResponse> createCreateAccountResponse(CreateAccountResponse value) {
        return new JAXBElement<CreateAccountResponse>(_CreateAccountResponse_QNAME, CreateAccountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DepositResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soapservice/", name = "depositResponse")
    public JAXBElement<DepositResponse> createDepositResponse(DepositResponse value) {
        return new JAXBElement<DepositResponse>(_DepositResponse_QNAME, DepositResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetHistoryResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soapservice/", name = "getHistoryResponse")
    public JAXBElement<GetHistoryResponse> createGetHistoryResponse(GetHistoryResponse value) {
        return new JAXBElement<GetHistoryResponse>(_GetHistoryResponse_QNAME, GetHistoryResponse.class, null, value);
    }

}
