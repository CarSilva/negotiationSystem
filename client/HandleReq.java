package client;
import protobuf.ProtoReqRecv.*;
import java.io.*;
import java.net.*;
import org.zeromq.ZMQ;

public class HandleReq extends Thread {
    private InputStream is;
    private OutputStream os;
    private Integer size;
    private ZMQ.Socket sub;
    private String username;

    public HandleReq (InputStream is, OutputStream os, ZMQ.Socket sub,
                      String username){
        this.is = is;
        this.os = os;
        this.size = -1;
        this.sub = sub;
        this.username = username;
    }

    @Override
    public void run(){
      while(true){
          String[] s = System.console().readLine().split(" ");
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
                break;
            case "list" :
                DirectoryAccess http = new DirectoryAccess();
                try{
                  String response = http.sendRequest("GET", "companies");
                  Json j = new Json();
                  System.out.println(j.parseToString(response));
                }catch(IOException e){
                  e.printStackTrace();
                }

            default :
                System.out.println("Not a valid option\tyou can try again");
          }
          String subNoti = username+" "+s[1];
          sub.subscribe(subNoti.getBytes());
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
