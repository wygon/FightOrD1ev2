package server;

import championAssets.*;
import engine.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.FightOrD1e;

public class GameServer {

    static final int PORT = 12345;
    private static List<ClientHandler> clients = new ArrayList<>();
    static ArrayList<ClientModel> clientsGameData = new ArrayList<>();
    static ArrayList<Champion> championsList = new ArrayList<>();
    private static final String CHAMPIONS_SETTINGS = "champions.settings";
    
    public static void main(String[] args) {
        ServerSocket s = null;
        Socket socket = null;
        try {
            s = new ServerSocket(PORT);
            System.out.println("Server has started");
            applyChampionsList();
            System.out.println("Champions written");
            applyClientsGameData();
            System.out.println("Applied clients game data");
            System.out.println(clientsGameData);
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
       //Full applying champion to list method
    public static void applyChampionsList() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(CHAMPIONS_SETTINGS));
            String line = reader.readLine();
            while (line != null) {
                String[] bigParts = line.split(";");
                String[] parts = bigParts[1].split(",");
                String champ = bigParts[0];
                Champion champion = switch (champ) {
                    case "LIFESTEALER" ->
                        new LifeStealer();
                    case "MAGE" ->
                        new Mage();
                    case "TANK" ->
                        new Tank();
                    case "FIGHTER" ->
                        new Fighter();
                    case "ASSASIN" ->
                        new Assasin();
                    default ->
                        throw new IllegalArgumentException("Invalid Chmapion type");
                };
                configureChampion(champion, parts);
                configureAbilities(champion, bigParts[2]);
                championsList.add(champion);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(FightOrD1e.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //Configuring champions
    private static void configureChampion(Champion champion, String[] parts) {
        champion.setName(parts[0]);
        champion.setHP(Double.parseDouble(parts[1]));
        champion.setLastRoundHP(Double.parseDouble(parts[1]));
        champion.setAttackDamage(Double.parseDouble(parts[2]));
        champion.setMagicDamage(Double.parseDouble(parts[3]));
        champion.setPhysicalResist(Double.parseDouble(parts[4]));
        champion.setMagicResist(Double.parseDouble(parts[5]));
        champion.setDistancePoint(Integer.parseInt(parts[6]));
    }
    //Applying abilities to champion
    private static void configureAbilities(Champion champion, String abilitiesPart) {
        String[] abilityParts = abilitiesPart.split("/");
        Ability[] abilities = new Ability[abilityParts.length];
        for (int i = 0; i < abilityParts.length; i++) {
            abilities[i] = Ability.fromString(abilityParts[i]);
        }
        champion.setAbilities(abilities);
    }
    
    private static void applyClientsGameData(){
        try{
            BufferedReader reader = new BufferedReader(new FileReader("players/data.csv"));
            String line = reader.readLine();
            while(line != null){
                String[] playerData = line.split(";");
                String name = playerData[0];
                int totalGames = Integer.parseInt(playerData[1]);
                int win = Integer.parseInt(playerData[2]);
                int lose = Integer.parseInt(playerData[3]);
                
//                boolean found = clientsGameData.stream()
//                        .anyMatch(c -> c.name.equals(name));
//                if(!found){
//                    clientsGameData.add(new ClientModel(name, totalGames, win, lose));
//                }
                clientsGameData.add(new ClientModel(name, totalGames, win, lose));
                
                line = reader.readLine();
            }
            reader.close();
        }catch(Exception ex) {System.out.println("Error reading players model" + ex);}
    }
    
//    public static void writeToDB(ClientModel newClient){
//        System.out.println("WRITING TO DB" + newClient);
//        try{
//            BufferedWriter writer = new BufferedWriter(new FileWriter("players/data.csv", true));
//            String[] data = newClient.getClientData();
//            String finalData = "";
//            for(String d : data) finalData += d + ";";
//            
//            writer.write(finalData);
//            writer.newLine();
//            writer.close();
//            
//        }catch(Exception ex) {System.out.println("Error with writing to DB" + ex);}
//    }
    public static void writeToDB(ClientModel newClient){
        System.out.println("WRITING TO DB" + newClient);
        try{
            BufferedReader reader = new BufferedReader(new FileReader("players/data.csv"));
            List<String> lines = new ArrayList<>();
            boolean updated = false;
            
            String line = reader.readLine();
            while(line != null){
                String[] parts = line.split(";");
                String name = parts[0];
                
                if(newClient.name.equals(name)){
                    String newData = String.join(";", newClient.getClientData());
                    lines.add(newData);
                    updated = true;
                } else {
                    lines.add(line);
                }
                line = reader.readLine();
            }
            reader.close();
            
            if(!updated){
                String newData = String.join(";", newClient.getClientData());
                lines.add(newData);
            }
            
            String finalMessage = "";
            
            BufferedWriter writer = new BufferedWriter(new FileWriter("players/data.csv", false));
            for(String l : lines) finalMessage += l + "\n";
            
            writer.write(finalMessage);
            writer.close();
            
        }catch(Exception ex) {System.out.println("Error with writing to DB" + ex);}
    }
    
    public static void updateDB(ClientModel updateClient){
        System.out.println("UPDATING CLIENT" + updateClient);
        writeToDB(updateClient);
    }
    
    public static void applyClientModel(ClientData client){
            Optional<ClientModel> optionalModel =
                clientsGameData.stream()
                .filter(model -> model.name.equals(client.name))
                .findAny();
            if(optionalModel.isPresent()){
                client.setModel(optionalModel.get());
            }else {
                System.out.println("Error with setting model.");
            }
    }
}
