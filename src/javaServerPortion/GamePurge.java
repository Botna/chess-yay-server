package javaServerPortion;


import java.util.Scanner;
import serverClasses.FileClass;
import chess2.source.GameBoard;
import chess2.source.GameContainer;
import chess2.source.SingleSpace;

public class GamePurge
{
	static GameContainer[] myContainer = null;
	static FileClass theFiles = new FileClass();
	public static void main(String[] args)
	{






		while(true)
		{
			myContainer = theFiles.loadGames();
			System.out.println("Game Purge Prog:");
			System.out.println("Current Games:");
			int count= 1;
			for(int i = 0; i< myContainer.length; i++)

			{
				System.out.print(count + ": ");
				System.out.print(myContainer[i].getWhiteTeam() + " vs " + myContainer[i].getBlackTeam());
				System.out.println("   " + myContainer[i].getWhiteVar() + "-----" + myContainer[i].getBlackVar() + " : Winner = " + myContainer[i].getWinner());
				count++;
			}
			System.out.println("Please select a game:");
			Scanner scan = new Scanner(System.in);

			int choice = scan.nextInt();
			int index = choice;

			if(choice> 0 && choice <= myContainer.length)
			{
				System.out.println("\nYou chose:" + myContainer[choice-1].getWhiteTeam() + " vs " + myContainer[choice-1].getBlackTeam());
				System.out.println("            " + myContainer[choice-1].getWhiteVar() + "-----" + myContainer[choice-1].getBlackVar());
				System.out.println("\nWhat do?");
				System.out.println("9: Delete");
				System.out.println("1: Debug");
				choice = scan.nextInt();
				if(choice == 9)
				{
					deleteGame(index);
				}
				else if (choice == 1);
				{
					debug(index);
				}

			}
			else
			{
				System.out.println("invalid choice:");
			}




		}










	}

	public static void deleteGame(int choice)
	{
		choice = choice -1;

		GameContainer[] temp = new GameContainer[myContainer.length - 1];
		GameContainer toDelete = null;
		int index = 0;
		for(int i = 0; i < myContainer.length; i++)
		{
			if(i != choice)
			{
				temp[index] = myContainer[i];
				index++;
			}
			else
			{
				toDelete = myContainer[i];
			}
		}

		theFiles.deleteGame(toDelete);

	}

	public static void debug(int choice)
	{
		choice = choice -1;

		


		GameBoard theBoard = theFiles.loadSingleGame(myContainer[choice]);
		
		


		System.out.println("Debugging!");
		
		theFiles.updateGame(myContainer[choice],theBoard);
	}

}
