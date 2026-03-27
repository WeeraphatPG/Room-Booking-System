package model;

public abstract class Room {
    private String roomId;
    private String roomName;
    private int capacity;
    private boolean isBooked;


    private int remainingSeconds = 0;

    public Room(String roomId, String roomName, int capacity) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.capacity = capacity;
        this.isBooked = false;
    }

    public String getRoomId() { return roomId; }
    public String getRoomName() { return roomName; }
    public int getCapacity() { return capacity; }
    public boolean isBooked() { return isBooked; }
    public void setBooked(boolean status) { this.isBooked = status; }

    public int getRemainingSeconds() { return remainingSeconds; }
    public void setRemainingSeconds(int seconds) {
        this.isBooked = (seconds > 0);
        this.remainingSeconds = seconds;
    }

    public void tick() {
        if (remainingSeconds > 0) {
            remainingSeconds--;
        }
    }

    public abstract String getRoomPolicy();
}