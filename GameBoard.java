import java.awt.Image;
import java.util.HashSet;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class GameBoard {

    public static final int ROW_COUNT    = 21;
    public static final int COLUMN_COUNT = 19;
    public static final int TILE_SIZE    = 32;
    public static final int BOARD_WIDTH  = COLUMN_COUNT * TILE_SIZE;
    public static final int BOARD_HEIGHT = ROW_COUNT    * TILE_SIZE;

    // Images
    public Image wallImage;
    public Image blueGhostImage;
    public Image orangeGhostImage;
    public Image pinkGhostImage;
    public Image redGhostImage;
    public Image pacmanUpImage;
    public Image pacmanDownImage;
    public Image pacmanLeftImage;
    public Image pacmanRightImage;
    public Image scaredGhostImage;
    public Image cherryImage;

    // Game object sets
    public HashSet<Block> walls;
    public HashSet<Block> foods;
    public HashSet<Block> ghosts;
    public HashSet<Block> cherries;
    public Block pacman;

    // X = wall, O = skip, P = pac man, ' ' = food
    // Ghosts: b = blue, o = orange, p = pink, r = red, C = cherry
    private static final String[] TILE_MAP = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XXXX X X X XXXX X",
        "X                 X",
        "X C XX XXXXX XX X X",
        "X X    X   X    X X",
        "X XXXX X X X XXXX X",
        "X      X X X      X",
        "XXXXXX XrX X XXXXXX",
        "O   b      o   p  O",
        "XXXXXX X X X XXXXXX",
        "X      X X X      X",
        "X XXXX X X X XXXX X",
        "X X    X   X    C X",
        "X X XX XXXXX XX X X",
        "X   X    P      X X",
        "XXX X X XXXXX X XXX",
        "X     X   X   X   X",
        "X XXXXXXX X XXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX"
    };

    public GameBoard(JPanel panel) {
        loadImages(panel);
        loadMap();
    }

    private void loadImages(JPanel panel) {
        wallImage        = new ImageIcon(panel.getClass().getResource("./wall.png")).getImage();
        blueGhostImage   = new ImageIcon(panel.getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(panel.getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage   = new ImageIcon(panel.getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage    = new ImageIcon(panel.getClass().getResource("./redGhost.png")).getImage();
        pacmanUpImage    = new ImageIcon(panel.getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage  = new ImageIcon(panel.getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage  = new ImageIcon(panel.getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(panel.getClass().getResource("./pacmanRight.png")).getImage();
        scaredGhostImage = new ImageIcon(panel.getClass().getResource("./scaredGhost.png")).getImage();
        cherryImage      = new ImageIcon(panel.getClass().getResource("./cherry2.png")).getImage();
    }

    public void loadMap() {
        walls    = new HashSet<>();
        foods    = new HashSet<>();
        ghosts   = new HashSet<>();
        cherries = new HashSet<>();

        for (int r = 0; r < ROW_COUNT; r++) {
            for (int c = 0; c < COLUMN_COUNT; c++) {
                char tile = TILE_MAP[r].charAt(c);
                int x = c * TILE_SIZE;
                int y = r * TILE_SIZE;

                switch (tile) {
                    case 'X':
                        walls.add(new Block(wallImage, x, y, TILE_SIZE, TILE_SIZE));
                        break;
                    case 'b':
                        Block blue = new Block(blueGhostImage, x, y, TILE_SIZE, TILE_SIZE);
                        blue.delay = 20;
                        ghosts.add(blue);
                        break;
                    case 'o':
                        Block orange = new Block(orangeGhostImage, x, y, TILE_SIZE, TILE_SIZE);
                        orange.delay = 30;
                        ghosts.add(orange);
                        break;
                    case 'p':
                        Block pink = new Block(pinkGhostImage, x, y, TILE_SIZE, TILE_SIZE);
                        pink.delay = 40;
                        ghosts.add(pink);
                        break;
                    case 'r':
                        Block red = new Block(redGhostImage, x, y, TILE_SIZE, TILE_SIZE);
                        red.delay = 10;
                        ghosts.add(red);
                        break;
                    case 'P':
                        pacman = new Block(pacmanRightImage, x, y, TILE_SIZE, TILE_SIZE);
                        break;
                    case ' ':
                        foods.add(new Block(null, x + 14, y + 14, 4, 4));
                        break;
                    case 'C':
                        cherries.add(new Block(cherryImage, x, y, TILE_SIZE, TILE_SIZE));
                        break;
                }
            }
        }

        // Give Block class access to walls for direction checking
        Block.walls = this.walls;
    }
}
