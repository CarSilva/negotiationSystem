package server;
import org.zeromq.ZMQ;

public class HandleRcv extends Thread{
    private ZMQ.Socket sub;
    public HandleRcv(ZMQ.Socket sub){
      this.sub = sub;
    }

    @Override
    public void run(){
      while(true){
        byte[] b = sub.recv();
			  String s = new String(b);
        String[] r = s.split(" ");
        System.out.println(r[1]+" "+r[2]+" "+r[3]+" "r[4]);
        String unsub = r[0]+r[1];
        sub.unsubscribe(unsub.getBytes());
      }
    }
}
