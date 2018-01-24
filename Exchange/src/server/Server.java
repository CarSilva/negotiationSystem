
package server;

import Controling_time.WorkingTime;
import exchange.Exchange;


public class Server {
    public static void main(String[] args) {

        int port = java.lang.Integer.parseInt(args[0]);
        Exchange exchange = new Exchange(Integer.parseInt(args[1]));
        WorkingTime wt = new WorkingTime(exchange, port);
        wt.scheduleOpenAndClose();
    }
}


