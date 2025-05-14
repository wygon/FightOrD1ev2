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
//            System.out.println(championsList);
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
}
