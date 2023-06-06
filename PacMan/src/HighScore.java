import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HighScore extends JFrame{
    public static DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> scores = new JList<>(listModel);
    public HighScore(){
        JScrollPane scrollPane = new JScrollPane(scores);
        scores.setCellRenderer(new NumberedCellRenderer());
        add(scrollPane);
        setVisible(true);
        setLocationRelativeTo(null);
        setSize(200, 300);
        loadScoresFromFile();
        sortScores();
    }
    private void loadScoresFromFile() {
        listModel.clear();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("scores.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                listModel.addElement(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void sortScores() {
        List<String> tempList = new ArrayList<>();
        for (int i = 0; i < listModel.size(); i++) {
            tempList.add(listModel.get(i));
        }
        Collections.sort(tempList, (s1, s2) -> {
            String[] parts1 = s1.split(" Time: ");
            String[] parts2 = s2.split(" Time: ");
            String time1 = parts1[1];
            String time2 = parts2[1];
            time1 = time1.replace("Time: ", "");
            time2 = time2.replace("Time: ", "");
            return time1.compareTo(time2);
        });
        listModel.clear();
        for (String item : tempList) {
            listModel.addElement(item);
        }
    }
    private class NumberedCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);

            String text = String.format("%d. %s", index + 1, value.toString());
            ((JLabel) component).setText(text);

            return component;
        }
    }
}