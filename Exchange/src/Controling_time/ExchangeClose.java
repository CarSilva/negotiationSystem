package Controling_time;

import exchange.Exchange;
import exchange.Share;
import httpCommunication.DirectoryAccess;
import httpCommunication.Json;

import java.io.IOException;

public class ExchangeClose implements Runnable {
    private boolean working;
    private Exchange exchange;
    private DirectoryAccess http;
    private Json json;

    public ExchangeClose(boolean working, Exchange exchange){
        this.working = working;
        this.exchange = exchange;
        this.http = new DirectoryAccess();
        this.json = new Json();
    }


    @Override
    public void run() {
        this.working = false;
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
