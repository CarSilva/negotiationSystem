package Controling_time;

import exchange.Exchange;
import org.zeromq.ZMQ;
import protocolBuffers.ProtoReqRecv;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExchangeOpen implements Runnable {
    private boolean working;
    private Exchange exchange;
    private int port;
    private ServerSocket srv;
    private ExecutorService executor;

    public ExchangeOpen(Exchange exchange, int port){
        this.working = true;
        this.exchange = exchange;
        this.port = port;
        this.executor = Executors.newFixedThreadPool(2);
    }

    @Override
    public void run() {

        try {
            srv = new ServerSocket(port);
            while (working) {
                Socket cli = srv.accept();
                InputStream cis = cli.getInputStream();
                OutputStream cos = cli.getOutputStream();
                ClientHandler ch = new ClientHandler(cis, cos, exchange);
                executor.execute(ch);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public ServerSocket getSrv() {
        System.out.println("tentei");
        return srv;
    }

    public void stop() {
        this.working = false;
        executor.shutdown();
        System.out.println("tentei");
    }

    class ClientHandler implements Runnable {
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
                            buy.getClientB());
                    ProtoReqRecv.ResponseAfterRecv rep = createReply(result, buy.getCompanyBuy());
                    sendPacket(rep);
                } else if(reply instanceof ProtoReqRecv.Sell){
                    ProtoReqRecv.Sell sell = (ProtoReqRecv.Sell) reply;
                    boolean result = exchange.sell_request(sell.getCompanySell(),
                            sell.getQttSell(),
                            sell.getPriceMin(),
                            sell.getClientS());
                    ProtoReqRecv.ResponseAfterRecv rep = createReply(result,sell.getCompanySell());
                    sendPacket(rep);
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
}
