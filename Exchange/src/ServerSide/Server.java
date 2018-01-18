
package ServerSide;

import ProtocolBuffers.ProtoReqRecv;

import java.io.*;
import java.net.*;

public class Server {
  public static void main(String[] args) {
    try{
    int port = java.lang.Integer.parseInt(args[0]);
    ServerSocket srv = new ServerSocket(port);
    while (true) {
      Socket cli=srv.accept();
      InputStream cis = cli.getInputStream();
      OutputStream cos = cli.getOutputStream();
      (new ClientHandler(cis, cos)).start();
    }
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}

class ClientHandler extends Thread {
  InputStream cis;
  OutputStream cos;

  ClientHandler(InputStream cis, OutputStream cos) {
    this.cis = cis;
    this.cos = cos;
  }

  public void run() {
    try {
    while (true) {
      int tam = cis.read();
      byte[] packetRead = new byte[tam];
      cis.read(packetRead, 0, tam);
      ProtoReqRecv.Buy buy =  ProtoReqRecv.Buy.parseFrom(packetRead);
      System.out.println(buy.getCompanyBuy());
    }
    } catch (java.io.IOException e) {
    }
  }
}
