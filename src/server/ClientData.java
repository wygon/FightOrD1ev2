package server;

import championAssets.*;
import java.io.*;
import java.util.concurrent.*;

public class ClientData {

    PrintWriter out;
    String name;
    String chosenChampionName;
    Champion chosenChampion;
    String gameId;
    ClientModel model;
    
    private BlockingQueue<String> commands = new LinkedBlockingQueue<>();

    public ClientData(String name, PrintWriter output) {
        this.name = name;
        this.out = output;
        chosenChampionName = null;
        chosenChampion = null;
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
        return model.wins;
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
        model.totalGames++;
        model.wins++;
        GameServer.updateDB(model);
    }
    
    public void addLose() {
        model.totalGames++;
        model.loses++;
        GameServer.updateDB(model);
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
    public void setModel(ClientModel model){
        this.model = model;
    }    
    public ClientModel getModel(){
        return model;
    }
}
