import bagel.Image;
import bagel.map.TiledMap;

/**
 * This class implements and controls the "Slicer" enemy. It extends base abstract class {@link Slicer}.
 * Regular slicers all have the same starting health, movement speed, image, reward, penalty, and on-death spawns.
 * <ul>
 *     <li>Starting HP: 1 HP</li>
 *     <li>Movement speed: 2px/frame</li>
 *     <li>Reward: $2</li>
 *     <li>On-death: Nothing</li>
 *     <li>Penalty: 1 life</li>
 * </ul>
 */
public class RegularSlicer extends Slicer {
    private static final int DEFAULT_HP = 1;
    private static final double DEFAULT_MOVEMENT_SPEED = 2;
    private static final Image DEFAULT_SLICER_IMAGE = new Image("res/images/slicer.png");
    private static final int DEFAULT_NUMBER_OF_CHILDREN = 0;
    private static final int DEFAULT_REWARD = 2;
    private static final int DEFAULT_PENALTY = 1;

    /**
     * Creates a new Regular Slicer at the beginning of the polyline of the input TiledMap.
     * @param slicerMap TiledMap containing one polyline for the slicer to follow.
     */
    public RegularSlicer(TiledMap slicerMap) {
        super(slicerMap, DEFAULT_HP, DEFAULT_MOVEMENT_SPEED, DEFAULT_SLICER_IMAGE, DEFAULT_NUMBER_OF_CHILDREN, DEFAULT_REWARD, DEFAULT_PENALTY);
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