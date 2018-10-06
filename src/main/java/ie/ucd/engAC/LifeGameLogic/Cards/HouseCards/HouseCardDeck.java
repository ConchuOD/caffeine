package ie.ucd.engAC.LifeGameLogic.Cards.HouseCards;

import ie.ucd.engAC.LifeGameLogic.Cards.CardConfigHandler;
import ie.ucd.engAC.LifeGameLogic.Cards.CardDeck;

public class HouseCardDeck extends CardDeck {
	private final String configString;
	
	public HouseCardDeck(String configString) {
		super();
		
		this.configString = configString;
		
		initialiseCards();
	}
	
	private void initialiseCards() {
		CardConfigHandler<HouseCard> houseCardConfigHandler = new DefaultHouseCardConfigHandler(configString);
		
		houseCardConfigHandler.initialiseCards();
	}
}
