package ie.ucd.engac.lifegamelogic.gamestates;

import ie.ucd.engac.GameConfig;
import ie.ucd.engac.lifegamelogic.GameLogic;
import ie.ucd.engac.messaging.LifeGameMessage;
import ie.ucd.engac.messaging.LifeGameMessageTypes;
import ie.ucd.engac.messaging.LifeGameRequestMessage;
import ie.ucd.engac.messaging.SpinResultMessage;

import java.util.ArrayList;
import java.util.HashMap;

public class SpinToWinGetWinnerState extends GameState {
	private static final int SPIN_TO_WIN_PRIZE_NOT_WON = -1;
	
	private final HashMap<Integer, ArrayList<Integer>> playerNumberChoiceMap;
	private int currentPlayerSpinningTheWheelIndex;
	private int numberSpun = 0;
	private boolean spinResultAvailable;
	
	SpinToWinGetWinnerState(HashMap<Integer, ArrayList<Integer>> playerNumberChoiceMap) {
		this.playerNumberChoiceMap = playerNumberChoiceMap; 
	}
	
	@Override
	public void enter(GameLogic gameLogic) {
		currentPlayerSpinningTheWheelIndex = gameLogic.getCurrentPlayerIndex();
		spinResultAvailable = false;
	}

	@Override
	public GameState handleInput(GameLogic gameLogic, LifeGameMessage lifeGameMessage) {

		if(lifeGameMessage.getLifeGameMessageType() == LifeGameMessageTypes.SpinResponse) {
			// Must keep track of the player that is currently spinning

			numberSpun = gameLogic.getSpinner().spinTheWheel();
            LifeGameMessage replyMessage = new SpinResultMessage(numberSpun);
            gameLogic.setResponseMessage(replyMessage);
			spinResultAvailable = true;
			return null;
		}
		else if(lifeGameMessage.getLifeGameMessageType() == LifeGameMessageTypes.AckResponse && spinResultAvailable) {
			
			int winningPlayerIndex = parseSpinToWinAction(numberSpun);
			
			if(winningPlayerIndex >= 0) {			
				// Update the player that won with the amount of money necessary
				assignSpinToWinPrize(gameLogic, winningPlayerIndex);
				
				// Set the next state to HandlePlayerMoveState, 
				// Set the message to a SpinRequest, shadow player is the one that won the game

				int spinToWinPrizeMoneyThousands = GameConfig.spin_to_win_prize_money / 1000;

				String eventMsg = "You won " + spinToWinPrizeMoneyThousands + "K," +
						" player " + gameLogic.getPlayerByIndex(winningPlayerIndex).getPlayerNumber() +
						". Player " + gameLogic.getCurrentPlayer().getPlayerNumber() +
					    "'s turn is over.";
				LifeGameMessage responseMessage = new LifeGameRequestMessage(LifeGameMessageTypes.SpinRequest,
						eventMsg,
						gameLogic.getShadowPlayer(currentPlayerSpinningTheWheelIndex));

				// The below message is never sent, as you enter EndTurnState,
				// and overwrite this responseMessage before returning...
				gameLogic.setResponseMessage(responseMessage);
				
				return new EndTurnState(eventMsg);
			}
			else {
				// No one won this turn
				String eventMsg = "Spin the wheel to determine the winner";
				LifeGameMessage responseMessage = new LifeGameRequestMessage(LifeGameMessageTypes.SpinRequest,eventMsg, gameLogic.getShadowPlayer(currentPlayerSpinningTheWheelIndex)
				);
				gameLogic.setResponseMessage(responseMessage);
			}
		}
		
		return null;
	}

    private int parseSpinToWinAction(int numberSpun) {
		// Must check the values in the hash map
		for(HashMap.Entry<Integer,ArrayList<Integer>> kvp : playerNumberChoiceMap.entrySet()) {
			if(kvp.getValue().contains(numberSpun)) {
				return kvp.getKey();
			}
		}
		
		return SPIN_TO_WIN_PRIZE_NOT_WON;
	}
	
	private void assignSpinToWinPrize(GameLogic gameLogic, int winningPlayerIndex) {
		gameLogic.getPlayerByIndex(winningPlayerIndex).addToBalance(GameConfig.spin_to_win_prize_money);
	}
}
