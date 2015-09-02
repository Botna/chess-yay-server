package javaServerPortion;


import java.io.IOException;
import java.util.UUID;

import serverClasses.FileClass;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;



import chess2.source.PlayerRecord;

public class ServerPusher
{


	//private final static String serverAPI = "AIzaSyCg7A1Eyl8QbYeh6i9FjDalYsPKCjZ4tRo";



	public static void pushMessageNotification(String player, String title, String message){


		//have to find the player and get thier REGID for the POST request.
		System.out.println("Just sent notification of: " + player + "/" + title + "/" + message);
		
		FileClass theFiles = new FileClass();
		PlayerRecord[] players = theFiles.loadPlayers();
		String regid = null;
		for(int i = 0; i < players.length; i++)
		{
			if(players[i].getPlayerName().equals(player))
			{
				//found it.
				regid = players[i].getRegID();
				i = players.length;
			}
		}
		if(regid != null)
		{

			Message temp = new Message.Builder()
			.addData("title", title)
			.addData("message", message)
			.build();

			Sender sender = new Sender("AIzaSyCg7A1Eyl8QbYeh6i9FjDalYsPKCjZ4tRo");
			try {
				Result result = sender.send(temp, regid, 2);
				String error = result.getErrorCodeName();
				if(error != null)
				{
					System.out.println(result.toString());
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}	
	}
	
	
	public static void pushGameNotification(String player, String title, String message, UUID game)
	{

		//have to find the player and get thier REGID for the POST request.
		System.out.println("Just sent notification of: " + player + "/" + title + "/" + message);
		
		FileClass theFiles = new FileClass();
		PlayerRecord[] players = theFiles.loadPlayers();
		String regid = null;
		for(int i = 0; i < players.length; i++)
		{
			if(players[i].getPlayerName().equals(player))
			{
				//found it.
				regid = players[i].getRegID();
				i = players.length;
			}
		}
		if(regid != null)
		{

			Message temp = new Message.Builder()
			.addData("title", title)
			.addData("message", message)
			.addData("guid", game.toString())
			.build();

			Sender sender = new Sender("AIzaSyCg7A1Eyl8QbYeh6i9FjDalYsPKCjZ4tRo");
			try {
				Result result = sender.send(temp, regid, 2);
				String error = result.getErrorCodeName();
				if(error != null)
				{
					System.out.println(result.toString());
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}	
	}
	
	
}