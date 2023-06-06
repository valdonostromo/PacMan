import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Stack;

public class Board extends JFrame implements KeyListener {
    private int[][] maze;
    private int pacmanX;
    private int pacmanY;
    private BoardModel boardModel;
    private JTable board;
    private Thread pacmanThread;
    private Thread timeThread;
    private int seconds;
    private int lastKeyPressed;
    private ImageIcon pacmanIcon;
    private ImageIcon dotIcon;
    private boolean[][] flags;
    public static int points = 0;
    public static int maxPoints = 0;
    private JLabel pointLabel;
    private JLabel timeLabel;
    private JPanel panel;
    public static String timeString;
    public Board() {


        generateBoard();
        startTimer();
        System.out.println(maxPoints);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        pacmanThread = new Thread(() -> {
            while (points != maxPoints) {
                movePacmanAutomatically();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        pacmanThread.start();
    }



    public void generateBoard() {
        pacmanIcon = new ImageIcon("PacMan/src/pacman.png");
        dotIcon = new ImageIcon("PacMan/src/dot.png");
        pointLabel = new JLabel("Points: 0");
        timeLabel = new JLabel();
        maze = generateMaze(Menu.row, Menu.col);
        flags = new boolean[maze.length][maze[0].length];
        boardModel = new BoardModel(maze);
        board = new JTable(boardModel);
        panel = new JPanel(new BorderLayout());
        panel.add(board, BorderLayout.CENTER);
        JPanel labelsPanel = new JPanel(new FlowLayout());
        labelsPanel.add(pointLabel);
        labelsPanel.add(timeLabel);


        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(labelsPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        board.setEnabled(false);
        board.setRowSelectionAllowed(false);
        board.setColumnSelectionAllowed(false);
        setLocationRelativeTo(null);


        panel.setPreferredSize(new Dimension(board.getRowHeight() * maze[0].length, board.getRowHeight() * maze.length));
        pack();


        for(int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                if(maze[i][j] == 0)
                    maxPoints++;
            }
        }
        TableCellRenderer cellRenderer = new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable board, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(board, value, isSelected, hasFocus, row, column);
                int cellValue = (int) value;
                if (cellValue == 1) {
                    c.setBackground(Color.BLUE);
                    setIcon(null);
                    setText(null);
                } else if (cellValue == 2) {
                    if (row == pacmanX && column == pacmanY) {
                        c.setBackground(Color.BLACK);
                        setIcon(pacmanIcon);
                        setHorizontalAlignment(CENTER);
                        setText(null);
                    }
                } else if (cellValue == 0) {
                    c.setBackground(Color.BLACK);
                    setIcon(dotIcon);
                    setHorizontalAlignment(CENTER);
                    setText(null);
                } else if (cellValue == 3) {
                    c.setBackground(Color.BLACK);
                    setIcon(null);
                    setText(null);
                } else if (cellValue == 4){
                    c.setBackground(Color.RED);
                    setIcon(null);
                    setText(" ");
                }
                return c;
            }
        };

        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                board.getColumnModel().getColumn(j).setCellRenderer(cellRenderer);
                if (maze[i][j] == 0) {
                    pacmanX = i;
                    pacmanY = j;
                }
            }
        }
    }

    //generator labiryntu powstał przy pomocy chatGPT
    public static int[][] generateMaze(int height, int width) {
        int[][] maze = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                maze[i][j] = 1;
            }
        }
        int startX = (int) (Math.random() * (height - 2)) + 1;
        int startY = (int) (Math.random() * (width - 2)) + 1;
        Stack<int[]> stack = new Stack<>();
        maze[startX][startY] = 0;
        stack.push(new int[]{startX, startY});
        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            int x = current[0];
            int y = current[1];
            int[][] neighbors = new int[][]{{x - 2, y}, {x + 2, y}, {x, y - 2}, {x, y + 2}};
            boolean hasValidNeighbor = false;
            for (int[] neighbor : neighbors) {
                int nx = neighbor[0];
                int ny = neighbor[1];

                if (nx >= 1 && nx < height - 1 && ny >= 1 && ny < width - 1 && maze[nx][ny] == 1) {
                    hasValidNeighbor = true;
                    break;
                }
            }
            if (hasValidNeighbor) {
                int[] chosenNeighbor = null;

                while (chosenNeighbor == null) {
                    int[] neighbor = neighbors[(int) (Math.random() * 4)];
                    int nx = neighbor[0];
                    int ny = neighbor[1];

                    if (nx >= 1 && nx < height - 1 && ny >= 1 && ny < width - 1 && maze[nx][ny] == 1) {
                        maze[nx][ny] = 0;
                        maze[(x + nx) / 2][(y + ny) / 2] = 0;
                        stack.push(neighbor);
                        chosenNeighbor = neighbor;
                    }
                }
            } else {
                stack.pop();
            }
        }
        return maze;
    }
    public void updatePoints(int points) {
        pointLabel.setText("Points: " + points);
    }
    private void startTimer() {
        timeThread = new Thread(() -> {
            while (points != maxPoints) {
                try {
                    Thread.sleep(1000);
                    seconds++;
                    SwingUtilities.invokeLater(this::updateTimeLabel);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        timeThread.start();
    }
    private void updateTimeLabel() {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        timeString = String.format(" Time: %d:%02d", minutes, remainingSeconds);
        timeLabel.setText(timeString);
    }
    private void movePacmanAutomatically() {
        synchronized (this) {
            int newPacmanX = pacmanX;
            int newPacmanY = pacmanY;

            if (lastKeyPressed == KeyEvent.VK_LEFT) {
                newPacmanY -= 1;
            } else if (lastKeyPressed == KeyEvent.VK_RIGHT) {
                newPacmanY += 1;
            } else if (lastKeyPressed == KeyEvent.VK_UP) {
                newPacmanX -= 1;
            } else if (lastKeyPressed == KeyEvent.VK_DOWN) {
                newPacmanX += 1;
            }

            if (newPacmanX >= 0 && newPacmanX < maze.length && newPacmanY >= 0 && newPacmanY < maze[0].length
                    && maze[newPacmanX][newPacmanY] != 1) {
                if (maze[newPacmanX][newPacmanY] == 0 && !flags[newPacmanX][newPacmanY]) {
                    flags[newPacmanX][newPacmanY] = true;
                    maze[newPacmanX][newPacmanY] = 3;
                    points++;
                    System.out.println(points);
                    updatePoints(points);
                }

                if (maze[pacmanX][pacmanY] != 3) {
                    maze[pacmanX][pacmanY] = flags[pacmanX][pacmanY] ? 3 : 0;
                }

                pacmanX = newPacmanX;
                pacmanY = newPacmanY;
                maze[pacmanX][pacmanY] = 2;

                boardModel.setMaze(maze);
                boardModel.fireTableDataChanged();
                board.repaint();
            } else {
                maze[pacmanX][pacmanY] = 2;

                boardModel.setMaze(maze);
                boardModel.fireTableDataChanged();
                board.repaint();
            }

            if (points == maxPoints) {
                setVisible(false);
                SwingUtilities.invokeLater(() -> new GameOverScreen());
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        synchronized (this) {
            int key = e.getKeyCode();
            lastKeyPressed = e.getKeyCode();
            int newPacmanX = pacmanX;
            int newPacmanY = pacmanY;

            if (key == KeyEvent.VK_LEFT) {
                newPacmanY -= 1;
            } else if (key == KeyEvent.VK_RIGHT) {
                newPacmanY += 1;
            } else if (key == KeyEvent.VK_UP) {
                newPacmanX -= 1;
            } else if (key == KeyEvent.VK_DOWN) {
                newPacmanX += 1;
            }
            else if (e.isControlDown() && e.isShiftDown() && key == KeyEvent.VK_Q) {
                points = 0;
                maxPoints = 0;
                setVisible(false);
                SwingUtilities.invokeLater(() -> new Menu());

            }
            if (newPacmanX >= 0 && newPacmanX < maze.length && newPacmanY >= 0 && newPacmanY < maze[0].length
                    && maze[newPacmanX][newPacmanY] != 1) {
                if (maze[newPacmanX][newPacmanY] == 0 && !flags[newPacmanX][newPacmanY]) {
                    flags[newPacmanX][newPacmanY] = true;
                    maze[newPacmanX][newPacmanY] = 3;
                    points++;
                    System.out.println(points);
                    updatePoints(points);
                }

                if (maze[pacmanX][pacmanY] != 3) {
                    maze[pacmanX][pacmanY] = flags[pacmanX][pacmanY] ? 3 : 0;
                }

                pacmanX = newPacmanX;
                pacmanY = newPacmanY;
                maze[pacmanX][pacmanY] = 2;

                boardModel.setMaze(maze);
                boardModel.fireTableDataChanged();
                board.repaint();
            }
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

}
