import java.awt.Image;
import java.util.HashSet;

public class Block {
    public int x;
    public int y;
    public int height;
    public int width;
    public Image image;
    public int delay;

    public int startX;
    public int startY;
    public char direction = 'U'; // U D L R
    public int velocityX = 0;
    public int velocityY = 0;

    // Reference to walls — set by GameBoard after construction
    public static HashSet<Block> walls;

    public Block(Image image, int x, int y, int width, int height) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.startX = x;
        this.startY = y;
    }

    public void updateDirection(char direction) {
        char preDirection = this.direction;
        this.direction = direction;
        updateVelocity();
        this.x += this.velocityX;
        this.y += this.velocityY;
        if (walls != null) {
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = preDirection;
                    updateVelocity();
                }
            }
        }
    }

    public void updateVelocity() {
        int tileSize = GameBoard.TILE_SIZE;
        if (this.direction == 'U') {
            this.velocityX = 0;
            this.velocityY = -tileSize / 4;
        } else if (this.direction == 'D') {
            this.velocityX = 0;
            this.velocityY = tileSize / 4;
        } else if (this.direction == 'L') {
            this.velocityX = -tileSize / 4;
            this.velocityY = 0;
        } else if (this.direction == 'R') {
            this.velocityX = tileSize / 4;
            this.velocityY = 0;
        }
    }

    public void reset() {
        this.x = this.startX;
        this.y = this.startY;
    }

    public static boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
               a.x + a.width > b.x &&
               a.y < b.y + b.height &&
               a.y + a.height > b.y;
    }
}
