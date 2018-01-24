package Controling_time;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import exchange.Exchange;
import exchange.Share;
import httpCommunication.DirectoryAccess;
import httpCommunication.Json;

import java.io.IOException;

public class ExchangeClose implements Runnable {
    private ExchangeOpen open;
    private Exchange exchange;
    private DirectoryAccess http;
    private Json json;

    public ExchangeClose(ExchangeOpen open, Exchange exchange){
        this.open = open;
        this.exchange = exchange;
        this.http = new DirectoryAccess();
        this.json = new Json();
    }


    @Override
    public void run() {
        try {
            open.getSrv().close();
            open.stop();
        } catch (IOException e) { e.printStackTrace(); }

        for(Share s : exchange.getShares().values()){
            String[] args = {s.getCompany_name(),
                            String.valueOf(s.getOpeningValue()),
                            String.valueOf(s.getClosingValue()),
                            String.valueOf(s.getMinimumValue()),
                            String.valueOf(s.getMaximumValue())};
            String send = json.createJsonTotal(args);
            String request = "company/"+s.getCompany_name();
            try {
                http.sendPutRequest(request, send);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
