package server;

import java.io.*;
import java.net.*;
import java.util.*;
import server.ClientData;

class Pair {
    public final ClientData left;
    public final ClientData right;
    
    public Pair(ClientData left, ClientData right) {
        this.left = left;
        this.right = right;
    }
}

public class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ClientData me;

    // a container to hold data about clients
    // it is a static variable, so each object can access it
    static HashSet<ClientData> clients = new HashSet<ClientData>();
    static Queue<ClientData> playersQueue = new LinkedList<>();
    static Map<String, Pair> activeGames = new HashMap<>();
    // a constructor, it prepares appriopriate streams associated to a socket
    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(),
                        true); // reminder: true means automatically buffer flushing
    }
    
    public void sendToAll(String senderName, String message){
        for (ClientData client : clients) 
            client.sendToMe("[" + senderName + "]", message);
    }

    void printClients() {
        Iterator<ClientData> iterator = clients.iterator();
        ClientData client;
        out.println("[SERVER]Active players " + clients.size() + ":");
        while (iterator.hasNext()) {
            client = iterator.next();
            out.println(client.getName());
        }
        out.println("--------");
    }

    public void run() {        
        String clientName=null;
        try {
            // asks for a name
            clientName = in.readLine();

            System.out.println("[SERVER]" + clientName + " logged in.");
            sendToAll("SERVER", clientName + " logged in.");

            // adds object with client data to container and prints updated clients list
            me = new ClientData(clientName, out);
            clients.add(me);
            printClients();

            // in a loop reads info from client and send to others
            while (true) {
                String info = in.readLine();
                if (info.equals("ENDLEAVEEND")) {
                    System.out.println("[SERVER]" + clientName + " logged out.");
                    sendToAll("SERVER", clientName + " logged out.");
                    clients.remove(me);
                    in.close();
                    out.close();
                    socket.close();
                    break;
                }
                if(info.startsWith("GAME"))
                    handleCommand(info);
                else
                    sendToAll(clientName, info);
            }
        } catch (IOException e) { }
    }

    public ClientData getClientData() {
        return me;
    }
    
    private void handleCommand(String info){
        String[] parts = info.split(">");
        String command = parts[0];
        System.out.println("Command: " + command);
        if(command.equals(GameCommand.FIND.toString())){
            String championName = parts[1];
            me.choosenChampion = championName;
            if(!playersQueue.isEmpty()) {
                ClientData enemy = playersQueue.poll();
                me.out.println(GameCommand.FOUND + ">" + "1" + ">" + 
                        enemy.name + ">" + 
                        enemy.choosenChampion);
                enemy.out.println(GameCommand.FOUND + ">" + "1" + ">" + 
                        me.name + ">" + 
                        me.choosenChampion);
                
//                HashMap<ClientData, ClientData> game = new HashMap<>();
//                game.put(me, enemy);
                Pair game = new Pair(me, enemy);
                activeGames.put("1", game);
            } else {
              playersQueue.add(me);
              me.out.println(GameCommand.WAITING);
            }
        } 
        else if(command.equals(GameCommand.FORFEIT.toString())) {
            String gameId = parts[1];
            ClientData winner;
            Pair pl = activeGames.get(gameId);
            me.out.println(GameCommand.FORFEIT);
            if(me == pl.left){
                pl.right.out.println(GameCommand.ENEMY_FORFEIT.toString());
                winner = pl.right;
            }
            else{
                pl.left.out.println(GameCommand.ENEMY_FORFEIT.toString());
                winner = pl.left;
            }
            
            gameEnd(gameId, pl, winner);
        }
        else if(command.equals(GameCommand.ATTACK.toString())) {
            String gameId = parts[1];
            String name = parts[2];
            String ability = parts[3];
            Pair pl = activeGames.get(gameId);
            ClientData target;
            if(me == pl.left){
                target = pl.right;
            } else {
                target = pl.left;
            }
            
            applyRoundHpTest(pl, gameId, name, target);
        }
        else {System.out.println("Other command");}
    }
    public void gameEnd(String gameId, Pair pl, ClientData winner) {
        sendToAll("SERVER", "GAME BETWEEN\n" + 
                pl.left.name + " & " +
                pl.right.name + "\n WON " + 
                winner.name.toUpperCase() + " CONGRATULATIONS");
    }
    public void applyRoundHpTest(Pair pl, String gameId, String name, ClientData target) {

        pl.left.out.println(
                GameCommand.ATTACK.toString() + ">" +
                gameId + ">" +
                name + ">" +
                target.name + ">" +
                "10"
        );
        pl.right.out.println(
                GameCommand.ATTACK.toString() + ">" +
                gameId + ">" +
                name + ">" +
                target.name + ">" +
                "10"
        );
    }
}