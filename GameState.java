/**
 * Holds all mutable runtime state for a Pac-Man game session.
 * Passed between PacMan, Renderer, and move logic so each class
 * reads/writes a single shared state object.
 */
public class GameState {
    public int  score       = 0;
    public int  lives       = 3;
    public boolean gameOver  = false;
    public boolean powerMode = false;
    public int  powerTimer  = 0;
    public boolean showMenu  = true;
    public boolean ghostBlink   = false;
    public int  blinkCounter = 0;
    public char nextDirection = 'R';

    public void reset() {
        score        = 0;
        lives        = 3;
        gameOver     = false;
        powerMode    = false;
        powerTimer   = 0;
        showMenu     = false;
        ghostBlink   = false;
        blinkCounter = 0;
        nextDirection = 'R';
    }
}
