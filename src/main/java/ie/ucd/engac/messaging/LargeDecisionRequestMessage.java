package ie.ucd.engac.messaging;

import java.util.ArrayList;

public class LargeDecisionRequestMessage extends LifeGameMessage {
	// Need to tell what is to be chosen between
	private final int relatedPlayerIndex;
	private final ArrayList<Chooseable> choices;

	public LargeDecisionRequestMessage(ArrayList<Chooseable> choices, int relatedPlayerIndex) {
		super(LifeGameMessageTypes.LargeDecisionRequest);
		this.relatedPlayerIndex = relatedPlayerIndex;
		this.choices = choices;
	}
	
	public int getRelatedPlayer() {
		return relatedPlayerIndex;
	}
	
	public ArrayList<Chooseable> getChoices(){
		return choices;
	}
}