package server;

public enum GameCommand {
    CHAT("CHAT"),
    FIND("GAME_FIND"),
    FIND_CANCEL("GAME_FIND_CANCEL"),
    FOUND("GAME_FOUND"),
    WAITING("GAME_WAITING"),
    WAITING_CANCEL("GAME_WAITING_CANCEL"),
    GET_STATS("GAME_GET_STATS"),
    UPDATE_USERS("GAME_UPDATE_USERS"),
    START("GAME_START"),
    END("GAME_END"),
    ATTACK("GAME_ATTACK"),
    APPLY_STATE("GAME_APPLY_STATE"),
    APPLY_LOGS("GAME_APPLY_LOGS"),
    APPLY_ABILITY_COUNT("GAME_APPLY_ABILITY_COUNT"),
    RESULT_WIN("GAME_RESULT_WIN"),
    RESULT_LOSE("GAME_RESULT_LOSE"),
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
