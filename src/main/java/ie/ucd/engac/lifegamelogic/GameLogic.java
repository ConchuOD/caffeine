package ie.ucd.engac.lifegamelogic;

import ie.ucd.engac.lifegamelogic.banklogic.Bank;
import ie.ucd.engac.lifegamelogic.cards.actioncards.ActionCard;
import ie.ucd.engac.lifegamelogic.cards.housecards.HouseCard;
import ie.ucd.engac.lifegamelogic.cards.occupationcards.OccupationCard;
import ie.ucd.engac.lifegamelogic.cards.occupationcards.OccupationCardTypes;
import ie.ucd.engac.lifegamelogic.gameboard.BoardLocation;
import ie.ucd.engac.lifegamelogic.gameboard.LogicGameBoard;
import ie.ucd.engac.lifegamelogic.gameboard.gameboardtiles.GameBoardTile;
import ie.ucd.engac.lifegamelogic.gamestates.GameState;
import ie.ucd.engac.lifegamelogic.gamestates.PathChoiceState;
import ie.ucd.engac.lifegamelogic.playerlogic.Player;
import ie.ucd.engac.lifegamelogic.playerlogic.PlayerColour;
import ie.ucd.engac.lifegamelogic.playerlogic.PlayerMoneyComparator;
import ie.ucd.engac.messaging.LifeGameMessage;
import ie.ucd.engac.messaging.ShadowPlayer;

import java.util.ArrayList;

public class GameLogic {
	private Bank bank;
	private Spinnable spinner;
	private ArrayList<Player> players;
	private ArrayList<Player> retiredPlayers;
	private LogicGameBoard gameBoard;
	private int currentPlayerIndex;
	private int numberOfUnConfiguredPlayers;
	private LifeGameMessage currentLifeGameMessageResponse;
	
	private GameState currentState;

    /**
     * Constructor
     * @param gameBoard the board to use for this game
     * @param numPlayers the number of players
     * @param spinner spinnable object to determine player rolls
     */
	public GameLogic(LogicGameBoard gameBoard, int numPlayers, Spinnable spinner) {
		this.gameBoard = gameBoard;
		this.spinner = spinner;
		bank = new Bank();
		
		initialisePlayers(numPlayers);
		
        retiredPlayers = new ArrayList<>();
		
		currentState = new PathChoiceState();
		currentState.enter(this);
	}

    /**
     * sets the spinnable object
     */
    public void setSpinner(Spinnable spinner) {
        this.spinner = spinner;
    }

    /**
     * function to handle messages from the user interface and compute the response
     * @param lifeGameMessage message from the user interface
     * @return message from the logic to the user interface
     */
    public LifeGameMessage handleInput(LifeGameMessage lifeGameMessage) {
		GameState nextGameState = currentState.handleInput(this, lifeGameMessage);
		
		if(nextGameState != null) {
			currentState = nextGameState;
			currentState.enter(this);
		}
		
		return getLifeGameMessageResponse();
	}

	// Player related

    /**
     * create a shadow player object based on a player
     * @param playerIndex the index of the player to create the shadow player based on
     * @return shadow player object
     */
    public ShadowPlayer getShadowPlayer(int playerIndex){
	    Player player = getPlayerByIndex(playerIndex);

	    int playerNumber = player.getPlayerNumber();
        PlayerColour playerColour = player.getPlayerColour();
        int martialStatus = player.getMaritalStatus().toInt();
        int numberOfDependants = player.getNumberOfDependants();
        OccupationCard occupationCard = player.getOccupationCard();
        ArrayList<HouseCard> houseCards = player.getHouseCards();
        int numLoans = player.getNumberOfLoans(this);
        int loans = player.getTotalLoansOutstanding(this);
        int currentMoney = player.getCurrentMoney();
        int numActionCards = player.getActionCards().size();
        GameBoardTile gameBoardTile = gameBoard.getGameBoardTileFromID(player.getCurrentLocation());

        return new ShadowPlayer(playerNumber,playerColour, martialStatus, numberOfDependants, occupationCard, houseCards, numLoans, loans, currentMoney, numActionCards, gameBoardTile);
    }

    /**
     * subtract from the balance of the current player
     * @param amountToSubtract non negative amount to subtract
     */
    public void subtractFromCurrentPlayersBalance(int amountToSubtract){ //TODO this function should not be public but is needed public for tests
        subtractFromPlayersBalance(currentPlayerIndex, amountToSubtract);
    }

    /**
     * subtract from a given players balance
     * @param playerIndex the player to deduct the money from
     * @param amountToSubtract non negative amount to subtract
     */
    public void subtractFromPlayersBalance(int playerIndex, int amountToSubtract){
        if (amountToSubtract >= 0) {
            Player player = getPlayerByIndex(playerIndex);
            while (player.getCurrentMoney() - amountToSubtract < 0) { // user has to take out loans or else they go bankrupt
                player.addToBalance(takeOutALoan(player.getPlayerNumber()));
            }
            player.subtractFromBalance(amountToSubtract);
        }
    }

    private void initialisePlayers(int numPlayers) {
        players = new ArrayList<>();

        for (int playerIndex = 0; playerIndex < numPlayers; playerIndex++) {
            players.add(new Player(playerIndex+1));
        }
        // All of these players require the user to set some initial characteristics
        numberOfUnConfiguredPlayers = numPlayers;
    }

