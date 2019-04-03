package UI;

import javax.swing.*;
import java.awt.*;

public class EditorPanel extends JFrame {
    public EditorPanel(String title, String data) throws HeadlessException {
        super(title);
        setSize(600, 400);
        setResizable(false);
        setLocationRelativeTo(null);

        JEditorPane pane = new JEditorPane();
        pane.setContentType("text/html");
        pane.setText(data);
        setContentPane(pane);

        setVisible(true);
    }
}
