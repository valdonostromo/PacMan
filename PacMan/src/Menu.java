import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;

public class Menu extends JFrame{
    private JPanel menu = new JPanel();
    private JButton start = new JButton("Start");
    private JButton newGame = new JButton("New Game");
    private JButton highScore = new JButton("High Score");
    private JButton exit = new JButton("Exit");

    private JFormattedTextField rowField = new JFormattedTextField(NumberFormat.getNumberInstance());
    private JFormattedTextField colField = new JFormattedTextField(NumberFormat.getNumberInstance());
    private JLabel label = new JLabel("Enter your window size (row/col)");
    public static int row;
    public static int col;


    public Menu(){
        menu.setLayout(new FlowLayout());
        add(menu);
        setSize(370, 150);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rowField.setColumns(5);
        colField.setColumns(5);
        menu.add(newGame, FlowLayout.LEFT);
        menu.add(highScore, FlowLayout.CENTER);
        menu.add(exit, FlowLayout.RIGHT);

        newGame.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menu.add(label);
                menu.add(rowField);
                menu.add(colField);
                menu.add(start);
                revalidate();
            }
        });

        exit.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        start.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Board.points = 0;
                Board.maxPoints = 0;
                try{
                    if(((Number) colField.getValue()).intValue() < 10 || ((Number) colField.getValue()).intValue() > 100 ||
                            ((Number) rowField.getValue()).intValue() < 10 || ((Number) rowField.getValue()).intValue() > 100
                    )
                        throw new Exception();
                    else {
                        col = ((Number) colField.getValue()).intValue();
                        row = ((Number) rowField.getValue()).intValue();
                        setVisible(false);
                        SwingUtilities.invokeLater(() -> new Board());
                    }
                }
                catch (Exception E){
                   JOptionPane.showMessageDialog(menu, "Enter correct values!");
                }
            }
        });
        highScore.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> new HighScore());

            }
        });

    }
}
