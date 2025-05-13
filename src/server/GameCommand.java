package server;

public enum GameCommand {
    CHAT("CHAT"),
    FIND("GAME_FIND"),
    FOUND("GAME_FOUND"),
    WAITING("GAME_WAITING"),
    START("GAME_START"),
    ATTACK("GAME_ATTACK"),
    NOT_YOUR_TURN("GAME_NOT_YOUR_TURN"),
    FORFEIT("GAME_FORFEIT"),
    ENEMY_FORFEIT("GAME_ENEMY_FORFEIT"),
    OPPONENT_DISCONNECTED("GAME_OPPONENT_DISCONNECTED"),
    DISCONNECT("DISCONNECT");
    private final String command;

    GameCommand(String command) {
        this.command = command;
    }
    @Override
    public String toString() {
        return command;
    }
}
