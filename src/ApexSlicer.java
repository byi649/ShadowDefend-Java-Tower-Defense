import bagel.Image;
import bagel.map.TiledMap;

/**
 * This class implements and controls the "Apex Slicer" enemy. It extends base abstract class {@link Slicer}.
 * Apex slicers all have the same starting health, movement speed, image, reward, penalty, and on-death spawns.
 * <ul>
 *     <li>Starting HP: 25 * {@link RegularSlicer} HP</li>
 *     <li>Movement speed: 0.5 * {@link MegaSlicer} movement speed</li>
 *     <li>Reward: $150</li>
 *     <li>On-death: Spawns 4 MegaSlicers</li>
 *     <li>Penalty: Sum of child slicers</li>
 * </ul>
 */
public class ApexSlicer extends Slicer {
    private static final int DEFAULT_HP = 25*RegularSlicer.getDefaultHp();
    private static final double DEFAULT_MOVEMENT_SPEED = 0.5*MegaSlicer.getDefaultMovementSpeed();
    private static final Image DEFAULT_SLICER_IMAGE = new Image("res/images/apexslicer.png");
    private static final int DEFAULT_NUMBER_OF_CHILDREN = 4;
    private static final int DEFAULT_REWARD = 150;
    private static final int DEFAULT_PENALTY = DEFAULT_NUMBER_OF_CHILDREN * MegaSlicer.getDefaultPenalty();

    /**
     * Creates a new Apex Slicer at the beginning of the polyline of the input TiledMap.
     * @param slicerMap TiledMap containing one polyline for the slicer to follow.
     */
    public ApexSlicer(TiledMap slicerMap) {
        super(slicerMap, DEFAULT_HP, DEFAULT_MOVEMENT_SPEED, DEFAULT_SLICER_IMAGE, DEFAULT_NUMBER_OF_CHILDREN, DEFAULT_REWARD, DEFAULT_PENALTY);
    }

    /**
     * Creates ONE {@link MegaSlicer} to spawn when this Apex Slicer dies. The child slicer inherits the Apex Slicer's
     * current position (slightly offset randomly) and destination. This should be called 4 times on death.
     * @return ONE MegaSlicer inheriting current slicer's position and destination.
     */
    @Override
    public MegaSlicer onDeath() {
        MegaSlicer childSlicer = new MegaSlicer(super.getSlicerMap());
        childSlicer.setDstNum(this.getDstNum());
        childSlicer.setPosition(childSpawnLocation());
        return childSlicer;
    }

}