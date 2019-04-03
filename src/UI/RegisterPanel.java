package UI;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JFrame {
    JButton ok, cancel;
    JTextField tf1, tf3, tf4, tf5;
    JPasswordField tf2;

    public RegisterPanel(String title) throws HeadlessException {
        super(title);
        setSize(360, 320);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);

        JLabel l1 = new JLabel("Username: ");
        tf1 = new JTextField();
        l1.setBounds(10, 50, 80, 20);
        tf1.setBounds(150, 50, 180, 20);

        JLabel l2 = new JLabel("Password: ");
        tf2 = new JPasswordField();
        l2.setBounds(10, 80, 80, 20);
        tf2.setBounds(150, 80, 180, 20);

        JLabel l3 = new JLabel("Name: ");
        tf3 = new JTextField();
        l3.setBounds(10, 110, 80, 20);
        tf3.setBounds(150, 110, 180, 20);

        JLabel l4 = new JLabel("Surname: ");
        tf4 = new JTextField();
        l4.setBounds(10, 140, 80, 20);
        tf4.setBounds(150, 140, 180, 20);

        JLabel l5 = new JLabel("Date of Birth: ");
        tf5 = new JTextField();
        l5.setBounds(10, 170, 80, 20);
        tf5.setBounds(150, 170, 180, 20);

        ok = new JButton("Ok");
        cancel = new JButton("Cancel");
        ok.setBounds(80, 220, 100, 40);
        cancel.setBounds(190, 220, 100, 40);

        add(ok);
        add(cancel);
        add(tf1);
        add(tf2);
        add(tf3);
        add(tf4);
        add(tf5);
        add(l1);
        add(l2);
        add(l3);
        add(l4);
        add(l5);

        setVisible(true);
    }
}
