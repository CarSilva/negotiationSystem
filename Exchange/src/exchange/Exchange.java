package exchange;

import httpCommunication.DirectoryAccess;
import httpCommunication.Json;

import java.io.IOException;
import java.util.*;

public class Exchange {

    private Map<String, Share> shares;
    DirectoryAccess http;
    Json json;

    public Exchange(int exchangeId) {
        this.shares = new HashMap<>();
        http = new DirectoryAccess();
        json = new Json();
        fillShares("exchange/"+exchangeId);
    }

    public Map<String, Share> getShares() {
        return this.shares;
    }

    public boolean buy_request(String share_name, int quantity,
                                            float price, String client) {
        boolean existsShare = false;
        Share share;
        synchronized (shares) {
            share = shares.get(share_name);
            if (share != null)
                existsShare = true;
        }
        share.add_buy_request(quantity, price, client);
        return existsShare;
    }

    public boolean sell_request(String share_name, int quantity,
                                             float price, String client) {
        boolean existsShare = false;
        Share share;
        synchronized (shares) {
            share = shares.get(share_name);
            if (share != null)
                existsShare = true;
        }
        share.add_sell_request(quantity, price, client);
        return existsShare;
    }

    public void fillShares(String query){
        String reply = "";
        try {
            reply = http.sendGetRequest(query);
            List<String> shareNames = json.parseArray(reply);
            for(String s : shareNames) {
                Share share = new Share(s);
                shares.put(s, share);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
