/*

Program description: MemoryTest is a Java Swing memory-matching game where players match pairs of colored circles on a grid. 
The project includes a timer, interactive GUI, 
and end-game dialogs for a complete gaming experience. 
It offers a simple yet engaging way for users to test and enhance their memory skills.

Author: Halim Ağdemir

E-mail address:agdemirhalim4@gmail.com  

Homework Number:Project-1 Memory Game

Last Changed:12/10/2023

*/


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.*;

// MemoryTest class represents the main game window
public class MemoryGame extends JFrame {
    // Constants for the game board
    private static final int BOARD_SIZE = 4;
    private static final int CELL_SIZE = 100;
    
    // 2D array to represent the game board
    private int[][] board;

    // 2D array to track correct guesses
    private boolean[][] correctGuesses;

    // Variables to store the current selected cell
    private int currentGuessRow = -1;
    private int currentGuessCol = -1;

    // Counter for incorrect guesses
    private int incorrectGuessCount = 0;

    // Variable to store the start time of the game
    private long startTime;

    // Panel to display the game board
    private MemoryPanel memoryPanel;

    // Constructor for MemoryTest class
    public MemoryGame() {
        initializeBoard();
        setTitle("Memory Game");
        setSize(CELL_SIZE * BOARD_SIZE + 20, CELL_SIZE * BOARD_SIZE + 40);
        correctGuesses = new boolean[BOARD_SIZE][BOARD_SIZE];
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize the MemoryPanel and add it to the JFrame
        memoryPanel = new MemoryPanel();
        add(memoryPanel);

        // Display colors for 3 seconds
        displayColorsFor3Seconds();

        // Add mouse listener to the MemoryPanel
        memoryPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int xMouse = e.getX();
                int yMouse = e.getY();

                // Calculate the indices of the clicked cell
                int xIndex = yMouse / CELL_SIZE;
                int yIndex = xMouse / CELL_SIZE;

                // Handle the mouse click event
                handleMouseClick(xIndex, yIndex);
            }
        });

        // Record the start time of the game
        startTime = System.currentTimeMillis();
    }

    // Initialize the game board with random values
    private void initializeBoard() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        Random rnd = new Random();

        // Populate the board with pairs of random values
        for (int i = 0; i < BOARD_SIZE * BOARD_SIZE / 2; i++) {
            for (int j = 0; j < 2; j++) {
                int x, y;
                do {
                    x = rnd.nextInt(BOARD_SIZE);
                    y = rnd.nextInt(BOARD_SIZE);
                } while (board[x][y] != 0);

                board[x][y] = i;
            }
        }
    }

    // Handle the mouse click event on the game board
    private void handleMouseClick(int xIndex, int yIndex) {
        // Check if the selected cell is out of bounds or already guessed correctly
        if (xIndex < 0 || xIndex >= BOARD_SIZE || yIndex < 0 || yIndex >= BOARD_SIZE || correctGuesses[xIndex][yIndex]) {
            return;
        }

        // Check if it's the first or second guess
        if (currentGuessRow == -1 && currentGuessCol == -1) {
            // First guess
            currentGuessRow = xIndex;
            currentGuessCol = yIndex;
            memoryPanel.displayCircle(currentGuessRow, currentGuessCol, board[currentGuessRow][currentGuessCol]);
        } else {
            // Second guess
            int prevValue = board[currentGuessRow][currentGuessCol];
            int currentValue = board[xIndex][yIndex];

            // Display the second circle
            memoryPanel.displayCircle(xIndex, yIndex, currentValue);

            // Check if the guesses match
            if (prevValue == currentValue && (currentGuessRow != xIndex || currentGuessCol != yIndex)) {
                // Correct guess
                correctGuesses[currentGuessRow][currentGuessCol] = true;
                correctGuesses[xIndex][yIndex] = true;

                // Repaint the panel
                memoryPanel.revalidate();
                memoryPanel.repaint();
            } else {
                // Incorrect guess
                incorrectGuessCount++;
            }

            // Create a timer to clear the circles after a delay
            Timer timer = new Timer(300, e -> {
                // Clear the first circle if it was not guessed correctly
                if (!correctGuesses[currentGuessRow][currentGuessCol]) {
                    memoryPanel.clearCell(currentGuessRow, currentGuessCol);
                }
                // Clear the second circle if it was not guessed correctly
                if (!correctGuesses[xIndex][yIndex]) {
                    memoryPanel.clearCell(xIndex, yIndex);
                }

                // Check if the game is over and display the appropriate dialog
                if (checkGameOver()) {
                    showGameOverDialog();
                } else if (incorrectGuessCount >= 3) {
                    showGameOverDialog();
                }

                // Reset the current guesses
                currentGuessRow = -1;
                currentGuessCol = -1;
            });

            // Start the timer
            timer.setRepeats(false);
            timer.start();
        }
    }

    // Check if the game is over
    private boolean checkGameOver() {
        int correctCount = 0;

        // Count the number of correct guesses
        for (boolean[] row : correctGuesses) {
            for (boolean correct : row) {
                if (correct) {
                    correctCount++;
                }
            }
        }

        // Check win condition and display appropriate dialog
        if (correctCount == BOARD_SIZE * BOARD_SIZE && incorrectGuessCount < 3) {
            showGameWonDialog();
            return true;
        } else if (incorrectGuessCount >= 3) {
            showGameOverDialog();
            return true;
        }

        return false;
    }

    // Display the game over dialog
    private void showGameOverDialog() {
        JOptionPane.showMessageDialog(this, "Game Over!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    // Display the game won dialog
    private void showGameWonDialog() {
        long endTime = System.currentTimeMillis();
        long elapsedTime = (endTime - startTime) / 1000;

        JOptionPane.showMessageDialog(this, "Congratulations! You won in " + elapsedTime + " seconds.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    // MemoryPanel class represents the panel for displaying the game board
    private class MemoryPanel extends JPanel {
        // Override paintComponent to customize the drawing of the panel
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Fill the panel background with black
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());

            // Iterate through the game board and draw circles or empty cells
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (correctGuesses[i][j]) {
                        // Draw a gray cell for correct guesses
                        g.setColor(Color.GRAY);
                        g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                        // Draw a colored circle for correct guesses
                        int value = board[i][j];
                        Color circleColor = getColorForValue(value);
                        int x = j * CELL_SIZE + CELL_SIZE / 2;
                        int y = i * CELL_SIZE + CELL_SIZE / 2;

                        g.setColor(circleColor);
                        g.fillOval(x - CELL_SIZE / 4, y - CELL_SIZE / 4, CELL_SIZE / 2, CELL_SIZE / 2);
                    } else {
                        // Draw a black cell for incorrect guesses or unrevealed cells
                        g.setColor(Color.BLACK);
                        g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                        // Draw a white border for cells
                        g.setColor(Color.WHITE);
                        g.drawRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                }
            }
        }

        // Display a colored circle on the specified cell
        void displayCircle(int xIndex, int yIndex, int value) {
            if (!correctGuesses[xIndex][yIndex]) {
                SwingUtilities.invokeLater(() -> {
                    Graphics g = getGraphics();
                    if (g != null) {
                        int x = yIndex * CELL_SIZE + CELL_SIZE / 2;
                        int y = xIndex * CELL_SIZE + CELL_SIZE / 2;

                        g.setColor(getColorForValue(value));
                        g.fillOval(x - CELL_SIZE / 4, y - CELL_SIZE / 4, CELL_SIZE / 2, CELL_SIZE / 2);
                    }
                });
            }
        }

        // Clear the specified cell
        void clearCell(int row, int col) {
            Graphics g = getGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            g.setColor(Color.WHITE);
            g.drawRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);

            repaint();
        }

        // Clear the entire panel
        void clearPanel() {
            correctGuesses = new boolean[BOARD_SIZE][BOARD_SIZE];
            repaint();
        }
    }

    // Display colored circles on the board for a short duration
    private void displayColorsFor3Seconds() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                int finalI = i;
                int finalJ = j;

                // Create a timer for each cell to display circles sequentially
                Timer timer = new Timer(300 + i * BOARD_SIZE + j * 100, e -> {
                    memoryPanel.displayCircle(finalI, finalJ, board[finalI][finalJ]);
                });
                timer.setRepeats(false);
                timer.start();
            }
        }

        // Create a timer to clear the MemoryPanel after 3 seconds
        Timer clearTimer = new Timer(3000 + BOARD_SIZE * BOARD_SIZE * 100, e -> {
            clearMemoryPanel();
            memoryPanel.repaint();  // Repaint the panel after clearing circles
        });
        clearTimer.setRepeats(false);
        clearTimer.start();
    }

    // Clear the MemoryPanel
    private void clearMemoryPanel() {
        memoryPanel.clearPanel();
    }

    // Get a color based on the value
    private Color getColorForValue(int value) {
        switch (value) {
            case 0:
                return Color.BLUE;
            case 1:
                return Color.YELLOW;
            case 2:
                return Color.RED;
            case 3:
                return Color.WHITE;
            case 4:
                return Color.PINK;
            case 5:
                return Color.MAGENTA;
            case 6:
                return Color.CYAN;
            case 7:
                return Color.GREEN;
            default:
                return Color.BLACK;
        }
    }

    // Main method to start the game
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MemoryGame memoryGame = new MemoryGame();
            memoryGame.setVisible(true);
        });
    }
}
