import java.awt.*;

public class Renderer {

    private final int boardWidth  = GameBoard.BOARD_WIDTH;
    private final int boardHeight = GameBoard.BOARD_HEIGHT;
    private final int tileSize    = GameBoard.TILE_SIZE;

    public void draw(Graphics g, GameBoard board, GameState state) {
        if (state.showMenu) {
            drawMenu(g);
            return;
        }

        // Pacman
        g.drawImage(board.pacman.image,
                board.pacman.x, board.pacman.y,
                board.pacman.width, board.pacman.height, null);

        // Ghosts
        for (Block ghost : board.ghosts) {
            if (state.powerMode) {
                if (state.powerTimer <= 60 && state.ghostBlink) {
                    g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
                } else {
                    g.drawImage(board.scaredGhostImage, ghost.x, ghost.y, ghost.width, ghost.height, null);
                }
            } else {
                g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.width, null);
            }
        }

        // Walls
        for (Block wall : board.walls) {
            g.drawImage(board.wallImage, wall.x, wall.y, wall.width, wall.height, null);
        }

        // Food dots
        g.setColor(Color.WHITE);
        for (Block food : board.foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        // Cherries
        for (Block cherry : board.cherries) {
            g.drawImage(cherry.image, cherry.x, cherry.y, cherry.width, cherry.height, null);
        }

        // HUD / overlays
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (state.gameOver) {
            drawGameOver(g, state);
        } else {
            g.setColor(Color.WHITE);
            g.drawString("Lives: " + state.lives + "  Score: " + state.score, tileSize / 2, tileSize / 2);
        }
    }

    private void drawMenu(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, boardWidth, boardHeight);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        String title = "PACMAN";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (boardWidth - titleWidth) / 2, 150);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.setColor(Color.WHITE);
        g.drawString("Rules:",                                    50, 250);
        g.drawString("- Eat all dots to win",                     50, 280);
        g.drawString("- Avoid ghosts",                            50, 310);
        g.drawString("- Eat cherry to power up",                  50, 340);
        g.drawString("- When powered, you can eat ghosts",        50, 370);

        g.setColor(Color.GREEN);
        String start = "Press SPACE to Start";
        int startWidth = g.getFontMetrics().stringWidth(start);
        g.drawString(start, (boardWidth - startWidth) / 2, 450);
    }

    private void drawGameOver(Graphics g, GameState state) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, boardWidth, boardHeight);

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        String text = "GAME OVER";
        int textWidth = g.getFontMetrics().stringWidth(text);
        int y = boardHeight / 2;
        g.drawString(text, (boardWidth - textWidth) / 2, y);

        g.setFont(new Font("Arial", Font.PLAIN, 25));
        g.setColor(Color.WHITE);
        String scoreText = "Score: " + state.score;
        int scoreWidth = g.getFontMetrics().stringWidth(scoreText);
        g.drawString(scoreText, (boardWidth - scoreWidth) / 2, y + 40);

        String retryText = "Press any key to restart";
        int retryWidth = g.getFontMetrics().stringWidth(retryText);
        g.drawString(retryText, (boardWidth - retryWidth) / 2, y + 80);
    }
}
