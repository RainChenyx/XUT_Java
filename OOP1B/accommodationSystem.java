// 酒店管理系统（使用数据库 & GUI）
// 该界面功能：住宿管理（办理入住/预约/查询入住情况/取消预约）

package OOP1B;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Room {
    private String type;
    private double price;
    private int quantity;

    public Room(String type, double price, int quantity) {
        this.type = type;
        this.price = price;
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void reduceQuantity() {
        if (quantity > 0) {
            quantity--;
        }
    }

    public void increaseQuantity() {
        quantity++;
    }

    public String getRoomInfo() {
        return String.format("%s - 价格: %.2f, 剩余数量: %d", type, price, quantity);
    }
}

class Reservation_A {
    private String contactName;
    private Room room;
    private int days;
    private String checkInDate;
    private String companions;

    public Reservation_A(String contactName, Room room, int days, String checkInDate, String companions) {
        this.contactName = contactName;
        this.room = room;
        this.days = days;
        this.checkInDate = checkInDate;
        this.companions = companions;
    }

    public String getContactName() {
        return contactName;
    }

    public Room getRoom() {
        return room;
    }

    public int getDays() {
        return days;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public String getCompanions() {
        return companions;
    }

    public double getTotalPrice() {
        return room.getPrice() * days;
    }

    public String toString() {
        return String.format("联系人: %s, 房间类型: %s, 入住天数: %d, 入住日期: %s, 同住者: %s, 总价: %.2f",
                contactName, room.getType(), days, checkInDate, companions, getTotalPrice());
    }
}

class Accommodation {
    private List<Room> rooms;
    private List<Reservation_A> reservations;

    public Accommodation() {
        rooms = new ArrayList<>();
        reservations = new ArrayList<>();
        loadRoomsFromDatabase();
    }

    private void loadRoomsFromDatabase() {
        try (Connection conn = DBUtil_AC.getConnection()) {
            String sql = "SELECT * FROM rooms";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String type = rs.getString("type");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                rooms.add(new Room(type, price, quantity));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Room> getAvailableRooms() {
        return rooms;
    }

    public boolean reserveRoom(String contactName, Room room, int days, String checkInDate, String companions) {
        if (room.getQuantity() > 0) {
            room.reduceQuantity();
            Reservation_A reservation = new Reservation_A(contactName, room, days, checkInDate, companions);
            reservations.add(reservation);
            insertReservationToDatabase(reservation);
            return true;
        }
        return false;
    }

    private void insertReservationToDatabase(Reservation_A reservation) {
        try (Connection conn = DBUtil_AC.getConnection()) {
            String sql = "INSERT INTO reservations (contact_name, room_type, days, check_in_date, companions, total_price) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, reservation.getContactName());
            stmt.setString(2, reservation.getRoom().getType());
            stmt.setInt(3, reservation.getDays());
            stmt.setDate(4, Date.valueOf(reservation.getCheckInDate()));
            stmt.setString(5, reservation.getCompanions());
            stmt.setDouble(6, reservation.getTotalPrice());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean cancelReservation(String contactName, String checkInDate) {
        for (Reservation_A reservation : reservations) {
            if (reservation.getContactName().equals(contactName) && reservation.getCheckInDate().equals(checkInDate)) {
                reservations.remove(reservation);
                reservation.getRoom().increaseQuantity();
                deleteReservationFromDatabase(reservation);
                return true;
            }
        }
        return false;
    }

    private void deleteReservationFromDatabase(Reservation_A reservation) {
        try (Connection conn = DBUtil_AC.getConnection()) {
            String sql = "DELETE FROM reservations WHERE contact_name = ? AND check_in_date = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, reservation.getContactName());
            stmt.setDate(2, Date.valueOf(reservation.getCheckInDate()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Reservation_A> getReservationsByDate(String date) {
        List<Reservation_A> result = new ArrayList<>();
        try (Connection conn = DBUtil_AC.getConnection()) {
            String sql = "SELECT * FROM reservations WHERE check_in_date = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String contactName = rs.getString("contact_name");
                String roomType = rs.getString("room_type");
                int days = rs.getInt("days");
                String companions = rs.getString("companions");
                Room room = getRoomByType(roomType);
                result.add(new Reservation_A(contactName, room, days, date, companions));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Room getRoomByType(String type) {
        for (Room room : rooms) {
            if (room.getType().equals(type)) {
                return room;
            }
        }
        return null;
    }
}

public class accommodationSystem {
    private Accommodation accommodation;

    public accommodationSystem() {
        accommodation = new Accommodation();
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("住宿管理系统");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLayout(new BorderLayout());

        // 顶部蓝色标题栏
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(59, 130, 246));
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("住宿管理系统");
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton reserveButton = new JButton("办理入住");
        JButton reserveDiscountButton = new JButton("预约（打8折）");
        JButton checkButton = new JButton("查看入住情况");
        JButton cancelButton = new JButton("取消入住/预约");
        JButton backButton = new JButton("返回主菜单");
        buttonPanel.add(reserveButton);
        buttonPanel.add(reserveDiscountButton);
        buttonPanel.add(checkButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(backButton);

        // 顶部总面板，包含标题栏和按钮栏
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);
        frame.add(topPanel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(7, 2));
        JLabel contactLabel = new JLabel("联系人（挂账者）:");
        JTextField contactField = new JTextField();
        JLabel companionsLabel = new JLabel("同住者（多个同住者则使用\";\"隔开）:");
        JTextField companionsField = new JTextField();
        JLabel roomTypeLabel = new JLabel("房间类型:");
        JComboBox<String> roomTypeCombo = new JComboBox<>();
        for (Room room : accommodation.getAvailableRooms()) {
            roomTypeCombo.addItem(room.getType());
        }
        JLabel priceLabel = new JLabel("房间价格/晚:");
        JLabel priceValueLabel = new JLabel("100.00");
        JLabel daysLabel = new JLabel("入住天数:");
        JTextField daysField = new JTextField();
        JLabel checkInLabel = new JLabel("入住日期（YYYY-MM-DD）:");
        JTextField checkInField = new JTextField();

        inputPanel.add(contactLabel);
        inputPanel.add(contactField);
        inputPanel.add(companionsLabel);
        inputPanel.add(companionsField);
        inputPanel.add(roomTypeLabel);
        inputPanel.add(roomTypeCombo);
        inputPanel.add(priceLabel);
        inputPanel.add(priceValueLabel);
        inputPanel.add(daysLabel);
        inputPanel.add(daysField);
        inputPanel.add(checkInLabel);
        inputPanel.add(checkInField);

        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(500, 100));

        roomTypeCombo.addActionListener(e -> {
            String selectedRoomType = (String) roomTypeCombo.getSelectedItem();
            for (Room room : accommodation.getAvailableRooms()) {
                if (room.getType().equals(selectedRoomType)) {
                    priceValueLabel.setText(String.valueOf(room.getPrice()));
                    break;
                }
            }
        });

        reserveButton.addActionListener(e -> {
            String contactName = contactField.getText().trim();
            String selectedRoomType = (String) roomTypeCombo.getSelectedItem();
            Room selectedRoom = null;

            for (Room room : accommodation.getAvailableRooms()) {
                if (room.getType().equals(selectedRoomType)) {
                    selectedRoom = room;
                    break;
                }
            }

            String daysText = daysField.getText().trim();
            String checkInDate = checkInField.getText().trim();
            String companions = companionsField.getText();

            if (!isValidDate(checkInDate)) {
                JOptionPane.showMessageDialog(frame, "请输入正确的日期格式（YYYY-MM-DD）！");
                return;
            }

            if (contactName.isEmpty() || daysText.isEmpty() || checkInDate.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "请填写完整信息！");
                return;
            }

            int days;
            try {
                days = Integer.parseInt(daysText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "入住天数必须是一个有效的数字！");
                return;
            }

            if (accommodation.reserveRoom(contactName, selectedRoom, days, checkInDate, companions)) {
                JOptionPane.showMessageDialog(frame, "入住登记成功！总价: " + selectedRoom.getPrice() * days);
                // 自动刷新入住情况
                List<Reservation_A> reservations = accommodation.getReservationsByDate(checkInDate);
                resultArea.setText("");
                if (reservations.isEmpty()) {
                    resultArea.setText("没有在该日期的入住/预约记录。");
                } else {
                    for (Reservation_A reservation : reservations) {
                        resultArea.append(reservation.toString() + "\n");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "入住登记失败，房间已满。");
            }
        });

        reserveDiscountButton.addActionListener(e -> {
            String contactName = contactField.getText().trim();
            String selectedRoomType = (String) roomTypeCombo.getSelectedItem();
            Room selectedRoom = null;

            for (Room room : accommodation.getAvailableRooms()) {
                if (room.getType().equals(selectedRoomType)) {
                    selectedRoom = room;
                    break;
                }
            }

            String daysText = daysField.getText().trim();
            String checkInDate = checkInField.getText().trim();
            String companions = companionsField.getText();

            // 验证日期格式
            if (!isValidDate(checkInDate)) {
                JOptionPane.showMessageDialog(frame, "请输入正确的日期格式（YYYY-MM-DD）！");
                return;
            }

            if (contactName.isEmpty() || daysText.isEmpty() || checkInDate.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "请填写完整信息！");
                return;
            }

            int days;
            try {
                days = Integer.parseInt(daysText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "入住天数必须是一个有效的数字！");
                return;
            }

            // 进行房间预定
            if (accommodation.reserveRoom(contactName, selectedRoom, days, checkInDate, companions)) {
                JOptionPane.showMessageDialog(frame, "预约成功！总价: " + 0.8 * selectedRoom.getPrice() * days);
                // 自动刷新入住情况
                List<Reservation_A> reservations = accommodation.getReservationsByDate(checkInDate);
                resultArea.setText("");
                if (reservations.isEmpty()) {
                    resultArea.setText("没有在该日期的入住/预约记录。");
                } else {
                    for (Reservation_A reservation : reservations) {
                        resultArea.append(reservation.toString() + "\n");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "预约失败，房间已满。");
            }
        });

        checkButton.addActionListener(e -> {
            String checkInDate = checkInField.getText().trim();

            if (!isValidDate(checkInDate)) {
                JOptionPane.showMessageDialog(frame, "请输入正确的日期格式（YYYY-MM-DD）！");
                return;
            }

            List<Reservation_A> reservations = accommodation.getReservationsByDate(checkInDate);
            resultArea.setText("");
            if (reservations.isEmpty()) {
                resultArea.setText("没有在该日期的入住/预约记录。");
            } else {
                for (Reservation_A reservation : reservations) {
                    resultArea.append(reservation.toString() + "\n");
                }
            }
        });

        cancelButton.addActionListener(e -> {
            String contactName = contactField.getText().trim();
            String checkInDate = checkInField.getText().trim();

            if (!isValidDate(checkInDate)) {
                JOptionPane.showMessageDialog(frame, "请输入正确的日期格式（YYYY-MM-DD）！");
                return;
            }

            if (accommodation.cancelReservation(contactName, checkInDate)) {
                JOptionPane.showMessageDialog(frame, "取消入住/预约成功！");
                // 自动刷新入住情况
                List<Reservation_A> reservations = accommodation.getReservationsByDate(checkInDate);
                resultArea.setText("");
                if (reservations.isEmpty()) {
                    resultArea.setText("没有在该日期的入住/预约记录。");
                } else {
                    for (Reservation_A reservation : reservations) {
                        resultArea.append(reservation.toString() + "\n");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "取消入住/预约失败，未找到相关记录。");
            }
        });

        backButton.addActionListener(e -> {
            frame.dispose();
            hotel.main(new String[]{});
        });

        frame.add(inputPanel, BorderLayout.CENTER);
        frame.add(scrollPane, BorderLayout.SOUTH);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private boolean isValidDate(String date) {
        String regex = "^\\d{4}-\\d{2}-\\d{2}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(date);
        return matcher.matches();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new accommodationSystem());
    }
}
