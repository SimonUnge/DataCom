/**
 * @author Johan Nykvist, Daniel Aldman
 */
public class ReliableSender extends SenderProtocol {

	private Channel channel;

	private Timer timer;

	private int windowSize;

	private double timeoutValue;

	private Packet sentPacket;

	private int nextSeqNum;

	ReliableSender(Channel aChannel, int aWindowSize, double aTimeout) {
		channel = aChannel;
		timer = new Timer(this);
		windowSize = aWindowSize;
		timeoutValue = aTimeout;
		nextSeqNum = 1;
	}

	void send(Data aDataChunk) {
		blockData();

		sentPacket = new Packet(nextSeqNum, aDataChunk);
		channel.send(sentPacket);

		Simulator.getInstance().log("reliable sender sent " + sentPacket);

		timer.start(timeoutValue);
	}

	public void receive(Packet aPacket) {
		Simulator.getInstance().log("reliable sender receives " + aPacket);

		if(aPacket.getSeqNum() == nextSeqNum) {
			timer.stop();
			nextSeqNum++;
			acceptData();
		}
	}

	public void timeout(Timer aTimer) {
		Simulator.getInstance().log("*** reliable sender timeouts ***");
		Simulator.getInstance().log("reliable sender resends " + sentPacket);
		timer.start(timeoutValue);
		channel.send(sentPacket);
	}
}
