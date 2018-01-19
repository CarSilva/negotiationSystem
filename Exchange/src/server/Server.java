
package server;

import exchange.Exchange;
import org.zeromq.ZMQ;
import protocolBuffers.ProtoReqRecv;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    public static void main(String[] args) {

        try {
            int port = java.lang.Integer.parseInt(args[0]);
            ServerSocket srv = new ServerSocket(port);
            Exchange exchange = new Exchange(Integer.parseInt(args[1]));
            while (true) {
                Socket cli = srv.accept();
                InputStream cis = cli.getInputStream();
                OutputStream cos = cli.getOutputStream();
                (new ClientHandler(cis, cos, exchange)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    InputStream cis;
    OutputStream cos;
    Exchange exchange;

    ClientHandler(InputStream cis, OutputStream cos, Exchange exchange) {
        this.cis = cis;
        this.cos = cos;
        this.exchange = exchange;
    }


    public void run() {
        try {
            ZMQ.Context context = ZMQ.context(1);
            ZMQ.Socket pub = context.socket(ZMQ.PUB);
            pub.connect("tcp://localhost:12348");
            //create ZMQ Socket publisher here
            while (true) {
                Object reply = null;
                int tam = cis.read();
                byte[] packetRead = new byte[tam];
                cis.read(packetRead, 0, tam);
                ProtoReqRecv.General general =  ProtoReqRecv.General
                                                .parseFrom(packetRead);
                if(general.hasBuy())
                    reply = general.getBuy();
                else if(general.hasSell())
                    reply = general.getSell();
                if(reply instanceof ProtoReqRecv.Buy){
                    ProtoReqRecv.Buy buy = (ProtoReqRecv.Buy) reply;
                    boolean result = exchange.buy_request(buy.getCompanyBuy(),
                                                        buy.getQttBuy(),
                                                        buy.getPriceMax(),
                                                        buy.getIdClientB(),
                                                        pub);
                    ProtoReqRecv.ResponseAfterRecv rep = createReply(result, buy.getCompanyBuy());
                    sendPacket(rep);
                }else if(reply instanceof ProtoReqRecv.Sell){
                    ProtoReqRecv.Sell sell = (ProtoReqRecv.Sell) reply;
                    boolean result = exchange.sell_request(sell.getCompanySell(),
                                                        sell.getQttSell(),
                                                        sell.getPriceMin(),
                                                        sell.getIdClientS(),
                                                        pub);
                    ProtoReqRecv.ResponseAfterRecv rep = createReply(result,sell.getCompanySell());
                    sendPacket(rep);
                }

            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket (ProtoReqRecv.ResponseAfterRecv reply){
        try{
            Integer size = reply.getSerializedSize();
            cos.write(size.byteValue());
            reply.writeTo(cos);
            cos.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public ProtoReqRecv.ResponseAfterRecv createReply(boolean result, String info){
        String reply = "";
        if(result)
            reply = "Order added with success on: " + info;
        else reply = "Error, no share with name " + info + " founded";
        return ProtoReqRecv.ResponseAfterRecv.newBuilder()
                                             .setRep(reply)
                                             .build();
    }
}
