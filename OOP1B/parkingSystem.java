// 酒店管理系统（使用数据库 & GUI）
// 该界面功能：停车管理（停车/离场/预约/修改允许停车条件）

package OOP1B;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class parkingSpot {
    String licensePlate;
    String ownerName;
    String purpose;

    public parkingSpot(String licensePlate, String ownerName, String purpose) {
        this.licensePlate = licensePlate;
        this.ownerName = ownerName;
        this.purpose = purpose;
    }

    @Override
    public String toString() {
        return "车牌号: " + licensePlate + ", 车主: " + ownerName + ", 停车目的: " + purpose;
    }
}

public class parkingSystem {
    private List<parkingSpot> parkingSpots = new ArrayList<>();
    private List<String> allowedPurposes = new ArrayList<>();
    private JTextArea parkingInfoArea;
    private JTextField licensePlateField, ownerNameField;
    private JComboBox<String> purposeComboBox;
    private JCheckBox diningCheckBox, accommodationCheckBox, employeeCheckBox, temporaryCheckBox;

    public parkingSystem() {
        createGUI();
        loadParkingData();
        updateParkingInfo();
    }

    private void createGUI() {
        JFrame frame = new JFrame("停车管理系统");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());

        // 顶部蓝色标题栏
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(59, 130, 246));
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("停车管理系统");
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        parkingInfoArea = new JTextArea();
        parkingInfoArea.setEditable(false);
        frame.add(new JScrollPane(parkingInfoArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(7, 2));
        licensePlateField = new JTextField();
        ownerNameField = new JTextField();
        purposeComboBox = new JComboBox<>(new String[]{"餐饮", "住宿", "员工", "临时停放"});

        inputPanel.add(new JLabel("车牌号:")); inputPanel.add(licensePlateField);
        inputPanel.add(new JLabel("车主姓名:")); inputPanel.add(ownerNameField);
        inputPanel.add(new JLabel("停车目的:")); inputPanel.add(purposeComboBox);

        JButton parkButton = new JButton("停车");
        JButton leaveButton = new JButton("离场");
        JButton reserveButton = new JButton("预约");
        JButton confirmButton = new JButton("更新允许停车类型");

        parkButton.addActionListener(e -> park());
        leaveButton.addActionListener(e -> leave());
        reserveButton.addActionListener(e -> reserve());
        confirmButton.addActionListener(e -> updateAllowedPurposes());

        inputPanel.add(parkButton); inputPanel.add(leaveButton);
        inputPanel.add(reserveButton); inputPanel.add(confirmButton);

        JPanel checkboxPanel = new JPanel(new FlowLayout());
        diningCheckBox = new JCheckBox("餐饮");
        accommodationCheckBox = new JCheckBox("住宿");
        employeeCheckBox = new JCheckBox("员工");
        temporaryCheckBox = new JCheckBox("临时停放");

        checkboxPanel.add(diningCheckBox);
        checkboxPanel.add(accommodationCheckBox);
        checkboxPanel.add(employeeCheckBox);
        checkboxPanel.add(temporaryCheckBox);
        inputPanel.add(new JLabel("允许的停车目的:"));
        inputPanel.add(checkboxPanel);

        JButton backButton = new JButton("返回主菜单");
        backButton.addActionListener(e -> {
            frame.dispose();
            hotel.main(new String[]{});
        });
        inputPanel.add(backButton);

        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void park() {
        String plate = licensePlateField.getText();
        String owner = ownerNameField.getText();
        String purpose = (String) purposeComboBox.getSelectedItem();

        if (plate.isEmpty() || owner.isEmpty()) {
            JOptionPane.showMessageDialog(null, "请填写所有信息");
            return;
        }
        if (!allowedPurposes.contains(purpose)) {
            JOptionPane.showMessageDialog(null, "该目的不允许停车");
            return;
        }

        parkingSpot spot = new parkingSpot(plate, owner, purpose);
        parkingSpots.add(spot);
        saveParkingData();
        updateParkingInfo();
        clearFields();
    }

    private void leave() {
        String plate = licensePlateField.getText();
        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(null, "请输入车牌号");
            return;
        }

        parkingSpot toRemove = null;
        for (parkingSpot spot : parkingSpots) {
            if (spot.licensePlate.equals(plate)) {
                toRemove = spot;
                break;
            }
        }

        if (toRemove != null) {
            parkingSpots.remove(toRemove);
            saveParkingData();
            updateParkingInfo();
        } else {
            JOptionPane.showMessageDialog(null, "未找到该车牌号的停车记录");
        }
        clearFields();
    }

    private void reserve() {
        String plate = licensePlateField.getText();
        String owner = ownerNameField.getText();
        String purpose = (String) purposeComboBox.getSelectedItem() + "（预约）";

        if (plate.isEmpty() || owner.isEmpty()) {
            JOptionPane.showMessageDialog(null, "请填写所有信息");
            return;
        }

        parkingSpot spot = new parkingSpot(plate, owner, purpose);
        parkingSpots.add(spot);
        saveParkingData();
        updateParkingInfo();
        clearFields();
    }

    private void updateAllowedPurposes() {
        try (Connection conn = DBUtil_P.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM allowed_purposes");

            String sql = "INSERT INTO allowed_purposes (purpose) VALUES (?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                if (diningCheckBox.isSelected()) { pstmt.setString(1, "餐饮"); pstmt.executeUpdate(); }
                if (accommodationCheckBox.isSelected()) { pstmt.setString(1, "住宿"); pstmt.executeUpdate(); }
                if (employeeCheckBox.isSelected()) { pstmt.setString(1, "员工"); pstmt.executeUpdate(); }
                if (temporaryCheckBox.isSelected()) { pstmt.setString(1, "临时停放"); pstmt.executeUpdate(); }
            }

            loadParkingData();
            updateParkingInfo();
            JOptionPane.showMessageDialog(null, "已更新允许的停车类型");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "保存失败：" + e.getMessage());
        }
    }

    private void loadParkingData() {
        parkingSpots.clear();
        allowedPurposes.clear();

        try (Connection conn = DBUtil_P.getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM parking_spots");
            while (rs.next()) {
                parkingSpots.add(new parkingSpot(
                        rs.getString("license_plate"),
                        rs.getString("owner_name"),
                        rs.getString("purpose")));
            }

            rs = stmt.executeQuery("SELECT purpose FROM allowed_purposes");
            while (rs.next()) {
                allowedPurposes.add(rs.getString("purpose"));
            }

            diningCheckBox.setSelected(allowedPurposes.contains("餐饮"));
            accommodationCheckBox.setSelected(allowedPurposes.contains("住宿"));
            employeeCheckBox.setSelected(allowedPurposes.contains("员工"));
            temporaryCheckBox.setSelected(allowedPurposes.contains("临时停放"));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "加载数据失败：" + e.getMessage());
        }
    }

    private void saveParkingData() {
        try (Connection conn = DBUtil_P.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM parking_spots");

            String sql = "INSERT INTO parking_spots (license_plate, owner_name, purpose) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (parkingSpot spot : parkingSpots) {
                    pstmt.setString(1, spot.licensePlate);
                    pstmt.setString(2, spot.ownerName);
                    pstmt.setString(3, spot.purpose);
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "保存停车数据失败：" + e.getMessage());
        }
    }

    private void updateParkingInfo() {
        parkingInfoArea.setText("");
        for (parkingSpot spot : parkingSpots) {
            parkingInfoArea.append(spot.toString() + "\n");
        }
    }

    private void clearFields() {
        licensePlateField.setText("");
        ownerNameField.setText("");
        purposeComboBox.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(parkingSystem::new);
    }
}