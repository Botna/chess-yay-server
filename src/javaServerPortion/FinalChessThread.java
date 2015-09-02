package javaServerPortion;

import java.io.*;
import java.net.*;

import chess2.source.GameBoard;
import chess2.source.GameContainer;
import chess2.source.PlayerRecord;
import chess2.source.SingleSpace;


import java.net.Socket;
import java.util.Date;
import java.util.UUID;

import serverClasses.FileClass;

public class FinalChessThread extends Thread {

	private Socket serverSocket = null;

	private final String LOGGED = "LOGGED";
	protected static PlayerRecord[] players;// = new PlayerRecord[10];
	protected static GameContainer[] currentGames;
	protected GameBoard myGameBoard;
	protected GameContainer myGameContainer;
	private FileClass theFiles = new FileClass();
	public FinalChessThread(Socket socket) {
		super("FinalChessThread");
		this.serverSocket = socket;
	}

	public void run() {

		GameContainer[] myGames = null;
		GameBoard returnGameBoard = null;

		try 
		{
			ObjectInputStream in = new ObjectInputStream(serverSocket.getInputStream());
			//DataOutputStream out =
			//new DataOutputStream(serverSocket.getOutputStream());
			ObjectOutputStream out = new ObjectOutputStream(serverSocket.getOutputStream());
			//System.out.println("Thread Created/Sockets set up.  Waiting");
			String packet = null;
			String returnPacket = null;

			packet = (String) in.readObject();



			if(packet != null)
			{
				if(packet.charAt(0) == '0')
				{

					//New User Request
					//packet format = 0:UserName:Password

					returnPacket = createNewUser(packet);
					//reponse format = 0:0:X for Successes with messages
					//				   0:1:X for Failures with messages.
				}
				else
				{	
					//not a registration, lets make sure the user actually exists.
					loadPlayers();
					boolean found = false;
					String[] data = packet.split(":");
					//make sure user exists and password is correct
					if(players != null)
					{
						for(int i = 0; i< players.length; i++)
						{
							if(players[i] != null)
							{

								if( players[i].getPlayerName().equals(data[1]) && players[i].getPassword().equals(data[2]))
								{
									found = true;

									i= players.length;
								}
							}
						}
						if(!found)
						{
							System.out.println("shits fucked");
							returnPacket= data[0]+":" + "Invalid Packet";

						}
						else
						{
							switch(packet.charAt(0))
							{
							case '1':
								//Login Request
								//packet format = 1:Username:Password
								returnPacket = login(packet);
								//response format = 1:0:X for success with messages
								//					1:1:X for wrong password
								//					1:2:X for user doesn't exist.
								break;
							case '2':
								//Current Games
								//packet format = 2:UserName
								myGames = currentGames(packet);

								//reponse format = 2:number of games:game1:game2:etc.

								break;
							case '3':
								//Player Request
								//packet format = 1:Username:Password
								//returnPacket = login(packet);
								//response format = 1:0:X for success with messages
								//					1:1:X for wrong password
								//					1:2:X for user doesn't exist.
								break;

							case '4':
								//Create New Game
								//packet format = 4:myName:opponent:myclass
								returnPacket = createNewGame(packet);

								//response = 4:0:X Success + message
								//		     4:1:X Failure + message

								break;
							case '5':
								//Send Game State
								//packet format = 5:myName:GUID
								returnGameBoard = sendGame(packet);
								myGameBoard = returnGameBoard; // and it shoudl be for a while;

								break;
							case '6':
								//Make Move
								//packet format = 5:myName:GUID:oldX:oldY:newX:newY:optionalX:optionalY
								returnPacket = makeMove(packet);
								break;
							case '7':
								//Update Black class
								myGameBoard = updateBlack(packet);
								returnGameBoard = myGameBoard;
								break;
							case '8':
								//start Skirmish
								//packet format = 8:myName:GUID:wager
								//returnPacket = enactSkirmish(packet);
								returnPacket = startSkirmish(packet);
								break;
							case '9':

								//enact skirmish
								returnPacket = enactSkirmish(packet);
								break;
							case 'A':
								//forfeit a game
								returnPacket = forfeitGame(packet);
								break;
							case 'B':
								//move with a pawn promotion as well
								returnPacket = promoteMove(packet);
								break;
							}
						}
					}

				}

				if(myGames != null)
				{	

					out.writeObject(myGames);
					myGames = null; //this is necessary to get us back to sending STRINGS

				}
				else if (returnGameBoard != null)
				{
					out.writeObject(returnGameBoard);
					returnGameBoard = null;
				}
				else
				{
					//not a 'send game' option, so just send back the object.
					out.writeObject(returnPacket);
				}

			}
			in.close();
			out.close();
			serverSocket.close();
		}

		catch (Exception e) {

			e.printStackTrace();

		}





	}





