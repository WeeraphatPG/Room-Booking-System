package model;

public class MeetingRoom extends Room {
    public MeetingRoom(String id, String name, int capacity) {
        super(id, name, capacity);
    }

    @Override
    public String getRoomPolicy() {
        return "Standard Policy: Please keep the room clean after use.";
    }
}