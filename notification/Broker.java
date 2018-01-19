package notification;

import org.zeromq.ZMQ;

public class Broker {
	public static void main(String[] args) {
		ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket subX = context.socket(ZMQ.XSUB);
		subX.bind("tcp://*:" + args[0]);
		ZMQ.Socket pubX = context.socket(ZMQ.XPUB);
		pubX.bind("tcp://*:" + args[1]);
		ZMQ.proxy(subX, pubX, null);

	}
}
