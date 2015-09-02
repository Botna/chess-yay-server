package javaServerPortion;

import java.io.*;
import java.net.*;

import chess2.source.GameBoard;
import chess2.source.GameContainer;
import chess2.source.PlayerRecord;
import chess2.source.SingleSpace;


import java.net.Socket;
import java.util.UUID;

import serverClasses.FileClass;

public class backupChessThread extends Thread {
//	private Socket serverSocket = null;
//	private String userName = "Unknown";
//	private final String INITIAL = "INITIAL";
//	private final String LOGGED = "LOGGED";
//	protected static PlayerRecord[] players;// = new PlayerRecord[10];
//	protected static GameContainer[] currentGames;
//	protected GameBoard myGameBoard;
//
//	protected GameContainer myGameContainer;
//	private static String state;
//	private FileClass theFiles = new FileClass();
//	public backupChessThread(Socket socket) {
//		super("FinalChessThread");
//		this.serverSocket = socket;
//	}
//
//	public void run() {
//
//		GameContainer[] myGames = null;
//		GameBoard returnGameBoard = null;
//
//		try 
//		{
//			ObjectInputStream in = new ObjectInputStream(serverSocket.getInputStream());
//			//DataOutputStream out =
//			//new DataOutputStream(serverSocket.getOutputStream());
//			ObjectOutputStream out = new ObjectOutputStream(serverSocket.getOutputStream());
//			System.out.println("Thread Created/Sockets set up.  Waiting");
//			String packet = null;
//			String returnPacket = null;
//
//			state = INITIAL;
//			while (state.equals(INITIAL))
//			{
//				packet = (String) in.readObject();
//
//
//
//				if(packet != null)
//				{
//					switch(packet.charAt(0))
//					{
//					case '0':
//						//New User Request
//						//packet format = 0:UserName:Password
//
//						returnPacket = createNewUser(packet);
//						//reponse format = 0:0:X for Successes with messages
//						//				   0:1:X for Failures with messages.
//						break;
//					case '1':
//						//Login Request
//						//packet format = 1:Username:Password
//						returnPacket = login(packet);
//						//response format = 1:0:X for success with messages
//						//					1:1:X for wrong password
//						//					1:2:X for user doesn't exist.
//
//					}
//
//				}
//
//				out.writeObject(returnPacket);
//
//			}
//
//			//now that we have left this, we should be logged in. 
//			//loop at this state till they do something else.
//
//			while (state.equals(LOGGED))
//			{
//				packet = (String) in.readObject();
//
//
//
//				if(packet != null)
//				{
//					switch(packet.charAt(0))
//					{
//					case '2':
//						//Current Games
//						//packet format = 2:UserName
//						myGames = currentGames(packet);
//
//						//reponse format = 2:number of games:game1:game2:etc.
//
//						break;
//					case '3':
//						//Player Request
//						//packet format = 1:Username:Password
//						//returnPacket = login(packet);
//						//response format = 1:0:X for success with messages
//						//					1:1:X for wrong password
//						//					1:2:X for user doesn't exist.
//						break;
//
//					case '4':
//						//Create New Game
//						//packet format = 4:myName:opponent:myclass
//						returnPacket = createNewGame(packet);
//
//						//response = 4:0:X Success + message
//						//		     4:1:X Failure + message
//
//						break;
//					case '5':
//						//Send Game State
//						//packet format = 5:myName:GUID
//						returnGameBoard = sendGame(packet);
//						myGameBoard = returnGameBoard; // and it shoudl be for a while;
//
//						break;
//					case '6':
//						//Make Move
//						//packet format = 5:myName:GUID:oldX:oldY:newX:newY:optionalX:optionalY
//						returnPacket = makeMove(packet);
//
//						break;
//					case '7':
//						//Update Black class
//						myGameBoard = updateBlack(packet);
//						returnGameBoard = myGameBoard;
//						break;
//					case '8':
//						//start Skirmish
//						//packet format = 8:myName:GUID:wager
//						//returnPacket = enactSkirmish(packet);
//						returnPacket = startSkirmish(packet);
//						break;
//					case '9':
//
//						//enact skirmish
//						returnPacket = enactSkirmish(packet);
//
//					}
//
//				}
//				if(myGames != null)
//				{	
//
//					out.writeObject(myGames);
//					myGames = null; //this is necessary to get us back to sending STRINGS
//
//				}
//				else if (returnGameBoard != null)
//				{
//					out.writeObject(returnGameBoard);
//					returnGameBoard = null;
//				}
//				else
//				{
//					//not a 'send game' option, so just send back the object.
//					out.writeObject(returnPacket);
//				}
//			}
//		} 
//
//		catch (Exception e) {
//
//			System.out.println(userName + " disconnected");
//		}
//
//
//
//	}
//
//
//
//	public void loadPlayers()
//	{
//		players = theFiles.loadPlayers();
//	}
//
//	public void loadGames()
//	{
//		currentGames = theFiles.loadGames();
//	}
//
//
//	public String createNewUser(String packet)
//	{
//
//		//need to read the playerList file.
//		loadPlayers();
//
//		String returnPacket = null;
//		String[] splitString = packet.split(":");
//		boolean found = false;
//		int numberPlayers = 0;
//		String returnString = "";
//		String temp = "";
//
//		System.out.println("Registration request for user: " + splitString[1]);
//		//check that user doesn't already exist.
//		if( players != null)
//		{
//
//
//			for(int i = 0; i< players.length; i++)
//			{
//				if(players[i] != null)
//				{
//					numberPlayers++;
//					if( players[i].getPlayerName().equals(splitString[1]))
//					{
//						found = true;
//						i= players.length;
//						returnString = "0:1:User Already Exists";
//						System.out.println(splitString[1] + " already exists as a player");
//						returnPacket = "0:1:User Already Exists.";
//					}
//				}
//			}
//		}
//		if(!found)
//		{
//
//
//
//			System.out.println("Created user " + splitString[1]);
//			PlayerRecord newPlayer = new PlayerRecord(splitString[1], splitString[2]);
//			//send it to FileClass to 
//			theFiles.writeNewPlayer(newPlayer);
//			returnPacket = "0:0:User Created";
//		}
//		return returnPacket;
//	}
//
//	public String login(String packet)
//	{
//		loadPlayers();
//		String[] splitString = packet.split(":");
//		boolean found = false;
//		int position = -1;
//		PlayerRecord me = null;
//		String returnString = "";
//		System.out.println(splitString[1] + " logging in");
//		//make sure user exists.
//		if(players != null)
//		{
//			for(int i = 0; i< players.length; i++)
//			{
//				if(players[i] != null)
//				{
//
//					if( players[i].getPlayerName().equals(splitString[1]))
//					{
//						found = true;
//						position = i;
//						i= players.length;
//
//
//					}
//				}
//			}
//		}
//		if(found)
//		{
//			if(players[position].getPassword().equals(splitString[2])){
//				returnString = "1:0:Logged In";
//				System.out.println(splitString[1] + " logged in succesfully.");
//
//				me = players[position];
//				userName = players[position].getPlayerName();
//				//update state to break first portion of thread.
//				state = LOGGED;
//			}
//			else
//			{
//				//password incorrect.
//				returnString = "1:1:Wrong Password";
//				System.out.println(splitString[1] + " didn't log in: wrong password.");
//			}
//		}
//		else
//		{
//			//user didn't exist, so you cant log in, you fool.
//			returnString = "1:2:User Doesn't Exist";
//			System.out.println(splitString[1] + " didn't log in: hasnt been created yet.");
//		}
//		
//		
//		if(me != null)
//		{
//			//succesfully logged in.  need to update the registration ID
//			me.setRegID(splitString[3]);
//			theFiles.updatePlayer(me);
//		}
//		return returnString;
//
//	}
//
//	public GameContainer[] currentGames(String packet)
//	{
//
//		loadGames();
//
//
//		String[] splitString = packet.split(":");
//		GameContainer[] temp = null;
//		GameContainer[] myGames = new GameContainer[1];
//		int games = 0;
//		System.out.println("Sending current games for player: " + splitString[1]);
//
//		if (currentGames == null)
//		{
//			return new GameContainer[1];
//		}
//		for(int i = 0; i < currentGames.length; i++)
//		{
//			if(currentGames[i].getBlackTeam().equals(splitString[1]) || currentGames[i].getWhiteTeam().equals(splitString[1]))
//			{
//				if( games == myGames.length)
//				{
//					//double the array.
//					temp = new GameContainer[games*2];
//
//					for(int j = 0; j< games; j++)
//					{
//						temp[j] = myGames[j];
//					}
//					myGames = null;
//					myGames = temp;
//				}
//				myGames[games] = currentGames[i];
//				games++;
//			}
//		}
//
//		return myGames;
//	}
//
//	public String createNewGame(String packet) throws Exception
//	{
//		loadPlayers();
//		loadGames();
//		String[] data = packet.split(":");
//		boolean found = false;
//		UUID newGuid = null;
//		 
//
//		//make sure opponent exists
//		if(data[1].equals(data[2]))
//		{
//			return "4:1:Don't play with yourself";
//		}
//		for(int i = 0; i < players.length; i++)
//		{
//
//			if(players[i].getPlayerName().equals(data[2]))
//			{
//				found = true;
//			}	
//		}
//
//		if( ! found)
//		{
//			return "4:1:Player Doesn't Exist";
//		}
//
//		//player found, create the game.
//		int tries = 0;
//		while(true) //after 5 failed guids, we have a problem.
//		{
//			newGuid = UUID.randomUUID();
//			found = false;
//
//			if(currentGames != null)
//			{
//				for(int i = 0; i< currentGames.length; i++)
//				{
//					if(currentGames[i].getGuid() == newGuid)
//					{
//						found = true;
//						i = currentGames.length;
//					}
//				}
//				if (tries > 5 && found == true)
//				{
//					System.out.println("Problm in UUID generation create new game");
//					throw new Exception("Problem in UUID generation create new game");
//				}
//				else if(found == false)
//				{
//					break;
//				}
//			}
//			else
//			{
//				break;
//			}
//		}
//
//		GameContainer newGame = new GameContainer(data[1], data[2], data[3].charAt(0), newGuid);
//
//
//		theFiles.writeNewGame(newGame);
//		System.out.println("Creating New Game: " + newGuid.toString() + " " + data[1] + " vs " + data[2]);
//		System.out.println("Informing " + data[2] + " that they have a new game!");
//		ServerPusher.pushNotication(data[2], "New Game", data[1] + " has challenged you to a game!");
//		String returnPacket = "4:0:Game Succesfully Created";
//		//if we get here, our UUID is ok.
//		return returnPacket;
//	}
//
//	public GameBoard sendGame(String packet)
//	{
//		String[] split = packet.split(":");
//		System.out.println(split[1] + " just requested game " + split[2]);
//		boolean found = false;
//		GameContainer currentGame = null;
//		//verify it exists before we try to open a file that doesnt exist.
//		for(int i = 0; i<currentGames.length; i++)
//		{
//			if( currentGames[i].getGuid().toString().equals(split[2]))
//			{
//				//gameFound.
//				System.out.println("Game found: It is " + currentGames[i].getWhiteTeam() + " vs " + currentGames[i].getBlackTeam());
//				found = true;
//				currentGame = currentGames[i];
//				i = currentGames.length;
//			}
//		}
//
//
//
//		if(found)
//		{
//			//this should be our currentGame for awhile
//			myGameContainer = currentGame;
//
//			return theFiles.loadSingleGame(currentGame);
//		}
//		else
//			return null;
//	}
//
//	public GameBoard updateBlack(String packet) throws Exception
//	{
//		String[] split = packet.split(":");
//		System.out.println("Black wants to update game " + split[1] + " with variant " + split[2]);
//		boolean found = false;
//		GameContainer currentGame = null;
//		//verify it exists before we try to open a file that doesnt exist.
//		for(int i = 0; i<currentGames.length; i++)
//		{
//			if( currentGames[i].getGuid().toString().equals(split[1]))
//			{
//				//gameFound.
//				System.out.println("Game found: It is " + currentGames[i].getWhiteTeam() + " vs " + currentGames[i].getBlackTeam());
//				found = true;
//				currentGame = currentGames[i];
//				i = currentGames.length;
//			}
//		}
//
//
//
//		if(found)
//		{
//			currentGame.setBlackVar(split[2].charAt(0));
//			GameBoard temp = theFiles.loadSingleGame(currentGame);
//			temp.updateBlackVariant(split[2].charAt(0));
//			ServerPusher.pushNotication(currentGame.getWhiteTeam(), "Ready to Play", currentGame.getBlackTeam() + " is ready to play!");
//			theFiles.updateGame(currentGame, temp);
//			myGameContainer = currentGame;
//			return theFiles.loadSingleGame(currentGame);
//		}
//		else
//			return null;
//
//
//
//	}
//
//	public String makeMove(String packet) 
//	{
//		//Dobule check we are operating on the right Game
//		String[] split = packet.split(":");
//		System.out.println(split[1] + " just made a move on game " + split[2]);
//		if (!split[2].equals(myGameBoard.getGuid().toString()))
//		{
//			System.out.println("Game error..check Make Move");
//			return split[0] + ":" + "error";
//		}
//		//lump the move back into an int[] for easy usage here and in causesCheck()
//		int count = 0;
//		int[] move = new int[split.length - 3];
//		for(int i = 3; i < split.length; i++)
//		{
//			move[count] = Integer.parseInt(split[i]);
//			count++;
//		}
//
//		//if(myGameBoard.causesCheck(move, myGameBoard.getTheGame()[move[0]][move[1]].getContents().getTeam() ) )
//		//{
//		//just put myself in check.  Return error message
//		//System.out.println(split[1] + "'s move just resulted in putting themselves in check");
//		//System.out.println("investigate this..shouldnt have been able to be sent via client");
//		//	return split[0] + ":" + "Cant move into check";
//		//}
//
//
//		//only do this if its my turn
//		boolean proceed = false;
//		if(myGameBoard.getWhoseTurn().equals(myGameBoard.getWhiteTeam())){
//			if(myGameBoard.getTheGame()[move[1]][move[0]].getContents().getTeam() == 'w')
//				proceed = true;
//			else 
//				proceed = false;
//		}
//		else
//		{
//			if(myGameBoard.getTheGame()[move[1]][move[0]].getContents().getTeam() == 'b')
//				proceed = true;
//			else 
//				proceed = false;
//		}
//
//		if(proceed)
//		{
//			try
//			{
//				myGameBoard.makeMove(move[0], move[1], move[2], move[3]);
//
//
//				if (move.length > 4)
//				{
//					//second move for 2 kings.
//					myGameBoard.makeMove(move[4], move[5], move[6], move[7]);
//				}
//
//			} catch (Exception e) {
//				System.out.println(e.getMessage());
//			}
//
//			myGameContainer.switchTurn();
//			myGameBoard.switchTurn();
//			
//			ServerPusher.pushNotication(myGameBoard.getWhoseTurn(), "Your Move", "A game is waiting on you...");
//			
//			String winner = "";
//			myGameBoard.determineCheck();
//			myGameContainer.setBlackCheck(myGameBoard.isBlackCheck());
//			myGameContainer.setWhiteCheck(myGameBoard.isWhiteCheck());
//			myGameContainer.setPromptDefense(myGameBoard.isPromptDefense());
//			myGameContainer.setPromptOffense(myGameBoard.isPromptOffense());
//			winner = myGameBoard.gameIsOver();
//			
//			if(!myGameBoard.isBlackCheck() && myGameBoard.blackMidline())
//			{
//				winner = myGameBoard.getBlackTeam();
//			}
//			if(!myGameBoard.isWhiteCheck() && myGameBoard.whiteMidline())
//			{
//				winner = myGameBoard.getWhiteTeam();
//			}
//			if(winner != null)
//			{
//				myGameBoard.setWinner(winner);
//				myGameBoard.setPlayable(false);
//				myGameContainer.setWinner(winner);
//
//				System.out.println(winner + " just won game " + split[2]);
//			}
//			//update gameBoard and gameContainer by switching them around. 
//			theFiles.updateGame(myGameContainer, myGameBoard);
//			return split[0] + ":" + "Move sent and confirmed";
//		}
//		else
//			return split[0] + ":" + "Cant Move: Not your turn";
//		
//		
//			
//		
//	}
//
//
//	private String startSkirmish(String packet)
//	{
//		String[] split = packet.split(":");
//		System.out.println(split[1] + " just enacted a skirmish " + split[2]);
//		System.out.println("They bet:" + split[3]);
//		System.out.println("Awaiting attacker's response.");
//		//need to send a notificatoin to the opposite of whose turn it is
//		if(split[1].equals(myGameBoard.getWhiteTeam()))
//		{
//			ServerPusher.pushNotication(myGameBoard.getBlackTeam(), "Skirmish", myGameBoard.getWhiteTeam() + " has challenged your move!");
//		}
//		else
//		{
//			ServerPusher.pushNotication(myGameBoard.getWhiteTeam(), "Skirmish", myGameBoard.getBlackTeam() + " has challenged your move!");
//		}
//		ServerPusher.pushNotication(myGameBoard.getWhoseTurn(), "Your Move", "A game is waiting on you...");
//		myGameBoard.setInsurance(Integer.parseInt(split[3]));
//		myGameBoard.setPromptDefense(false);
//		myGameBoard.setPromptOffense(true);
//		myGameContainer.setPromptDefense(false);
//		myGameContainer.setPromptOffense(true);
//		theFiles.updateGame(myGameContainer, myGameBoard);
//		return  split[0] + ":" + "Wager Sent";
//	}
//	private String enactSkirmish(String packet) {
//
//		String[] split = packet.split(":");
//		System.out.println(split[1] + " just finished a skirmish " + split[2]);
//		System.out.println("They bet:" + split[3]);
//		int row = 0, col = 0;
//		String message;
//
//		//find the lastMove highlighted enemy piece. 
//		for(int i = 0; i <8; i++)
//		{
//			for(int j = 0; j<8; j++)
//			{
//				if(myGameBoard.getTheGame()[i][j].getLastMove() 
//						&& myGameBoard.getTheGame()[i][j].getContents().getContents() != '-'
//						&& myGameBoard.getTheGame()[i][j].getContents().getContents() != 'x')
//				{
//					row = i;
//					col = j;
//				}
//			}
//		}
//
//		int payment = 0;
//		if(myGameBoard.getPieceJustTaken().getContents().getTier() < myGameBoard.getTheGame()[row][col].getContents().getTier())
//		{
//			//defender's piece was a lower tier then the attackers.  They must pay an additional token
//			//in order to initiate a skirmish.
//			payment = 1;
//		}
//		if(Integer.parseInt(split[3]) < myGameBoard.getInsurance())
//		{
//			System.out.println("Defender took Attacker's piece");
//			message = "Took Attacking Piece";
//			myGameBoard.getTheGame()[row][col] = new SingleSpace();
//			if(split[1].equals(myGameBoard.getWhiteTeam()))
//			{
//				myGameBoard.setBlackTokens(myGameBoard.getBlackTokens() - myGameBoard.getInsurance()-payment);
//				myGameBoard.setWhiteTokens(myGameBoard.getWhiteTokens() - Integer.parseInt(split[3]));
//			}
//
//			else
//			{
//				myGameBoard.setBlackTokens(myGameBoard.getBlackTokens() - Integer.parseInt(split[3]));
//				myGameBoard.setWhiteTokens(myGameBoard.getWhiteTokens() - myGameBoard.getInsurance()-payment);
//			}
//		}
//		else
//		{
//			System.out.println("Attacker lived");
//			message = "Attacking piece survived";
//			if(split[1].equals(myGameBoard.getWhiteTeam()))
//			{
//				myGameBoard.setBlackTokens(myGameBoard.getBlackTokens() - myGameBoard.getInsurance()- payment);
//				myGameBoard.setWhiteTokens(myGameBoard.getWhiteTokens() - Integer.parseInt(split[3]));
//			}
//			else
//			{
//				myGameBoard.setBlackTokens(myGameBoard.getBlackTokens() - Integer.parseInt(split[3]));
//				myGameBoard.setWhiteTokens(myGameBoard.getWhiteTokens() - myGameBoard.getInsurance()- payment);
//
//			}
//		}
//		//now that the skirmish is resolved, clear out the last piece taken
//		//and turn of fthe 'prompt defense flag.
//		myGameBoard.setInsurance(-1);
//		myGameBoard.setPromptOffense(false);
//		myGameBoard.setPieceJustTaken(null);
//		myGameContainer.setPromptOffense(false);
//		
//			ServerPusher.pushNotication(myGameBoard.getWhoseTurn(), "Your Move", "A game is waiting on you...");
//		
//		myGameBoard.determineCheck();
//		myGameContainer.setBlackCheck(myGameBoard.isBlackCheck());
//		myGameContainer.setWhiteCheck(myGameBoard.isWhiteCheck());
//		String winner = myGameBoard.gameIsOver();
//		if(winner != null)
//		{
//			myGameBoard.setWinner(winner);
//			myGameBoard.setPlayable(false);
//			myGameContainer.setWinner(winner);
//
//			System.out.println(winner + " just won game " + split[2]);
//		}
//		//update gameBoard and gameContainer by switching them around. 
//		theFiles.updateGame(myGameContainer, myGameBoard);
//		return split[0] + ":" + message;
//	}


}
