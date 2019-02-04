package service;

import dao.Dao;
import dao.MongoDao;
import model.Account;
import model.Transfer;
import resource.RestErrorFormat;
import soapservice.ServiceException;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Class managing transfers between different banks.
 */
public class ExternalTransferService {
    private Dao dao = new MongoDao();
    private static Map<String, String> banks = new HashMap<>();

    /**
     * Loads external banks list from csv file.
     */
    public ExternalTransferService() {
        if(banks.isEmpty()) {
            File file = new File(getClass().getClassLoader().getResource("banks.csv").getFile());
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String[] splits = scanner.nextLine().split(",");
                    banks.put(splits[0].trim(), splits[1].trim());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Receives transfer fron external bank.
     * @param targetAccount target account number.
     * @param transfer transfer information.
     * @return response containing HTTP status and body in case of error.
     */
    public Response executeExternalTransfer(String targetAccount, Transfer transfer) {
        Account account = dao.getAccount(targetAccount);
        if(account == null) {
            return Response.status(404).build();
        }
        if(transfer.getAmount() == null || transfer.getAmount().compareTo(BigInteger.ZERO) < 0) {
            return Response.status(400).entity(new RestErrorFormat("amount", "doesn't exist or is negative")).build();
        }
        if(transfer.getTitle() == null || transfer.getTitle().length() < 1 || transfer.getTitle().length() > 255) {
            return Response.status(400).entity(new RestErrorFormat("title", "must be between 1 and 255 characters")).build();
        }
        if(transfer.getName() == null || transfer.getName().length() < 1 || transfer.getName().length() > 255) {
            return Response.status(400).entity(new RestErrorFormat("name", "must be between 1 and 255 characters")).build();
        }
        if(transfer.getSource_account() == null || transfer.getSource_account().length() != 26) {
            return Response.status(400).entity(new RestErrorFormat("source_account", "must have exactly 26 characters")).build();
        }
        account.setBalance(account.getBalance().add(transfer.getAmount()));
        dao.saveAccount(account);
        transfer.setTarget_account(targetAccount);
        dao.saveHistory(transfer);
        return Response.status(201).build();
    }

    /**
     * Sends request to external bank.
     * @param transfer transfer data.
     * @return true if operation was successful.
     * @throws ServiceException if external bank returned error code.
     */
    public boolean sendExternalTransfer(Transfer transfer) throws ServiceException {
        String bankId = transfer.getTarget_account().substring(2, 10);
        Client client = ClientBuilder.newClient();
        String bankUrl = banks.get(bankId);
        if(bankUrl == null) {
            return false;
        }
        WebTarget webTarget = client.target(bankUrl).path("accounts/"+transfer.getTarget_account()+"/history");
        Response response = webTarget.request(MediaType.APPLICATION_JSON)
            .header("Authorization", "Basic YWRtaW46YWRtaW4=")
            .post(Entity.entity(transfer, MediaType.APPLICATION_JSON));
        System.out.println(response.getStatus());
        if(response.getStatus() == 201) {
            return true;
        } else if (response.getStatus() == 401) {
            throw new ServiceException("External authorization error");
        } else if(response.getStatus() == 404) {
            throw new ServiceException("External account not found");
        } else if(response.getStatus() == 400) {
            RestErrorFormat restErrorFormat = response.readEntity(RestErrorFormat.class);
            throw new ServiceException(restErrorFormat.getError_field() + " " + restErrorFormat.getError());
        }
        return false;
    }

    /**
     * Checks if given account number belongs to an external bank.
     * @param accountNumber number to check.
     * @return true if number doesn't belong to this bank.
     */
    public boolean isExternalAccount(String accountNumber) {
        if(accountNumber == null) {
            return false;
        }
        return !"00117256".equals(accountNumber.substring(2, 10));
    }

}
