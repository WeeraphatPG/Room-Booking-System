package main;

import model.Booking;
import model.MeetingRoom;
import model.Room;
import service.BookingSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main extends JFrame {
    private BookingSystem system;

    private final Color COLOR_BG = new Color(248, 249, 250);
    private final Color COLOR_PRIMARY = new Color(59, 130, 246);
    private final Color COLOR_PRIMARY_HOVER = new Color(37, 99, 235);
    private final Color COLOR_TEXT_DARK = new Color(31, 41, 55);
    private final Color COLOR_TEXT_LIGHT = new Color(255, 255, 255);

    public Main() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("OptionPane.background", Color.WHITE);
            UIManager.put("Panel.background", Color.WHITE);
        } catch (Exception e) { e.printStackTrace(); }

        system = new BookingSystem();
        system.addRoom(new MeetingRoom("M01", "Study Room A", 5));
        system.addRoom(new MeetingRoom("M02", "Study Room B", 5));
        system.addRoom(new MeetingRoom("M03", "Study Room C", 5));
        system.addRoom(new MeetingRoom("M04", "Study Room D", 5));
        system.addRoom(new MeetingRoom("M05", "Study Room E", 5));

        setupUI();
    }

    private void setupUI() {
        setTitle("University Room Booking System");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setBackground(COLOR_BG);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(new EmptyBorder(60, 0, 40, 0));

        JLabel titleLabel = new JLabel("Room Booking");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("prototype");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subLabel.setForeground(new Color(107, 114, 128));
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(titleLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        topPanel.add(subLabel);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(COLOR_BG);
        centerPanel.setLayout(new GridLayout(2, 2, 40, 40));
        centerPanel.setBorder(new EmptyBorder(10, 150, 40, 150));

        JButton btnOption1 = createModernButton("View Rooms");
        JButton btnOption2 = createModernButton("Book a Room");
        JButton btnOption3 = createModernButton("View Bookings");
        JButton btnOption4 = createModernButton("Cancel Booking");

        centerPanel.add(btnOption1);
        centerPanel.add(btnOption2);
        centerPanel.add(btnOption3);
        centerPanel.add(btnOption4);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(COLOR_BG);
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(new EmptyBorder(0, 0, 60, 0));

        JButton btnExit = createModernButton("Exit System");
        btnExit.setPreferredSize(new Dimension(200, 50));
        btnExit.setBackground(new Color(239, 68, 68));
        btnExit.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnExit.setBackground(new Color(220, 38, 38)); }
            public void mouseExited(MouseEvent e) { btnExit.setBackground(new Color(239, 68, 68)); }
        });

        bottomPanel.add(btnExit);
        add(bottomPanel, BorderLayout.SOUTH);


        btnOption1.addActionListener(e -> showMessage(system.getAllRoomsDetails(), "Available Rooms"));

        btnOption2.addActionListener(e -> {

            List<Room> availableRooms = system.getAvailableRooms();


            if (availableRooms.isEmpty()) {
                showMessage("Sorry, all rooms are currently booked.\nPlease try again later.", "No Rooms Available");
                return;
            }


            String[] roomOptions = new String[availableRooms.size()];
            for (int i = 0; i < availableRooms.size(); i++) {
                Room r = availableRooms.get(i);
                roomOptions[i] = r.getRoomId() + " - " + r.getRoomName() + " (" + r.getCapacity() + " pax)";
            }


            JComboBox<String> roomDropdown = new JComboBox<>(roomOptions);
            JTextField nameField = new JTextField(10);
            JTextField hoursField = new JTextField(10);

            JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
            panel.setBackground(Color.WHITE);
            panel.add(new JLabel("Select Room:"));        panel.add(roomDropdown);
            panel.add(new JLabel("Student Name:"));       panel.add(nameField);
            panel.add(new JLabel("Duration (Hours):"));   panel.add(hoursField);

            int option = JOptionPane.showConfirmDialog(this, panel, "Booking Form", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                try {

                    String selectedOption = (String) roomDropdown.getSelectedItem();
                    String roomId = selectedOption.split(" - ")[0];
                    String result = system.createBooking(
                            roomId, nameField.getText(), Integer.parseInt(hoursField.getText())
                    );

                    if (result.startsWith("RECEIPT|")) {
                        String[] data = result.split("\\|");
                        showReceipt(data[1], data[2], data[3], data[4]);
                    } else {
                        showMessage(result, "Booking Error");
                    }
                } catch (Exception ex) {
                    showMessage("Error: Please enter a valid number for hours.", "Input Error");
                }
            }
        });

        btnOption3.addActionListener(e -> showMessage(system.getAllBookingsDetails(), "Booking History"));

        btnOption4.addActionListener(e -> {

            List<Booking> activeBookings = system.getActiveBookings();

            if (activeBookings.isEmpty()) {
                showMessage("There are no active bookings to cancel.", "No Active Bookings");
                return;
            }

            String[] bookingOptions = new String[activeBookings.size()];
            for (int i = 0; i < activeBookings.size(); i++) {
                Booking b = activeBookings.get(i);

                bookingOptions[i] = b.getBookingId() + " - " + b.getRoom().getRoomName() + " (" + b.getUser().getStudentName() + ")";
            }

            JComboBox<String> bookingDropdown = new JComboBox<>(bookingOptions);

            JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
            panel.setBackground(Color.WHITE);
            panel.add(new JLabel("Select Booking to Cancel:"));
            panel.add(bookingDropdown);

            int option = JOptionPane.showConfirmDialog(this, panel, "Cancel Booking", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {

                String selectedOption = (String) bookingDropdown.getSelectedItem();

                String bId = selectedOption.split(" - ")[0];


                showMessage(system.cancelBookingLogic(bId), "Cancellation Status");
            }
        });

        btnExit.addActionListener(e -> System.exit(0));
    }

    private JButton createModernButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(COLOR_PRIMARY);
        btn.setForeground(COLOR_TEXT_LIGHT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { if(btn.getBackground().equals(COLOR_PRIMARY)) btn.setBackground(COLOR_PRIMARY_HOVER); }
            @Override
            public void mouseExited(MouseEvent e) { if(btn.getBackground().equals(COLOR_PRIMARY_HOVER)) btn.setBackground(COLOR_PRIMARY); }
        });
        return btn;
    }

    private void showMessage(String message, String title) {
        JTextArea textArea = new JTextArea(message);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 15));
        textArea.setEditable(false);
        textArea.setBackground(new Color(250, 250, 250));
        textArea.setForeground(COLOR_TEXT_DARK);
        textArea.setMargin(new Insets(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 450));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));

        JOptionPane.showMessageDialog(this, scrollPane, title, JOptionPane.PLAIN_MESSAGE);
    }

    private void showReceipt(String bookingId, String roomName, String studentName, String hours) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        StringBuilder receipt = new StringBuilder();
        receipt.append("******************************************\n");
        receipt.append("          BOOKING CONFIRMATION            \n");
        receipt.append("******************************************\n\n");
        receipt.append(String.format("%-15s: %s\n", "Date", dtf.format(now)));
        receipt.append(String.format("%-15s: %s\n", "Booking ID", bookingId));
        receipt.append("------------------------------------------\n");
        receipt.append(String.format("%-15s: %s\n", "Student Name", studentName));
        receipt.append(String.format("%-15s: %s\n", "Room", roomName));
        receipt.append(String.format("%-15s: %s Hours\n", "Duration", hours));
        receipt.append("------------------------------------------\n\n");
        receipt.append("   Please keep this ID for cancellation.  \n");
        receipt.append("              THANK YOU!                  \n");
        receipt.append("******************************************\n");

        JTextArea textArea = new JTextArea(receipt.toString());
        textArea.setFont(new Font("Monospaced", Font.BOLD, 16));
        textArea.setEditable(false);
        textArea.setBackground(new Color(255, 253, 242));
        textArea.setMargin(new Insets(20, 20, 20, 20));

        JOptionPane.showMessageDialog(this, textArea, "Booking Successful", JOptionPane.PLAIN_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}