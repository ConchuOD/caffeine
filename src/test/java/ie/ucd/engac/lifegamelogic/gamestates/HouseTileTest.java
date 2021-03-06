package ie.ucd.engac.lifegamelogic.gamestates;

import TestOnly.TestHelpers;
import ie.ucd.engac.GameConfig;
import ie.ucd.engac.lifegamelogic.GameLogic;
import ie.ucd.engac.lifegamelogic.cards.housecards.HouseCard;
import ie.ucd.engac.lifegamelogic.cards.occupationcards.OccupationCard;
import ie.ucd.engac.lifegamelogic.gameboard.BoardLocation;
import ie.ucd.engac.lifegamelogic.playerlogic.CareerPathTypes;
import ie.ucd.engac.lifegamelogic.playerlogic.MaritalStatus;
import ie.ucd.engac.lifegamelogic.playerlogic.Player;
import ie.ucd.engac.messaging.DecisionResponseMessage;
import ie.ucd.engac.messaging.LifeGameMessage;
import ie.ucd.engac.messaging.LifeGameMessageTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HouseTileTest {
    private final int NUM_PLAYERS = 2;
    private final String PRIOR_TILE_LOCATION = "ak";
    private final String HOUSE_TILE_LOCATION = "al";

    @Test
    void testNoAction() {
        // Set up test
        GameLogic gameLogic = TestHelpers.setupTestGenericPreconditions(NUM_PLAYERS, 1);

        // Assert preconditions
        OccupationCard occupationCard = gameLogic.getPlayerByIndex(0).getOccupationCard();
        int numberOfActionCards = gameLogic.getPlayerByIndex(0).getNumberOfActionCards();
        CareerPathTypes careerPath = gameLogic.getPlayerByIndex(0).getCareerPath();
        MaritalStatus maritalStatus = gameLogic.getPlayerByIndex(0).getMaritalStatus();

        Player player = gameLogic.getCurrentPlayer();
        player.addToBalance(1000000); //ensure player can buy, we are not testing loans

        int playerInitMoney = player.getCurrentMoney();

        player.setCurrentLocation(new BoardLocation(PRIOR_TILE_LOCATION));

        // Mock messages to logic, performing move functionality
        LifeGameMessage initialMessage = new LifeGameMessage(LifeGameMessageTypes.SpinResponse);
        LifeGameMessage responseMessage = gameLogic.handleInput(initialMessage);

        assertEquals(LifeGameMessageTypes.SpinResult, responseMessage.getLifeGameMessageType(),"Expected message not received");
        LifeGameMessage spinMessage = new LifeGameMessage(LifeGameMessageTypes.AckResponse);
        responseMessage = gameLogic.handleInput(spinMessage);

        assertEquals(LifeGameMessageTypes.LargeDecisionRequest, responseMessage.getLifeGameMessageType(),"Expected message not received");

        // Provide mock UI response
        int choiceIndex = 0; //do nothing
        initialMessage = new DecisionResponseMessage(choiceIndex, LifeGameMessageTypes.LargeDecisionResponse);

        responseMessage = gameLogic.handleInput(initialMessage);
        assertEquals(responseMessage.getLifeGameMessageType(), LifeGameMessageTypes.AckRequest);

        // Assert that player 0 has been rxed no cards
        assertEquals(player.getHouseCards().size(), 0);
        assertEquals(player.getCurrentMoney(), playerInitMoney);

        assertEquals(gameLogic.getNumberOfUninitialisedPlayers(), 0);
        assertEquals(occupationCard, gameLogic.getPlayerByIndex(0).getOccupationCard());
        assertEquals(HOUSE_TILE_LOCATION, gameLogic.getPlayerByIndex(0).getCurrentLocation().getLocation());
        assertNull(gameLogic.getPlayerByIndex(0).getPendingBoardForkChoice());
        assertEquals(numberOfActionCards, gameLogic.getPlayerByIndex(0).getNumberOfActionCards());
        assertEquals(careerPath, gameLogic.getPlayerByIndex(0).getCareerPath());
        assertEquals(maritalStatus, gameLogic.getPlayerByIndex(0).getMaritalStatus());
    }

    @Test
    void testHousePurchaseLoanOptNo(){
        //PART 3 - CHOOSING A HOUSE, CANNOT AFFORD, NO LOAN
        // Set up test
        GameLogic gameLogic = TestHelpers.setupTestGenericPreconditions(NUM_PLAYERS, 1);

        OccupationCard occupationCard = gameLogic.getPlayerByIndex(0).getOccupationCard();
        int numberOfActionCards = gameLogic.getPlayerByIndex(0).getNumberOfActionCards();
        CareerPathTypes careerPath = gameLogic.getPlayerByIndex(0).getCareerPath();
        MaritalStatus maritalStatus = gameLogic.getPlayerByIndex(0).getMaritalStatus();

        //ensure player cannot afford the house
        Player player = gameLogic.getCurrentPlayer();
        gameLogic.subtractFromCurrentPlayersBalance(player.getCurrentMoney()-1);

        // Assert preconditions
        assertEquals(0,player.getHouseCards().size());

        int playerInitMoney = player.getCurrentMoney();

        player.setCurrentLocation(new BoardLocation(PRIOR_TILE_LOCATION));

        // Mock messages to logic, performing pathChoiceState functionality
        LifeGameMessage initialMessage = new LifeGameMessage(LifeGameMessageTypes.SpinResponse);
        LifeGameMessage responseMessage = gameLogic.handleInput(initialMessage);

        assertEquals(LifeGameMessageTypes.SpinResult, responseMessage.getLifeGameMessageType(),"Expected message not received");
        LifeGameMessage spinMessage = new LifeGameMessage(LifeGameMessageTypes.AckResponse);
        responseMessage = gameLogic.handleInput(spinMessage);

        assertEquals(LifeGameMessageTypes.LargeDecisionRequest,responseMessage.getLifeGameMessageType(),"Expected message not received");

        // Provide mock UI response
        int choiceIndex = 1; //buy a house
        initialMessage = new DecisionResponseMessage(choiceIndex, LifeGameMessageTypes.LargeDecisionResponse);

        responseMessage = gameLogic.handleInput(initialMessage);

        assertEquals(LifeGameMessageTypes.OptionDecisionRequest,responseMessage.getLifeGameMessageType(),"Expected message not received");

        //construct choice message
        int cardChoiceIndex = 0;
        //chose a card
        initialMessage = new DecisionResponseMessage(cardChoiceIndex,LifeGameMessageTypes.OptionDecisionResponse);
        responseMessage = gameLogic.handleInput(initialMessage);

        //cannot afford the house asked if we want a loan
        assertEquals(LifeGameMessageTypes.OptionDecisionRequest, responseMessage.getLifeGameMessageType(),"Expected message not received");

        int loanYesNo = 0; //choose no
        initialMessage = new DecisionResponseMessage(loanYesNo,LifeGameMessageTypes.OptionDecisionResponse);
        responseMessage = gameLogic.handleInput(initialMessage);
        //check we got back an end turn message
        assertEquals(LifeGameMessageTypes.AckRequest, responseMessage.getLifeGameMessageType(),"Expected message not received");

        //assert that player 0 has been rxed no cards
        assertEquals(player.getHouseCards().size(), 0);
        assertEquals(player.getCurrentMoney(), playerInitMoney);

        assertEquals(gameLogic.getNumberOfUninitialisedPlayers(), 0);
        assertEquals(occupationCard, gameLogic.getPlayerByIndex(0).getOccupationCard());
        assertEquals(HOUSE_TILE_LOCATION, gameLogic.getPlayerByIndex(0).getCurrentLocation().getLocation());
        assertNull(gameLogic.getPlayerByIndex(0).getPendingBoardForkChoice());
        assertEquals(numberOfActionCards, gameLogic.getPlayerByIndex(0).getNumberOfActionCards());
        assertEquals(careerPath, gameLogic.getPlayerByIndex(0).getCareerPath());
        assertEquals(maritalStatus, gameLogic.getPlayerByIndex(0).getMaritalStatus());
    }

    @Test
    void testHousePurchaseLoanOptYes(){
        //PART 3 - CHOOSING A HOUSE, CANNOT AFFORD, TAKE OUT LOAN
        // Set up test
        GameLogic gameLogic = TestHelpers.setupTestGenericPreconditions(NUM_PLAYERS, 1);


        OccupationCard occupationCard = gameLogic.getPlayerByIndex(0).getOccupationCard();
        int numberOfActionCards = gameLogic.getPlayerByIndex(0).getNumberOfActionCards();
        CareerPathTypes careerPath = gameLogic.getPlayerByIndex(0).getCareerPath();
        MaritalStatus maritalStatus = gameLogic.getPlayerByIndex(0).getMaritalStatus();

        //ensure player cannot afford the house
        Player player = gameLogic.getCurrentPlayer();
        gameLogic.subtractFromCurrentPlayersBalance(player.getCurrentMoney()-1);

        // Assert preconditions
        assertEquals(0,player.getHouseCards().size());

        int playerInitMoney = player.getCurrentMoney();
        assertEquals(1,playerInitMoney);

        player.setCurrentLocation(new BoardLocation(PRIOR_TILE_LOCATION));

        // Mock messages to logic, performing pathChoiceState functionality
        LifeGameMessage initialMessage = new LifeGameMessage(LifeGameMessageTypes.SpinResponse);
        LifeGameMessage responseMessage = gameLogic.handleInput(initialMessage);

        assertEquals(LifeGameMessageTypes.SpinResult, responseMessage.getLifeGameMessageType(),"Expected message not received");
        LifeGameMessage spinMessage = new LifeGameMessage(LifeGameMessageTypes.AckResponse);
        responseMessage = gameLogic.handleInput(spinMessage);

        assertEquals(LifeGameMessageTypes.LargeDecisionRequest,responseMessage.getLifeGameMessageType(),"Expected message not received");

        // Provide mock UI response
        int choiceIndex = 1; //buy a house
        initialMessage = new DecisionResponseMessage(choiceIndex, LifeGameMessageTypes.LargeDecisionResponse);

        responseMessage = gameLogic.handleInput(initialMessage);

        assertEquals(LifeGameMessageTypes.OptionDecisionRequest,responseMessage.getLifeGameMessageType(),"Expected message not received");

        //construct choice message and save the housecard for later comparison
        int cardChoiceIndex = 0;
        //chose a card
        initialMessage = new DecisionResponseMessage(cardChoiceIndex,LifeGameMessageTypes.OptionDecisionResponse);
        responseMessage = gameLogic.handleInput(initialMessage);

        //cannot afford the house asked if we want a loan
        assertEquals(LifeGameMessageTypes.OptionDecisionRequest, responseMessage.getLifeGameMessageType(),"Expected message not received");

        int loanYesNo = 1; //choose yes
        initialMessage = new DecisionResponseMessage(loanYesNo,LifeGameMessageTypes.OptionDecisionResponse);
        responseMessage = gameLogic.handleInput(initialMessage);
        //check we got back an end turn message
        assertEquals(LifeGameMessageTypes.AckRequest, responseMessage.getLifeGameMessageType(),"Expected message not received");

        //assert that player 0 has been rxed a card
        assertEquals(player.getHouseCards().size(), 1);

        //calculate players end money
        HouseCard houseCard = player.getHouseCards().get(0);
        int houseCost = houseCard.getPurchasePrice();
        double potentialDeficit = (double)(houseCost-playerInitMoney);
        int totalInLoans = (int)(GameConfig.loan_amount*Math.ceil(potentialDeficit/GameConfig.loan_amount));
        int playerEndMoney = playerInitMoney-houseCost+totalInLoans;

        //assert correct loan calculation
        assertEquals(player.getCurrentMoney(), playerEndMoney);

        assertEquals(gameLogic.getNumberOfUninitialisedPlayers(), 0);
        assertEquals(occupationCard, gameLogic.getPlayerByIndex(0).getOccupationCard());
        assertEquals(HOUSE_TILE_LOCATION, gameLogic.getPlayerByIndex(0).getCurrentLocation().getLocation());
        assertNull(gameLogic.getPlayerByIndex(0).getPendingBoardForkChoice());
        assertEquals(numberOfActionCards, gameLogic.getPlayerByIndex(0).getNumberOfActionCards());
        assertEquals(careerPath, gameLogic.getPlayerByIndex(0).getCareerPath());
        assertEquals(maritalStatus, gameLogic.getPlayerByIndex(0).getMaritalStatus());
    }

    @Test
    void testHousePurchaseNoLoanAndSale(){
        // Set up test
        GameLogic gameLogic = TestHelpers.setupTestGenericPreconditions(NUM_PLAYERS, 1);

        OccupationCard occupationCard = gameLogic.getPlayerByIndex(0).getOccupationCard();
        int numberOfActionCards = gameLogic.getPlayerByIndex(0).getNumberOfActionCards();
        CareerPathTypes careerPath = gameLogic.getPlayerByIndex(0).getCareerPath();
        MaritalStatus maritalStatus = gameLogic.getPlayerByIndex(0).getMaritalStatus();

        Player player = gameLogic.getCurrentPlayer();
        player.addToBalance(1000000); //ensure player can buy, we are not testing loans

        // Assert preconditions
        assertEquals(0, player.getHouseCards().size());

        int playerInitMoney = player.getCurrentMoney();

        player.setCurrentLocation(new BoardLocation(PRIOR_TILE_LOCATION));

        // Mock messages to logic, performing  functionality
        LifeGameMessage initialMessage = new LifeGameMessage(LifeGameMessageTypes.SpinResponse);
        LifeGameMessage responseMessage = gameLogic.handleInput(initialMessage);

        assertEquals(LifeGameMessageTypes.SpinResult, responseMessage.getLifeGameMessageType(),"Expected message not received");
        LifeGameMessage spinMessage = new LifeGameMessage(LifeGameMessageTypes.AckResponse);
        responseMessage = gameLogic.handleInput(spinMessage);

        assertEquals(LifeGameMessageTypes.LargeDecisionRequest,responseMessage.getLifeGameMessageType(),"Expected message not received");

        // Provide mock UI response
        int choiceIndex = 1; //buy a house
        initialMessage = new DecisionResponseMessage(choiceIndex, LifeGameMessageTypes.LargeDecisionResponse);
        responseMessage = gameLogic.handleInput(initialMessage);
        assertEquals(LifeGameMessageTypes.OptionDecisionRequest,responseMessage.getLifeGameMessageType(),"Expected message not received");

        //construct choice message and save the housecard for later comparison
        int cardChoiceIndex = 0;
        //chose a card
        initialMessage = new DecisionResponseMessage(cardChoiceIndex,LifeGameMessageTypes.OptionDecisionResponse);
        responseMessage = gameLogic.handleInput(initialMessage);
        //check we got back an end turn message
        assertEquals(LifeGameMessageTypes.AckRequest, responseMessage.getLifeGameMessageType(),"Expected message not received");

        HouseCard playerHouseCard = player.getHouseCards().get(0);

        //sssert that player 0 has been rxed a card, that it is the correct one & the correct $ deducted
        assertEquals(1, player.getHouseCards().size());
        HouseCard houseCard = player.getHouseCards().get(0);
        int houseCost = houseCard.getPurchasePrice();
        assertEquals(playerInitMoney-houseCost, player.getCurrentMoney());
        assertEquals(playerHouseCard.getHouseType().toString(),houseCard.getHouseType().toString(),"Wrong type of house.");

        //check that nothing else has changed
        assertEquals(0, gameLogic.getNumberOfUninitialisedPlayers());
        assertEquals(occupationCard, gameLogic.getPlayerByIndex(0).getOccupationCard());
        assertEquals(HOUSE_TILE_LOCATION, gameLogic.getPlayerByIndex(0).getCurrentLocation().getLocation());
        assertNull(gameLogic.getPlayerByIndex(0).getPendingBoardForkChoice());
        assertEquals(numberOfActionCards, gameLogic.getPlayerByIndex(0).getNumberOfActionCards());
        assertEquals(careerPath, gameLogic.getPlayerByIndex(0).getCareerPath());
        assertEquals(maritalStatus, gameLogic.getPlayerByIndex(0).getMaritalStatus());

        initialMessage = new LifeGameMessage(LifeGameMessageTypes.AckResponse);
        responseMessage = gameLogic.handleInput(initialMessage);

        assertEquals(LifeGameMessageTypes.SpinRequest, responseMessage.getLifeGameMessageType(),"Expected message not received");

        gameLogic.setNextPlayerToCurrent(); //go back to this player again
        player.setCurrentLocation(new BoardLocation(PRIOR_TILE_LOCATION)); //and move him to prior tile

        // Mock messages to logic, performing  functionality
        initialMessage = new LifeGameMessage(LifeGameMessageTypes.SpinResponse);
        responseMessage = gameLogic.handleInput(initialMessage);

        assertEquals(LifeGameMessageTypes.SpinResult, responseMessage.getLifeGameMessageType(),"Expected message not received");
        spinMessage = new LifeGameMessage(LifeGameMessageTypes.AckResponse);
        responseMessage = gameLogic.handleInput(spinMessage);

        assertEquals(LifeGameMessageTypes.LargeDecisionRequest,responseMessage.getLifeGameMessageType(),"Expected message not received");

        // Choose the house to sell.
        choiceIndex = 2; //sell a house
        initialMessage = new DecisionResponseMessage(choiceIndex, LifeGameMessageTypes.LargeDecisionResponse);
        responseMessage = gameLogic.handleInput(initialMessage);
        assertEquals(LifeGameMessageTypes.LargeDecisionRequest, responseMessage.getLifeGameMessageType(),"Expected message not received");

        //need large decision request to choose which card. Turns out we only have one.
        choiceIndex = 0;
        initialMessage = new DecisionResponseMessage(choiceIndex, LifeGameMessageTypes.LargeDecisionResponse);
        responseMessage = gameLogic.handleInput(initialMessage);

        //should now be spinning to choose the sale price
        assertEquals(LifeGameMessageTypes.SpinRequest,responseMessage.getLifeGameMessageType(),"Expected message not received");
        // Mock messages to logic, performing  functionality
        initialMessage = new LifeGameMessage(LifeGameMessageTypes.SpinResponse);
        responseMessage = gameLogic.handleInput(initialMessage);

        assertEquals(LifeGameMessageTypes.SpinResult, responseMessage.getLifeGameMessageType(),"Expected message not received");
        spinMessage = new LifeGameMessage(LifeGameMessageTypes.AckResponse);
        responseMessage = gameLogic.handleInput(spinMessage);

        assertEquals(LifeGameMessageTypes.AckRequest,responseMessage.getLifeGameMessageType(),"Expected message not received");
        //card should be sold so do the maths
        int houseSalePrice = houseCard.getSpinForSalePrice(true); //true for odd (we have spinner that only returns 1
        int playerEndMoney = playerInitMoney-houseCost+houseSalePrice;

        //check the player has the correct money and no housecard
        assertEquals(playerEndMoney,player.getCurrentMoney());
        assertEquals(0, player.getHouseCards().size());

        assertEquals(gameLogic.getNumberOfUninitialisedPlayers(), 0);
        assertEquals(occupationCard, gameLogic.getPlayerByIndex(0).getOccupationCard());
        assertEquals(HOUSE_TILE_LOCATION, gameLogic.getPlayerByIndex(0).getCurrentLocation().getLocation());
        assertNull(gameLogic.getPlayerByIndex(0).getPendingBoardForkChoice());
        assertEquals(numberOfActionCards, gameLogic.getPlayerByIndex(0).getNumberOfActionCards());
        assertEquals(careerPath, gameLogic.getPlayerByIndex(0).getCareerPath());
        assertEquals(maritalStatus, gameLogic.getPlayerByIndex(0).getMaritalStatus());
    }

    @Test
    void testHousePurchaseSecondHouse(){
        GameLogic gameLogic = TestHelpers.setupTestGenericPreconditions(NUM_PLAYERS, 1);

        OccupationCard occupationCard = gameLogic.getPlayerByIndex(0).getOccupationCard();
        int numberOfActionCards = gameLogic.getPlayerByIndex(0).getNumberOfActionCards();
        CareerPathTypes careerPath = gameLogic.getPlayerByIndex(0).getCareerPath();
        MaritalStatus maritalStatus = gameLogic.getPlayerByIndex(0).getMaritalStatus();


        Player player = gameLogic.getCurrentPlayer();
        player.addHouseCard(gameLogic.getTopHouseCard()); //give them a housecard
        player.addToBalance(1000000); //ensure player can buy, we are not testing loans

        // Assert preconditions
        assertEquals(1, player.getHouseCards().size());

        int playerInitMoney = player.getCurrentMoney();

        player.setCurrentLocation(new BoardLocation(PRIOR_TILE_LOCATION));

        // Mock messages to logic, performing pathChoiceState functionality
        LifeGameMessage initialMessage = new LifeGameMessage(LifeGameMessageTypes.SpinResponse);
        LifeGameMessage responseMessage = gameLogic.handleInput(initialMessage);

        assertEquals(LifeGameMessageTypes.SpinResult, responseMessage.getLifeGameMessageType(),"Expected message not received");
        LifeGameMessage spinMessage = new LifeGameMessage(LifeGameMessageTypes.AckResponse);
        responseMessage = gameLogic.handleInput(spinMessage);

        assertEquals(LifeGameMessageTypes.LargeDecisionRequest,responseMessage.getLifeGameMessageType(),"Expected message not received");

        // Provide mock UI response
        int choiceIndex = 1; //buy a house
        initialMessage = new DecisionResponseMessage(choiceIndex, LifeGameMessageTypes.LargeDecisionResponse);

        responseMessage = gameLogic.handleInput(initialMessage);

        assertEquals(LifeGameMessageTypes.OptionDecisionRequest,responseMessage.getLifeGameMessageType(),"Expected message not received");

        //construct choice message and save the housecard for later comparison
        int cardChoiceIndex = 0;
        //chose a card
        initialMessage = new DecisionResponseMessage(cardChoiceIndex,LifeGameMessageTypes.OptionDecisionResponse);
        responseMessage = gameLogic.handleInput(initialMessage);
        //check we got back an edn turn message
        assertEquals(LifeGameMessageTypes.AckRequest, responseMessage.getLifeGameMessageType(),"Expected message not received");

        //sssert that player 0 has been rxed a card, that it is the correct one & the correct $ deducted
        HouseCard housecard = player.getHouseCards().get(1); //get the 2nd house car
        int houseCost = housecard.getPurchasePrice();
        assertEquals(2, player.getHouseCards().size());
        assertEquals(playerInitMoney-houseCost, player.getCurrentMoney());

        //check that nothing else has changed
        assertEquals(0, gameLogic.getNumberOfUninitialisedPlayers());
        assertEquals(occupationCard, gameLogic.getPlayerByIndex(0).getOccupationCard());
        assertEquals(HOUSE_TILE_LOCATION, gameLogic.getPlayerByIndex(0).getCurrentLocation().getLocation());
        assertNull(gameLogic.getPlayerByIndex(0).getPendingBoardForkChoice());
        assertEquals(numberOfActionCards, gameLogic.getPlayerByIndex(0).getNumberOfActionCards());
        assertEquals(careerPath, gameLogic.getPlayerByIndex(0).getCareerPath());
        assertEquals(maritalStatus, gameLogic.getPlayerByIndex(0).getMaritalStatus());

    }
}