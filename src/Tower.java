import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Point;
import bagel.util.Vector2;

/**
 * Abstract class Tower implements object for player to place on map that attacks slicers.
 * @see {@link StationaryTower}
 * @see {@link AirSupport}
 */
public abstract class Tower {
    private final Image image;

    private Point position;
    private Vector2 faceDirection;

    /**
     * Creates tower at position with image, facing right.
     * @param position place to put the tower.
     * @param image image of tower.
     */
    public Tower(Point position, Image image) {
        this.position = position;
        this.image = image;
        faceDirection = Vector2.right;
    }

    /**
     * Renders tower, turning it towards last set direction.
     */
    public void draw() {
        DrawOptions opt = new DrawOptions().setRotation(Math.atan2(faceDirection.y, faceDirection.x));
        image.draw(position.x, position.y, opt);
    }

    /**
     * Turns tower to specified vector.
     * @param direction vector centred on tower specifying direction.
     */
    public void turn(Vector2 direction) {
        faceDirection = direction;
    }

    public Image getImage() {
        return image;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

}
