package ie.ucd.engac.lifegamelogic.gamestates;

import ie.ucd.engac.lifegamelogic.GameLogic;
import ie.ucd.engac.lifegamelogic.cards.occupationcards.careercards.CareerCard;
import ie.ucd.engac.lifegamelogic.playerlogic.CareerPathTypes;
import ie.ucd.engac.messaging.DecisionResponseMessage;
import ie.ucd.engac.messaging.LifeGameMessage;
import ie.ucd.engac.messaging.LifeGameMessageTypes;
import ie.ucd.engac.messaging.LifeGameRequestMessage;

public class ProcessStandardCareerState extends GameState {

	@Override
	public void enter(GameLogic gameLogic) {
		// Must send a message to transition to processStandardCareer
		gameLogic.getCurrentPlayer().setCareerPath(CareerPathTypes.StandardCareer);

		// Set the response message to "CardChoice"
		// Get the two top CareerCards
		CareerCard firstCareerCardChoice = (CareerCard) gameLogic.getTopStandardCareerCard();
		CareerCard secondCareerCardChoice = (CareerCard) gameLogic.getTopStandardCareerCard();

		LifeGameMessage replyMessage = setupChoiceAndMessage(
				gameLogic.getCurrentPlayer().getPlayerNumber(),
				firstCareerCardChoice, secondCareerCardChoice,
				"Choose career card", gameLogic.getCurrentShadowPlayer());

		// Need to store both choices so that we can assign the chosen one to the
		// correct player,
		// and push the unchosen one to the bottom of the correct deck.
		gameLogic.setResponseMessage(replyMessage);
	}

	@Override
	public GameState handleInput(GameLogic gameLogic, LifeGameMessage lifeGameMessage) {
		if(lifeGameMessage.getLifeGameMessageType() == LifeGameMessageTypes.OptionDecisionResponse) {
			DecisionResponseMessage careerCardChoiceMessage = (DecisionResponseMessage) lifeGameMessage;
			
			int choiceIndex = careerCardChoiceMessage.getChoiceIndex();

			//call static method in superclass to set/return card
            actOnOccupationCardChoice(gameLogic, choiceIndex);
			
			gameLogic.movePlayerToInitialCareerPath(gameLogic.getCurrentPlayerIndex());
			
			// Need to set the reply message to SpinRequest
			int playNum = gameLogic.getCurrentPlayer().getPlayerNumber();
			String eventMessage = "Player " + playNum + "'s turn.";
			LifeGameRequestMessage spinRequestMessage = new LifeGameRequestMessage(LifeGameMessageTypes.SpinRequest,eventMessage, gameLogic.getShadowPlayer(gameLogic.getCurrentPlayerIndex()));
			gameLogic.setResponseMessage(spinRequestMessage);

			gameLogic.decrementNumberOfUninitialisedPlayers();

			return new HandlePlayerMoveState();
		}
		
		return null;
	}

}
