import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {

    private final GameBoard board;
    private final GameState state;
    private final Renderer  renderer;

    private Timer gameLoop;
    private final char[]  directions = {'U', 'D', 'L', 'R'};
    private final Random  random     = new Random();

    private ArrayList<Point> pacmanTrail = new ArrayList<>();

    public PacMan() {
        setPreferredSize(new Dimension(GameBoard.BOARD_WIDTH, GameBoard.BOARD_HEIGHT));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        board    = new GameBoard(this);
        state    = new GameState();
        renderer = new Renderer();

        // Give ghosts an initial random direction
        for (Block ghost : board.ghosts) {
            ghost.updateDirection(directions[random.nextInt(4)]);
        }

        gameLoop = new Timer(50, this); // 20 fps
        gameLoop.start();
    }
  
    // Painting
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.draw(g, board, state);
    }

    // Movement & collision
    public void move() {
        if (state.showMenu) return;

        updatePowerMode();
        movePacman();
        moveGhosts();
        checkFoodCollision();
        checkCherryCollision();

        if (board.foods.isEmpty()) {
            board.loadMap();
            resetPositions();
        }
    }

    private void updatePowerMode() {
        if (!state.powerMode) return;

        state.powerTimer--;
        if (state.powerTimer <= 60) {
            state.blinkCounter++;
            if (state.blinkCounter % 8 == 0) {
                state.ghostBlink = !state.ghostBlink;
            }
        }
        if (state.powerTimer <= 0) {
            state.powerMode    = false;
            state.powerTimer   = 0;
            state.ghostBlink   = false;
            state.blinkCounter = 0;
        }
    }

    private void movePacman() {
        int tileSize = GameBoard.TILE_SIZE;

        // Try to turn if aligned to grid
        if (board.pacman.x % tileSize == 0 && board.pacman.y % tileSize == 0) {
            if (canMove(board.pacman, state.nextDirection)) {
                board.pacman.updateDirection(state.nextDirection);
            }
        }

        board.pacman.x += board.pacman.velocityX;
        board.pacman.y += board.pacman.velocityY;
        pacmanTrail.add(new Point(board.pacman.x, board.pacman.y));
        if (pacmanTrail.size() > 300) pacmanTrail.remove(0);

        // Wrap around
        if (board.pacman.x < 0) {
            board.pacman.x = GameBoard.BOARD_WIDTH - tileSize;
        } else if (board.pacman.x + board.pacman.width > GameBoard.BOARD_WIDTH) {
            board.pacman.x = 0;
        }

        // Update sprite image based on direction
        if      (board.pacman.velocityX > 0) board.pacman.image = board.pacmanRightImage;
        else if (board.pacman.velocityX < 0) board.pacman.image = board.pacmanLeftImage;
        else if (board.pacman.velocityY < 0) board.pacman.image = board.pacmanUpImage;
        else if (board.pacman.velocityY > 0) board.pacman.image = board.pacmanDownImage;

        // Wall collision
        for (Block wall : board.walls) {
            if (Block.collision(board.pacman, wall)) {
                board.pacman.x -= board.pacman.velocityX;
                board.pacman.y -= board.pacman.velocityY;
                break;
            }
        }
    }

    private void moveGhosts() {
        int tileSize = GameBoard.TILE_SIZE;

        for (Block ghost : board.ghosts) {
            // Check ghost-pacman collision
            if (Block.collision(ghost, board.pacman)) {
                if (state.powerMode) {
                    ghost.reset();
                    state.score += 200;
                } else {
                    state.lives--;
                    if (state.lives == 0) {
                        state.gameOver = true;
                        return;
                    }
                    resetPositions();
                }
            }

            // Force upward when on row 9 (ghost house exit row)
            if (ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }

            // AI: follow pacman trail or wander randomly
            if (pacmanTrail.size() > ghost.delay) {
                Point target = pacmanTrail.get(pacmanTrail.size() - ghost.delay);
                int dx = target.x - ghost.x;
                int dy = target.y - ghost.y;
                char bestDir = (Math.abs(dx) > Math.abs(dy))
                        ? (dx > 0 ? 'R' : 'L')
                        : (dy > 0 ? 'D' : 'U');
                if (canMove(ghost, bestDir)) ghost.updateDirection(bestDir);
            } else {
                if (random.nextInt(15) == 0) {
                    char newDir = directions[random.nextInt(4)];
                    if (canMove(ghost, newDir)) ghost.updateDirection(newDir);
                }
            }

            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            // Wrap around
            if (ghost.x < 0) {
                ghost.x = GameBoard.BOARD_WIDTH - tileSize;
            } else if (ghost.x + ghost.width > GameBoard.BOARD_WIDTH) {
                ghost.x = 0;
            }

            // Wall bounce
            for (Block wall : board.walls) {
                if (Block.collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= GameBoard.BOARD_WIDTH) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    ghost.updateDirection(directions[random.nextInt(4)]);
                }
            }
        }
    }

    private void checkFoodCollision() {
        Block eaten = null;
        for (Block food : board.foods) {
            if (Block.collision(board.pacman, food)) {
                eaten = food;
                state.score += 10;
                break;
            }
        }
        board.foods.remove(eaten);
    }

    private void checkCherryCollision() {
        Block eaten = null;
        for (Block cherry : board.cherries) {
            if (Block.collision(board.pacman, cherry)) {
                eaten = cherry;
                state.score += 50;
                state.powerMode  = true;
                state.powerTimer = 200;
                break;
            }
        }
        board.cherries.remove(eaten);
    }

    // Helpers
    public boolean canMove(Block block, char direction) {
        int vx = 0, vy = 0;
        int step = GameBoard.TILE_SIZE / 4;
        if (direction == 'U') vy = -step;
        if (direction == 'D') vy =  step;
        if (direction == 'L') vx = -step;
        if (direction == 'R') vx =  step;

        Block temp = new Block(null, block.x + vx, block.y + vy, block.width, block.height);
        for (Block wall : board.walls) {
            if (Block.collision(temp, wall)) return false;
        }
        return true;
    }

    public void resetPositions() {
        board.pacman.reset();
        board.pacman.velocityX = 0;
        board.pacman.velocityY = 0;
        pacmanTrail.clear();
        for (Block ghost : board.ghosts) {
            ghost.reset();
            ghost.updateDirection(directions[random.nextInt(4)]);
        }
    }

    // Game loop
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (state.gameOver) gameLoop.stop();
    }

    // Input
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {}
  
    @Override
    public void keyReleased(KeyEvent e) {
        if (state.showMenu && e.getKeyCode() == KeyEvent.VK_SPACE) {
            state.showMenu = false;
            return;
        }
        if (state.gameOver) {
            board.loadMap();
            resetPositions();
            state.reset();
            gameLoop.start();
            return;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP    -> state.nextDirection = 'U';
            case KeyEvent.VK_DOWN  -> state.nextDirection = 'D';
            case KeyEvent.VK_LEFT  -> state.nextDirection = 'L';
            case KeyEvent.VK_RIGHT -> state.nextDirection = 'R';
        }
    }
}
