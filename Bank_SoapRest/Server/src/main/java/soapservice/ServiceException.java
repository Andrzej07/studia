package soapservice;

import javax.xml.ws.WebFault;

/**
 * Custom exception for Web Services fault handling.
 * Created by macie on 19/01/2018.
 */
@WebFault
public class ServiceException extends Exception {
    public ServiceException(String msg) {
        super(msg);
    }
}
