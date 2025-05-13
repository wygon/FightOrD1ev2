package server;

import engine.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {

    static final int PORT = 12345;
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        ServerSocket s = null;
        Socket socket = null;
        try {
            s = new ServerSocket(PORT);
            System.out.println("Server has started");
            while (true) {
                socket = s.accept();
                System.out.println("New client has connected");
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
        } finally {
            try {
                socket.close();
                s.close();
            } catch (IOException e) {
            }
        }
    }

}
