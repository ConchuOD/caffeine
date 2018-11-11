package ie.ucd.engac.lifegamelogic.gamestatehandling;

import ie.ucd.engac.lifegamelogic.cards.occupationcards.OccupationCard;
import ie.ucd.engac.messaging.Chooseable;
import ie.ucd.engac.messaging.DecisionResponseMessage;
import ie.ucd.engac.messaging.LifeGameMessage;
import ie.ucd.engac.messaging.LifeGameMessageTypes;

public class GraduationState extends GameState {
	
	@Override
	public void enter(GameLogic gameLogic) {
		// player has no card at this stage, so no reason to return the old one
		// Take the two top college career cards off the top the deck

		// Give choice of top college cards			
		OccupationCard firstCollegeCareerCard = gameLogic.getTopCollegeCareerCard();
        OccupationCard secondCollegeCareerCard = gameLogic.getTopCollegeCareerCard();

        // Construct a message with these choices
        LifeGameMessage replyMessage = setupChoiceAndMessage(
            gameLogic.getCurrentPlayer().getPlayerNumber(),
            (Chooseable) firstCollegeCareerCard,
            (Chooseable) secondCollegeCareerCard,
            "Choose new college career card");

        // Need to store both choices so that we can assign the chosen one to the
        // correct player,
        // and push the unchosen one to the bottom of the correct deck.
        gameLogic.setResponseMessage(replyMessage);
	}

	@Override
	public GameState handleInput(GameLogic gameLogic, LifeGameMessage lifeGameMessage) {
		if(lifeGameMessage.getLifeGameMessageType() == LifeGameMessageTypes.OptionDecisionResponse) {
		    int choiceIndex = ((DecisionResponseMessage) lifeGameMessage).getChoiceIndex();

            //call static method in superclass to set/return card
            actOnOccupationCardChoice(gameLogic, choiceIndex);

			String graduationStateEndMessage = "You chose the " + gameLogic.getCurrentPlayer().getOccupationCard().getOccupationCardType() + " card."; //TODO many chained methods
			
			return new EndTurnState(graduationStateEndMessage);
		}		
		return null;
	}

	@Override
	public void exit(GameLogic gameLogic) {}
}
