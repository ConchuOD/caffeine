package ie.ucd.engac.lifegamelogic.gamestatehandling;

import ie.ucd.engac.lifegamelogic.cards.housecards.HouseCard;
import ie.ucd.engac.lifegamelogic.playerlogic.Player;
import ie.ucd.engac.messaging.*;

import java.util.ArrayList;

public class HouseSaleState implements GameState { //TODO this entire class

    int choiceIndex;

    @Override
    public void enter(GameLogic gameLogic) {
        // Get the CareerCards owned by this player

        ArrayList<HouseCard> cards = gameLogic.getCurrentPlayer().getHouseCards();
        ArrayList<Chooseable> choices = new ArrayList<>();
        for (HouseCard houseCard:cards){
            choices.add( (Chooseable) houseCard );
        }
        String eventMessage = "Which house would you like to sell?";
        LifeGameMessage replyMessage = new LargeDecisionRequestMessage(choices,gameLogic.getCurrentPlayer().getPlayerNumber(), eventMessage);
        // Need to store both choices so that we can assign the chosen one to the
        // correct player,
        // and push the unchosen one to the bottom of the correct deck.
        gameLogic.setResponseMessage(replyMessage);

    }

    @Override
    @SuppressWarnings("Duplicates")
    public GameState handleInput(GameLogic gameLogic, LifeGameMessage lifeGameMessage) {

        //TODO potential exception if erroneous spin reponse received
        if (lifeGameMessage.getLifeGameMessageType() == LifeGameMessageTypes.LargeDecisionResponse) {
            LargeDecisionResponseMessage choiceMessage = (LargeDecisionResponseMessage) lifeGameMessage;

            choiceIndex = choiceMessage.getChoiceIndex();

            int playNum = gameLogic.getCurrentPlayer().getPlayerNumber();
            String eventMessage = "Player " + playNum + ", spin to determine sale price.";
            SpinRequestMessage spinRequestMessage = new SpinRequestMessage(gameLogic.getShadowPlayer(gameLogic.getCurrentPlayerIndex()), playNum, eventMessage);
            gameLogic.setResponseMessage(spinRequestMessage);

            return null;
        }
        else if(lifeGameMessage.getLifeGameMessageType() == LifeGameMessageTypes.SpinResponse) {
            int spinNum = gameLogic.getSpinner().spinTheWheel();
            Player player = gameLogic.getCurrentPlayer();
            player.sellHouseCard(choiceIndex,spinNum);

            return new EndTurnState();
        }
        return null;
    }

    @Override
    public void exit(GameLogic gameLogic) {
        // Must clear the sent message?
    }
}