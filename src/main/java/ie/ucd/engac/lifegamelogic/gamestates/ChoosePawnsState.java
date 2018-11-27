package ie.ucd.engac.lifegamelogic.gamestates;

import ie.ucd.engac.lifegamelogic.GameLogic;
import ie.ucd.engac.messaging.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ChoosePawnsState extends GameState {

    private ArrayList<Pawn> pawns;
    private HashSet<String> remainingColourChoices;
    private HashMap<String,Color>  colourMap; //TODO AWT colours
    private ArrayList<Chooseable> outgoingChoices;
    private int initialPlayerIndex;
    private int awaitingInfoFromPlayerIndex;

    @Override
    public void enter(GameLogic gameLogic) {
        // Must set the response message to a choice between possible colours


    }

    @Override
    public GameState handleInput(GameLogic gameLogic, LifeGameMessage lifeGameMessage) {
        GameState nextState = null;

        if(lifeGameMessage.getLifeGameMessageType() == LifeGameMessageTypes.StartupMessage){
            awaitingInfoFromPlayerIndex = gameLogic.getCurrentPlayerIndex();
            initialPlayerIndex = awaitingInfoFromPlayerIndex;

            //TODO remove hard coded colours?
            setupColours();

            outgoingChoices = getRemainingChooseableStrings();

            // First message with current player's number
            int playerNumber = gameLogic.getPlayerByIndex(awaitingInfoFromPlayerIndex).getPlayerNumber();

            LifeGameMessageTypes requestType = LifeGameMessageTypes.LargeDecisionRequest;
            LifeGameMessage replyMessage = new DecisionRequestMessage(outgoingChoices,
                    playerNumber, "Player "+playerNumber+", pick a pawn colour.", requestType);
            gameLogic.setResponseMessage(replyMessage);
        }
        else if(lifeGameMessage.getLifeGameMessageType() == LifeGameMessageTypes.LargeDecisionResponse){
                // Check who this reply relates to
                parsePlayerResponse((DecisionResponseMessage) lifeGameMessage, gameLogic.getPlayerByIndex(awaitingInfoFromPlayerIndex).getPlayerNumber());

                int nextPlayer = gameLogic.getNextPlayerIndex(awaitingInfoFromPlayerIndex);

                if (nextPlayer == initialPlayerIndex) {
                    LifeGameMessage replyMessage = new UIConfigMessage(pawns, "Game Ready!");
                    gameLogic.setResponseMessage(replyMessage);
                }
                else{
                    awaitingInfoFromPlayerIndex = gameLogic.getNextPlayerIndex(awaitingInfoFromPlayerIndex);
                    outgoingChoices = getRemainingChooseableStrings();

                    // First message with current player's number
                    int playerNumber = gameLogic.getPlayerByIndex(awaitingInfoFromPlayerIndex).getPlayerNumber();

                    LifeGameMessageTypes requestType = LifeGameMessageTypes.LargeDecisionRequest;
                    LifeGameMessage replyMessage = new DecisionRequestMessage(outgoingChoices,
                            playerNumber, "Player "+playerNumber+", pick a pawn colour.", requestType);
                    gameLogic.setResponseMessage(replyMessage);
                }
        }
        else if(lifeGameMessage.getLifeGameMessageType() == LifeGameMessageTypes.AckResponse){
            LifeGameMessage replyMessage = constructPathChoiceMessage(gameLogic.getCurrentPlayer().getPlayerNumber());
            gameLogic.setResponseMessage(replyMessage);
            nextState = new PathChoiceState();
        }

        return nextState;
    }

    @Override
    public void exit(GameLogic gameLogic) {

    }

    private void parsePlayerResponse(DecisionResponseMessage lifeGameMessage, int relatedPlayerNumber) {
        // Must set what the player has chosen, and remove what they have chosen from the allowable remaining
        // choices
        String selectedColourName = outgoingChoices.get(lifeGameMessage.getChoiceIndex()).displayChoiceDetails();
        Color colour = colourMap.get(selectedColourName);
        if(colour != null) {
            pawns.add(new Pawn(0.0, 0.0, colour, relatedPlayerNumber, 0));

            // Remove the selected number from the set of allowable numbers
            remainingColourChoices.remove(selectedColourName);
        }
        else{
            System.err.println("Invalid colour selection.");
        }
    }

    private ArrayList<Chooseable> getRemainingChooseableStrings() {
        ArrayList<Chooseable> chooseableStrings = new ArrayList<>();

        for (String chooseableString : remainingColourChoices) {
            chooseableStrings.add(new ChooseableString(chooseableString));
        }

        return chooseableStrings;
    }

    private void setupColours(){
        pawns = new ArrayList<>();
        colourMap = new HashMap<>();
        remainingColourChoices = new HashSet<>();
        //pink,  blue,  green  and  yellow

        colourMap.put("Pink",Color.PINK);
        remainingColourChoices.add("Pink");

        colourMap.put("Blue",Color.BLUE);
        remainingColourChoices.add("Blue");

        colourMap.put("Green",Color.GREEN);
        remainingColourChoices.add("Green");

        colourMap.put("Yellow",Color.YELLOW);
        remainingColourChoices.add("Yellow");
    }

}