package client;

import org.zeromq.ZMQ;
import protobuf.ProtoAuth.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    static String user;
    public static void main(String[] args) throws Exception {
        if(args.length < 2)
            System.exit(1);
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        Socket s = new Socket(host, port);
        InputStream is = s.getInputStream();
        OutputStream os = s.getOutputStream();
        boolean bool = auth(is, os);
        if(!bool){
            System.out.println("Something went wrong. Please try again");
        }
        while(!bool){
            bool = auth(is,os);
            if(!bool){
                System.out.println("Something went wrong. Please try again");
            }
        }

        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket sub = context.socket(ZMQ.SUB);
        sub.connect("tcp://localhost:12349");
        Thread handleReq = new HandleReq(is, os, sub, user);
        handleReq.start();
        Thread handleRcv = new HandleRecv(sub);
        handleRcv.start();
    }

    public static boolean auth(InputStream is, OutputStream os){
        Scanner sc = new Scanner(System.in);
        boolean result = false;
        System.out.println("Login/Registo?");
        String registerOrnot = "";
        String choice = sc.nextLine();
        if(choice.equals("login"))
            registerOrnot = choice;
        else if(choice.equals("registo"))
            registerOrnot = choice;
        else {
            System.out.println("Error");
            System.exit(1);
        }
        System.out.println("Username:");
        String username = sc.nextLine();
        System.out.println("Password:");
        String pwd = sc.nextLine();
        user = username;
        try{
            Auth auth = createAuth(username, pwd, registerOrnot);
            Integer size = auth.getSerializedSize();
            os.write(size.byteValue());
            auth.writeTo(os);
            os.flush();
            int tam = is.read();
            byte[] read = new byte[tam];
            is.read(read, 0, tam);
            ResponseAuth rA = ResponseAuth.parseFrom(read);
            if(rA.getStatusResponse().equals("ok"))
                result = true;
            else result = false;
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    static Auth createAuth(String username, String pwd, String register) {
        return Auth.newBuilder()
                .setName(username)
                .setPassword(pwd)
                .setRegister(register)
                .build();
    }
}
