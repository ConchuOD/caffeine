package ie.ucd.engac.messaging;

public class LargeDecisionResponseMessage extends LifeGameResponseMessage {
    private final int choiceIndex;

    public LargeDecisionResponseMessage(int choiceIndex) {
        super(LifeGameMessageTypes.LargeDecisionResponse);

        this.choiceIndex = choiceIndex;
    }

    public int getChoiceIndex() {
        return choiceIndex;
    }
}
