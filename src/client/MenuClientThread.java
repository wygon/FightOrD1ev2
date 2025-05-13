package client;

import java.awt.*;
import java.io.*;

public class MenuClientThread extends Thread{
    private BufferedReader in;
    GameClient client;
    
    public MenuClientThread(GameClient client)
    {
        this.client = client;
        try {
            in = new BufferedReader(new InputStreamReader(client.socket.getInputStream()));
        }catch(Exception e) {}
    }
    
    public void run() {
        String mess;
        try{
            while((mess = in.readLine()) != null) {
                System.out.println("SERVER RESPONSE: " + mess);
                if(mess.startsWith("GAME")){
                    handleCommands(mess);
                } else{
                    client.getUi().getMessageTextArea().append(mess + "\n");
                    client.getUi().getMessageTextArea().scrollRectToVisible(new Rectangle(0, client.getUi().getMessageTextArea().getHeight()-2, 1, 1));
                }
            }
            in.close();
        } catch(IOException e)
        {
            client.getUi().getMessageTextArea().append("Error " + e.getMessage());
            client.getUi().getReconnectButton().setVisible(true);
        }
    }
    
    private void handleCommands(String mess)
    {
        String[] parts = mess.split(">");
        String gameId;
        String command = parts[0];
        CardPanel ui = client.getUi();
        
//        ui.getMessageTextArea().append(command);
        switch(command)
        {
            case "GAME_WAITING":
               ui.getMessageTextArea().append("Waiting for opponent...\n");
                client.findGame();
                break;
            case "GAME_FOUND":
                gameId = parts[1];
                String enemyName = parts[2];
                String enemyChampion = parts[3];
                ui.getMessageTextArea().append("GAMEID: " + gameId);
                ui.getMessageTextArea().append("YOUR OPPONENT IS " + enemyName + "\n");
                client.foundOponent(gameId, enemyName, enemyChampion);
                break;
            case "GAME_FORFEIT":
                client.forfeit(false);
                break;
            case "GAME_ENEMY_FORFEIT":
                client.forfeit(true);
                break;
            case "GAME_ATTACK":
                gameId = parts[1];
                String sender = parts[2];
                String target = parts[3];
                double hp = Double.parseDouble(parts[4]);
                    client.attack(gameId, sender, target, hp);
                break;
        }
    }
}