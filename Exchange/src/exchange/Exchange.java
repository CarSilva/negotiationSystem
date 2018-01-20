package exchange;

import Interface.Request;
import httpDirectory.DirectoryAccess;
import httpDirectory.Json;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.*;

public class Exchange {

    private Map<String, Share> shares;
    DirectoryAccess http;
    Json json;

    public Exchange(int exchangeId){
        this.shares = new HashMap<>();
        Share s = new Share("iota", 2.4,0.1,0.2,2.4);
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


    class Share {

        String company_name;
        double openingValue;
        double closingValue;
        double minimumValue;
        double maximumValue;
        Queue<RequestBuy> buy_requests;
        Queue<RequestSell> sell_requests;

        Share(String company_name, double opValue, double clValue,
              double minValue, double maxValue){
            this.company_name = company_name;
            this.openingValue = opValue;
            this.closingValue = clValue;
            this.minimumValue = minValue;
            this.maximumValue = maxValue;
            Comparator<Request> cmp = new OrderQueue();
            this.buy_requests = new PriorityQueue<>(11, cmp);
            this.sell_requests = new PriorityQueue<>(11, cmp);
        }

        public class OrderQueue implements Comparator<Request>{
            @Override
            public int compare(Request req, Request t1) {
                if(req.getTimestamp() > t1.getTimestamp())
                    return 1;
                if(req.getTimestamp() < t1.getTimestamp())
                    return -1;
                return 0;
            }
        }

        class RequestBuy implements Request {
            int quantity;
            float price;
            long timestamp;
            ZMQ.Socket pub;
            String client;

            RequestBuy(int quantity, float price, ZMQ.Socket pub,
                       String client, long timestamp) {
                this.quantity = quantity;
                this.price = price;
                this.pub = pub;
                this.client = client;
                this.timestamp = timestamp;
            }

            RequestBuy(int quantity, float price) {
                this.quantity = quantity;
                this.price = price;
            }

            public long getTimestamp() {
                return timestamp;
            }
        }

        class RequestSell implements Request{
            int quantity;
            float price;
            long timestamp;
            ZMQ.Socket pub;
            String client;

            RequestSell(int quantity, float price) {
                this.quantity = quantity;
                this.price = price;
            }

            RequestSell(int quantity, float price, ZMQ.Socket pub,
                        String client, long timestamp) {
                this.quantity = quantity;
                this.price = price;
                this.pub = pub;
                this.client = client;
                this.timestamp = timestamp;
            }

            @Override
            public long getTimestamp() {
                return this.timestamp;
            }
        }

       synchronized void add_buy_request(int quantity, float price,
                                         ZMQ.Socket pub, String client) {

            RequestBuy buy = new RequestBuy(quantity, price, pub,
                                            client, System.currentTimeMillis());

            if (sell_requests.isEmpty())
                buy_requests.add(buy);

            else {
                trade(buy);
            }
        }

        synchronized void add_sell_request(int quantity, float price,
                                           ZMQ.Socket pub, String client) {

            RequestSell sell = new RequestSell(quantity, price, pub,
                                                client, System.currentTimeMillis());

            if (buy_requests.isEmpty())
                sell_requests.add(sell);

            else {
                trade(sell);
            }
        }

       synchronized void trade(RequestBuy reqBuy) {

            for(RequestSell sell : sell_requests){
                if(sell.price <= reqBuy.price) {
                    float tradePrice = (sell.price + reqBuy.price) / 2;
                    if (sell.quantity < reqBuy.quantity) {
                        int remaining = reqBuy.quantity - sell.quantity;
                        reqBuy.quantity = remaining;
                        sell.pub.send(sell.client+" "+company_name+" Sold "
                                +" "+tradePrice +" "+ sell.quantity);
                        //Say nothing to the buyer --> informs seller
                    }else if(sell.quantity > reqBuy.quantity){
                        int remaining = sell.quantity - reqBuy.quantity;
                        sell.quantity = remaining;
                        reqBuy.pub.send(reqBuy.client+" "+company_name+" Bought "
                                +" "+tradePrice +" "+ reqBuy.quantity);
                        break;
                        //Say nothing to the seller --> informs buyer
                    }else {
                        sell_requests.remove(sell);
                        buy_requests.remove(reqBuy);
                        reqBuy.pub.send(reqBuy.client+" "+company_name+" Bought "
                                +" "+tradePrice +" "+ reqBuy.quantity);
                        sell.pub.send(sell.client+" "+company_name+" Sold "
                                +" "+tradePrice +" "+ sell.quantity);
                        //Informs seller and buyer
                        break;
                    }
                }
            }
        }


        synchronized void trade(RequestSell reqSell) {
            for(RequestBuy buy : buy_requests){
                if(buy.price >= reqSell.price) {
                    float tradePrice = (buy.price + reqSell.price) / 2;
                    if (buy.quantity < reqSell.quantity) {
                        int remaining = reqSell.quantity - buy.quantity;
                        reqSell.quantity = remaining;
                        buy.pub.send(buy.client+" "+company_name+" Bought "
                                +" "+tradePrice +" "+ buy.quantity);
                        //Say nothing to the seller --> informs buyer
                    }else if(buy.quantity > reqSell.quantity){
                        int remaining = buy.quantity - reqSell.quantity;
                        buy.quantity = remaining;
                        reqSell.pub.send(reqSell.client+" "+company_name+" Sold "
                                +" "+tradePrice +" "+ reqSell.quantity);
                        break;
                        //Say nothing to the buyer --> informs seller
                    }else {
                        sell_requests.remove(reqSell);
                        buy_requests.remove(buy);
                        buy.pub.send(buy.client+" "+company_name+" Bought "
                                +" "+tradePrice +" "+ buy.quantity);
                        reqSell.pub.send(reqSell.client+" "+company_name+" Sold"
                                +" "+tradePrice +" "+ reqSell.quantity);
                        //Informs seller and buyer
                        break;
                    }
                }
            }
        }


    }
}