    public ArrayList<Player> getPlayers(){
    	return players;
    }

    public int getCurrentPlayerIndex(){
	    return currentPlayerIndex;
    }
	
	public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void setNextPlayerToCurrent() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public int getNumberOfPlayers() {
        return players.size();
    }

    public Player getPlayerByIndex(int playerIndex) {
        if(playerIndex < 0 || playerIndex > players.size()) {
            return null;
        }
        return players.get(playerIndex);
    }

    public int getNextPlayerIndex(int playerIndex) {
        if(playerIndex < 0 || playerIndex > players.size()) {
            return -1;
        }
        return (playerIndex + 1) % players.size();
    }
    
    public int getNumberOfUninitialisedPlayers() {
        return numberOfUnConfiguredPlayers;
    }

    public void decrementNumberOfUninitialisedPlayers() {
        if(numberOfUnConfiguredPlayers > 0) {
            numberOfUnConfiguredPlayers--;
        }
    }

    public Spinnable getSpinner() {
    	return spinner;
    }
    
    // Retirement related
    public int retireCurrentPlayer() throws RuntimeException {
	    try{
	        Player playerToRetire = players.get(currentPlayerIndex);
	        int retirementBonus = playerToRetire.computeRetirementBonuses(getNumberOfRetiredPlayers());
            playerToRetire.addToBalance(retirementBonus);

            int loanRepaymentCost = getTotalOutstandingLoans(playerToRetire.getPlayerNumber());
            subtractFromCurrentPlayersBalance(loanRepaymentCost);
            repayAllLoans(playerToRetire.getPlayerNumber());

            players.remove(currentPlayerIndex);
            retiredPlayers.add(playerToRetire);

            if(players.size()>0){ //correct the index after removal unless the game is over
                correctCurrentPlayerIndexAfterRetirement();
            }
            return playerToRetire.getCurrentMoney();
        }
        catch (IndexOutOfBoundsException ex){
	        throw new RuntimeException("Attempted to retire a player that does not exist. No player at index: " + currentPlayerIndex);
        }
    }

    private void correctCurrentPlayerIndexAfterRetirement(){
        currentPlayerIndex = (currentPlayerIndex-1)%players.size();
    }

    private int getNumberOfRetiredPlayers(){
	    return retiredPlayers.size();
    }

    public ArrayList<Player> getRankedRetiredPlayers(){
	    ArrayList<Player> ranked = retiredPlayers;
        ranked.sort(new PlayerMoneyComparator());
        return ranked;
    }

    // Career related
    public void movePlayerToInitialCollegeCareerPath(int playerIndex) {
        BoardLocation collegeCareerPathInitialLocation = gameBoard.getOutboundNeighbours(new BoardLocation("a")).get(1);

        players.get(playerIndex).setCurrentLocation(collegeCareerPathInitialLocation);
    }

    public void movePlayerToInitialCareerPath(int playerIndex) {
        BoardLocation careerPathInitialLocation = gameBoard.getOutboundNeighbours(new BoardLocation("a")).get(0);

        players.get(playerIndex).setCurrentLocation(careerPathInitialLocation);
    }

    // GameBoard related
    public LogicGameBoard getGameBoard() {
		return gameBoard;
	}

    public ArrayList<BoardLocation> getAdjacentForwardLocations(BoardLocation currentBoardLocation) {
        return gameBoard.getOutboundNeighbours(currentBoardLocation);
    }

	// Bank related
    public void extractMoneyFromBank(int amountToExtract) {
        bank.extractMoney(amountToExtract);
    }

    public int getNumberOfLoans(int playerNumber) {
        return bank.getNumberOfOutstandingLoans(playerNumber);
    }

    private void repayAllLoans(int playerNumber){
        bank.repayAllLoans(playerNumber);
    }

    public int getTotalOutstandingLoans(int playerNumber) {
        return bank.getOutstandingLoanTotal(playerNumber);
    }

    public int takeOutALoan(int playerNumber){
	    return bank.takeOutALoan(playerNumber);
    }

    // Message related
    private LifeGameMessage getLifeGameMessageResponse() {
        return currentLifeGameMessageResponse;
    }

    public void setResponseMessage(LifeGameMessage lifeGameMessage) {
        currentLifeGameMessageResponse = lifeGameMessage;
    }

    // Occupation card related
    public OccupationCard getTopStandardCareerCard() {
        return bank.getTopStandardCareerCard();
    }

    public OccupationCard getTopCollegeCareerCard() {
        return bank.getTopCollegeCareerCard();
    }

    public void returnOccupationCard(OccupationCard occupationCardToBeReturned) {
        if(occupationCardToBeReturned.getOccupationCardType() == OccupationCardTypes.Career) {
            bank.returnStandardCareerCard(occupationCardToBeReturned);
        }
        else {
            bank.returnCollegeCareerCard(occupationCardToBeReturned);
        }
    }

    // Action card related
    public ActionCard getTopActionCard() {
        return bank.getTopActionCard();
    }

    // House card related
    public HouseCard getTopHouseCard() {
        return bank.getTopHouseCard();
    }

    public void returnHouseCard(HouseCard houseCardToBeReturned) {
        bank.returnHouseCard(houseCardToBeReturned);
    }
}