	public void loadPlayers()
	{
		players = theFiles.loadPlayers();
	}

	public void loadGames()
	{
		currentGames = theFiles.loadGames();
	}


	public String createNewUser(String packet)
	{

		//need to read the playerList file.
		loadPlayers();

		String returnPacket = null;
		String[] splitString = packet.split(":");
		boolean found = false;



		System.out.println("Registration request for user: " + splitString[1]);
		//check that user doesn't already exist.
		if( players != null)
		{


			for(int i = 0; i< players.length; i++)
			{
				if(players[i] != null)
				{

					if( players[i].getPlayerName().equals(splitString[1]))
					{
						found = true;
						i= players.length;
						System.out.println(splitString[1] + " already exists as a player");
						returnPacket = "0:1:User Already Exists.";
					}
				}
			}
		}
		if(!found)
		{



			System.out.println("Created user " + splitString[1]);
			PlayerRecord newPlayer = new PlayerRecord(splitString[1], splitString[2]);
			//send it to FileClass to 
			theFiles.writeNewPlayer(newPlayer);
			returnPacket = "0:0:User Created";
		}
		return returnPacket;
	}

	public String login(String packet)
	{
		loadPlayers();
		String[] splitString = packet.split(":");
		boolean found = false;
		int position = -1;
		PlayerRecord me = null;
		String returnString = "";
		//System.out.println(splitString[1] + " logging in");
		//make sure user exists.
		if(players != null)
		{
			for(int i = 0; i< players.length; i++)
			{
				if(players[i] != null)
				{

					if( players[i].getPlayerName().equals(splitString[1]))
					{
						found = true;
						position = i;
						i= players.length;


					}
				}
			}
		}
		if(found)
		{
			if(players[position].getPassword().equals(splitString[2])){
				returnString = "1:0:Logged In";
				System.out.println(splitString[1] + " just checked in.");

				me = players[position];
				//update state to break first portion of thread.
			}
			else
			{
				//password incorrect.
				returnString = "1:1:Wrong Password";
				System.out.println(splitString[1] + " didn't log in: wrong password.");
			}
		}
		else
		{
			//user didn't exist, so you cant log in, you fool.
			returnString = "1:2:User Doesn't Exist";
			System.out.println(splitString[1] + " didn't log in: hasnt been created yet.");
		}


		if(me != null)
		{
			//succesfully logged in.  need to update the registration ID
			me.setRegID(splitString[3]);
			theFiles.updatePlayer(me);
		}
		return returnString;

	}

