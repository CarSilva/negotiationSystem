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
        System.out.println(s);
        String[] answer = s.split(" ");
        String unsub = answer[0]+answer[1];
        sub.unsubscribe(unsub.getBytes());
      }
    }
}
