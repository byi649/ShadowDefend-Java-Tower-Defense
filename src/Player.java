/**
 * This class implements and controls the "Player" object. There should only be one player per level.
 * Players start with 25 health and 500 gold. Players lose health whenever slicers reach their goal, ending the game
 * when they reach 0 health. Players gain gold by killing slicers and finishing waves, and can spend it on buying towers.
 */

public class Player {
    private static final int STARTING_HEALTH = 25;
    private static final int STARTING_GOLD = 500;

    private int health;
    private int gold;

    /**
     * Creates a new Player object, with 25 starting health and 500 starting gold.
     */
    public Player() {
        this.health = STARTING_HEALTH;
        this.gold = STARTING_GOLD;
    }

    /**
     * Spends player's gold if possible, returning false if transaction was unsuccessful.
     * @param n amount of gold to spent.
     * @return true if successful transaction, false otherwise.
     */
    public boolean spendGold(int n) {
        if (gold >= n) {
            gold -= n;
            return true;
        } else {
            return false;
        }
    }

    public void gainGold(int n) {
        gold += n;
    }

    public void loseHealth(int n) {
        health -= n;
    }

    public int getHealth() {
        return health;
    }

    public int getGold() {
        return gold;
    }
}
