import javax.swing.table.AbstractTableModel;

public class BoardModel extends AbstractTableModel {
    private int[][] data;

    public BoardModel(int[][] data) {
        this.data = data;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return data[0].length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    public void setMaze(int[][] maze) {
    }
}
