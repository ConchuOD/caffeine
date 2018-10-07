package ie.ucd.engAC.LifeGameLogic;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import ie.ucd.engAC.LifeGameLogic.Cards.CardDeck;
import ie.ucd.engAC.LifeGameLogic.Cards.ActionCards.ActionCardDeck;
import ie.ucd.engAC.LifeGameLogic.Cards.ActionCards.CareerCardDeck;
import ie.ucd.engAC.LifeGameLogic.Cards.CollegeCareerCards.CollegeCareerCardDeck;
import ie.ucd.engAC.LifeGameLogic.Cards.HouseCards.HouseCardDeck;

public class Bank {

	private ActionCardDeck actionCardDeck;
	private HouseCardDeck houseCardDeck;
	private CareerCardDeck careerCardDeck;
	private CollegeCareerCardDeck collegeCareerCardDeck;

	// Pull in the action card deck config from the config file.
	// Where should this config be stored, what format should it be in?
	public Bank() {
		InitialiseDecks();
	}

	private void InitialiseDecks() {
		// Initialise decks of different types
		actionCardDeck = new ActionCardDeck();
		actionCardDeck.shuffle();

		String houseCardDeckConfigFileLocation = "src/main/resources/HouseCardConfig.json";
		String careerCardDeckConfigFileLocation = "src/main/resources/CareerCardConfig.json";
		String collegeCareerCardDeckConfigFileLocation = "src/main/resources/CollegeCareerCardConfig.json";
		
		byte[] encodedHouseCardDeckConfigContent = new byte[0];
		byte[] encodedCareerCardDeckConfigContent = new byte[0];
		byte[] encodedCollegeCareerCardDeckConfigContent = new byte[0];
		
		try {
			encodedHouseCardDeckConfigContent = Files.readAllBytes(Paths.get(houseCardDeckConfigFileLocation));
			encodedCareerCardDeckConfigContent = Files.readAllBytes(Paths.get(careerCardDeckConfigFileLocation));
			encodedCollegeCareerCardDeckConfigContent = Files.readAllBytes(Paths.get(collegeCareerCardDeckConfigFileLocation));
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		Charset charset = Charset.defaultCharset();
		
		String houseCardDeckConfigString = new String(encodedHouseCardDeckConfigContent, charset);
		String careerCardDeckConfigString = new String(encodedCareerCardDeckConfigContent, charset);
		String collegeCareerCardDeckConfigString = new String(encodedCollegeCareerCardDeckConfigContent, charset);

		houseCardDeck = new HouseCardDeck(houseCardDeckConfigString);
		houseCardDeck.shuffle();
		careerCardDeck = new CareerCardDeck(careerCardDeckConfigString);
		careerCardDeck.shuffle();
		collegeCareerCardDeck = new CollegeCareerCardDeck(collegeCareerCardDeckConfigString);
		collegeCareerCardDeck.shuffle();
	}
}
