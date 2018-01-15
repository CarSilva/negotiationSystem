package server;
import java.io.InputStream;
import java.io.OutputStream;

public class HandleReq extends Thread {
    private InputStream is;
    private OutputStream os;

    public HandleReq (InputStream is, OutputStream os){
        this.is = is;
        this.os = os;
    }

    @Override
    public void run(){
      while(true){
          String s = System.console().readLine();
          switch (s.split(" ")[0]) {
            case "buy" :
                System.out.println("buy");
                break;
            case "sell" :
                System.out.println("sell");
                break;
            default :
                System.out.println("Not a valid option\tyou can try again");
          }
      }
    }


}
