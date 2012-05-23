package query;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class QueryRequest
{
	private ByteArrayOutputStream byteStream;
	private DataOutputStream dataStream;
	
	static byte[] MAGIC = {(byte) 0xFE, (byte) 0xFD};
	byte type;
	int sessionID;
	byte[] payload;
	
	public QueryRequest()
	{
		int size = 1460;
		byteStream = new ByteArrayOutputStream(size);
		dataStream = new DataOutputStream(byteStream);
	}
	public QueryRequest(byte type)
	{
		this.type = type;
		//TODO move static type variables to Request
	}
	
	//convert the data in this request to a byte array to send to the server
	byte[] toBytes()
	{
		byteStream.reset();
		
		try
		{
			dataStream.write(MAGIC);
			dataStream.write(type);
			dataStream.writeInt(sessionID);
			dataStream.write(payloadBytes());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return byteStream.toByteArray();
	}
	
	private byte[] payloadBytes()
	{
		if(type == MCQuery.HANDSHAKE)
		{
			return new byte[]{}; //return empty byte array
		}
		else //(type == MCQuery.STAT)
		{
			return payload;
		}
	}
	
	protected void setPayload(int load)
	{
		this.payload = ByteUtils.intToBytes(load);
	}
}
