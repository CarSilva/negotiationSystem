package directory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static java.lang.Math.round;

public class Exchange {

    private Map<String, Share> shares = new HashMap<>();
    //falta povoar o mapa com shares

    private void add_request(String share_name, int quantity, float price, boolean flag) {

        Share share = shares.get(share_name);
        //procura por share_name (key) mas pode-se alterar e procurar também por shares da companhia x

        if (flag) //true para buy
            share.add_buy_request(quantity, price);
        else
            share.add_sell_request(quantity, price);
    }

    private class Share {

        private String company_name;
        private Queue<Request> buy_requests = new LinkedList<>();
        private Queue<Request> sell_requests = new LinkedList<>();

        private void add_buy_request(int quantity, float price) {

            Request buy = new Request(quantity, price);

            if (sell_requests.isEmpty())
                buy_requests.add(buy);

            else {
                Request sell = sell_requests.remove();
                aux(buy, sell);
            }
        }

        private void add_sell_request(int quantity, float price) {

            Request sell = new Request(quantity, price);

            if (buy_requests.isEmpty())
                sell_requests.add(sell);

            else {
                Request buy = buy_requests.remove();
                aux(buy, sell);
            }
        }

        private void aux(Request buy, Request sell) {

            float sell_price = sell.getPrice();
            float buy_price = buy.getPrice();

            float mean = (sell_price + buy_price)/2;

            int sell_quantity = sell.getQuantity();
            int buy_quantity = buy.getQuantity();

            if (sell_quantity < buy_quantity) {
                int remaining = buy_quantity - sell_quantity;
                buy_requests.add(new Request(remaining, buy_price));
                //efectuada compra: quantidade=sell_quantity; preço=mean
            } else

            if (sell_quantity > buy_quantity) {
                int remaining = sell_quantity - buy_quantity;
                sell_requests.add(new Request(remaining, sell_price));
                //efectuada compra: quantidade=buy_quantity; preço=mean

            } //else
            //efectuada compra: quantidade=sell_quantity=buy_quantity; preço: mean
        }

        private class Request {

            private int quantity;
            private float price;

            private Request(int quantity, float price) {
                this.quantity = quantity;
                this.price = price;
            }

            public float getPrice() {
                return price;
            }

            public int getQuantity() {
                return quantity;
            }
        }
    }
}