	public GameContainer[] currentGames(String packet)
	{

		loadGames();
		String[] splitString = packet.split(":");
		int count = 0;
		GameContainer[] myGames = new GameContainer[1];

		int games = 0;
		System.out.println("Sending current games for player: " + splitString[1]);

		if (currentGames == null)
		{
			return new GameContainer[1];
		}
		for(int i = 0; i < currentGames.length; i++)
		{
			if(currentGames[i].getBlackTeam().equals(splitString[1]) || currentGames[i].getWhiteTeam().equals(splitString[1]))
			{
				count++;


			}
		}
		myGames = new GameContainer[count];
		int index = 0;
		for(int i = 0; i < currentGames.length; i++)
		{
			if(currentGames[i].getBlackTeam().equals(splitString[1]) || currentGames[i].getWhiteTeam().equals(splitString[1]))
			{
				myGames[index] = currentGames[i];
				index++;

			}
		}

		GameContainer temp = null;


		//create a list of 'myturns' since we dont want to influence the myTurn on the gameboard yet, and myturn isnt always accurate.
		


		for(int i = 0; i <= myGames.length; i++)
		{
//			boolean[] myTurn = new boolean[myGames.length];
//
//			for(int k =0; k<myGames.length;k++)
//			{
//				myTurn[k] = false;
//				if((myGames[k].getBlackVar() <65 || myGames[k].getBlackVar() > 122 )&& myGames[k].getBlackTeam().equals(splitString[1]))
//						{
//					myTurn[k] = true;
//						}
//				else if(myGames[k].getTurn().equals(splitString[1]))
//				{
//					myTurn[k] = true;
//				}
//			}
			
			
			for(int j = 0; j<myGames.length; j++)
			{

				if(j+1 < myGames.length)
				{
					if(myGames[j+1].getTimeEnd() != null && myGames[j].getTimeEnd() != null)
					{
						if(myGames[j].getTimeEnd().before(myGames[j+1].getTimeEnd()))
						{
							//both finished and out of order, flip em.
							temp = myGames[j];
							myGames[j] = myGames[j+1];
							myGames[j+1] = temp;
						}

					}
					else 
					{


						if(myGames[j].getTimeEnd() != null)
						{
							//first one is finished, but second isnt.  Flip em
							temp = myGames[j];
							myGames[j] = myGames[j+1];
							myGames[j+1] = temp;
						}
						else if(myGames[j].getTimeEnd() == null && myGames[j+1].getTimeEnd() == null)
						{
							

							if(!myGames[j].getTurn().equals(splitString[1]) && myGames[j+1].getTurn().equals(splitString[1]))
							//if(!myTurn[j] && myTurn[j+1])
							{
								//first game it isnt my turn, but the second game it is.  just flip them.
								temp = myGames[j];
								myGames[j] = myGames[j+1];
								myGames[j+1] = temp;
							}
							else if(myGames[j].getTurn().equals(splitString[1]) && myGames[j+1].getTurn().equals(splitString[1])
									||
									!myGames[j].getTurn().equals(splitString[1]) && !myGames[j+1].getTurn().equals(splitString[1]))
							{
								//games are the 'same' so it comes down to date.
								if(myGames[j].getLastPlay().before(myGames[j+1].getLastPlay()))
								{

									temp = myGames[j];
									myGames[j] = myGames[j+1];
									myGames[j+1] = temp;

								}
							}

						}
					}
				}
			}
		}

		//re-order the games 
		return myGames;

	}

	public String createNewGame(String packet) throws Exception
	{
		loadPlayers();
		loadGames();
		String[] data = packet.split(":");
		boolean found = false;
		UUID newGuid = null;

		//make sure opponent exists
		if(data[1].equals(data[3]))
		{
			return "4:1:Don't play with yourself";
		}
		for(int i = 0; i < players.length; i++)
		{
			if(players[i].getPlayerName().equals(data[3]))
			{
				found = true;
			}	
		}

		if( ! found)
		{
			return "4:1:Player Doesn't Exist";
		}

		//player found, create the game.
		int tries = 0;
		while(true) //after 5 failed guids, we have a problem.
		{
			newGuid = UUID.randomUUID();
			found = false;

			if(currentGames != null)
			{
				for(int i = 0; i< currentGames.length; i++)
				{
					if(currentGames[i].getGuid() == newGuid)
					{
						found = true;
						i = currentGames.length;
					}
				}
				if (tries > 5 && found == true)
				{
					System.out.println("Problm in UUID generation create new game");
					throw new Exception("Problem in UUID generation create new game");
				}
				else if(found == false)
				{
					break;
				}
			}
			else
			{
				break;
			}
		}

		GameContainer newGame = new GameContainer(data[1], data[3], data[4].charAt(0), newGuid);


		theFiles.writeNewGame(newGame);
		System.out.println("Creating New Game: " + newGuid.toString() + " " + data[1] + " vs " + data[2]);
		System.out.println("Informing " + data[3] + " that they have a new game!");
		ServerPusher.pushMessageNotification(data[3], "New Game", data[1] + " has challenged you to a game!");
		String returnPacket = "4:0:Game Succesfully Created";
		//if we get here, our UUID is ok.
		return returnPacket;
	}



	public GameBoard sendGame(String packet)
	{
		loadGames();
		String[] split = packet.split(":");
		System.out.println(split[1] + " just requested game " + split[3]);
		boolean found = false;
		GameContainer currentGame = null;
		//verify it exists before we try to open a file that doesnt exist.
		for(int i = 0; i<currentGames.length; i++)
		{
			if( currentGames[i].getGuid().toString().equals(split[3]))
			{
				//gameFound.
				System.out.println("Game found: It is " + currentGames[i].getWhiteTeam() + " vs " + currentGames[i].getBlackTeam());
				found = true;
				currentGame = currentGames[i];
				i = currentGames.length;
			}
		}



		if(found)
		{
			//this should be our currentGame for awhile
			myGameContainer = currentGame;

			return theFiles.loadSingleGame(currentGame);
		}
		else
			return null;
	}

