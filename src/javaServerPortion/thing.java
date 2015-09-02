package javaServerPortion;



import java.io.*;
import java.util.Scanner;

import chess2.source.PlayerRecord;


public class thing
{
	public static void main(String[] args)
	{

		class Temp implements Serializable
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private int a;
			//private int d;
			private int b;
			private int c;
			
			public int getA() {
				return a;
			}
			public void setA(int a) {
				this.a = a;
			}
			public int getB() {
				return b;
			}
			public void setB(int b) {
				this.b = b;
			}
			public int getC() {
				return c;
			}
			public void setC(int c) {
				this.c = c;
			}
			public void thing(int c)
			{
				a = b;
			}
//			public int getD() {
//				return d;
//			}
//			public void setD(int c) {
//				this.d = c;
//			}

		}
		
		
		int choice = 0;
		Temp temp;
while(true)
{
		System.out.println("1 = In");
		System.out.println("2 = out");
		Scanner scan = new Scanner(System.in);
		choice = scan.nextInt();


		if(choice == 1)
		{

			File file = new File("\\temp\\thing.txt");
			
			try{
			InputStream fileIn = new FileInputStream(file);
			//lock fileIn
			

			//specifies the file has data. recreate our FIleInputStream;
			InputStream bufferIn = new BufferedInputStream(fileIn);
			ObjectInputStream in = new ObjectInputStream(bufferIn);
			temp = (Temp) in.readObject();
			in.close();
			bufferIn.close();
			fileIn.close();
			}
			catch(Exception e )
			{
				System.out.println("woops");
			}

		}
		else
		{
			File file = new File("\\temp\\thing.txt");

			try{
				file.createNewFile();
				
				
				OutputStream fileOut = new FileOutputStream(file);
				OutputStream buffer = new BufferedOutputStream(fileOut);
				ObjectOutputStream out = new ObjectOutputStream(buffer);
				
				temp = new Temp();
				temp.setA(1);
				temp.setB(2);
				temp.setC(3);
				//temp.setD(4);
				out.writeObject(temp);
				out.close();
				buffer.close();
				fileOut.close();
				
				

			}
			catch(Exception e)
			{
				System.out.println("woops");
			}
		}


	}

	}


}


