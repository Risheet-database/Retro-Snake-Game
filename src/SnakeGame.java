import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {

    private class Tile {
        int x;
        int y;

        public Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    int boardwidth;
    int boardheight;
    int tileSize = 25;

    // Snake
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    // Food
    Tile food;
    Random random;

    // Game logic
    Timer gameLoop;
    int velocityX;
    int velocityY;
    boolean gameOver = false;

    // CardLayout for menu and game
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private MenuPanel menuPanel;

    // SnakeGame constructor
    public SnakeGame(int boardwidth, int boardheight) {
        // Setup the card layout and panels
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        menuPanel = new MenuPanel(this);

        mainPanel.add(menuPanel, "Menu");
        mainPanel.add(this, "Game");

        JFrame frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        this.boardwidth = boardwidth;
        this.boardheight = boardheight;

        // Set up the game panel
        setPreferredSize(new Dimension(this.boardwidth, this.boardheight));
        setBackground(Color.BLACK);

        addKeyListener(this);
        setFocusable(true);

        // Initialize game components
        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<Tile>();

        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        velocityX = 0;
        velocityY = 0;

        gameLoop = new Timer(100, this);
    }

    // Method to start the game
    public void startGame() {
        // Reset game state
        snakeHead = new Tile(5, 5);
        snakeBody.clear();
        placeFood();
        velocityX = 0;
        velocityY = 0;
        gameOver = false;

        // Switch to the game panel
        cardLayout.show(mainPanel, "Game");
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Food
        g.setColor(Color.red);
        g.fill3DRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize, true);

        // Snake head
        g.setColor(Color.green);
        g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);

        // Snake body
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
            g.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize, true);
        }

        // Score
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (gameOver) {
            g.setColor(Color.RED);
            g.drawString("Game Over: " + snakeBody.size(), tileSize - 16, tileSize);
        } else {
            g.drawString("Score: " + snakeBody.size(), tileSize - 16, tileSize);
        }
    }

    // Randomize food location
    public void placeFood() {
        food.x = random.nextInt(boardwidth / tileSize);
        food.y = random.nextInt(boardheight / tileSize);
    }

    public boolean collision(Tile Tile1, Tile Tile2) {
        return Tile1.x == Tile2.x && Tile1.y == Tile2.y;
    }

    public void move() {
        // Eat food
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        // Snake body movement
        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) {
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                Tile prevSnakePart = snakeBody.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        // Snake head movement
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        // Game over conditions
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
            if (collision(snakeHead, snakePart)) {
                gameOver = true;
            }
        }

        // Collision with border
        if (snakeHead.x * tileSize < 0 || snakeHead.x * tileSize > boardwidth 
            || snakeHead.y * tileSize < 0 || snakeHead.y > boardheight) {
            gameOver = true;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
            repaint();
        } else {
            gameLoop.stop();
        }
    }

    // Snake movement according to key presses without going in the reverse direction
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    // MenuPanel class for the main menu
    private class MenuPanel extends JPanel {
        private JButton startButton;
        private JButton exitButton;

        public MenuPanel(SnakeGame game) {
            setLayout(new GridBagLayout());
            setBackground(Color.BLACK);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);

            startButton = new JButton("Start Game");
            startButton.setFont(new Font("Arial", Font.PLAIN, 24));
            startButton.setFocusPainted(false);
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    game.startGame();
                }
            });

            exitButton = new JButton("Exit");
            exitButton.setFont(new Font("Arial", Font.PLAIN, 24));
            exitButton.setFocusPainted(false);
            exitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });

            gbc.gridx = 0;
            gbc.gridy = 0;
            add(startButton, gbc);

            gbc.gridy = 1;
            add(exitButton, gbc);
        }
    }
}
