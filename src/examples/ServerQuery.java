package examples;
import query.*;

public class ServerQuery
{
	public static void main(String[] args)
	{
		MCQuery query = new MCQuery();
		String status;
		
		//basic status
		status = query.basicStat().toString();
		System.out.println(status);
		
		//full status
		status = query.fullStat().toString();
		System.out.println(status);
		
		//getting the result as a json string
		status = query.basicStat().asJSON();
		System.out.println(status);
		
		//getting full status as a json string
		status = query.fullStat().asJSON();
		System.out.println(status);
	}
}
