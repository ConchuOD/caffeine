package ie.ucd.engAC.LifeGameLogic.Cards.ActionCards;

import ie.ucd.engAC.LifeGameLogic.Cards.CardConfigHandler;
import ie.ucd.engAC.LifeGameLogic.Cards.CardDeck;
import ie.ucd.engAC.LifeGameLogic.Cards.CareerCards.CareerCard;
import ie.ucd.engAC.LifeGameLogic.Cards.CareerCards.DefaultCareerCardConfigHandler;

public class CareerCardDeck extends CardDeck {
	private final String configString;
	
	public CareerCardDeck(String configString) {
		super();
		
		this.configString = configString;
		
		initialiseCards();
	}
	
	private void initialiseCards() {
		CardConfigHandler<CareerCard> careerCardConfigHandler = new DefaultCareerCardConfigHandler(configString);
		
		careerCardConfigHandler.initialiseCards();
	}
}
