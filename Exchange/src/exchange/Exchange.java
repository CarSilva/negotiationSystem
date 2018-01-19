package exchange;

import httpDirectory.DirectoryAccess;
import httpDirectory.Json;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

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
                                            float price, int idClient, ZMQ.Socket pub) {
        boolean existsShare = false;
        Share share = shares.get(share_name);
        if(share != null)
            existsShare = true;
        share.add_buy_request(quantity, price, pub, idClient);
        return existsShare;
    }

    public synchronized boolean sell_request(String share_name, int quantity,
                                             float price, int idClient, ZMQ.Socket pub) {
        boolean existsShare = false;
        Share share = shares.get(share_name);
        if(share != null)
            existsShare = true;
        share.add_sell_request(quantity, price, pub, idClient);
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
            this.buy_requests = new PriorityQueue<>();
            this.sell_requests = new PriorityQueue<>();
        }

        class RequestBuy {
            int quantity;
            float price;
            ZMQ.Socket pub;
            int idClient;

            RequestBuy(int quantity, float price, ZMQ.Socket pub, int idClient) {
                this.quantity = quantity;
                this.price = price;
                this.pub = pub;
                this.idClient = idClient;
            }

            RequestBuy(int quantity, float price) {
                this.quantity = quantity;
                this.price = price;
            }
        }

        class RequestSell{
            int quantity;
            float price;
            ZMQ.Socket pub;
            int idClient;

            RequestSell(int quantity, float price) {
                this.quantity = quantity;
                this.price = price;
            }

            RequestSell(int quantity, float price, ZMQ.Socket pub, int idClient) {
                this.quantity = quantity;
                this.price = price;
                this.pub = pub;
                this.idClient = idClient;
            }
        }

       synchronized void add_buy_request(int quantity, float price,
                                         ZMQ.Socket pub, int idClient) {

            RequestBuy buy = new RequestBuy(quantity, price, pub, idClient);

            if (sell_requests.isEmpty())
                buy_requests.add(buy);

            else {
                trade(buy);
            }
        }

        synchronized void add_sell_request(int quantity, float price,
                                           ZMQ.Socket pub, int idClient) {

            RequestSell sell = new RequestSell(quantity, price, pub, idClient);

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
                        sell.pub.send(sell.idClient+" "+company_name
                                        +" "+tradePrice +" "+ sell.quantity);
                        //Say nothing to the buyer --> informs seller
                    }else if(sell.quantity > reqBuy.quantity){
                        int remaining = sell.quantity - reqBuy.quantity;
                        sell.quantity = remaining;
                        reqBuy.pub.send(reqBuy.idClient+" "+company_name
                                +" "+tradePrice +" "+ reqBuy.quantity);
                        break;
                        //Say nothing to the seller --> informs buyer
                    }else {
                        sell_requests.remove(sell);
                        buy_requests.remove(reqBuy);
                        reqBuy.pub.send(reqBuy.idClient+" "+company_name
                                +" "+tradePrice +" "+ reqBuy.quantity);
                        sell.pub.send(sell.idClient+" "+company_name
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
                        buy.pub.send(buy.idClient+" "+company_name
                                +" "+tradePrice +" "+ buy.quantity);
                        //Say nothing to the seller --> informs buyer
                    }else if(buy.quantity > reqSell.quantity){
                        int remaining = buy.quantity - reqSell.quantity;
                        buy.quantity = remaining;
                        reqSell.pub.send(reqSell.idClient+" "+company_name
                                +" "+tradePrice +" "+ reqSell.quantity);
                        break;
                        //Say nothing to the buyer --> informs seller
                    }else {
                        sell_requests.remove(reqSell);
                        buy_requests.remove(buy);
                        buy.pub.send(buy.idClient+" "+company_name
                                +" "+tradePrice +" "+ buy.quantity);
                        reqSell.pub.send(reqSell.idClient+" "+company_name
                                +" "+tradePrice +" "+ reqSell.quantity);
                        //Informs seller and buyer
                        break;
                    }
                }
            }
        }


    }
}
