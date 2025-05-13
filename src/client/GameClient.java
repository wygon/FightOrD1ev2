package client;

import championAssets.*;
import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;
import server.GameCommand;

public class GameClient {
    private CardPanel ui;
    protected String name;
    protected PrintWriter out;
    protected BufferedReader in;
    protected Champion choosenChampion;
    private String currentGameId;
    protected boolean searchingGame;
    private boolean isConnected;
    
    protected Socket socket;
    protected final int SERVER_PORT = 12345;
    protected final String SERVER_ADDRESS = "127.0.0.1";
    protected InetAddress iAddress;
    
     public GameClient(CardPanel ui, String nickname) {
        this.ui = ui;
        this.name = nickname;
        choosenChampion = null;
        iAddress = null;
        socket = null;
        searchingGame = false;
        isConnected = false;
    }

    void connect() {
        isConnected = true;
        ui.getReconnectButton().setVisible(false);
        try {
            iAddress = InetAddress.getByName(SERVER_ADDRESS);
            ui.getMessageTextArea().append("Connected to chat.\n");
        } catch (IOException e) {
            System.exit(0);
        }

        try {
            socket = new Socket(iAddress, SERVER_PORT);
        } catch (IOException e) {
            ui.getMessageTextArea().append("Connection failed");
            isConnected = false;
            ui.getReconnectButton().setVisible(true);
            return;
        }
        Thread readerFromServer = new MenuClientThread(this);
        readerFromServer.start();
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(name);
        } catch (IOException e) {
            ui.getMessageTextArea().append("Error: " + e.getMessage());
            ui.getReconnectButton().setVisible(true);
        }
//        ui.getReconnectButton().setVisible(!isConnected);
    }

    public void sendMessage(String mess)
    {
        if(mess.length() >= 1)
            out.println(mess);
    }
    public void sendGameCommand(GameCommand command, String gameId, String player)
    {
        sendMessage(command + "|" + gameId + "|" + player);
    }
    
    public void findGame(){
        searchingGame = true;
        ui.showSearchingGameWindow(true);
    }
    public void foundOponent(String gameId, String enemyName, String enemyChampion){
        ui.sgw.setVisible(false);
        ui.setVisible(false);
        ui.fightStart(gameId, enemyName, enemyChampion);
    }
    
    public void forfeit(boolean win){
        if(win)
            JOptionPane.showMessageDialog(
                    ui.fightPanel,
                    "YOU WON CONGRATULATION",
                    "WIN",
                    JOptionPane.INFORMATION_MESSAGE
            );
        else
            JOptionPane.showMessageDialog(
                    ui.fightPanel,
                    "YOU LOST",
                    "LOST",
                    JOptionPane.INFORMATION_MESSAGE
            );
        ui.fightPanel.setVisible(false);
        ui.setVisible(true);
    }
    public void attack(String gameId, String sender, String target, Double hp){
        ui.fightPanel.adjustUi(gameId, sender, target, hp);
    }
    public CardPanel getUi() {
        return ui;
    }
}
