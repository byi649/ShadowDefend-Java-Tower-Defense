import bagel.Image;
import bagel.map.TiledMap;

/**
 * This class implements and controls the "Super Slicer" enemy. It extends base abstract class {@link Slicer}.
 * Super slicers all have the same starting health, movement speed, image, reward, penalty, and on-death spawns.
 * <ul>
 *     <li>Starting HP: {@link RegularSlicer} HP</li>
 *     <li>Movement speed: 3/4 * {@link RegularSlicer} movement speed</li>
 *     <li>Reward: $15</li>
 *     <li>On-death: Spawns 2 RegularSlicers</li>
 *     <li>Penalty: Sum of child slicers</li>
 * </ul>
 */
public class SuperSlicer extends Slicer {
    private static final int DEFAULT_HP = RegularSlicer.getDefaultHp();
    private static final double DEFAULT_MOVEMENT_SPEED = 0.75*RegularSlicer.getDefaultMovementSpeed();
    private static final Image DEFAULT_SLICER_IMAGE = new Image("res/images/superslicer.png");
    private static final int DEFAULT_NUMBER_OF_CHILDREN = 2;
    private static final int DEFAULT_REWARD = 15;
    private static final int DEFAULT_PENALTY = DEFAULT_NUMBER_OF_CHILDREN * RegularSlicer.getDefaultPenalty();

    /**
     * Creates a new Super Slicer at the beginning of the polyline of the input TiledMap.
     * @param slicerMap TiledMap containing one polyline for the slicer to follow.
     */
    public SuperSlicer(TiledMap slicerMap) {
        super(slicerMap, DEFAULT_HP, DEFAULT_MOVEMENT_SPEED, DEFAULT_SLICER_IMAGE, DEFAULT_NUMBER_OF_CHILDREN, DEFAULT_REWARD, DEFAULT_PENALTY);
    }

    /**
     * Creates ONE {@link RegularSlicer} to spawn when this Super Slicer dies. The child slicer inherits the Super Slicer's
     * current position (slightly offset randomly) and destination. This should be called 2 times on death.
     * @return ONE RegularSlicer inheriting current slicer's position and destination.
     */
    @Override
    public RegularSlicer onDeath() {
        RegularSlicer childSlicer = new RegularSlicer(super.getSlicerMap());
        childSlicer.setDstNum(this.getDstNum());
        childSlicer.setPosition(childSpawnLocation());
        return childSlicer;
    }

    public static int getDefaultHp() {
        return DEFAULT_HP;
    }

    public static double getDefaultMovementSpeed() {
        return DEFAULT_MOVEMENT_SPEED;
    }

    public static int getDefaultPenalty() {
        return DEFAULT_PENALTY;
    }

}