import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.IOException;

public class GameOverScreen extends JFrame{
    private JPanel panel = new JPanel();
    private JLabel label = new JLabel("Enter your nickname: ");
    private JButton save = new JButton("save");
    private JTextField textField = new JTextField();

    public GameOverScreen(){
        panel.setLayout(new FlowLayout());
        add(panel);
        setSize(400,400);
        setVisible(true);
        setLocationRelativeTo(null);
        panel.add(label, FlowLayout.LEFT);
        panel.add(textField, FlowLayout.CENTER);
        panel.add(save, FlowLayout.RIGHT);
        textField.setColumns(10);

        save.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    FileWriter writer = new FileWriter("scores.txt", true);
                    String nick = textField.getText();
                    writer.write(nick);
                    writer.write(" points: " + Board.points);
                    writer.write(Board.timeString);
                    writer.write(System.lineSeparator());
                    writer.flush();
                    setVisible(false);
                    SwingUtilities.invokeLater(() -> new Menu());
                } catch (IOException E) {
                    E.printStackTrace();
                }
            }
        });


    }


}
