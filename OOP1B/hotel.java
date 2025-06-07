// 酒店管理系统（使用数据库 & GUI）
// 该界面功能：跳转各个模块（餐饮、住宿、停车、人员管理）

package OOP1B;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class hotel {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("陈雨翔连锁酒店管理系统");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 300);
            frame.setLocationRelativeTo(null);

            frame.setLayout(new BorderLayout());

            JPanel card = new JPanel();
            card.setOpaque(false);
            card.setLayout(new BorderLayout());

            JPanel header = new JPanel();
            header.setBackground(new Color(59, 130, 246));
            header.setPreferredSize(new Dimension(frame.getWidth(), 60));
            header.setLayout(new GridBagLayout());
            JLabel title = new JLabel("酒店管理系统");
            title.setForeground(Color.WHITE);
            title.setFont(title.getFont().deriveFont(20f).deriveFont(Font.BOLD));
            header.add(title);
            card.add(header, BorderLayout.NORTH);

            JPanel body = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
            body.setBackground(new Color(255, 255, 255, 230));
            body.setBorder(new EmptyBorder(10, 10, 10, 10));

            String[] names = {"餐饮", "住宿", "停车", "管理人员"};
            Class<?>[] targets = {restaurant.class, accommodationSystem.class,
                    parkingSystem.class, administration.class};
            for (int i = 0; i < names.length; i++) {
                JButton btn = new JButton(names[i]);
                final Class<?> cls = targets[i];
                btn.setPreferredSize(new Dimension(120, 40));
                btn.addActionListener(e -> {
                    try {
                        frame.setVisible(false);
                        cls.getMethod("main", String[].class)
                                .invoke(null, (Object) new String[]{});
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                body.add(btn);
            }

            card.add(body, BorderLayout.CENTER);

            frame.add(card, BorderLayout.CENTER);

            frame.setVisible(true);
        });
    }
}