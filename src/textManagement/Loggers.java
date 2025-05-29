package textManagement;

import main.FightOrD1e;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class Loggers {

    private final static String LOGS_FILE = "-logs.txt";
    //this function is responsible for showing information to user in terminal or/and saving info to logs.txt

    public final static void logMessage(String gameId, String message, boolean log, boolean terminal) {
        if (log) {
            try {
                String path = "logs/" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                File folder = new File(path);
                if(!folder.exists()) folder.mkdirs();
                String fileName =  path + "/GAME-" + gameId + LOGS_FILE;
                PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)), true);
                writer.println(message);
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(FightOrD1e.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (terminal) {
            System.out.println(message);
        }
    }

    //function is responsible for printing information from files.
    public final static void readFile(String file, boolean log, boolean terminal) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            String[] splitted = line.split(";");
            for (String word : splitted) {
                logMessage("", word, log, terminal);
            }
            //line = reader.readLine();
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(FightOrD1e.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final static void clearScreen() {
        //there should be console cleaing function
        //simmilar to windows cmd 'system("cls");'
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n");
    }

    //checking if input is Integer value.
    public static int choiceValidator(Scanner scanner) {
        int choice;
        while (true) {
            try {
//              Loggers.logMessage("Choice: ", false, true);
                choice = scanner.nextInt();
                break;
            } catch (java.util.InputMismatchException e) {
                Loggers.logMessage(null, "Wrong input type. Try again.", false, true);
                scanner.next();
            }
        }
        return choice;
    }
}
