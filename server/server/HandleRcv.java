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
        String[] answer = s.split(" ");
        System.out.println(answer[1]+answer[2]+answer[3]+answer[4]);
        String unsub = answer[0]+answer[1];
        sub.unsubscribe(unsub.getBytes());
      }
    }
}
