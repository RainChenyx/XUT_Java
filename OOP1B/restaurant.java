// 酒店管理系统（使用数据库 & GUI）
// 该界面功能：餐饮管理（管理菜单（展示菜单/添加菜品/删除菜品/修改菜品）/顾客点菜/预约/结账/查询预定信息/查看入座情况）

package OOP1B;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.sql.*;

public class restaurant {
    static int lunchReservation = 0;
    static int dinnerReservation = 0;
    static int MAX = 100;
    static int MAX_TABLES = 10; // 最大桌号
    static ArrayList<Order> orders = new ArrayList<>(); // 点单列表
    static ArrayList<Reservation> reservations = new ArrayList<>(); // 预定信息列表

    static void foodOperation(JFrame mainFrame) {
        mainFrame.setVisible(false); // 隐藏主界面
        JFrame frame = new JFrame("餐饮管理系统");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(59, 130, 246));
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel titleLabel = new JLabel("菜品管理系统");
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 5, 5));

        JButton displayButton = new JButton("展示菜单");
        JButton addButton = new JButton("添加菜品");
        JButton deleteButton = new JButton("删除菜品");
        JButton changeButton = new JButton("修改菜品");
        JButton backButton = new JButton("返回主菜单");

        displayButton.addActionListener(e -> displayMenu());
        addButton.addActionListener(e -> addFood());
        deleteButton.addActionListener(e -> deleteFood());
        changeButton.addActionListener(e -> changeFood());
        backButton.addActionListener(e -> {
            frame.dispose();
            mainFrame.setVisible(true);
        });

        // 添加窗口关闭事件监听器
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                frame.dispose();
                mainFrame.setVisible(true);
            }
        });

        buttonPanel.add(displayButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(changeButton);
        buttonPanel.add(backButton);

        frame.setLayout(new BorderLayout());
        frame.add(titlePanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    static String[][] load_food() {
        String[][] foods = new String[100][3];
        int index = 0;
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, price, sell_count FROM menu")) {
            
            while (rs.next() && index < 100) {
                foods[index][0] = rs.getString("name");
                foods[index][1] = String.valueOf(rs.getDouble("price"));
                foods[index][2] = String.valueOf(rs.getInt("sell_count"));
                index++;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "从数据库加载菜单失败: " + e.getMessage());
        }
        
        return foods;
    }

    static void displayMenu() {
        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM menu")) {

            textArea.setText("菜单列表：\n");
            while (rs.next()) {
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int sellCount = rs.getInt("sell_count");
                String formattedLine = String.format("菜名：%-10s  价格：%-10.2f  销量：%2d\n", name, price, sellCount);
                textArea.append(formattedLine);            }
        } catch (SQLException e) {
            textArea.setText("数据库读取菜单失败：" + e.getMessage());
        }

        JFrame menuFrame = new JFrame("当前菜单列表");
        menuFrame.add(scrollPane);
        menuFrame.setSize(400, 300);
        menuFrame.setLocationRelativeTo(null);
        menuFrame.setVisible(true);
        menuFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    static void addFood() {
        String name = JOptionPane.showInputDialog("请输入菜品名称：");
        String priceStr = JOptionPane.showInputDialog("请输入菜品价格：");

        if (name == null || priceStr == null) {
            JOptionPane.showMessageDialog(null, "取消添加");
            return;
        }
        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "价格输入无效，请输入数字。");
            return;
        }

        String sql = "INSERT INTO menu(name, price, sell_count) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setInt(3, 0);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "添加成功！");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "数据库写入失败: " + e.getMessage());
        }
    }

    static void deleteFood() {
        displayMenu();
        String name = JOptionPane.showInputDialog("请输入要删除的菜品名称：");
        if (name == null) {
            JOptionPane.showMessageDialog(null, "取消删除");
            return;
        }

        String sql = "DELETE FROM menu WHERE name = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(null, "删除成功！");
            } else {
                JOptionPane.showMessageDialog(null, "没有找到该菜品！");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "数据库删除失败: " + e.getMessage());
        }
    }

    static void changeFood() {
        displayMenu();
        String name = JOptionPane.showInputDialog("请输入要修改的菜品名称：");
        if (name == null) { 
            JOptionPane.showMessageDialog(null, "取消修改");
            return;
        }
        String newName = JOptionPane.showInputDialog("请输入新的菜品名称：");
        String newPriceStr = JOptionPane.showInputDialog("请输入新的菜品价格：");
        if (newName == null || newPriceStr == null) { 
            JOptionPane.showMessageDialog(null, "取消修改");
            return;
        }
        double newPrice;
        try {
            newPrice = Double.parseDouble(newPriceStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "价格输入无效，请输入数字。"); 
            return;
        }

        String sql = "UPDATE menu SET name=?, price=? WHERE name=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setDouble(2, newPrice);
            ps.setString(3, name);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(null, "修改成功！");
            } else {
                JOptionPane.showMessageDialog(null, "查无此菜！");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "数据库更新失败: " + e.getMessage());
        }
    }

    static void reserveTable() {
        JComboBox<String> mealCombo = new JComboBox<>(new String[]{"午餐", "晚餐"});
        JComboBox<Integer> countCombo = new JComboBox<>();
        for (int i = 1; i <= 10; i++) countCombo.addItem(i);

        JTextField nameField = new JTextField(10);
        JTextField phoneField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new JLabel("选择餐次："));
        panel.add(mealCombo);
        panel.add(new JLabel("预约人数："));
        panel.add(countCombo);
        panel.add(new JLabel("联系人（结账者）姓名："));
        panel.add(nameField);
        panel.add(new JLabel("电话："));
        panel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(null, panel, "桌位预约",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "顾客取消预约");
            return;
        }

        String mealTypeStr = (String) mealCombo.getSelectedItem();
        int mealType = mealTypeStr.equals("午餐") ? 1 : 2;
        int numberOfPeople = (int) countCombo.getSelectedItem();
        String customerName = nameField.getText().trim();
        String customerPhone = phoneField.getText().trim();

        // 验证联系人和电话
        if (customerName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "请输入联系人姓名！");
            return;
        }
        if (customerPhone.isEmpty()) {
            JOptionPane.showMessageDialog(null, "请输入联系电话！");
            return;
        }
        // 验证电话号码格式（简单验证是否为数字且长度合理）
        if (!customerPhone.matches("\\d{11}")) {
            JOptionPane.showMessageDialog(null, "请输入11位有效的手机号码！");
            return;
        }

        // 检查该餐次已预约的桌数
        int reservedCount = 0;
        for (Reservation reservation : reservations) {
            if (reservation.getMealType().equals(mealTypeStr)) {
                reservedCount++;
            }
        }
        if (reservedCount >= 5) {
            JOptionPane.showMessageDialog(null, "该餐次已预约满5桌，无法继续预约。");
            return;
        }

        // 查找未被点单和未被预约的桌号
        int tableNumber = -1;
        for (int i = 1; i <= MAX_TABLES; i++) {
            boolean occupied = false;
            // 检查是否被点单占用
            for (Order order : orders) {
                if (Integer.parseInt(order.getTableNumber()) == i && order.getMealType() == mealType) {
                    occupied = true;
                    break;
                }
            }
            // 检查是否被预约占用
            for (Reservation reservation : reservations) {
                if (reservation.getTableNumber() == i && reservation.getMealType().equals(mealTypeStr)) {
                    occupied = true;
                    break;
                }
            }
            if (!occupied) {
                tableNumber = i;
                break;
            }
        }

        if (tableNumber == -1) {
            JOptionPane.showMessageDialog(null, "没有空桌，无法预约。");
        } else {
            reservations.add(new Reservation(mealTypeStr, numberOfPeople, tableNumber, customerName, customerPhone));
            JOptionPane.showMessageDialog(null,
                    "预约成功！餐次：" + mealTypeStr + "，桌号：" + tableNumber + "，人数：" + numberOfPeople +
                            "，联系人（结账者）姓名：" + customerName + "，电话：" + customerPhone);
        }
    }

    static void queryReservation() {
        if (reservations.isEmpty()) {
            JOptionPane.showMessageDialog(null, "当前没有任何预定信息。");
            return;
        }

        StringBuilder allReservations = new StringBuilder();
        for (Reservation reservation : reservations) {
            allReservations.append("餐次：").append(reservation.getMealType()).append("\n")
                    .append("桌号：").append(reservation.getTableNumber()).append("\n")
                    .append("人数：").append(reservation.getNumberOfPeople()).append("\n")
                    .append("姓名：").append(reservation.getCustomerName()).append("\n")
                    .append("电话：").append(reservation.getCustomerPhone()).append("\n")
                    .append("------------------------\n");
        }

        JTextArea textArea = new JTextArea(allReservations.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 250));

        JOptionPane.showMessageDialog(null, scrollPane, "所有预定信息", JOptionPane.INFORMATION_MESSAGE);
    }

    static void placeOrder() {
        JComboBox<String> mealCombo = new JComboBox<>(new String[]{"午餐", "晚餐"});
        JComboBox<String> tableCombo = new JComboBox<>();
        for (int i = 1; i <= MAX_TABLES; i++) tableCombo.addItem(String.valueOf(i));

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("请选择餐次：")); panel.add(mealCombo);
        panel.add(new JLabel("请选择桌号：")); panel.add(tableCombo);

        int result = JOptionPane.showConfirmDialog(null, panel, "点菜基本信息",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "顾客取消点菜"); return;
        }
        String mealTypeStr = (String) mealCombo.getSelectedItem();
        int mealType = mealTypeStr.equals("午餐") ? 1 : 2;
        int tableNumber = Integer.parseInt((String) tableCombo.getSelectedItem());

        // 检查并移除该桌位的预约信息
        reservations.removeIf(reservation -> 
            reservation.getTableNumber() == tableNumber && 
            reservation.getMealType().equals(mealTypeStr));

        ArrayList<String> orderedFoods = new ArrayList<>();
        displayMenu();
        String[][] foods = load_food();
        while (true) {
            String foodName = JOptionPane.showInputDialog("请输入菜品名称（输入回车完成点单）：");
            if (foodName == null) {
                JOptionPane.showMessageDialog(null, "顾客取消点菜");
                return;
            }
            foodName = foodName.trim();
            if ("".equals(foodName))
                break;
            boolean found = false;
            for (int i = 0; i < foods.length; i++) {
                if (foods[i][0] != null && foods[i][0].equals(foodName)) {
                    foods[i][2] = String.valueOf(Integer.parseInt(foods[i][2]) + 1);
                    orderedFoods.add(foodName);
                    found = true;
                    break;
                }
            }
            if (!found) JOptionPane.showMessageDialog(null, "没有找到该菜品！");
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("menu.txt"))) {
            for (String[] food : foods) {
                if (food != null && food[0] != null) writer.write(food[0] + " " + food[1] + " " + food[2] + "\n");
            }
        } catch (IOException e) { throw new RuntimeException(e); }

        for (String name : orderedFoods) {
            String sql = "UPDATE menu SET sell_count = sell_count + 1 WHERE name = ?";
            try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.executeUpdate();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "数据库更新失败: " + e.getMessage());
            }
        }

        String notes = JOptionPane.showInputDialog("备注：");

        for (String name : orderedFoods) orders.add(new Order(String.valueOf(tableNumber), name, mealType));
        if (orderedFoods.isEmpty())
            JOptionPane.showMessageDialog(null, "没有点任何菜品。");
        else JOptionPane.showMessageDialog(null,
                "点单成功！桌号：" + tableNumber + "，菜品：" + String.join(", ", orderedFoods) + "，备注：" + notes);
    }

    static void checkout() {
        JComboBox<String> mealCombo = new JComboBox<>(new String[]{"午餐", "晚餐"});
        JComboBox<String> tableCombo = new JComboBox<>();
        for (int i = 1; i <= MAX_TABLES; i++) tableCombo.addItem(String.valueOf(i));

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("请选择用餐类型：")); panel.add(mealCombo);
        panel.add(new JLabel("请选择桌号：")); panel.add(tableCombo);

        int result = JOptionPane.showConfirmDialog(null, panel, "结账信息",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) { JOptionPane.showMessageDialog(null, "顾客取消结账"); return; }
        String mealTypeStr = (String) mealCombo.getSelectedItem();
        int mealType = mealTypeStr.equals("午餐") ? 1 : 2;
        int tableNumber = Integer.parseInt((String) tableCombo.getSelectedItem());

        String[][] foods = load_food();

        double totalAmount = 0.0;
        StringBuilder details = new StringBuilder("桌号 " + tableNumber + " 的点单及价格：\n");

        boolean hasOrder = false;
        for (Order order : orders) {
            if (Integer.parseInt(order.getTableNumber()) == tableNumber && order.getMealType() == mealType) {
                String item = order.getFoodName();
                double price = 0;
                for (String[] food : foods) {
                    if (food[0] != null && food[0].equals(item)) {
                        price = Double.parseDouble(food[1]);
                        break;
                    }
                }
                details.append(item).append(" - ").append(price).append("元\n");
                totalAmount += price;
                hasOrder = true;
            }
        }

        if (!hasOrder) {
            JOptionPane.showMessageDialog(null, "该桌目前为空桌，没有点单信息。");
            return;
        }

        details.append("\n总金额：").append(totalAmount).append("元");

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        JOptionPane.showMessageDialog(null, scrollPane, "结账详情", JOptionPane.INFORMATION_MESSAGE);

        // 移除订单信息
        orders.removeIf(order -> Integer.parseInt(order.getTableNumber()) == tableNumber && order.getMealType() == mealType);
        // 移除预约信息
        reservations.removeIf(reservation -> reservation.getTableNumber() == tableNumber && reservation.getMealType().equals(mealTypeStr));
        
        JOptionPane.showMessageDialog(null, "结账完成，桌位已清空！");
    }

    static void viewTableStatus() {
        JFrame frame = new JFrame("空桌情况");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setEditable(false);

        // 午餐占用情况
        textArea.append("午餐占用情况：\n");
        for (int i = 1; i <= MAX_TABLES; i++) {
            boolean occupied = false;
            for (Order order : orders) {
                if (Integer.parseInt(order.getTableNumber()) == i && order.getMealType() == 1) {
                    occupied = true;
                    textArea.append("桌号 " + i + "：点单占用\n");
                    break;
                }
            }
            for (Reservation reservation : reservations) {
                if (reservation.getTableNumber() == i && reservation.getMealType().equals("午餐")) {
                    occupied = true;
                    textArea.append("桌号 " + i + "：预约占用\n");
                    break;
                }
            }
            if (!occupied) {
                textArea.append("桌号 " + i + "：空闲\n");
            }
        }

        // 晚餐占用情况
        textArea.append("\n晚餐占用情况：\n");
        for (int i = 1; i <= MAX_TABLES; i++) {
            boolean occupied = false;
            for (Order order : orders) {
                if (Integer.parseInt(order.getTableNumber()) == i && order.getMealType() == 2) {
                    occupied = true;
                    textArea.append("桌号 " + i + "：点单占用\n");
                    break;
                }
            }
            for (Reservation reservation : reservations) {
                if (reservation.getTableNumber() == i && reservation.getMealType().equals("晚餐")) {
                    occupied = true;
                    textArea.append("桌号 " + i + "：预约占用\n");
                    break;
                }
            }
            if (!occupied) {
                textArea.append("桌号 " + i + "：空闲\n");
            }
        }

        frame.add(scrollPane);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        DBUtil.initDatabaseAndTable();
        JFrame frame = new JFrame("餐饮管理系统");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(59, 130, 246));
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel titleLabel = new JLabel("餐饮管理系统");
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        JPanel buttonPanel = new JPanel(new GridLayout(3, 3, 10, 10));

        JButton menuButton = new JButton("管理菜单");
        JButton orderButton = new JButton("顾客点菜");
        JButton reservationButton = new JButton("顾客预约");
        JButton checkoutButton = new JButton("顾客结账");
        JButton queryButton = new JButton("查询预定信息");
        JButton viewTablesButton = new JButton("查看入座情况");
        JButton backButton = new JButton("返回主菜单");

        buttonPanel.add(menuButton);
        buttonPanel.add(orderButton);
        buttonPanel.add(reservationButton);
        buttonPanel.add(checkoutButton);
        buttonPanel.add(queryButton);
        buttonPanel.add(viewTablesButton);
        buttonPanel.add(backButton);

        frame.setLayout(new BorderLayout());
        frame.add(titlePanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);

        menuButton.addActionListener(e -> foodOperation(frame));
        orderButton.addActionListener(e -> placeOrder());
        reservationButton.addActionListener(e -> reserveTable());
        checkoutButton.addActionListener(e -> checkout());
        queryButton.addActionListener(e -> queryReservation());
        viewTablesButton.addActionListener(e -> viewTableStatus());

        backButton.addActionListener(e -> {
            frame.dispose();
            hotel.main(new String[]{});
        });

        frame.setVisible(true);
    }
}

class Reservation {
    private String mealType;
    private int numberOfPeople;
    private int tableNumber;
    private String customerName;
    private String customerPhone;

    public Reservation(String mealType, int numberOfPeople, int tableNumber, String customerName, String customerPhone) {
        this.mealType = mealType;
        this.numberOfPeople = numberOfPeople;
        this.tableNumber = tableNumber;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
    }

    public String getMealType() { return mealType; }
    public int getNumberOfPeople() { return numberOfPeople; }
    public int getTableNumber() { return tableNumber; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
}

class Order {
    private String tableNumber;
    private String foodName;
    private int mealType;

    public Order(String tableNumber, String foodName, int mealType) {
        this.tableNumber = tableNumber;
        this.foodName = foodName;
        this.mealType = mealType;
    }

    public String getTableNumber() { return tableNumber; }
    public String getFoodName() { return foodName; }
    public int getMealType() { return mealType; }
}
