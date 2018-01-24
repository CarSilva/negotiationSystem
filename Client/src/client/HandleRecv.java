package client;

import org.zeromq.ZMQ;

public class HandleRecv extends Thread {
    private ZMQ.Socket sub;
    public HandleRecv(ZMQ.Socket sub){
        this.sub = sub;
    }

    @Override
    public void run(){
        sub.subscribe("/ns".getBytes());
        sub.subscribe("/rs".getBytes());
        while(true) {
            byte[] b = sub.recv();
            String s = new String(b);
            String[] r = s.split(" ");
            if (r[0].equals("/ns"))
                sub.subscribe(s.substring(4).getBytes());
            else if (r[0].equals("/rs"))
                sub.unsubscribe(s.substring(4).getBytes());
            else {
                sub.unsubscribe((r[0] + r[1]).getBytes());
                System.out.println(r[2] + " " + r[0] + " ~> " + "trade price: " + r[3] + "; trade quantity: " + r[4]);
            }
        }
    }
}
