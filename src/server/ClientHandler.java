package server;

import championAssets.*;
import engine.*;
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
    static Map<String, Fight> activeGames = new HashMap<>();
    static Map<String, Integer> fightWinners = new HashMap<>();

    // a constructor, it prepares appriopriate streams associated to a socket
    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(),
                true); // reminder: true means automatically buffer flushing
    }

    public void sendToAll(String senderName, String message) {
        for (ClientData client : clients) {
            client.sendToMe("[" + senderName + "]" + message);
        }
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
        String clientName = null;
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
                if (info.startsWith("GAME"))
                    try {
                    handleCommand(info);
                } catch (NullPointerException ex) {
                    System.out.println("Game is ended");
                } else {
                    sendToAll(clientName, info);
                }
            }
        } catch (IOException e) {
        }
    }

    public ClientData getClientData() {
        return me;
    }

    private void handleCommand(String info) {
        String[] parts = info.split(">");
        String command = parts[0];
        System.out.println("Command: " + command);
        if (command.equals(GameCommand.FIND.toString())) {
            String championName = parts[1];
            me.chosenChampionName = championName;
            if (!playersQueue.isEmpty()) {
                ClientData enemy = playersQueue.poll();
//                Optional<Champion> mechamp = GameServer.championsList.stream()
//                        .filter(p -> p.getName().equals(me.chosenChampionName))
//                        .findAny();
                applyChampionCopy();
                String gameId = UUID.randomUUID().toString().substring(0, 8);
                Fight f = new Fight(gameId, me, enemy, fightWinners);
                new Thread(f).start();
                me.setMyGameId(gameId);
                enemy.setMyGameId(gameId);
                me.out.println(GameCommand.FOUND + ">"
                        + gameId + ">"
                        + enemy.name + ">"
                        + enemy.chosenChampionName);
                enemy.out.println(GameCommand.FOUND + ">"
                        + gameId + ">"
                        + me.name + ">"
                        + me.chosenChampionName);
                activeGames.put(gameId, f);
            } else {
                playersQueue.add(me);
//                Optional<Champion> mechamp = GameServer.championsList.stream()
//                        .filter(p -> p.getName().equals(me.chosenChampionName))
//                        .findAny();
                applyChampionCopy();
                me.out.println(GameCommand.WAITING);
            }
        } 
        else if(command.equals(GameCommand.FIND_CANCEL.toString())){
            playersQueue.remove(me);
            me.out.println(GameCommand.WAITING_CANCEL);
        }
        else if (command.equals(GameCommand.FORFEIT.toString())) {
//            Fight f = activeGames.get(me.gameId);
            String gameId = parts[1];
            Fight f = activeGames.get(gameId);
            ClientData winner;
            me.out.println(GameCommand.FORFEIT);
            if (me == f.tm.getCurrentPlayer()) {
                f.tm.getNextPlayer().out.println(GameCommand.ENEMY_FORFEIT.toString());
                winner = f.tm.getNextPlayer();
            } else {
                f.tm.getCurrentPlayer().out.println(GameCommand.ENEMY_FORFEIT.toString());
                winner = f.tm.getCurrentPlayer();
            }

            gameEnd(gameId, f, winner);
        } else if (command.equals(GameCommand.ATTACK.toString())) {

            Fight f = activeGames.get(me.gameId);
            ClientData currentTurnPlayer = f.tm.getCurrentPlayer();
            if (me != currentTurnPlayer) {
                me.out.println(GameCommand.NOT_YOUR_TURN.toString());
                return;
            }
            String ability = parts[3];
            me.addCommand(ability);
        } else {
            System.out.println("Other command");
        }
    }

    private void applyChampionCopy() {
        Optional<Champion> mechamp = GameServer.championsList.stream()
                        .filter(p -> p.getName().equals(me.chosenChampionName))
                        .findAny();
        Champion base = mechamp.get();
        if (base instanceof Assasin) {
            me.chosenChampion = new Assasin((Assasin) base);
        } else if (base instanceof Mage) {
            me.chosenChampion = new Mage((Mage) base);
        } else if (base instanceof Fighter) {
            me.chosenChampion = new Fighter((Fighter) base);
        } else if (base instanceof Tank) {
            me.chosenChampion = new Tank((Tank) base);
        }
    }

    public void gameEnd(String gameId, Fight f, ClientData winner) {
        sendToAll("SERVER", "GAME BETWEEN\n"
                + f.tm.getCurrentPlayer().name + " & "
                + f.tm.getNextPlayer().name + "\n WON "
                + winner.name.toUpperCase() + " CONGRATULATIONS");
//        f.end();
        activeGames.remove(gameId);
    }
}
