package resource;

import model.Account;
import model.Transfer;
import service.AccountsService;
import service.ExternalTransferService;
import soapservice.ServiceException;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.util.List;

/**
 * Handles REST requests
 */
@Path("accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(value = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class AccountsEndpoint {
    private AccountsService accountsService = new AccountsService();
    private ExternalTransferService externalTransferService = new ExternalTransferService();

    /**
     * Receives transfer from another bank
     * @param number target account number
     * @param transfer transfer data
     * @return
     */
    @POST
    @Path("/{number}/history")
    public Object receiveExternalTransfer(@PathParam("number") String number, Transfer transfer){
        if(transfer != null) {
            return externalTransferService.executeExternalTransfer(number, transfer);
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("sendexternal")
    public Object spamujkrzycha() throws ServiceException {
        Transfer transfer = new Transfer();
        transfer.setTarget_account("45001172831310246548841100");
        transfer.setSource_account("");
        transfer.setAmount(new BigInteger("66"));
        transfer.setName("Name");
        transfer.setTitle("title");
        for(int i = 0; i < 1; i++) {
            externalTransferService.sendExternalTransfer(transfer);
        }
        return 0;
    }
}
