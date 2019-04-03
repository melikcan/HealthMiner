package UI;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JFrame {

    JButton login, cancel;
    JTextField tf1;
    JPasswordField tf2;

    public LoginPanel(String title) throws HeadlessException {
        super(title);

        setSize(360, 200);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);

        JLabel l1 = new JLabel("Username: ");
        l1.setBounds(10, 30, 80, 20);
        tf1 = new JTextField();
        tf1.setBounds(150, 30, 180, 20);
        JLabel l2 = new JLabel("Password: ");
        l2.setBounds(10, 60, 80, 20);
        tf2 = new JPasswordField();
        tf2.setBounds(150, 60, 180, 20);

        login = new JButton("Login");
        login.setBounds(80, 100, 100, 40);
        cancel = new JButton("Cancel");
        cancel.setBounds(190, 100, 100, 40);

        add(login);
        add(cancel);
        add(tf1);
        add(tf2);
        add(l1);
        add(l2);

        setVisible(true);
    }
}
