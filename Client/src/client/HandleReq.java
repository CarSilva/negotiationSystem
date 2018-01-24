package client;

import httpCommunication.DirectoryAccess;
import httpCommunication.Json;

import org.zeromq.ZMQ;


import protobuf.ProtoAuth;
import protobuf.ProtoAuth.Auth;
import protobuf.ProtoReqRecv.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static client.Client.createAuth;
import static client.Client.user;

public class HandleReq extends Thread {
    private InputStream is;
    private OutputStream os;
    private Integer size;
    private ZMQ.Socket sub;
    private ZMQ.Socket pubIn;
    private String username;
    private DirectoryAccess http;
    private List<String> subscriptions;

    public HandleReq (InputStream is, OutputStream os, ZMQ.Socket sub,
                      ZMQ.Context context, String username){
        this.is = is;
        this.os = os;
        this.size = -1;
        this.sub = sub;
        this.username = username;
        this.http = new DirectoryAccess();
        this.pubIn = context.socket(ZMQ.PUB);
        pubIn.bind("ipc://local"+username);
        sub.connect("ipc://local"+username);
        this.subscriptions = new ArrayList<>();
    }

    @Override
    public void run(){
        Scanner sc = new Scanner(System.in);
        while(true){
            String[] s = sc.nextLine().split(" ");
            switch (s[0]) {
                case "logout":
                    if(s.length > 1){
                        System.out.println("Too much arguments");
                        break;
                    }
                    Logout logout = createLogout(username);
                    General generalLogout = createGeneralLogout(logout);
                    try{
                        size = generalLogout.getSerializedSize();
                        os.write(size.byteValue());
                        generalLogout.writeTo(os);
                        os.flush();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                    receiveReply();
                    break;
                case "buy" :
                    if(s.length < 4 && s.length < 4){
                        System.out.println("Wrong arguments");
                    }
                    int exIdb = getExchange(s[1]);
                    if(exIdb == -1) {
                        System.out.println("No such company");
                        continue;
                    }
                    pubIn.send("/ns " + s[1] + " " + username);
                    Pair pb = getExchangeInfo(exIdb);
                    Buy buy = createBuy(s[1], Integer.parseInt(s[2]),
                            Float.parseFloat(s[3]), username, pb.host, pb.port);
                    General generalBuy = createGeneralBuy(buy);
                    try{
                        size = generalBuy.getSerializedSize();
                        os.write(size.byteValue());
                        generalBuy.writeTo(os);
                        os.flush();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                    receiveReply();
                    break;
                case "sell" :
                    if(s.length < 4 && s.length < 4){
                        System.out.println("Wrong arguments");
                    }
                    int exIds = getExchange(s[1]);
                    if(exIds == -1) {
                        System.out.println("No such company");
                        continue;
                    }
                    pubIn.send("/ns " + s[1] + " " + username);
                    Pair ps  = getExchangeInfo(exIds);
                    Sell sell = createSell(s[1], Integer.parseInt(s[2]),
                            Float.parseFloat(s[3]), username, ps.host, ps.port);
                    General generalSell = createGeneralSell(sell);
                    try{
                        size = generalSell.getSerializedSize();
                        os.write(size.byteValue());
                        generalSell.writeTo(os);
                        os.flush();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                    receiveReply();
                    break;
                case "subscribe" :
                    int nSubs = subscriptions.size() + s.length;
                    if(nSubs > 10){
                        System.out.println("The limit of 10 subscriptions was hited");
                        System.out.println("Please unsubscribe some of the companies");
                        break;
                    }
                    for(int i = 1; i < s.length; i++){
                        pubIn.send("/ns "+s[i]);
                        subscriptions.add(s[i]);
                    }
                    System.out.println("All companies subscribed");
                    break;
                case "unsubscribe" :
                    for(int i = 1; i < s.length; i++){
                        if(subscriptions.contains(s[i])){
                            subscriptions.remove(s[i]);
                            pubIn.send("/rs "+s[i]);
                        }else{
                            System.out.println("Company "+s[1]+" wasn't subscribed");
                        }
                    }
                    System.out.println("All subscriptions unsubscribed");
                    break;
                case "list" :
                    if(s.length > 1){
                        System.out.println("Too much arguments");
                        break;
                    }
                    try{
                      String response = http.sendRequest("companies", "GET");
                      Json js = new Json();
                      System.out.println(js.parseArray(response));
                    }catch(IOException e){
                      e.printStackTrace();
                    }
                    break;

                default :
                    System.out.println("Not a valid option\tyou can try again");
            }

        }
    }

    private void receiveReply() {
        try{
            int tam = is.read();
            byte[] packetRead = new byte[tam];
            is.read(packetRead, 0, tam);
            ResponseAfterRecv reply =
                    ResponseAfterRecv.parseFrom(packetRead);
            System.out.println(reply.getRep());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private General createGeneralBuy(Buy buy) {
        return General.newBuilder()
                .setBuy(buy)
                .build();
    }

    private General createGeneralSell(Sell sell) {
        return General.newBuilder()
                .setSell(sell)
                .build();
    }

    private General createGeneralLogout(Logout logout){
        return General.newBuilder()
                .setLogout(logout)
                .build();
    }

    private Buy createBuy(String company, int quantity,
                         float priceMax, String client,
                          String host, int port) {
        return Buy.newBuilder()
                .setCompanyBuy(company)
                .setQttBuy(quantity)
                .setPriceMax(priceMax)
                .setClientB(client)
                .setHost(host)
                .setPort(port)
                .build();
    }

    private Logout createLogout(String username){
        return Logout.newBuilder()
                .setUsername(username)
                .build();
    }
    private Sell createSell(String company, int quantity,
                           float priceMin, String client,
                            String host, int port) {
        return Sell.newBuilder()
                .setCompanySell(company)
                .setQttSell(quantity)
                .setPriceMin(priceMin)
                .setClientS(client)
                .setHost(host)
                .setPort(port)
                .build();
    }

    private int getExchange(String company) {
        int id = -1;
        try {
            Json js = new Json();
            String response = http.sendRequest("company/"+company, "GET");
            if(response.contains("Error"))
                return -1;
            id = js.getExchangeId(response);
            return id;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return id;
    }

    private Pair getExchangeInfo(int exchangeId) {
        Pair p = null;
        try {
            Json js = new Json();
            String response = http.sendRequest("exchange/"+exchangeId, "GET");
            if(response.contains("Error"))
                return new Pair("error", -1);
            String host = js.getHost(response);
            int port = js.getPort(response);
            p =  new Pair(host, port);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return p;
    }

    class Pair {
        String host;
        int port;
        Pair (String host, int port){
            this.host = host;
            this.port = port;
        }
    }
}
