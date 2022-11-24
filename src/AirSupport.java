import bagel.Image;
import bagel.Window;
import bagel.util.Point;
import bagel.util.Vector2;

/**
 * This class implements and controls the "Air Support" moving tower. It extends base abstract class {@link Tower}.
 * Air support towers all have the same flight speed, damage, and build cost.
 * <ul>
 *     <li>Flight speed: 5px/frame</li>
 *     <li>Damage of explosive: 500</li>
 *     <li>Build cost: $500</li>
 * </ul>
 * Air support towers fly from the edge of the screen to the opposite edge, starting from left to right and then
 * alternating for each successive new tower instanced. They should be destroyed by the caller once the tower leaves
 * the window. Air support towers drop {@link Explosive}s at randomised intervals along their flight path.
 */
public class AirSupport extends Tower {
    private static final double flightSpeed = 5;
    private static final int damage = 500;
    private static final int cost = 500;
    private static boolean lastPlacedVertical = true;

    private final boolean flyVertical;

    private Double explosiveCooldown;

    /**
     * Creates a new air support tower at given position.
     * If the previous air support tower was flying from top to bottom, this one will fly left to right and vice versa.
     * The unfixed dimension (eg x if top to bottom) is taken from the input point.
     * {@link AirSupport#attack()} must be called each frame to fly the plane and drop explosives.
     * @param position the position of the cursor when bought.
     */
    public AirSupport(Point position) {
        super(position, new Image("res/images/airsupport.png"));
        if (lastPlacedVertical) {
            this.setPosition(new Point(0, position.y));
            this.turn(Vector2.down);
        } else {
            this.setPosition(new Point(position.x, 0));
            this.turn(Vector2.left);
        }
        // Flip flight line
        lastPlacedVertical = !lastPlacedVertical;
        flyVertical = lastPlacedVertical;
        this.explosiveCooldown = generateExplosiveCooldown();
    }

    /**
     * Generates a random number between 0 - 180, representing the number of frames before the next explosive will drop.
     * Technically the random number is 0 exclusive, but this is irrelevant as cooldown of 0<x<1 will drop on the
     * next frame anyway.
     * @return random real number between 0 - 180.
     */
    private Double generateExplosiveCooldown() {
        return ((1 - Math.random()) * 3 * 60);
    }

    /**
     * Ticks time forward for the air support tower, moving the plane and dropping an explosive if possible.
     * This must be called every frame. This also manages the explosive drop timer, generating new ones when needed.
     * @return {@link Explosive} on current position if the plane drops an explosive, else {@code null}.
     */
    public Explosive attack() {
        if (!flyVertical) {
            this.setPosition(new Point(this.getPosition().x + flightSpeed, this.getPosition().y));
        } else {
            this.setPosition(new Point(this.getPosition().x , this.getPosition().y + flightSpeed));
        }
        explosiveCooldown--;
        if (explosiveCooldown <= 0) {
            explosiveCooldown = generateExplosiveCooldown();
            return new Explosive(this.getPosition(), damage);
        }
        return null;
    }

    /**
     * Checks if the air support tower is within the game window.
     * @return true if centre of plane is within game window, false otherwise.
     */
    public boolean inWindow() {
        return this.getPosition().x < Window.getWidth() && this.getPosition().y < Window.getHeight();
    }

    public static int getCost() {
        return cost;
    }

}
