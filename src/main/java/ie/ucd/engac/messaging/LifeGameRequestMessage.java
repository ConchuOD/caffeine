package ie.ucd.engac.messaging;

public abstract class LifeGameRequestMessage extends LifeGameMessage {

    private String eventMsg;

    LifeGameRequestMessage(LifeGameMessageTypes lifeGameMessageType, String eventMsg){
        super(lifeGameMessageType);
        this.eventMsg = eventMsg;
    }

    public String getEventMsg(){
        return eventMsg;
    }
}