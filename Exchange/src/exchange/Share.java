package exchange;

import Interface.Request;
import httpCommunication.DirectoryAccess;
import httpCommunication.Json;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class Share {

    String company_name;
    double openingValue;
    double closingValue;
    double minimumValue;
    double maximumValue;
    int numberOfTrades;
    ZMQ.Socket pub;
    Queue<RequestBuy> buy_requests;
    Queue<RequestSell> sell_requests;
    Json json;
    DirectoryAccess http;

    public String getCompany_name() {
        return company_name;
    }

    public double getOpeningValue() {
        return openingValue;
    }

    public double getClosingValue() {
        return closingValue;
    }

    public double getMinimumValue() {
        return minimumValue;
    }

    public double getMaximumValue() {
        return maximumValue;
    }

    public Share(String company_name){
        this.company_name = company_name;
        this.openingValue = -1;
        this.closingValue = -1;
        this.minimumValue = Integer.MAX_VALUE;
        this.maximumValue = -1;
        this.numberOfTrades = 0;
        ZMQ.Context context = ZMQ.context(1);
        this.pub = context.socket(ZMQ.PUB);
        pub.connect("tcp://localhost:12348");
        Comparator<Request> cmp = new OrderQueue();
        this.buy_requests = new PriorityQueue<>(11, cmp);
        this.sell_requests = new PriorityQueue<>(11, cmp);
        json = new Json();
        http = new DirectoryAccess();
    }


    class OrderQueue implements Comparator<Request>{
        @Override
        public int compare(Request req, Request t1) {
            if(req.getTimestamp() > t1.getTimestamp())
                return 1;
            if(req.getTimestamp() < t1.getTimestamp())
                return -1;
            return 0;
        }
    }



    void add_buy_request(int quantity, float price, String client) {

        RequestBuy buy = new RequestBuy(quantity, price,
                client, System.currentTimeMillis());

        if (sell_requests.isEmpty())
            buy_requests.add(buy);

        else {
            trade(buy);
        }
    }

    void add_sell_request(int quantity, float price, String client) {

        RequestSell sell = new RequestSell(quantity, price,
                client, System.currentTimeMillis());

        if (buy_requests.isEmpty())
            sell_requests.add(sell);

        else {
            trade(sell);
        }
    }

   synchronized void trade(RequestBuy reqBuy) {
        boolean add = false;
        for(RequestSell sell : sell_requests){
            if(sell.price <= reqBuy.price) {
                float tradePrice = (sell.price + reqBuy.price) / 2;
                this.numberOfTrades++;
                if(this.numberOfTrades == 1){
                    this.openingValue = tradePrice;
                    sendMinorInfoDirectory("OPEN", this.openingValue);
                }
                if(tradePrice < minimumValue){
                    minimumValue = tradePrice;
                    sendMinorInfoDirectory("MIN", minimumValue);
                }else if(tradePrice > maximumValue) {
                    maximumValue = tradePrice;
                    sendMinorInfoDirectory("MAX", maximumValue);
                }
                if (sell.quantity < reqBuy.quantity) {
                    int remaining = reqBuy.quantity - sell.quantity;
                    reqBuy.quantity = remaining;

                    reqBuy.price_sum += tradePrice;
                    reqBuy.trade_counter++;

                    sell.price_sum += tradePrice;
                    sell.trade_counter++;

                    pub.send(company_name+" "+sell.client+" Sold"
                            +" "+sell.price_sum/sell.trade_counter +" "+ sell.initial_quantity);

                    sell_requests.remove(sell);
                    buy_requests.add(reqBuy);
                    add = true;
                    //Say nothing to the buyer --> informs seller
                }else if(sell.quantity > reqBuy.quantity){
                    int remaining = sell.quantity - reqBuy.quantity;
                    sell.quantity = remaining;
                    reqBuy.quantity = 0;

                    reqBuy.price_sum += tradePrice;
                    reqBuy.trade_counter++;

                    sell.price_sum += tradePrice;
                    sell.trade_counter++;


                    pub.send(company_name+" "+reqBuy.client+" Bought"
                            +" "+reqBuy.price_sum/reqBuy.trade_counter +" "+ reqBuy.initial_quantity);
                    break;
                    //Say nothing to the seller --> informs buyer
                }else {
                    reqBuy.price_sum += tradePrice;
                    reqBuy.trade_counter++;

                    sell.price_sum += tradePrice;
                    sell.trade_counter++;

                    pub.send(company_name+" "+reqBuy.client+" Bought"
                            +" "+reqBuy.price_sum/reqBuy.trade_counter +" "+ reqBuy.initial_quantity);
                    pub.send(company_name+" "+sell.client+" Sold"
                            +" "+sell.price_sum/sell.trade_counter +" "+ sell.initial_quantity);

                    sell_requests.remove(sell);
                    reqBuy.quantity = 0;
                    //Informs seller and buyer
                    break;
                }
            }
        }
        if(reqBuy.quantity != 0 && !add){
            buy_requests.add(reqBuy);
        }
    }


   synchronized void trade(RequestSell reqSell) {
        boolean add = false;
        for(RequestBuy buy : buy_requests){
            if(buy.price >= reqSell.price) {
                float tradePrice = (buy.price + reqSell.price) / 2;
                this.numberOfTrades++;
                if(this.numberOfTrades == 1){
                    this.openingValue = tradePrice;
                    sendMinorInfoDirectory("OPEN", this.openingValue);
                }
                if(tradePrice < this.minimumValue) {
                    this.minimumValue = tradePrice;
                    sendMinorInfoDirectory("MIN", this.minimumValue);
                }else if(tradePrice > this.maximumValue) {
                    this.maximumValue = tradePrice;
                    sendMinorInfoDirectory("MAX", this.maximumValue);
                }
                if (buy.quantity < reqSell.quantity) {
                    int remaining = reqSell.quantity - buy.quantity;
                    reqSell.quantity = remaining;

                    buy.price_sum += tradePrice;
                    buy.trade_counter++;

                    reqSell.price_sum += tradePrice;
                    reqSell.trade_counter++;

                    pub.send(company_name+" "+buy.client+" Bought"
                            +" "+buy.price_sum/buy.trade_counter +" "+ buy.initial_quantity);

                    buy_requests.remove(buy);
                    sell_requests.add(reqSell);
                    add = true;
                    //Say nothing to the seller --> informs buyer
                }else if(buy.quantity > reqSell.quantity){
                    int remaining = buy.quantity - reqSell.quantity;
                    buy.quantity = remaining;
                    reqSell.quantity = 0;

                    buy.price_sum += tradePrice;
                    buy.trade_counter++;

                    reqSell.price_sum += tradePrice;
                    reqSell.trade_counter++;

                    pub.send(company_name+" "+reqSell.client+" Sold"
                            +" "+reqSell.price_sum/reqSell.trade_counter +" "+ reqSell.initial_quantity);
                    break;
                    //Say nothing to the buyer --> informs seller
                }else {
                    buy.price_sum += tradePrice;
                    buy.trade_counter++;

                    reqSell.price_sum += tradePrice;
                    reqSell.trade_counter++;

                    pub.send(company_name+" "+buy.client+" Bought"
                            +" "+buy.price_sum/buy.trade_counter +" "+ buy.initial_quantity);

                    pub.send(company_name+" "+reqSell.client+" Sold"
                            +" "+reqSell.price_sum/reqSell.trade_counter +" "+ reqSell.initial_quantity);

                    buy_requests.remove(buy);
                    reqSell.quantity = 0;
                    //Informs seller and buyer
                    break;
                }
            }
        }
        if(reqSell.quantity != 0 && !add){
            sell_requests.add(reqSell);
        }
    }

    void sendMinorInfoDirectory(String type, double value){
        String[] s = {this.company_name, String.valueOf(value)};
        String js = "";
        if(type == "MIN"){
            js = json.createJsonMin(s);
        }else if(type == "MAX"){
            js = json.createJsonMax(s);
        }else if(type == "OPEN"){
            js = json.createJsonOpening(s);
        }
        String request = "company/"+this.company_name;
        try {
            http.sendPutRequest(request, js);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendTotalInfoDirectory(){
        String[] s = {this.company_name,
                        String.valueOf(this.openingValue),
                        String.valueOf(this.closingValue),
                        String.valueOf(this.minimumValue),
                        String.valueOf(this.maximumValue)};
        String js = json.createJsonTotal(s);
        String request = "company/"+this.company_name;
        try {
            http.sendPutRequest(request,js);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

                        /*AUX CLASSES*/

    class RequestBuy implements Request {
        int quantity;
        int initial_quantity;
        float price;
        float price_sum;
        int trade_counter;
        long timestamp;
        String client;

        RequestBuy(int quantity, float price,
                   String client, long timestamp) {
            this.quantity = quantity;
            this.initial_quantity = quantity;
            this.price = price;
            this.price_sum = 0;
            this.trade_counter = 0;
            this.client = client;
            this.timestamp = timestamp;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    class RequestSell implements Request{
        int quantity;
        int initial_quantity;
        float price;
        float price_sum;
        int trade_counter;
        long timestamp;
        String client;

        RequestSell(int quantity, float price,
                    String client, long timestamp) {
            this.quantity = quantity;
            this.initial_quantity = quantity;
            this.price = price;
            this.price_sum = 0;
            this.trade_counter = 0;
            this.client = client;
            this.timestamp = timestamp;
        }

        public long getTimestamp() {
            return this.timestamp;
        }
    }

}