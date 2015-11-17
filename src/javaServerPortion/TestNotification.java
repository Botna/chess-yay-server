package javaServerPortion;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;



public class TestNotification
{
	
	public static void main(String[] args)
	{
		String message ="Fixed my shit! Update!";
	
		ServerPusher.pushMessageNotification("neil","Chess2", message);
		ServerPusher.pushMessageNotification("matt","Chess2", message);
	ServerPusher.pushMessageNotification("dave","Chess2", message);
		   ServerPusher.pushMessageNotification("andrew","Chess2", message);
		   ServerPusher.pushMessageNotification("jeff","Chess2", message);
		  ServerPusher.pushMessageNotification("jimbob999","Chess2", message);
		
		
		
	}
	
	
}