// 酒店管理系统（使用数据库 & GUI）
// 该界面功能：人员管理（添加人员/删除人员/查询人员信息）

package OOP1B;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

abstract class Staff {
    protected String name;
    protected String position;

    public Staff(String name, String position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return position + ": " + name;
    }
}

class RoomServiceStaff extends Staff {
    public RoomServiceStaff(String name) {
        super(name, "客房服务人员");
    }
}

class FrontDeskStaff extends Staff {
    public FrontDeskStaff(String name) {
        super(name, "前台服务员");
    }
}

class LobbyStaff extends Staff {
    public LobbyStaff(String name, String position) {
        super(name, position);
    }
}

class Doorman extends LobbyStaff {
    public Doorman(String name) {
        super(name, "门童");
    }
}

class Guide extends LobbyStaff {
    public Guide(String name) {
        super(name, "引导员");
    }
}

class LobbyManager extends LobbyStaff {
    public LobbyManager(String name) {
        super(name, "大厅经理");
    }
}

class OtherManager extends LobbyStaff {
    public OtherManager(String name) {
        super(name, "其他管理人员");
    }
}

public class administration {
    private ArrayList<Staff> staffList;
    private JTextArea staffTextArea;

    public administration() {
        staffList = new ArrayList<>();
    }

    public void loadStaffFromDatabase() {
        try (Connection connection = DBUtil_AD.getConnection()) {
            String query = "SELECT name, position FROM staff";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String name = rs.getString("name");
                String position = rs.getString("position");
                addStaff(createStaff(position, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveStaffToDatabase(Staff staff) {
        try (Connection connection = DBUtil_AD.getConnection()) {
            String query = "INSERT INTO staff (name, position) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, staff.getName());
            stmt.setString(2, staff.getPosition());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteStaffFromDatabase(Staff staff) {
        try (Connection connection = DBUtil_AD.getConnection()) {
            String query = "DELETE FROM staff WHERE name = ? AND position = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, staff.getName());
            stmt.setString(2, staff.getPosition());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Staff createStaff(String position, String name) {
        switch (position) {
            case "客房服务人员":
                return new RoomServiceStaff(name);
            case "前台服务员":
                return new FrontDeskStaff(name);
            case "门童":
                return new Doorman(name);
            case "引导员":
                return new Guide(name);
            case "大厅经理":
                return new LobbyManager(name);
            case "其他管理人员":
                return new OtherManager(name);
            default:
                return null;
        }
    }

    public void addStaff(Staff staff) {
        staffList.add(staff);
    }

    public void removeStaff(Staff staff) {
        staffList.remove(staff);
    }

    public void updateStaffTextArea(String selectedPosition) {
        StringBuilder sb = new StringBuilder();
        for (Staff staff : staffList) {
            if (staff != null && (selectedPosition.equals("所有职位") || staff.getPosition().equals(selectedPosition))) {
                sb.append(staff.toString()).append("\n");
            }
        }
        staffTextArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        administration admin = new administration();
        admin.loadStaffFromDatabase();
        JFrame frame = new JFrame("人员管理系统");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        // 顶部蓝色标题栏
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(59, 130, 246));
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("人员管理系统");
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new FlowLayout());

        JTextField nameField = new JTextField(15);
        JComboBox<String> positionComboBox = new JComboBox<>(new String[]{
                "所有职位", "客房服务人员", "前台服务员", "门童", "引导员", "大厅经理", "其他管理人员"
        });
        JButton addButton = new JButton("添加人员");
        JButton removeButton = new JButton("删除人员");
        JButton showButton = new JButton("查询选中职位人员信息（查询所有职位请先选择所有职位）");

        admin.staffTextArea = new JTextArea(10, 30);
        admin.staffTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(admin.staffTextArea);
        admin.updateStaffTextArea("所有职位");

        JButton backButton = new JButton("返回主菜单");
        backButton.addActionListener(e -> {
            frame.dispose();
            hotel.main(new String[]{});
        });
        mainPanel.add(backButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String position = (String) positionComboBox.getSelectedItem();

                if (position.equals("所有职位")) {
                    JOptionPane.showMessageDialog(frame, "请选择一个具体职位！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (name.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "姓名不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Staff staff = new Staff(name, position) {
                    @Override
                    public String toString() {
                        return position + ": " + name;
                    }
                };

                admin.addStaff(staff);
                admin.updateStaffTextArea((String) positionComboBox.getSelectedItem());
                nameField.setText("");
                admin.saveStaffToDatabase(staff);
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                for (Staff staff : admin.staffList) {
                    if (staff != null && staff.getName().equals(name)) {
                        admin.removeStaff(staff);
                        admin.updateStaffTextArea((String) positionComboBox.getSelectedItem());
                        admin.deleteStaffFromDatabase(staff);
                        nameField.setText("");
                        JOptionPane.showMessageDialog(frame, "人员删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
                JOptionPane.showMessageDialog(frame, "未找到该人员！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        showButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPosition = (String) positionComboBox.getSelectedItem();
                admin.updateStaffTextArea(selectedPosition);
            }
        });

        mainPanel.add(new JLabel("姓名:"));
        mainPanel.add(nameField);
        mainPanel.add(new JLabel("选中职位:"));
        mainPanel.add(positionComboBox);
        mainPanel.add(addButton);
        mainPanel.add(removeButton);
        mainPanel.add(showButton);
        mainPanel.add(scrollPane);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
