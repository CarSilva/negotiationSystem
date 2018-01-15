package server;

import server.ProtoAuthOrder.Auth;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

import java.io.*;
import java.net.*;

public class Runner {

    public static void main(String[] args) throws Exception {
        if(args.length < 2)
            System.exit(1);
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        Socket s = new Socket(host, port);
        InputStream is = s.getInputStream();
        OutputStream os = s.getOutputStream();
        auth(is, os);
        Thread handleReq = new HandleReq(is, os);
        handleReq.start();
        Thread handleRcv = new HandleRcv(is, os);
        handleRcv.start();
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
            //byte[] ba = auth.toByteArray();
            auth.writeTo(os);
            os.flush();
            ResponseAuth rA = ResponseAuth.parseFrom(is);
            System.out.println(rA.getOk());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    static Auth createAuth(String username, String pwd, String register) {
        return
                Auth.newBuilder()
                        .setName(username)
                        .setPassword(pwd)
                        .setRegister(register)
                        .build();
    }
}
