import bagel.Image;
import bagel.map.TiledMap;

/**
 * This class implements and controls the "Mega Slicer" enemy. It extends base abstract class {@link Slicer}.
 * Mega slicers all have the same starting health, movement speed, image, reward, penalty, and on-death spawns.
 * <ul>
 *     <li>Starting HP: 2 * {@link SuperSlicer} HP</li>
 *     <li>Movement speed: Same as {@link SuperSlicer} movement speed</li>
 *     <li>Reward: $10</li>
 *     <li>On-death: Spawns 2 SuperSlicers</li>
 *     <li>Penalty: Sum of child slicers</li>
 * </ul>
 */
public class MegaSlicer extends Slicer {
    private static final int DEFAULT_HP = 2*SuperSlicer.getDefaultHp();
    private static final double DEFAULT_MOVEMENT_SPEED = SuperSlicer.getDefaultMovementSpeed();
    private static final Image DEFAULT_SLICER_IMAGE = new Image("res/images/megaslicer.png");
    private static final int DEFAULT_NUMBER_OF_CHILDREN = 2;
    private static final int DEFAULT_REWARD = 10;
    private static final int DEFAULT_PENALTY = DEFAULT_NUMBER_OF_CHILDREN * SuperSlicer.getDefaultPenalty();

    /**
     * Creates a new Mega Slicer at the beginning of the polyline of the input TiledMap.
     * @param slicerMap TiledMap containing one polyline for the slicer to follow.
     */
    public MegaSlicer(TiledMap slicerMap) {
        super(slicerMap, DEFAULT_HP, DEFAULT_MOVEMENT_SPEED, DEFAULT_SLICER_IMAGE, DEFAULT_NUMBER_OF_CHILDREN, DEFAULT_REWARD, DEFAULT_PENALTY);
    }

    /**
     * Creates ONE {@link SuperSlicer} to spawn when this Mega Slicer dies. The child slicer inherits the Mega Slicer's
     * current position (slightly offset randomly) and destination. This should be called 2 times on death.
     * @return ONE SuperSlicer inheriting current slicer's position and destination.
     */
    @Override
    public SuperSlicer onDeath() {
        SuperSlicer childSlicer = new SuperSlicer(super.getSlicerMap());
        childSlicer.setDstNum(this.getDstNum());
        childSlicer.setPosition(childSpawnLocation());
        return childSlicer;
    }

    public static double getDefaultMovementSpeed() {
        return DEFAULT_MOVEMENT_SPEED;
    }

    public static int getDefaultPenalty() {
        return DEFAULT_PENALTY;
    }

}