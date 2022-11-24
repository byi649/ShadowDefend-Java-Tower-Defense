import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Point;
import bagel.util.Vector2;
import bagel.map.TiledMap;

/**
 * This abstract class provides a base from all slicer enemies to build upon. Slicers move along the polyline of the map
 * and disappear at the end, damaging the player. Slicers destroyed on the way by towers grant the player gold and may
 * spawn child slicers at it's current position which continue along the polyline as well.
 */
public abstract class Slicer {
    private static double MOVEMENT_SCALAR = 1;

    private final TiledMap slicerMap;
    private final double finalMovementSpeed;
    private final int numberOfChildren;
    private final int reward;
    private final int penalty;
    private final Image slicerImage;

    private Point position;
    private Vector2 vector;
    private int dstNum;
    private int health;

    /**
     * Creates a new slicer at the start of the polyline, looking to the right. The slicer's movement speed is scaled
     * by {@code MOVEMENT_SCALAR} which can be manually set, and is shared among all slicers.
     * @param slicerMap TiledMap containing polyline for slicer to travel along.
     * @param health starting health for the slicer.
     * @param movementSpeed base movement speed for the slicer.
     * @param slicerImage image of slicer.
     * @param numberOfChildren amount of child slicers to spawn when killed by a tower.
     * @param reward amount of gold to reward player when killed by a tower.
     * @param penalty amount of health to take away from player when slicer reaches end of the polyline.
     */
    public Slicer(TiledMap slicerMap, int health, double movementSpeed, Image slicerImage, int numberOfChildren, int reward, int penalty) {
        this.slicerMap = slicerMap;
        this.health = health;
        this.finalMovementSpeed = movementSpeed * MOVEMENT_SCALAR;
        this.slicerImage = slicerImage;
        this.numberOfChildren = numberOfChildren;
        this.reward = reward;
        this.penalty = penalty;
        this.vector = Vector2.right;

        position = slicerMap.getAllPolylines().get(0).get(0);
        dstNum = 1;
    }

    /**
     * Renders the slicer at it's current position, facing it toward it's destination.
     */
    public void draw() {
        DrawOptions opt = new DrawOptions().setRotation(Math.atan2(vector.y, vector.x));
        slicerImage.draw(position.x, position.y, opt);
    }

    /**
     * Updates movement vector towards next point in polyline.
     */
    public void updateVector() {
        vector = getDestination().asVector().sub(position.asVector());
        vector = vector.normalised().mul(finalMovementSpeed);
    }

    /**
     * Returns the next point of the polyline the slicer is moving towards
     * @return Point co-ordinate of current destination of slicer (not final destination).
     */
    public Point getDestination() {
        return slicerMap.getAllPolylines().get(0).get(dstNum);
    }

    /**
     * Checks distance to next destination
     * @return how far to the next destination (not final destination).
     */
    public double checkDistanceToDst() {
        return position.distanceTo(getDestination());
    }

    /**
     * Sets the destination of the slicer to the next point in the polyline. Updates movement vector.
     * @return false if slicer has reached final destination, true otherwise.
     */
    public boolean updateDestination() {
        dstNum++;
        if (dstNum >= slicerMap.getAllPolylines().get(0).size()) {
            return false;
        }
        updateVector();
        return true;
    }

    /**
     * Moves the slicer one step. Should be called every frame.
     */
    public void move() {
        this.position = new Point(position.x + vector.x, position.y + vector.y);
    }

    /**
     * Deals damage to the slicer
     * @param damage amount of damage.
     * @return true if slicer dies, false otherwise.
     */
    public boolean takeDamage(int damage) {
        health = health - damage;
        return health <= 0;
    }

    /**
     * Generates a random point nearby current position to spawn child slicers.
     * This is to prevent child slicers from completely overlapping and appearing as one.
     * The new point will be in a 15x15 pixel square around the current point.
     * @return point randomly within 15x15 square around current point
     */
    public Point childSpawnLocation() {
        return new Point(this.getPosition().x + Math.random()*15, this.getPosition().y + Math.random()*15);
    }

    public Slicer onDeath() {
        return null;
    }

    public int getHealth() {
        return health;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public TiledMap getSlicerMap() {
        return slicerMap;
    }

    public int getDstNum() {
        return dstNum;
    }

    public void setDstNum(int dstNum) {
        if (dstNum >= slicerMap.getAllPolylines().get(0).size()) {
            throw new IllegalArgumentException();
        }
        this.dstNum = dstNum;
    }

    public static void setMovementScalar(double movementScalar) {
        MOVEMENT_SCALAR = movementScalar;
    }

    public double getFinalMovementSpeed() {
        return finalMovementSpeed;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public int getReward() {
        return reward;
    }

    public int getPenalty() {
        return penalty;
    }

}
