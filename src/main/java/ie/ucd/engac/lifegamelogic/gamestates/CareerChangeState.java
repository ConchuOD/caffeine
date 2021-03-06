package ie.ucd.engac.lifegamelogic.gamestates;

import ie.ucd.engac.lifegamelogic.GameLogic;
import ie.ucd.engac.lifegamelogic.cards.actioncards.ActionCardTypes;
import ie.ucd.engac.lifegamelogic.cards.occupationcards.OccupationCard;
import ie.ucd.engac.lifegamelogic.playerlogic.CareerPathTypes;
import ie.ucd.engac.messaging.Chooseable;
import ie.ucd.engac.messaging.DecisionResponseMessage;
import ie.ucd.engac.messaging.LifeGameMessage;
import ie.ucd.engac.messaging.LifeGameMessageTypes;

public class CareerChangeState extends GameState {

    @Override
    public void enter(GameLogic gameLogic) {
        // Must send a message to transition to processStandardCareer
        CareerPathTypes careerPathType = gameLogic.getCurrentPlayer().getCareerPath();

        // Must return the old career card to the bottom of the deck
        OccupationCard currentOccupationCard = gameLogic.getCurrentPlayer().getOccupationCard();
        if (currentOccupationCard != null){
            // Shouldn't be null, but just in case
            gameLogic.returnOccupationCard(currentOccupationCard);
        }

        OccupationCard firstCareerCardChoice;
        OccupationCard secondCareerCardChoice;

        // Get the two top CareerCards
        firstCareerCardChoice = gameLogic.getTopRelevantOccupationCard(careerPathType);
        secondCareerCardChoice = gameLogic.getTopRelevantOccupationCard(careerPathType);

        String eventMessage = ActionCardTypes.CareerChange + " Action: Choose a new career.";

        // Set the response message to "CardChoice"
        LifeGameMessage replyMessage = setupChoiceAndMessage(
                gameLogic.getCurrentPlayer().getPlayerNumber(),
                (Chooseable) firstCareerCardChoice,
                (Chooseable) secondCareerCardChoice,
                eventMessage,
                gameLogic.getCurrentShadowPlayer());

        // Need to store both choices so that we can assign the chosen one to the
        // correct player,
        // and push the unchosen one to the bottom of the correct deck.
        gameLogic.setResponseMessage(replyMessage);
    }

    @Override
    public GameState handleInput(GameLogic gameLogic, LifeGameMessage lifeGameMessage) {
        if (lifeGameMessage.getLifeGameMessageType() == LifeGameMessageTypes.OptionDecisionResponse) {
            DecisionResponseMessage careerCardChoiceMessage = (DecisionResponseMessage) lifeGameMessage;

            int choiceIndex = careerCardChoiceMessage.getChoiceIndex();

            // Call static method in superclass to set/return card
            actOnOccupationCardChoice(gameLogic, choiceIndex);
            return new EndTurnState("Career changed!");
        }
        return null;
    }
}
