package server;

import server.ProtoAuthOrder.Auth;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

import java.io.*;
import java.net.*;

public class Client {

  public static void main(String[] args) {
    try{
    if(args.length<2)
      System.exit(1);
    String host = args[0];
    int port = Integer.parseInt(args[1]);
    Socket s = new Socket(host, port);
    CodedInputStream cis = CodedInputStream.newInstance(s.getInputStream());
    CodedOutputStream cos = CodedOutputStream.newInstance(s.getOutputStream());
    System.out.println("Username:");
    String username = System.console().readLine();
    System.out.println("Password:");
    String pwd = System.console().readLine();
    Auth auth = createAuth(username, pwd);
    byte[] ba = auth.toByteArray();
    cos.writeRawBytes(ba);
    cos.flush();
    /*
    while (true) {
      System.out.println("Len: " + ba.length);
      cos.writeUInt32NoTag(ba.length);
      System.out.println("Wrote Len");
      cos.writeRawBytes(ba);
      System.out.println("Wrote " + ba.length + " bytes");
      cos.flush();
      Thread.sleep(3000);
    }*/
    //os.close();
    //s.shutdownOutput();
    }catch(Exception e){
      e.printStackTrace();
      System.exit(0);
    }
  }

  static Auth createAuth(String username, String pwd) {
    return
      Auth.newBuilder()
      .setName(username)
      .setPassword(pwd)
      .build();
  }

}