//Szymon Wygoński
package engine;

import server.ClientData;
import textManagement.Loggers;

public class StatisticTable {

    private ClientData player1;
    private ClientData player2;
    private TurnManager tm;

    public StatisticTable(ClientData player1, ClientData player2, TurnManager turnManager) {
        this.player1 = player1;
        this.player2 = player2;
        this.tm = turnManager;
    }

    public StatisticTable() {
    }

    public ClientData getPlayer1() {
        return player1;
    }

    public ClientData getPlayer2() {
        return player2;
    }

    public TurnManager getTurnManager() {
        return tm;
    }

    public void setPlayer1(ClientData player1) {
        this.player1 = player1;
    }

    public void setPlayer2(ClientData player2) {
        this.player2 = player2;
    }

    public void setTurnManager(TurnManager turnManager) {
        this.tm = turnManager;
    }

    public void displayBattleStatistics() {
        Loggers.logMessage("[" + tm.getCurrentChampion().getName() + "] " + tm.getCurrentChampion().lessStats(), false, true);
        Loggers.logMessage("[" + tm.getNextChampion().getName() + "] " + tm.getNextChampion().lessStats(), false, true);
    }

}
