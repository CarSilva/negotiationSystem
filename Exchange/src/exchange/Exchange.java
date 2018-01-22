package exchange;

import httpDirectory.DirectoryAccess;
import httpDirectory.Json;
import org.zeromq.ZMQ;

import java.util.*;

public class Exchange {

    private Map<String, Share> shares;
    DirectoryAccess http;
    Json json;

    public Exchange(int exchangeId){
        this.shares = new HashMap<>();
        Share s = new Share("iota");
        shares.put("iota", s);
        /*json = new Json();
        http = new DirectoryAccess();
        String query = "companies";
        fillShares(query);*/
    }


    public synchronized boolean buy_request(String share_name, int quantity,
                                            float price, String client, ZMQ.Socket pub) {
        boolean existsShare = false;
        Share share = shares.get(share_name);
        if(share != null)
            existsShare = true;
        share.add_buy_request(quantity, price, pub, client);
        return existsShare;
    }

    public synchronized boolean sell_request(String share_name, int quantity,
                                             float price, String client, ZMQ.Socket pub) {
        boolean existsShare = false;
        Share share = shares.get(share_name);
        if(share != null)
            existsShare = true;
        share.add_sell_request(quantity, price, pub, client);
        return existsShare;
    }
/*
    public void fillShares(String query){
        String reply = "";
        try {
            reply = http.sendRequest(query,"GET");
            JSONArray array = json.parseArray(reply);
            for(int i = 0; i < array.size(); i++){
                JSONObject e = (JSONObject) array.get(i);
                String name = (String) e.get("name");
                double opValue = (double) e.get("openingValue");
                double clValue = (double) e.get("closingValue");
                double minValue = (double) e.get("minimumValue");
                double maxValue = (double) e.get("maximumValue");
                Share share = new Share(name, opValue,
                                        clValue, minValue,
                                        maxValue);
                shares.put(name, share);
            }
        } catch (IOException|ParseException e) {
            e.printStackTrace();
        }
    }
*/
}
