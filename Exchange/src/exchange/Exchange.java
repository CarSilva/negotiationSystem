package exchange;

import httpDirectory.DirectoryAccess;
import httpDirectory.Json;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

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
        json = new Json();
        http = new DirectoryAccess();
        String query = "companies";
        fillShares(query);
    }


    public boolean buy_request(String share_name, int quantity, float price) {
        boolean existsShare = false;
        Share share = shares.get(share_name);
        if(share != null)
            existsShare = true;
        // procura por share_name (key) mas pode-se alterar
        // e procurar também por shares da companhia x
        share.add_buy_request(quantity, price);
        return existsShare;
    }

    public void sell_request(String share_name, int quantity, float price) {
        Share share = shares.get(share_name);
        share.add_sell_request(quantity, price);
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

            RequestBuy(int quantity, float price) {
                this.quantity = quantity;
                this.price = price;
            }
        }

        class RequestSell{
            int quantity;
            float price;

            RequestSell(int quantity, float price) {
                this.quantity = quantity;
                this.price = price;
            }
        }

        void add_buy_request(int quantity, float price) {

            RequestBuy buy = new RequestBuy(quantity, price);

            if (sell_requests.isEmpty())
                buy_requests.add(buy);

            else {
                //Request sell = sell_requests.remove();
                trade(buy);
            }
        }

        void add_sell_request(int quantity, float price) {

            RequestSell sell = new RequestSell(quantity, price);

            if (buy_requests.isEmpty())
                sell_requests.add(sell);

            else {
                //Request buy = buy_requests.remove();
                trade(sell);
            }
        }

        void trade(RequestBuy reqBuy) {

            for(RequestSell sell : sell_requests){
                if(sell.price <= reqBuy.price) {
                    float tradePrice = (sell.price + reqBuy.price) / 2;
                    if (sell.quantity < reqBuy.quantity) {
                        int remaining = reqBuy.quantity - sell.quantity;
                        reqBuy.quantity = remaining;
                        //Say nothing to the buyer --> informs seller
                    }else if(sell.quantity > reqBuy.quantity){
                        int remaining = sell.quantity - reqBuy.quantity;
                        sell.quantity = remaining;
                        break;
                        //Say nothing to the seller --> informs buyer
                    }else {
                        sell_requests.remove(sell);
                        buy_requests.remove(reqBuy);
                        //Informs seller and buyer
                        break;
                    }
                }
            }
        }


        void trade(RequestSell reqSell) {

            for(RequestBuy buy : buy_requests){
                if(buy.price >= reqSell.price) {
                    float tradePrice = (buy.price + reqSell.price) / 2;
                    if (buy.quantity < reqSell.quantity) {
                        int remaining = reqSell.quantity - buy.quantity;
                        reqSell.quantity = remaining;
                        //Say nothing to the seller --> informs buyer
                    }else if(buy.quantity > reqSell.quantity){
                        int remaining = buy.quantity - reqSell.quantity;
                        buy.quantity = remaining;
                        break;
                        //Say nothing to the buyer --> informs seller
                    }else {
                        sell_requests.remove(reqSell);
                        buy_requests.remove(buy);
                        //Informs seller and buyer
                        break;
                    }
                }
            }
        }


    }
}
