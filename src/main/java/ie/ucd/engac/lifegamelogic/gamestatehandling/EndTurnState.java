package ie.ucd.engac.lifegamelogic.gamestatehandling;

import ie.ucd.engac.messaging.AckRequestMessage;
import ie.ucd.engac.messaging.LifeGameMessage;
import ie.ucd.engac.messaging.LifeGameMessageTypes;

public class EndTurnState implements GameState {
    public void enter(GameLogic gameLogic){
        int playNum = gameLogic.getCurrentPlayer().getPlayerNumber();
        String eventMessage = "Player " + playNum + "'s turn is over.";
        AckRequestMessage ackRequestMessage = new AckRequestMessage(playNum, eventMessage);
        gameLogic.setResponseMessage(ackRequestMessage);
    }

    public GameState handleInput(GameLogic gameLogic, LifeGameMessage lifeGameMessage){
        if (lifeGameMessage.getLifeGameMessageType() == LifeGameMessageTypes.AckResponse){

            gameLogic.setNextPlayerToCurrent();
            if (gameLogic.getNumberOfUninitialisedPlayers() > 0) { //
                // Must send a message to choose a career path, etc.
                System.out.println("Still player left to initialise");
                LifeGameMessage replyMessage = PathChoiceState.constructPathChoiceMessage(gameLogic.getCurrentPlayer().getPlayerNumber());
                gameLogic.setResponseMessage(replyMessage);

                return new PathChoiceState();
            }
            return new HandlePlayerMoveState(); // didnt receive the correct message, looping //TODO figure out if this condition is correct
        }

        return null;
    }

    public void exit(GameLogic gameLogic){

    }
}