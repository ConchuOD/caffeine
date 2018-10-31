package ie.ucd.engac.lifegamelogic.gamestatehandling;

import ie.ucd.engac.lifegamelogic.playerlogic.Player;
import ie.ucd.engac.messaging.*;

import java.util.ArrayList;

public class GameOverState implements GameState {

    private String eventMessage;

    public GameOverState(){}

    //TODO constructor with the situational event message
    public void enter(GameLogic gameLogic){

        ArrayList<Player> rankings = gameLogic.getRankedRetiredPlayers();
        LifeGameMessage responseMessage = new EndGameMessage(rankings);
        gameLogic.setResponseMessage(responseMessage);

    }

    public GameState handleInput(GameLogic gameLogic, LifeGameMessage lifeGameMessage){
        return null;
    }

    public void exit(GameLogic gameLogic){

    }
}