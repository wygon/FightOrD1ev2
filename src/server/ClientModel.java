package server;

public class ClientModel {
    String name;
    int totalGames;
    int wins;
    int loses;
    
    public ClientModel(String name) {
        this.name = name;
        totalGames = 0;
        wins = 0;
        loses = 0;
    }
    public ClientModel(String name, int totalGames, int wins,  int loses) {
        this.name = name;
        this.totalGames = totalGames;
        this.wins = wins;
        this.loses = loses;
    }
    
    @Override
    public String toString(){
        return name + " " + totalGames + " " + wins + " " + loses;
    }
    
    public String getName(){
        return name;
    }
    
    public String[] getClientData(){
        return new String[]{name , String.valueOf(totalGames) , String.valueOf(wins), String.valueOf(loses)};
    }
}
