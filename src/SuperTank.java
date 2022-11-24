import bagel.util.Point;
import bagel.Image;

/**
 * This class implements and controls the "SuperTank" stationary tower. It extends base abstract class {@link StationaryTower}.
 * SuperTank towers all have the same range, projectile cooldown, damage, and build cost.
 * <ul>
 *     <li>Range: 150px</li>
 *     <li>Projectile cooldown: 30 frames</li>
 *     <li>Damage: 3*{@link Tank} damage</li>
 *     <li>Build cost: $600</li>
 * </ul>
 */
public class SuperTank extends StationaryTower {
    private static final int DEFAULT_RADIUS = 150;
    private static final int DEFAULT_PROJECTILE_COOLDOWN = 30;
    private static final int DEFAULT_DAMAGE = 3*Tank.getDamage();
    private static final int DEFAULT_COST = 600;
    private static final Image DEFAULT_IMAGE = new Image("res/images/supertank.png");
    private static final Image DEFAULT_PROJECTILE_IMAGE = new Image("res/images/supertank_projectile.png");

    /**
     * Creates a new SuperTank tower at given position
     * @param position place to spawn the tower.
     */
    public SuperTank(Point position) {
        super(position, DEFAULT_IMAGE, DEFAULT_RADIUS, DEFAULT_PROJECTILE_COOLDOWN, DEFAULT_PROJECTILE_IMAGE, DEFAULT_DAMAGE);
    }

    public static int getCost() {
        return DEFAULT_COST;
    }

}
