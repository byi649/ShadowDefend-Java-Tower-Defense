import bagel.util.Point;
import bagel.util.Vector2;
import bagel.Image;

/**
 * This class implements and controls the "Projectile" object.
 * Projectiles are spawned from {@link StationaryTower}s but are controlled by the base game class afterwards.
 * Projectiles chase after a specified target, dealing damage to them when they reach their target.
 * They have a speed of 10px/frame.
 */
public class Projectile {
    private static final double PROJECTILE_SPEED = 10;

    private final Image image;
    private final Slicer target;
    private final int damage;

    private Point position;
    private Vector2 vector;

    /**
     * Creates a new projectile.
     * @param image image of projectile.
     * @param position where the projectile is spawned.
     * @param target the slicer to chase after.
     * @param damage amount of damage to deal to the slicer when reached.
     */
    public Projectile(Image image, Point position, Slicer target, int damage) {
        this.image = image;
        this.position = position;
        this.target = target;
        this.damage = damage;
    }

    /**
     * Renders the projectile at it's current position.
     */
    public void draw() {
        image.draw(position.x, position.y);
    }

    /**
     * Moves the projectile towards it's target, at speed 10px/frame. Should be called every frame.
     */
    public void move() {
        updateVector();
        this.position = new Point(position.x + vector.x, position.y + vector.y);
    }

    /**
     * Updates vector of projectile so it homes onto slicer.
     * This is called in {@link Projectile#move()} and does not need to be called elsewhere.
     */
    private void updateVector() {
        vector = target.getPosition().asVector().sub(position.asVector());
        vector = vector.normalised().mul(PROJECTILE_SPEED);
    }

    /**
     * Checks if projectile has reached the target. A projectile has reached it's target if the centre of the projectile
     * is within some small distance to the centre of the target, where the small distance is to take into account
     * overshooting rubberbanding.
     * @return true if projectile is close enough to target, false otherwise.
     */
    public boolean checkIntersectionWithTarget() {
        return position.distanceTo(target.getPosition()) <= PROJECTILE_SPEED + target.getFinalMovementSpeed();
    }

    public boolean checkTargetIsAlive() {
        return target.getHealth() > 0;
    }

    public Image getImage() {
        return image;
    }

    public int getDamage() {
        return damage;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public Slicer getTarget() {
        return target;
    }
}