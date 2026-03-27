package service;

import javax.swing.Timer;
import model.Room;
import model.Booking;
import model.User;
import java.util.ArrayList;
import java.util.List;

public class BookingSystem {
    private List<Room> rooms = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();
    private int bookingCounter = 1;

    public BookingSystem() {
        Timer timer = new Timer(1000, e -> {
            for (Booking b : bookings) {
                if (b.isActive()) {
                    Room r = b.getRoom();
                    r.tick();

                    if (r.getRemainingSeconds() <= 0) {
                        b.cancelBooking();
                    }
                }
            }
        });
        timer.start();
    }

    public void addRoom(Room room) { rooms.add(room); }


    public List<Room> getAvailableRooms() {
        List<Room> available = new ArrayList<>();
        for (Room r : rooms) {
            if (!r.isBooked()) {
                available.add(r);
            }
        }
        return available;
    }

    public String getAllRoomsDetails() {
        StringBuilder sb = new StringBuilder("=== University Room List ===\n\n");
        for (Room r : rooms) {
            String status = r.isBooked() ? "[Booked]" : "[Available]";
            sb.append("Room ID: ").append(r.getRoomId())
                    .append(" | ").append(r.getRoomName())
                    .append(" (Cap: ").append(r.getCapacity()).append(" pax)\n");
            sb.append("Status : ").append(status).append("\n");

            if (r.isBooked()) {
                int h = r.getRemainingSeconds() / 3600;
                int m = (r.getRemainingSeconds() % 3600) / 60;
                int s = r.getRemainingSeconds() % 60;
                sb.append(String.format("Time Left: %02d:%02d:%02d\n", h, m, s));
            }

            sb.append("Policy : ").append(r.getRoomPolicy()).append("\n");
            sb.append("--------------------------------------------------\n");
        }
        return sb.toString();
    }

    public Room searchRoom(String roomId) {
        for (Room r : rooms) {
            if (r.getRoomId().equalsIgnoreCase(roomId)) return r;
        }
        return null;
    }

    public String createBooking(String roomId, String studentName, int hours) {
        Room room = searchRoom(roomId);
        if (room == null) return "Error: Room ID not found.";
        if (room.isBooked()) return "Error: This room is already booked.";


        if (hours <= 0) {
            return "Error: Booking duration must be at least 1 hour.";
        }
        if (hours > 10) {
            return "Error: Maximum booking duration is 10 hours.";
        }


        User user = new User(studentName);
        String bId = "B" + String.format("%03d", bookingCounter++);
        Booking newBooking = new Booking(bId, user, room, hours);
        bookings.add(newBooking);

        room.setRemainingSeconds(hours * 3600);

        return "RECEIPT|" + bId + "|" + room.getRoomName() + "|" + studentName + "|" + hours;
    }

    public String getAllBookingsDetails() {
        if (bookings.isEmpty()) return "No booking records found.";
        StringBuilder sb = new StringBuilder("--- Booking History ---\n\n");
        for (Booking b : bookings) {
            boolean isCancelled = !b.isActive();
            sb.append("ID: ").append(b.getBookingId())
                    .append(" | Room: ").append(b.getRoom().getRoomName())
                    .append(" | Student: ").append(b.getUser().getStudentName())
                    .append(" | Status: ").append(isCancelled ? "[Cancelled]" : "[Active]")
                    .append("\n");
        }
        return sb.toString();
    }
    public List<Booking> getActiveBookings() {
        List<Booking> activeList = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.isActive()) {
                activeList.add(b);
            }
        }
        return activeList;
    }

    public String cancelBookingLogic(String bId) {
        for (Booking b : bookings) {
            if (b.getBookingId().equalsIgnoreCase(bId)) {
                if (b.cancelBooking()) {
                    return "Successfully cancelled booking ID " + bId + "!";
                } else {
                    return "This booking has already been cancelled or expired.";
                }
            }
        }
        return "Error: Booking ID not found.";
    }
}