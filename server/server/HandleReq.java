package server;
import server.ProtoReqRecv.Buy;
import server.ProtoReqRecv.Sell;
import server.ProtoReqRecv.General;
import java.io.*;
import java.net.*;

public class HandleReq extends Thread {
    private InputStream is;
    private OutputStream os;
    Integer size;

    public HandleReq (InputStream is, OutputStream os){
        this.is = is;
        this.os = os;
        this.size = -1;
    }

    @Override
    public void run(){
      while(true){
          String[] s = System.console().readLine().split(" ");
          switch (s[0]) {
            case "buy" :
                Buy buy = createBuy(s[1], Integer.parseInt(s[2]), Float.parseFloat(s[3]));
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
                Sell sell = createSell(s[1], Integer.parseInt(s[2]), Float.parseFloat(s[3]));
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
            default :
                System.out.println("Not a valid option\tyou can try again");
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

    public Buy createBuy(String company, int quantity, float priceMax){
        return Buy.newBuilder()
                  .setCompanyBuy(company)
                  .setQttBuy(quantity)
                  .setPriceMax(priceMax)
                  .build();

    }

    public Sell createSell(String company, int quantity, float priceMin){
      return Sell.newBuilder()
                .setCompanySell(company)
                .setQttSell(quantity)
                .setPriceMin(priceMin)
                .build();
    }


}
