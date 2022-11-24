import bagel.util.Point;
import bagel.Image;

/**
 * This class implements and controls the "Explosive" object.
 * Explosives all have the same range and explode timer.
 * <ul>
 *     <li>Range: 200</li>
 *     <li>Explosion timer: 120 frames</li>
 * </ul>
 * Explosives lie dormant until their timer counts down to zero, then explodes, dealing damage to all slicers in range.
 */
public class Explosive {
    private static final Image image = new Image("res/images/explosive.png");
    private static final int range = 200;

    private final int damage;

    private Point position;
    private Double timeToExplosion = 2.0 * 60;

    /**
     * Creates a new Explosive at given position, that explodes for given damage.
     * @param position place to put the explosive.
     * @param damage how much damage all slicers in range take each.
     */
    public Explosive(Point position, int damage) {
        this.position = position;
        this.damage = damage;
    }

    /**
     * Renders the explosive image at its given position
     */
    public void draw() {
        image.draw(position.x, position.y);
    }

    /**
     * Ticks time down for the explosion timer for one frame, returning true if it explodes.
     * Should be called every frame.
     * @return true if explode time has been reached, false otherwise.
     */
    public boolean tick() {
        timeToExplosion--;
        return timeToExplosion <= 0;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public int getDamage() {
        return damage;
    }

    public static int getRange() {
        return range;
    }

}