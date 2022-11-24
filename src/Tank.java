import bagel.util.Point;
import bagel.Image;

/**
 * This class implements and controls the "Tank" stationary tower. It extends base abstract class {@link StationaryTower}.
 * Tank towers all have the same range, projectile cooldown, damage, and build cost.
 * <ul>
 *     <li>Range: 100px</li>
 *     <li>Projectile cooldown: 60 frames</li>
 *     <li>Damage: 1</li>
 *     <li>Build cost: $250</li>
 * </ul>
 */
public class Tank extends StationaryTower {
    private static final int DEFAULT_RADIUS = 100;
    private static final int DEFAULT_PROJECTILE_COOLDOWN = 60;
    private static final int DEFAULT_DAMAGE = 1;
    private static final int DEFAULT_COST = 250;
    private static final Image DEFAULT_IMAGE = new Image("res/images/tank.png");
    private static final Image DEFAULT_PROJECTILE_IMAGE = new Image("res/images/tank_projectile.png");

    /**
     * Creates a new Tank tower at given position
     * @param position place to spawn the tower.
     */
    public Tank(Point position) {
        super(position, DEFAULT_IMAGE, DEFAULT_RADIUS, DEFAULT_PROJECTILE_COOLDOWN, DEFAULT_PROJECTILE_IMAGE, DEFAULT_DAMAGE);
    }

    public static int getCost() {
        return DEFAULT_COST;
    }

    public static int getDamage() {
        return DEFAULT_DAMAGE;
    }

}
