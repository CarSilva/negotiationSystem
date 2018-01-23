package client;

import httpCommunication.DirectoryAccess;
import org.zeromq.ZMQ;

import protobuf.ProtoReqRecv.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HandleReq extends Thread {
    private InputStream is;
    private OutputStream os;
    private Integer size;
    private ZMQ.Socket sub;
    private String username;
    private DirectoryAccess http;
    private List<String> subscriptions;

    public HandleReq (InputStream is, OutputStream os, ZMQ.Socket sub,
                      String username){
        this.is = is;
        this.os = os;
        this.size = -1;
        this.sub = sub;
        this.username = username;
        this.http = new DirectoryAccess();
        this.subscriptions = new ArrayList<>();
    }

    @Override
    public void run(){
        Scanner sc = new Scanner(System.in);
        while(true){
            String[] s = sc.nextLine().split(" ");
            switch (s[0]) {
                case "buy" :
                    Buy buy = createBuy(s[1], Integer.parseInt(s[2]),
                            Float.parseFloat(s[3]), username);
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
                    Sell sell = createSell(s[1], Integer.parseInt(s[2]),
                            Float.parseFloat(s[3]), username);
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
                        sub.subscribe(s[i].getBytes());
                        subscriptions.add(s[i]);
                    }
                    System.out.println("All companies subscribed");
                    break;
                case "unsubscribe" :
                    for(int i = 1; i < s.length; i++){
                        if(subscriptions.contains(s[i])){
                            subscriptions.remove(s[i]);
                            sub.unsubscribe(s[i].getBytes());
                        }else{
                            System.out.println("Company "+s[1]+" wasn't subscribed");
                        }
                    }
                    System.out.println("All subscriptions unsubscribed");
                    break;
            /*case "list" :
                try{
                  String response = http.sendRequest("GET", "companies");
                  Json js = new Json();
                  System.out.println(js.parseArray(response));
                }catch(IOException e){
                  e.printStackTrace();
                }
                break;
                */
                default :
                    System.out.println("Not a valid option\tyou can try again");
            }
            String subNoti = username+" "+s[1];
            sub.subscribe(subNoti.getBytes());
        }
    }

    public void receiveReply(){
        try{
            int tam = is.read();
            byte[] packetRead = new byte[tam];
            is.read(packetRead, 0, tam);
            ResponseAfterRecv reply =  ResponseAfterRecv.parseFrom(packetRead);
            System.out.println(reply.getRep());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public General createGeneralBuy(Buy buy){
        return General.newBuilder()
                .setBuy(buy)
                .build();
    }

    public General createGeneralSell(Sell sell){
        return General.newBuilder()
                .setSell(sell)
                .build();
    }

    public Buy createBuy(String company, int quantity,
                         float priceMax, String client){
        return Buy.newBuilder()
                .setCompanyBuy(company)
                .setQttBuy(quantity)
                .setPriceMax(priceMax)
                .setClientB(client)
                .build();

    }

    public Sell createSell(String company, int quantity,
                           float priceMin, String client){
        return Sell.newBuilder()
                .setCompanySell(company)
                .setQttSell(quantity)
                .setPriceMin(priceMin)
                .setClientS(client)
                .build();
    }
}