	public GameBoard updateBlack(String packet) throws Exception
	{
		loadGames();
		String[] split = packet.split(":");
		System.out.println("Black wants to update game " + split[3] + " with variant " + split[4]);
		boolean found = false;
		GameContainer currentGame = null;
		//verify it exists before we try to open a file that doesnt exist.
		for(int i = 0; i<currentGames.length; i++)
		{
			if( currentGames[i].getGuid().toString().equals(split[3]))
			{
				//gameFound.
				System.out.println("Game found: It is " + currentGames[i].getWhiteTeam() + " vs " + currentGames[i].getBlackTeam());
				found = true;
				currentGame = currentGames[i];
				i = currentGames.length;
			}
		}



		if(found)
		{
			currentGame.setBlackVar(split[4].charAt(0));
			GameBoard temp = theFiles.loadSingleGame(currentGame);
			temp.updateBlackVariant(split[4].charAt(0));
			ServerPusher.pushMessageNotification(currentGame.getWhiteTeam(), "Ready to Play", currentGame.getBlackTeam() + " is ready to play!");
			theFiles.updateGame(currentGame, temp);
			myGameContainer = currentGame;
			myGameContainer.setLastPlay( new Date());
			return theFiles.loadSingleGame(currentGame);
		}
		else
			return null;
	}

