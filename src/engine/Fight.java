//Szymon Wygoński
package engine;

import championAssets.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import server.ClientData;
import server.GameCommand;
import textManagement.Loggers;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Fight implements Runnable {

    public final TurnManager tm;
    String gameId;
    private final StatisticTable st;
    private Champion winner;
    private ArrayList<Champion> championsList;

    Scanner input = new Scanner(System.in);

    public Fight(String gameId, ClientData player1, ClientData player2, Map<String, Integer> fightWinners) {
        this.gameId = gameId;
        this.tm = new TurnManager(this, player1, player2);
        this.st = new StatisticTable(player1, player2, tm);
        this.championsList = new ArrayList<>();
    }

    @Override
    public void run() {
        startGame();
    }

    public void startGame() {
//        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        Loggers.logMessage(gameId, "=================================================\n[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] Game: " + gameId + "\n[" + tm.getCurrentChampion().getName() + "] vs [" + tm.getNextChampion().getName() + "]", true, false);
        Loggers.clearScreen();
        Loggers.logMessage(gameId, "Champions description: " + tm.getCurrentChampion() + tm.getCurrentChampion().printAbilities() + tm.getNextChampion() + tm.getNextChampion().printAbilities(), false, true);
        tm.whoStart();
//        Loggers.logMessage("Fight start: \n[" + tm.getCurrentPlayer().getName() + "] with his champion [" + tm.getCurrentChampion().getName() + "]", false, true);
//        Loggers.logMessage("His opponent is : \n[" + tm.getNextPlayer().getName() + "] with his champion [" + tm.getNextChampion().getName() + "]", false, true);
        startFight();
    }

    //Start fight 
    public void startFight() {
        tm.setTotalMovesCount(0);
        fightLoop();
        end();
    }
    
//    @Override
    public void end() {
        st.displayBattleStatistics();
        String mess = "";
        if (tm.getCurrentChampion().getHP() <= 0) {
            mess = "[" + tm.getCurrentChampion().getName() + "]Died... [" + tm.getNextChampion().getName() + "]is a WINNER ";
            tm.getNextPlayer().addWin();
            //Not working wgile first argument is Champion(and not String)
            setWinner(tm.getNextChampion());
            tm.getNextPlayer().sendToMe(GameCommand.RESULT_WIN.toString());
            tm.getCurrentPlayer().sendToMe(GameCommand.RESULT_LOSE.toString());
        } else if (tm.getNextPlayer().getChampion().getHP() <= 0) {
            mess = "[" + tm.getNextChampion().getName() + "]Died... [" + tm.getCurrentChampion().getName() + "]is a WINNER ";
            tm.getCurrentPlayer().addWin();
            setWinner(tm.getCurrentChampion());
            tm.getCurrentPlayer().sendToMe(GameCommand.RESULT_WIN.toString());
            tm.getNextPlayer().sendToMe(GameCommand.RESULT_LOSE.toString());
        } else {
            mess = "[ALMOST IMPOSSIBLE]TIE[" + tm.getNextChampion().getName() + "] and [" + tm.getCurrentChampion().getName() + "]DIED!";
        }
        if (!mess.equals("")) {
            Loggers.logMessage(null, mess, true, true);
        }
    }

    private void sendActualInformation(String info) {
        if(!info.trim().equals("")) sendLogMessage(tm.getCurrentPlayer().getName(), info);
        tm.getCurrentPlayer().sendToMe(
                GameCommand.APPLY_STATE + ">"
                + gameId + ">"
                + tm.getCurrentPlayer().getName() + ">"
                + tm.getCurrentChampion().getHP() + ">"
                + tm.getNextPlayer().getName() + ">"
                + tm.getNextChampion().getHP()
        );
        tm.getNextPlayer().sendToMe(
                GameCommand.APPLY_STATE + ">"
                + gameId + ">"
                + tm.getNextPlayer().getName() + ">"
                + tm.getNextChampion().getHP() + ">"
                + tm.getCurrentPlayer().getName() + ">"
                + tm.getCurrentChampion().getHP()
        );
    }
    protected void sendLogMessage(String name, String mess){
        tm.getCurrentPlayer().sendToMe(
                GameCommand.APPLY_LOGS + ">" +
                        name + ">" +
                        mess
        );
        tm.getNextPlayer().sendToMe(
                GameCommand.APPLY_LOGS + ">" +
                        name + ">" +
                        mess
        );
    }
    //Main fight file
    private void fightLoop() {
        do {
            int wybor;
            Champion ally = tm.getCurrentChampion();
            Champion enemy = tm.getNextChampion();
            String info = "[" + tm.getCurrentPlayer().getName() + "][" + ally.getName() + "] ITS YOUR TURN!";
            tm.setTourPoint(0);
            tm.effectsManagement();
            tm.rangeCheck();
            if (tm.getTourPoint() <= 2) {
                Loggers.logMessage(gameId, "[SERVER][" + tm.getCurrentPlayer().getName() + "][" + ally.getName() + "] ITS YOUR TURN!", false, true);
//                info += "[" + tm.getCurrentPlayer().getName() + "][" + ally.getName() + "] ITS YOUR TURN!";
            }
            System.out.println("INFO VALUE:" + info);
            sendActualInformation(info);
            while (!tm.isGameOver() && tm.getTourPoint() <= 2) {
                info = "";
                String hp1 = String.format("%.2f", ally.getHP());
                String hp2 = String.format("%.2f", enemy.getHP());
                Loggers.logMessage(gameId, 
                        "[" + ally.getName() + "] HP: " + hp1 + "\n"
                        + "[" + enemy.getName() + "] HP: " + hp2 + "\n\n"
                        + "Move: " + (tm.getTourPoint() + 1) + "/3\n"
                        + "[1]Attack [" + ally.getAttackDamage() + "]", false, true);
                int i = 0;
                for (Ability a : ally.getAbilities()) {
                    Loggers.logMessage(gameId, "[" + (i++ + 2) + "]" + a.getName() + " [" + a.getValue() + "]" + "[Uses " + a.getUsesLeft() + "]", false, true);
                }
                Loggers.logMessage(gameId, """
                                [10]See stats
                                [99]End move
                                [135]Forfeit
                                [0]Wyczysc""", false, true);
//                wybor = Loggers.choiceValidator(input);
                String moveStr = "-1";
                try {
                    moveStr = tm.getCurrentPlayer().takeCommand();
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
                wybor = Integer.parseInt(moveStr) + 1;
                switch (wybor) {
                    case 1:
                        tm.addTourPoint(1);
                        tm.useAbility(new Ability("Auto attack", "auto attack", ally.getAttackDamage(), "aa", 112));
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                        int abilityIndex = wybor - 2;
                        if (ally.getAbilities().length > abilityIndex) {
                            Ability ability = ally.getAbilities()[abilityIndex];
                            if (ability.getUsesLeft() > 0) {
                                tm.addTourPoint(1);
                                tm.useAbility(ability);
                                tm.getCurrentPlayer().sendToMe(GameCommand.APPLY_ABILITY_COUNT + ">" + abilityIndex);
                            } else {
                                Loggers.logMessage(gameId, "Ability " + ability.getName() + " has no uses left!", false, true);
                                info += "Ability " + ability.getName() + " has no uses left!";
                            }
                        } else {
                            Loggers.logMessage(gameId, ally.getName() + " does not have " + (abilityIndex + 2) + " abilities!", false, true);
                            info += ally.getName() + " does not have " + (abilityIndex + 2) + " abilities!";
                        }
                        break;
                    case 10:
                        Loggers.clearScreen();
                        Loggers.logMessage(gameId, ally.getName() + ":\n" + ally.lessStats(), false, true);
                        break;
                    case 99:
                        Loggers.clearScreen();
                        tm.setTourPoint(3);
                        break;
                    case 135:
                        ally.setHP(0);
                    case 0:
                        Loggers.clearScreen();
                        break;
                    default:
                        Loggers.logMessage(gameId, "Invalid choice!", false, true);
                        break;
                }
                Loggers.logMessage(gameId, "=================================================", false, true);
                System.out.println("INFO VALUE IN WHILE:" + info);
                sendActualInformation(info);
            }
            tm.endTurn();
        } while (!tm.isGameOver());
    }

    //Setting winner
    private void setWinner(Champion winner) {
        this.winner = winner;
    }

    public Champion getWinner() {
        return winner;
    }
}
