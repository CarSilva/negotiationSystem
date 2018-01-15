package server;

import server.ProtoAuth.Auth;
import server.ProtoAuth.ResponseAuth;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

import java.io.*;
import java.net.*;

public class Client {

    public static void main(String[] args) throws Exception {
        if(args.length < 2)
            System.exit(1);
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        Socket s = new Socket(host, port);
        InputStream is = s.getInputStream();
        OutputStream os = s.getOutputStream();
        auth(is, os);
        /*Thread handleReq = new HandleReq(is, os);
        handleReq.start();
        Thread handleRcv = new HandleRcv(is, os);
        handleRcv.start();*/
    }

    public static void auth(InputStream is, OutputStream os){
        try{
            System.out.println("Login/Registo?");
            String registerOrnot = null;
            String choice = System.console().readLine();
            if(choice.equals("login"))
                registerOrnot = choice;
            else if(choice.equals("registo"))
                registerOrnot = choice;
            else {
                System.out.println("Error");
                System.exit(1);
            }
            System.out.println("Username:");
            String username = System.console().readLine();
            System.out.println("Password:");
            String pwd = System.console().readLine();
            Auth auth = createAuth(username, pwd, registerOrnot);
            Integer size = auth.getSerializedSize();
            os.write(size.byteValue());
            auth.writeTo(os);
            os.flush();
            int tam = is.read();
            byte[] read = new byte[tam];
            is.read(read, 0, tam);
            ResponseAuth rA = ResponseAuth.parseFrom(read);
            System.out.println(rA.getStatusResponse());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    static Auth createAuth(String username, String pwd, String register) {
        return Auth.newBuilder()
                        .setName(username)
                        .setPassword(pwd)
                        .setRegister(register)
                        .build();
    }
}
