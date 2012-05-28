package query;

import java.net.*;

/**
 * A class that handles Minecraft Query protocol requests
 * 
 * @author Ryan McCann
 */
public class MCQuery
{
	final static byte HANDSHAKE = 9;
	final static byte STAT = 0;
	
	String serverAddress = "localhost";
	int queryPort = 25565; // the default minecraft query port
	
	int localPort = 25566; // the local port we're connected to the server on
	
	private DatagramSocket socket = null; //prevent socket already bound exception
	private int token;
	
	public MCQuery(){} // for testing, defaults to "localhost:25565"
	public MCQuery(String address)
	{
		this(address, 25565);
	}
	public MCQuery(String address, int port)
	{
		serverAddress = address;
		queryPort = port;
	}
	
	// used to get a session token
	private void handshake()
	{
		QueryRequest req = new QueryRequest();
		req.type = HANDSHAKE;
		req.sessionID = generateSessionID();
		
		int val = 11 - req.toBytes().length; //should be 11 bytes total
		byte[] input = ByteUtils.padArrayEnd(req.toBytes(), val);
		byte[] result = sendUDP(input);
		
		token = Integer.parseInt(new String(result).trim());
	}

	/**
	 * Use this to get basic status information from the server.
	 * @return a <code>QueryResponse</code> object
	 */
	public QueryResponse basicStat()
	{
		handshake(); //get the session token first

		QueryRequest req = new QueryRequest(); //create a request
		req.type = STAT;
		req.sessionID = generateSessionID();
		req.setPayload(token);
		byte[] send = req.toBytes();
		
		byte[] result = sendUDP(send);
		
		QueryResponse res = new QueryResponse(result, false);
		return res;
	}
	
	/**
	 * Use this to get more information, including players, from the server.
	 * @return a <code>QueryResponse</code> object
	 */
	public QueryResponse fullStat()
	{
//		basicStat() calls handshake()
//		QueryResponse basicResp = this.basicStat();
//		int numPlayers = basicResp.onlinePlayers; //TODO use to determine max length of full stat
		
		handshake();
		
		QueryRequest req = new QueryRequest();
		req.type = STAT;
		req.sessionID = generateSessionID();
		req.setPayload(token);
		req.payload = ByteUtils.padArrayEnd(req.payload, 4); //for full stat, pad the payload with 4 null bytes
		
		byte[] send = req.toBytes();
		
		byte[] result = sendUDP(send);
		
		/*
		 * note: buffer size = base + #players(online) * 16(max username length)
		 */
		
		QueryResponse res = new QueryResponse(result, true);
		return res;
	}
	
	private byte[] sendUDP(byte[] input)
	{
		try
		{
			while(socket == null)
			{
				try {
					socket = new DatagramSocket(localPort); //create the socket
				} catch (BindException e) {
					++localPort; // increment if port is already in use
				}
			}
			
			//create a packet from the input data and send it on the socket
			InetAddress address = InetAddress.getByName(serverAddress); //create InetAddress object from the address
			DatagramPacket packet1 = new DatagramPacket(input, input.length, address, queryPort);
			socket.send(packet1);
			
			//receive a response in a new packet
			byte[] out = new byte[1024]; //TODO guess at max size
			DatagramPacket packet = new DatagramPacket(out, out.length);
			socket.setSoTimeout(500); //one half second timeout
			socket.receive(packet);
			
			return packet.getData();
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
		catch (SocketTimeoutException e)
		{
			System.err.println("Socket Timeout! Is the server offline?");
			//System.exit(1);
			// throw exception
		}
		catch (UnknownHostException e)
		{
			System.err.println("Unknown host!");
			e.printStackTrace();
			//System.exit(1);
			// throw exception
		}
		catch (Exception e) //any other exceptions that may occur
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	private int generateSessionID()
	{
		/*
		 * Can be anything, so we'll just use 1 for now. Apparently it can be omitted altogether.
		 * TODO: increment each time, or use a random int
		 */
		return 1;
	}
	
	@Override
	public void finalize()
	{
		socket.close();
	}
	
	//debug
	static void printBytes(byte[] arr)
	{
		for(byte b : arr) System.out.print(b + " ");
		System.out.println();
	}
	static void printHex(byte[] arr)
	{
		System.out.println(toHex(arr));
	}
	static String toHex(byte[] b)
	{
		String out = "";
		for(byte bb : b)
		{
			out += String.format("%02X ", bb);
		}
		return out;
	}
	
	//Testing
	public static void main(String args[])
	{
		MCQuery mc = new MCQuery();
		
		System.out.println(mc.basicStat().toString());
		System.out.println(mc.basicStat().asJSON());
		System.out.println("=====================");
		System.out.println(mc.fullStat().toString());
		System.out.println(mc.fullStat().asJSON());
	}
}
