package UI;

import control.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInterface extends JFrame {

    private Controller control;
    private User currentUser;
    private JLabel currentUserLabel, un, fn, uid;
    private JButton openB;
    private Map<String, String> userBook;
    private JList<String> list1;
    private JTable jt;

    public UserInterface() {
        super("Chain Control System");
        setSize(1020, 700);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);

        // initialize data
        control = new Controller();
        userBook = new HashMap<>();
        control.isbv();
        demoSetUsers();

        // register button
        JButton register = new JButton("Register");
        register.setBounds(780, 20, 100, 40);
        add(register);
        register.addActionListener(e -> registerNewUser());

        // login button
        JButton login = new JButton("Login");
        login.setBounds(900, 20, 100, 40);
        add(login);
        login.addActionListener(e -> loginUser());

        // current user label
        currentUserLabel = new JLabel("Please Login!");
        currentUserLabel.setBounds(30, 20, 400, 40);
        currentUserLabel.setFont(new Font("Serif", Font.BOLD, 24));
        add(currentUserLabel);

        // profile button
        JButton profileButton = new JButton("Profile");
        profileButton.setBounds(20, 160, 200, 30);
        add(profileButton);
        profileButton.addActionListener(e -> showProfile());

        // files button
        JButton filesButton = new JButton("Files");
        filesButton.setBounds(20, 200, 200, 30);
        add(filesButton);
        filesButton.addActionListener(e -> showUserFiles());


        // permissions button
        JButton permButton = new JButton("File Permissions");
        permButton.setBounds(20, 240, 200, 30);
        add(permButton);
        permButton.addActionListener(e -> resetWindow());

        // other button
        JButton otherButton = new JButton("");
        otherButton.setBounds(20, 280, 200, 30);
        add(otherButton);

        // other2 button
        JButton other2Button = new JButton("");
        other2Button.setBounds(20, 320, 200, 30);
        add(other2Button);
        other2Button.addActionListener(e -> {
            String fileID = list1.getSelectedValue();
            EditorPanel showFile = new EditorPanel(fileID, fileID);
        });

        // test
        control.isbv();
        this.currentUser = control.loginUser(control.getUserHash(userBook.get("hospital")), "123");
        String target = userBook.get("user1");
        String target2 = userBook.get("user2");
        control.newFile(SampleRecords.file0, target, currentUser);
        control.newFile(SampleRecords.file10, target, currentUser);
        control.newFile(SampleRecords.file01, target, currentUser);
        control.newFile(SampleRecords.file01, target2, currentUser);
        this.currentUser = control.loginUser(control.getUserHash(userBook.get("user1")), "123");
        String file1 = currentUser.getFiles().get(0);
        this.currentUser = control.loginUser(control.getUserHash(userBook.get("doctor1")), "123");
        currentUser = control.grantAccess(file1, userBook.get("user1"), currentUser);

        control.updateUser(currentUser);
        updateUserLabel();
        control.isbv();
    }

    // register button actions
    private void registerNewUser() {
        RegisterPanel r = new RegisterPanel("Register New User");

        r.cancel.addActionListener(e -> r.dispose());
        r.ok.addActionListener(e -> {
            String userName = r.tf1.getText();
            String password = new String(r.tf2.getPassword());
            String name = r.tf3.getText();
            String surname = r.tf4.getText();
            //String birth = r.tf5.getText();
            String uid = control.createUser(0, userName, password, name, surname);
            userBook.put(userName, uid);
            r.dispose();
        });
    }

    // login button actions
    private void loginUser() {
        LoginPanel l = new LoginPanel("User Login");

        l.cancel.addActionListener(e -> l.dispose());
        l.login.addActionListener(e -> {
            String userID = userBook.get(l.tf1.getText());
            String userHash = control.getUserHash(userID);
            String userPass = new String(l.tf2.getPassword());
            if (userHash.equals("none")) {
                JOptionPane.showMessageDialog(l, "Login Error: User not found!");
            } else {
                User logged = control.loginUser(userHash, userPass);
                if (logged == null)
                    JOptionPane.showMessageDialog(l, "Wrong user name or password!");
                else {
                    resetWindow();
                    this.currentUser = logged;
                    updateUserLabel();
                    resetWindow();
                    l.dispose();
                    resetWindow();
                }
            }
        });
    }

    // profile button actions
    private void showProfile() {
        resetWindow();
        if (currentUser != null) {
            un = new JLabel("User Name: \t\t" + currentUser.getUserName());
            un.setBounds(300, 160, 400, 40);
            uid = new JLabel("User ID: \t\t" + currentUser.getUserID());
            uid.setBounds(300, 220, 400, 40);
            fn = new JLabel("Full Name: \t\t" + currentUser.getFullName());
            fn.setBounds(300, 280, 400, 40);

            add(un);
            add(uid);
            add(fn);
        }
    }

    // file button actions
    private void showUserFiles() {
        resetWindow();
        if (currentUser.getUserType() == 0) {
            DefaultListModel<String> l1 = new DefaultListModel<>();
            List<String> userFiles = currentUser.getFiles();
            for (String s : userFiles) {
                l1.addElement(s);
            }
            list1 = new JList<>(l1);
            list1.setBounds(300, 160, 560, 400);

            openB = new JButton("Open");
            openB.setBounds(880, 160, 80, 20);
            add(list1);
            add(openB);
            openB.addActionListener(e -> {
                String fileID = list1.getSelectedValue();
                String content = control.reachFileContent(fileID, currentUser);
                EditorPanel showFile = new EditorPanel(fileID, content);
            });
        } else {
            String[][] list = new String[100][2];
            int i = 0;
            for (Map.Entry<String, List<String>> entry : currentUser.getFilePermissions().entrySet()) {
                for (String s : entry.getValue()) {
                    String[] temp = {entry.getKey(), s};
                    list[i++] = temp;
                }
            }
            String[] column = {"User ID", "File Name"};
            jt = new JTable(list, column);
            jt.setBounds(300, 160, 560, 400);
            //JScrollPane sp = new JScrollPane(jt);
            add(jt);
            repaint();
            //add(sp);

            openB = new JButton("Open");
            openB.setBounds(880, 160, 80, 20);
            add(openB);
            openB.addActionListener(e -> {
                int selectedRow = jt.getSelectedRow();
                String patientID = list[selectedRow][0];
                String fileID = list[selectedRow][1];
                String content = control.reachFileContent(fileID, patientID, currentUser);
                EditorPanel showFile = new EditorPanel(fileID, content);
            });
        }
        repaint();
    }

    private void addUser(int type, String userName, String password, String name, String surname) {
        String uid = control.createUser(type, userName, password, name, surname);
        userBook.put(userName, uid);
    }

    private void demoSetUsers() {
        System.out.println("Waiting miners...");

        addUser(0, "user1", "123", "Alisha", "Peck");
        addUser(0, "user2", "123", "Everett", "Baldwin");
        addUser(0, "user3", "123", "Adaline", "Downer");
        addUser(1, "doctor1", "123", "Kandace", "Faulkner");
        addUser(1, "doctor2", "123", "Betsy", "Walters");
        addUser(2, "hospital", "123", "Medical", "Center");
        addUser(0, "user7", "123", "2Alisha", "Peck");
        addUser(0, "user8", "123", "2Alisha", "Peck");
        addUser(0, "user9", "123", "2Alisha", "Peck");
        addUser(0, "user10", "123", "2Alisha", "Peck");
        addUser(0, "user11", "123", "2Alisha", "Peck");
        addUser(0, "user12", "123", "2Alisha", "Peck");

    }

    private void resetWindow() {
        try {
            remove(list1);
        } catch (Exception ignored) {
        }
        try {
            remove(openB);

        } catch (Exception ignored) {
        }
        try {
            remove(jt);
        } catch (Exception ignored) {
        }
        try {
            remove(un);
            remove(uid);
            remove(fn);
        } catch (Exception ignored) {
        }

        revalidate();
        repaint();

        openB = null;
        list1 = null;
        jt = null;

    }

    private void updateUserLabel() {
        this.currentUserLabel.setText("Welcome " + currentUser.getUserName() + "!");
    }
}
