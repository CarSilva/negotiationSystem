import java.io.InputStream;
import java.io.OutputStream;

public class HandleRcv extends Thread{
    private InputStream is;
    private OutputStream os;

    public HandleRcv(InputStream is, OutputStream os){
        this.is = is;
        this.os = os;
    }

    @Override
    public void run(){

    }
}
