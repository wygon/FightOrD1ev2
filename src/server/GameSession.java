//package server;
//
//import championAssets.*;
//import engine.*;
//import java.util.*;
//
//public class GameSession {
//    private String gameId;
//    private ClientHandler player1;
//    private ClientHandler player2;
//    private Game game;
//    private boolean isGameStarted = false;
//    private Map<String, Champion> selectedChampions = new HashMap<>();
//    
//    public GameSession(String gameId, ClientHandler player1, ClientHandler player2)
//    {
//        this.gameId = gameId;
//        this.player1 = player1;
//        this.player2 = player2;
//        
//        Player p1 = new Player(player1.getName());
//        Player p2 = new Player(player2.getName());
//        
//        this.game = new Game(gameId, p1, p2);
//        
//        startGame();
//    }
//    private void startGame(){
////        player1.getClientData().setChoosenChampion()
////        game.getTM().getCurrentPlayer().setChampion(player1.getChoosenChampion());
//    }
//}
