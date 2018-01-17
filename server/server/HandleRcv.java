package server;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.IOException;

import server.ProtoReqRecv.*;

public class HandleRcv extends Thread{
    private InputStream is;
    private OutputStream os;

    public HandleRcv(InputStream is, OutputStream os){
        this.is = is;
        this.os = os;
    }

    @Override
    public void run(){
      while(true){
        Object response = null;
        try{
          int tam = is.read();
          byte[] packetRead = new byte[tam];
          is.read(packetRead, 0, tam);
          Reply reply =  Reply.parseFrom(packetRead);
          if(reply.hasRAR()){
            response = reply.getRAR();
          }
          else
            if(reply.hasUR())
              response = reply.getUR();
          if(response instanceof ResponseAfterRecv){
            ResponseAfterRecv rAR = (ResponseAfterRecv) response;
            System.out.println(rAR.getRep());
          }
          else if(response instanceof UpdateReply){
            UpdateReply uR = (UpdateReply) response;
            System.out.println(uR.getResult());
            System.out.println(uR.getCompany());
            System.out.println(uR.getQuantity());
            System.out.println(uR.getPrice());
          }
        }catch(IOException e){
          e.printStackTrace();
        }

      }
    }
}
