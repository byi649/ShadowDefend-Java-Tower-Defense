import bagel.util.Point;
import bagel.Image;
import bagel.util.Vector2;

/**
 * Abstract class extending {@link Tower}. Used for stationary towers {@link Tank} and {@link SuperTank}. These towers
 * stand still and fire projectiles at nearby slicers. Implements attack logic and slicer targeting logic.
 */
public abstract class StationaryTower extends Tower {
    private final int radius;
    private final int projectileCooldown;
    private final Image projectileImage;
    private final int damage;

    private Double currentCooldown = 0.0;

    /**
     * Creates a new StationaryTower at given point and parameters.
     * @param position position of tower placement.
     * @param image image of tower.
     * @param radius attack radius of tower.
     * @param projectileCooldown how long between projectile shots from this tower.
     * @param projectileImage image of projectile.
     * @param damage how much projectiles deal.
     */
    public StationaryTower(Point position, Image image, int radius, int projectileCooldown, Image projectileImage, int damage) {
        super(position, image);
        this.radius = radius;
        this.projectileCooldown = projectileCooldown;
        this.projectileImage = projectileImage;
        this.damage = damage;
    }

    /**
     * Ticks cooldown down while searching for the nearest slicer in range. The tower will turn to face the target, and
     * will fire a projectile if the projectile cooldown has reached zero. This should be called every frame.
     * @param level level object containing slicers.
     * @return {@link Projectile} object with target slicer and damage, else null if no slicers in range.
     */
    public Projectile attack(Level level) {
        currentCooldown--;
        Slicer target = getNearestSlicer(level);
        if (target != null) {
            Vector2 tempVector = target.getPosition().asVector().sub(this.getPosition().asVector());
            tempVector = new Point(-tempVector.y, tempVector.x).asVector();
            turn(tempVector);
            if (currentCooldown <= 0) {
                currentCooldown = (double) this.getProjectileCooldown();
                return spawnProjectile(target);
            }
        }
        return null;
    }

    /**
     * Gets the nearest slicer in range in a specified level.
     * @param level level to search for slicers.
     * @return nearest slicer in range, null if none exist.
     */
    public Slicer getNearestSlicer(Level level) {
        return level.getNearestSlicerInRange(this.getPosition(), this.getRadius());
    }

    /**
     * Creates a projectile targeting specified slicer that will deal tower's damage upon reaching it.
     * @param target slicer to target.
     * @return {@link Projectile} with specified target and damage.
     */
    public Projectile spawnProjectile(Slicer target) {
        return new Projectile(projectileImage, this.getPosition(), target, damage);
    }

    public int getRadius() {
        return radius;
    }

    public int getProjectileCooldown() {
        return projectileCooldown;
    }

}
