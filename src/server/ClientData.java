package server;

import championAssets.*;
import java.io.*;
import java.util.concurrent.*;

public class ClientData {

    PrintWriter out;
    String name;
    String chosenChampionName;
    Champion chosenChampion;
    int wins;
    String gameId;
    
    private BlockingQueue<String> commands = new LinkedBlockingQueue<>();

    public ClientData(String name, PrintWriter output) {
        this.name = name;
        this.out = output;
        chosenChampionName = null;
        chosenChampion = null;
        wins = 0;
        gameId = null;
    }

    public synchronized void sendToMe(String message) {
        out.println(message);
    }

    public PrintWriter getOutput() {
        return out;
    }

    public String getName() {
        return name;
    }

    public int getWins() {
        return wins;
    }

    public Champion getChampion() {
        return chosenChampion;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChampion(Champion champion) {
        this.chosenChampion = champion;
    }

    public void addWin() {
        this.wins++;
    }

    public String getMyGameId() {
        return gameId;
    }

    public void setMyGameId(String myGameId) {
        this.gameId = myGameId;
    }

    public void addCommand(String command) {
        commands.add(command);
    }

    public String takeCommand() throws InterruptedException {
        return commands.take();
    }

    public String getGameId() {
        return gameId;
    }
}
