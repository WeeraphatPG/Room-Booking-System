package model;

public class Booking implements Cancel {
    private String bookingId;
    private User user; // Association
    private Room room; // Association
    private int hours;
    private boolean isActive;

    public Booking(String bookingId, User user, Room room, int hours) {
        this.bookingId = bookingId;
        this.user = user;
        this.room = room;
        this.hours = hours;
        this.isActive = true;
        this.room.setBooked(true);
    }

    public Room getRoom(){
        return this.room;
    }

    public String getBookingId() { return bookingId; }

    @Override
    public boolean cancelBooking() {
        if (isActive) {
            isActive = false;
            room.setBooked(false);
            return true;
        }
        return false;
    }
    public User getUser() { return this.user; }

    public boolean isActive() {
        return this.isActive;
    }
}