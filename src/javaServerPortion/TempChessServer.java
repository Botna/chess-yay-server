package javaServerPortion;

import java.net.*;
import java.io.*;

public class TempChessServer{
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;

        try {
            serverSocket = new ServerSocket(9812);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 9812.");
            System.exit(-1);
        }
        System.out.println("Started listening");
        while (listening)
        	
        
        new TempChessThread(serverSocket.accept()).start();
        System.out.println("grabbed a new client");
        serverSocket.close();
        
    }
}