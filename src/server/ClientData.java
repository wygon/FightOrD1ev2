package server;

import championAssets.*;
import client.CardPanel;
import java.io.*;
public class ClientData {
    PrintWriter out;
    String name;
    String choosenChampion;
    
    public ClientData(String name, PrintWriter output) {
        this.name = name;
        this.out = output;
        choosenChampion = null;
    }
    public synchronized void sendToMe(String sender, String message) {
        out.println(sender + ": "+ message);
    }

    public PrintWriter getOutput() {
        return out;
    }

    public String getName() {
        return name;
    }

//    public Champion getChoosenChampion() {
//        return choosenChampion;
//    }
//
//    public void setChoosenChampion(Champion choosenChampion) {
//        this.choosenChampion = choosenChampion;
//    }
    
}
