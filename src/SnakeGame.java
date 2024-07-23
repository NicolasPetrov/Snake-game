import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Random;
import java.io.IOException;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private final int GRID_SIZE = 20;
    private final int CELL_SIZE = 20;
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private int initialGameSpeed = 100;
    private int gameSpeed = 100;

    private Timer timer;
    private LinkedList<Point> snake;
    private Point food;
    private Point specialFood;
    private int direction;
    private boolean gameOver;
    private int score;
    private boolean specialFoodExists;

    private Clip backgroundMusic;

    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        askDifficulty();
        initGame();
        loadBackgroundMusic();
    }

    private void askDifficulty() {
        String[] options = {"Easy", "Medium", "Hard"};
        int response = JOptionPane.showOptionDialog(null, "Select Difficulty", "Snake Game",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (response == 0) {
            initialGameSpeed = 200;
        } else if (response == 1) {
            initialGameSpeed = 100;
        } else if (response == 2) {
            initialGameSpeed = 50;
        }
        gameSpeed = initialGameSpeed;
    }

    private void initGame() {
        snake = new LinkedList<>();
        snake.add(new Point(WIDTH / 2, HEIGHT / 2));
        direction = 1;
        gameOver = false;
        score = 0;
        specialFoodExists = false;
        spawnFood();
        if (timer != null) {
            timer.stop();
        }
        startTimer();
    }

    private void loadBackgroundMusic() {
        try {
            File soundFile = new File("background.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioIn);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void playSoundEffect(String soundFileName) {
        try {
            File soundFile = new File(soundFileName);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void startTimer() {
        timer = new Timer(gameSpeed, this);
        timer.start();
    }

    private void adjustSpeed() {
        gameSpeed = Math.max(50, initialGameSpeed - score / 10 * 10);
        if (timer != null) {
            timer.setDelay(gameSpeed);
        }
    }

    private void spawnFood() {
        Random random = new Random();
        food = new Point(random.nextInt(WIDTH / CELL_SIZE) * CELL_SIZE,
                random.nextInt(HEIGHT / CELL_SIZE) * CELL_SIZE);

        if (!specialFoodExists && random.nextInt(10) < 2) {
            specialFood = new Point(random.nextInt(WIDTH / CELL_SIZE) * CELL_SIZE,
                    random.nextInt(HEIGHT / CELL_SIZE) * CELL_SIZE);
            specialFoodExists = true;
        }
    }

    private void move() {
        Point head = snake.getFirst();
        Point newHead = new Point(head);

        switch (direction) {
            case 0: newHead.y -= CELL_SIZE; break;
            case 1: newHead.x += CELL_SIZE; break;
            case 2: newHead.y += CELL_SIZE; break;
            case 3: newHead.x -= CELL_SIZE; break;
        }

        if (newHead.equals(food)) {
            playSoundEffect("eat.wav");
            snake.addFirst(newHead);
            score += 10;
            spawnFood();
            adjustSpeed();
        } else if (specialFoodExists && newHead.equals(specialFood)) {
            playSoundEffect("bonus.wav");
            snake.addFirst(newHead);
            score += 50;
            specialFoodExists = false;
            adjustSpeed();
        } else {
            snake.addFirst(newHead);
            snake.removeLast();
        }
    }

    private boolean checkCollision() {
        Point head = snake.getFirst();

        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
            return true;
        }

        for (int i = 1; i < snake.size(); ++i) {
            if (head.equals(snake.get(i))) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("Game Over", WIDTH / 3, HEIGHT / 2);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Score: " + score, WIDTH / 3, HEIGHT / 2 + 50);
        } else {
            g.setColor(Color.GREEN);
            for (Point p : snake) {
                g.fillRect(p.x, p.y, CELL_SIZE, CELL_SIZE);
            }

            g.setColor(Color.RED);
            g.fillRect(food.x, food.y, CELL_SIZE, CELL_SIZE);

            if (specialFoodExists) {
                g.setColor(Color.BLUE);
                g.fillRect(specialFood.x, specialFood.y, CELL_SIZE, CELL_SIZE);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, 10, 20);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
            gameOver = checkCollision();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (!gameOver) {
            switch (key) {
                case KeyEvent.VK_UP: if (direction != 2) direction = 0; break;
                case KeyEvent.VK_RIGHT: if (direction != 3) direction = 1; break;
                case KeyEvent.VK_DOWN: if (direction != 0) direction = 2; break;
                case KeyEvent.VK_LEFT: if (direction != 1) direction = 3; break;
            }
        } else if (key == KeyEvent.VK_SPACE) {
            askDifficulty();
            initGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}