	public String makeMove(String packet) 
	{
		loadGames();
		//Dobule check we are operating on the right Game
		String[] split = packet.split(":");
		boolean found = false;


		for(int i = 0; i<currentGames.length; i++)
		{
			if( currentGames[i].getGuid().toString().equals(split[3]))
			{
				//gameFound.
				//System.out.println("Game found: It is " + currentGames[i].getWhiteTeam() + " vs " + currentGames[i].getBlackTeam());
				found = true;
				myGameContainer = currentGames[i];
				i = currentGames.length;
			}
		}
		if(found)
		{
			myGameBoard = theFiles.loadSingleGame(myGameContainer);
			System.out.println(split[1] + " just made a move on game " + split[3]);
			//lump the move back into an int[] for easy usage here and in causesCheck()
			int count = 0;
			int[] move = new int[split.length - 4];
			for(int i = 4; i < 12; i++)
			{
				move[count] = Integer.parseInt(split[i]);
				count++;
			}



			//only do this if its my turn
			boolean proceed = false;
			if(myGameBoard.getWhoseTurn().equals(myGameBoard.getWhiteTeam())){
				if(myGameBoard.getTheGame()[move[1]][move[0]].getContents().getTeam() == 'w')
					proceed = true;
				else 
					proceed = false;
			}
			else
			{
				if(myGameBoard.getTheGame()[move[1]][move[0]].getContents().getTeam() == 'b')
					proceed = true;
				else 
					proceed = false;
			}

			if(proceed)
			{

				myGameBoard.clearHighlight();
				//myGameBoard.clearLastMove();
				try
				{
					//we are about to make a move(s).  Reset the 2 prompt Booleans so they dont interfere with anything,
					//especially if a king is moved
					myGameBoard.setPromptDefense(false);
					myGameBoard.setPromptOffense(false);



					myGameBoard.makeMove(move[0], move[1], move[2], move[3]);


					if (move.length > 4)
					{
						//check for validity
						if(move[4] != 9){
							//second move for 2 kings.
							
							//back up the last move and restore it.
							int[][] lastMove = new int[][]{{-1,-1},{-1,-1}};
							
							for (int i = 0; i < 8; i++)
							{
								for(int j = 0; j<8; j++)
								{
									if(myGameBoard.getTheGame()[i][j].getLastMove())
									{
										if(lastMove[0][0] == -1)
										{
											lastMove[0][0] = i;
											lastMove[0][1] = j;
										}
										else
										{
											lastMove[1][0] = i;
											lastMove[1][1] =j;
										}
									}
								}
							}
							myGameBoard.makeMove(move[4], move[5], move[6], move[7]);
							
							
							myGameBoard.getTheGame()[lastMove[0][0]][lastMove[0][1]].setLastMove(true);
							myGameBoard.getTheGame()[lastMove[1][0]][lastMove[1][1]].setLastMove(true);
						}
					}

					if(split[12].charAt(0) != ' ')
					{
						//we have a promotion, lets attempt to promote if its valid.
						myGameBoard.promotePiece(move[2],move[3],split[split.length-1].charAt(0));
					}

				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

				myGameContainer.switchTurn();
				myGameBoard.switchTurn();
				myGameBoard.increaseNumberOfMoves();



				String pushTitle = "Your Move";
				String pushMessage = "A game is waiting on you...";


				String winner = "";
				myGameBoard.determineCheck();
				//myGameBoard.pushTurn();
				myGameContainer.setBlackCheck(myGameBoard.isBlackCheck());
				myGameContainer.setWhiteCheck(myGameBoard.isWhiteCheck());
				myGameContainer.setPromptDefense(myGameBoard.isPromptDefense());
				myGameContainer.setPromptOffense(myGameBoard.isPromptOffense());
				myGameContainer.setLastPlay( new Date());
				winner = myGameBoard.gameIsOver();

				if(!myGameBoard.isBlackCheck() && myGameBoard.blackMidline())
				{
					winner = myGameBoard.getBlackTeam();
				}
				if(!myGameBoard.isWhiteCheck() && myGameBoard.whiteMidline())
				{
					winner = myGameBoard.getWhiteTeam();
				}
				if(winner != null)
				{
					pushTitle = "Game Over";
					pushMessage = "One of your games just ended...";
					myGameBoard.setWinner(winner);
					myGameBoard.setPlayable(false);
					myGameContainer.setWinner(winner);
					myGameContainer.setTimeEnd(new Date());
					System.out.println(winner + " just won game " + split[3]);
				}

				ServerPusher.pushMessageNotification(myGameBoard.getWhoseTurn(), pushTitle, pushMessage);



				//update gameBoard and gameContainer by switching them around. 
				theFiles.updateGame(myGameContainer, myGameBoard);
				return split[0] + ":" + "Move sent and confirmed";
			}
			else
				return split[0] + ":" + "Cant Move...Not your turn";
		}
		return split[0] + ":" + "Game Not Found";
	}


	private String startSkirmish(String packet)
	{
		String[] split = packet.split(":");
		boolean found = false;
		for(int i = 0; i<currentGames.length; i++)
		{
			if( currentGames[i].getGuid().toString().equals(split[3]))
			{
				//gameFound.
				//System.out.println("Game found: It is " + currentGames[i].getWhiteTeam() + " vs " + currentGames[i].getBlackTeam());
				found = true;
				myGameContainer = currentGames[i];
				i = currentGames.length;
			}
		}
		if(found)
		{
			myGameBoard = theFiles.loadSingleGame(myGameContainer);


			System.out.println(split[1] + " just enacted a skirmish " + split[3]);
			System.out.println("They bet:" + split[4]);


			//find out if the enemy even has any tokens.
			//if they dont, then we dont switch turns, we just kill the piece and return control back
			//to the defender
			int enemyTokens = -1;
			if(myGameBoard.getWhiteTeam().equals(split[1]))
			{
				enemyTokens = myGameBoard.getBlackTokens();
			}
			else
				enemyTokens = myGameBoard.getWhiteTokens();

			if(enemyTokens != 0)
			{
				System.out.println("Awaiting attacker's response.");
				//need to send a notificatoin to the opposite of whose turn it is
				if(split[1].equals(myGameBoard.getWhiteTeam()))
				{
					ServerPusher.pushMessageNotification(myGameBoard.getBlackTeam(), "Skirmish", myGameBoard.getWhiteTeam() + " has challenged your move!");
				}
				else
				{
					ServerPusher.pushMessageNotification(myGameBoard.getWhiteTeam(), "Skirmish", myGameBoard.getBlackTeam() + " has challenged your move!");
				}
				//ServerPusher.pushNotication(myGameBoard.getWhoseTurn(), "Your Move", "A game is waiting on you...");
				myGameBoard.setInsurance(Integer.parseInt(split[4]));
				myGameBoard.setPromptDefense(false);
				myGameBoard.setPromptOffense(true);
				myGameBoard.increaseNumberOfMoves();
				myGameContainer.setPromptDefense(false);
				myGameContainer.setLastPlay( new Date());
				myGameContainer.setPromptOffense(true);
				theFiles.updateGame(myGameContainer, myGameBoard);
				return  split[0] + ":" + "Wager Sent";
			}
			else
			{
				System.out.println("Attacker had zero tokens, they automatically lose");
				if(split[1].equals(myGameBoard.getWhiteTeam()))
				{
					ServerPusher.pushMessageNotification(myGameBoard.getBlackTeam(), "Skirmish", myGameBoard.getWhiteTeam() + " has taken your piece!");
				}
				else
				{
					ServerPusher.pushMessageNotification(myGameBoard.getWhiteTeam(), "Skirmish", myGameBoard.getBlackTeam() + " has taken your piece!");
				}
				myGameContainer.setLastPlay( new Date());
				myGameContainer.setPromptDefense(false);
				myGameBoard.setPromptDefense(false);
				myGameBoard.increaseNumberOfMoves();
				int row = -1,col = -1;
				//find the lastMove highlighted enemy piece. 

				boolean isPawn = false;
				for(int i = 0; i <8; i++)
				{
					for(int j = 0; j<8; j++)
					{
						if(myGameBoard.getTheGame()[i][j].getLastMove() 
								&& myGameBoard.getTheGame()[i][j].getContents().getContents() != '-'
								&& myGameBoard.getTheGame()[i][j].getContents().getContents() != 'x')
						{
							row = i;
							col = j;

							if(myGameBoard.getTheGame()[i][j].getContents().getContents() == 'p')
							{
								isPawn = true;
							}

						}
					}
				}

				//remove the necessary tokens from the defender.
				if(myGameBoard.getBlackTeam().equals(split[1]))
				{
					//black loses tokens for the skirmish
					myGameBoard.setBlackTokens(myGameBoard.getBlackTokens() -1);
					if( myGameBoard.getTheGame()[row][col].getContents().getTier() > myGameBoard.getPieceJustTaken().getContents().getTier())
					{
						myGameBoard.setBlackTokens(myGameBoard.getBlackTokens() -1);
					}

					if(isPawn)
					{
						//give them 1 back
						myGameBoard.setBlackTokens(myGameBoard.getBlackTokens() +1);
					}
				}
				else
				{
					//white loses tokens for the skirmish.


					myGameBoard.setWhiteTokens(myGameBoard.getWhiteTokens() -1);
					if( myGameBoard.getTheGame()[row][col].getContents().getTier() > myGameBoard.getPieceJustTaken().getContents().getTier())
					{
						myGameBoard.setWhiteTokens(myGameBoard.getWhiteTokens() -1);
					}

					if(isPawn)
					{
						//give them 1 back
						myGameBoard.setWhiteTokens(myGameBoard.getWhiteTokens() +1);
					}
				}

				myGameBoard.getTheGame()[row][col] = new SingleSpace();
				theFiles.updateGame(myGameContainer, myGameBoard);
				return  split[0] + ":" + "Piece taken by default";
			}

		}
		return split[0] + ":" + "Game Not Found";
	}
	private String enactSkirmish(String packet) {


		String[] split = packet.split(":");

		boolean found = false;
		for(int i = 0; i<currentGames.length; i++)
		{
			if( currentGames[i].getGuid().toString().equals(split[3]))
			{
				//gameFound.
				//System.out.println("Game found: It is " + currentGames[i].getWhiteTeam() + " vs " + currentGames[i].getBlackTeam());
				found = true;
				myGameContainer = currentGames[i];
				i = currentGames.length;
			}
		}
		if(found)
		{
			myGameBoard = theFiles.loadSingleGame(myGameContainer);
			System.out.println(split[1] + " just finished a skirmish " + split[3]);
			System.out.println("They bet:" + split[4]);
			int row = 0, col = 0;
			String message;

			boolean isPawn = false;
			//find the lastMove highlighted enemy piece. 
			for(int i = 0; i <8; i++)
			{
				for(int j = 0; j<8; j++)
				{
					if(myGameBoard.getTheGame()[i][j].getLastMove() 
							&& myGameBoard.getTheGame()[i][j].getContents().getContents() != '-'
							&& myGameBoard.getTheGame()[i][j].getContents().getContents() != 'x')
					{
						row = i;
						col = j;
						if(myGameBoard.getTheGame()[i][j].getContents().getContents() == 'p')
						{
							isPawn = true;
						}
					}
				}
			}

			int payment = 0;

			if(myGameBoard.getPieceJustTaken() == null)
			{
				System.out.println("Piece just taken was null, heres our problems");
				//return "DickButt";
			}
			if(myGameBoard.getPieceJustTaken().getContents().getTier() < myGameBoard.getTheGame()[row][col].getContents().getTier())
			{
				//defender's piece was a lower tier then the attackers.  They must pay an additional token
				//in order to initiate a skirmish.
				payment = 1;
			}
			if(Integer.parseInt(split[4]) < myGameBoard.getInsurance())
			{
				System.out.println("Defender took Attacker's piece");
				message = "Took Attacking Piece";
				myGameBoard.addCaptured(myGameBoard.getTheGame()[row][col].getContents());
				myGameBoard.getTheGame()[row][col] = new SingleSpace();
				myGameBoard.getTheGame()[row][col].setLastMove(true);
				if(split[1].equals(myGameBoard.getWhiteTeam()))
				{
					myGameBoard.setBlackTokens(myGameBoard.getBlackTokens() - myGameBoard.getInsurance()-payment);
					myGameBoard.setWhiteTokens(myGameBoard.getWhiteTokens() - Integer.parseInt(split[4]));
					if(isPawn)
					{
						//give them 1 back
						myGameBoard.setBlackTokens(myGameBoard.getBlackTokens() +1);
					}
				}

				else
				{
					myGameBoard.setBlackTokens(myGameBoard.getBlackTokens() - Integer.parseInt(split[4]));
					myGameBoard.setWhiteTokens(myGameBoard.getWhiteTokens() - myGameBoard.getInsurance()-payment);
					if(isPawn)
					{
						//give them 1 back
						myGameBoard.setWhiteTokens(myGameBoard.getWhiteTokens() +1);
					}
				}
			}
			else
			{
				System.out.println("Attacker lived");
				message = "Attacking piece survived";
				if(split[1].equals(myGameBoard.getWhiteTeam()))
				{
					myGameBoard.setBlackTokens(myGameBoard.getBlackTokens() - myGameBoard.getInsurance()- payment);
					myGameBoard.setWhiteTokens(myGameBoard.getWhiteTokens() - Integer.parseInt(split[4]));
				}
				else
				{
					myGameBoard.setBlackTokens(myGameBoard.getBlackTokens() - Integer.parseInt(split[4]));
					myGameBoard.setWhiteTokens(myGameBoard.getWhiteTokens() - myGameBoard.getInsurance()- payment);

				}
			}
			//now that the skirmish is resolved, clear out the last piece taken
			//and turn of fthe 'prompt defense flag.
			//myGameBoard.pushChallenge(Integer.parseInt(split[4]), myGameBoard.getInsurance());
			myGameBoard.setInsurance(-1);
			myGameBoard.setPromptOffense(false);
			myGameBoard.setPieceJustTaken(null);
			myGameBoard.increaseNumberOfMoves();
			myGameContainer.setPromptOffense(false);

			myGameContainer.setLastPlay( new Date());
			ServerPusher.pushMessageNotification(myGameBoard.getWhoseTurn(), "Your Move", "A game is waiting on you...");

			myGameBoard.determineCheck();
			myGameContainer.setBlackCheck(myGameBoard.isBlackCheck());
			myGameContainer.setWhiteCheck(myGameBoard.isWhiteCheck());
			String winner = myGameBoard.gameIsOver();
			if(winner != null)
			{
				myGameBoard.setWinner(winner);
				myGameBoard.setPlayable(false);
				myGameContainer.setWinner(winner);
				myGameContainer.setTimeEnd(new Date());

				System.out.println(winner + " just won game " + split[3]);
			}
			//update gameBoard and gameContainer by switching them around. 
			theFiles.updateGame(myGameContainer, myGameBoard);
			return split[0] + ":" + message;
		}
		return split[0] + ":Game Not Found";
	}


	private String forfeitGame(String packet) {
		String[] split = packet.split(":");

		boolean found = false;
		for(int i = 0; i<currentGames.length; i++)
		{
			if( currentGames[i].getGuid().toString().equals(split[3]))
			{
				//gameFound.
				//System.out.println("Game found: It is " + currentGames[i].getWhiteTeam() + " vs " + currentGames[i].getBlackTeam());
				found = true;
				myGameContainer = currentGames[i];
				i = currentGames.length;
			}
		}
		if(found)
		{
			myGameBoard = theFiles.loadSingleGame(myGameContainer);
			//figure out what team just forfeited, cause they just lost.

			if(split[1].equals(myGameBoard.getWhiteTeam()))
			{
				//black is the winner.
				myGameBoard.setWinner(myGameBoard.getBlackTeam());
				myGameBoard.setPlayable(false);
				myGameContainer.setWinner(myGameBoard.getBlackTeam());
				myGameContainer.setTimeEnd(new Date());
				ServerPusher.pushMessageNotification(myGameBoard.getBlackTeam(), "Enemy Forfeited", "Your game just ended.");
			}
			else
			{
				//white is the winner.
				myGameBoard.setWinner(myGameBoard.getWhiteTeam());
				myGameBoard.setPlayable(false);
				myGameContainer.setWinner(myGameBoard.getWhiteTeam());
				myGameContainer.setTimeEnd(new Date());
				ServerPusher.pushMessageNotification(myGameBoard.getWhiteTeam(),  "Enemy Forfeited", "Your game just ended.");
			}

			theFiles.updateGame(myGameContainer, myGameBoard);
			return split[0] + ":" + "Game Forfeited";

		}


		return null;
	}

	private String promoteMove(String packet) {


		loadGames();
		//Dobule check we are operating on the right Game
		String[] split = packet.split(":");
		boolean found = false;


		for(int i = 0; i<currentGames.length; i++)
		{
			if( currentGames[i].getGuid().toString().equals(split[3]))
			{
				//gameFound.
				//System.out.println("Game found: It is " + currentGames[i].getWhiteTeam() + " vs " + currentGames[i].getBlackTeam());
				found = true;
				myGameContainer = currentGames[i];
				i = currentGames.length;
			}
		}
		if(found)
		{
			myGameBoard = theFiles.loadSingleGame(myGameContainer);
			System.out.println(split[1] + " just made a move on game " + split[3]);
			//lump the move back into an int[] for easy usage here and in causesCheck()
			int count = 0;
			int[] move = new int[split.length - 4];
			for(int i = 4; i < split.length; i++)
			{
				move[count] = Integer.parseInt(split[i]);
				count++;
			}



			//only do this if its my turn
			boolean proceed = false;
			if(myGameBoard.getWhoseTurn().equals(myGameBoard.getWhiteTeam())){
				if(myGameBoard.getTheGame()[move[1]][move[0]].getContents().getTeam() == 'w')
					proceed = true;
				else 
					proceed = false;
			}
			else
			{
				if(myGameBoard.getTheGame()[move[1]][move[0]].getContents().getTeam() == 'b')
					proceed = true;
				else 
					proceed = false;
			}

			if(proceed)
			{

				myGameBoard.clearHighlight();
				myGameBoard.clearLastMove();
				try
				{
					//we are about to make a move(s).  Reset the 2 prompt Booleans so they dont interfere with anything,
					//especially if a king is moved
					myGameBoard.setPromptDefense(false);
					myGameBoard.setPromptOffense(false);



					myGameBoard.makeMove(move[0], move[1], move[2], move[3]);

					//TODO  Fix two kings and promote
					//if (move.length > 4)
					//{
					//second move for 2 kings.
					//myGameBoard.makeMove(move[4], move[5], move[6], move[7]);
					//}

				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

				myGameContainer.switchTurn();
				myGameBoard.switchTurn();
				myGameBoard.increaseNumberOfMoves();

				myGameBoard.promotePiece(move[2],move[3],split[split.length-1].charAt(0));


				String pushTitle = "Your Move";
				String pushMessage = "A game is waiting on you...";


				String winner = "";
				myGameBoard.determineCheck();
				//myGameBoard.pushTurn();
				myGameContainer.setBlackCheck(myGameBoard.isBlackCheck());
				myGameContainer.setWhiteCheck(myGameBoard.isWhiteCheck());
				myGameContainer.setPromptDefense(myGameBoard.isPromptDefense());
				myGameContainer.setPromptOffense(myGameBoard.isPromptOffense());
				myGameContainer.setLastPlay( new Date());
				winner = myGameBoard.gameIsOver();

				if(!myGameBoard.isBlackCheck() && myGameBoard.blackMidline())
				{
					winner = myGameBoard.getBlackTeam();
				}
				if(!myGameBoard.isWhiteCheck() && myGameBoard.whiteMidline())
				{
					winner = myGameBoard.getWhiteTeam();
				}
				if(winner != null)
				{
					pushTitle = "Game Over";
					pushMessage = "One of your games just ended...";
					myGameBoard.setWinner(winner);
					myGameBoard.setPlayable(false);
					myGameContainer.setWinner(winner);
					myGameContainer.setTimeEnd(new Date());
					System.out.println(winner + " just won game " + split[3]);
				}

				ServerPusher.pushMessageNotification(myGameBoard.getWhoseTurn(), pushTitle, pushMessage);



				//update gameBoard and gameContainer by switching them around. 
				theFiles.updateGame(myGameContainer, myGameBoard);
				return split[0] + ":" + "Move sent and confirmed";
			}
			else
				return split[0] + ":" + "Cant Move...Not your turn";
		}
		return split[0] + ":" + "Game Not Found";
	}
}
