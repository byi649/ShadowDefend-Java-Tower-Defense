import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Point;
import bagel.util.Vector2;
import bagel.map.TiledMap;

public class Slicer {
    private Point position;
    private double MOVEMENT_SCALAR;
    private final Image slicerImage = new Image("res/images/slicer.png");
    private final TiledMap slicerMap;
    private Vector2 vector;
    private int dstNum;

    // Spawns a slicer at the start of the map
    // Inputs:
    // TiledMap slicerMap: map on which to spawn the slicer
    // double MOVEMENT_SCALAR: speed of slicer relative to 1px/frame
    public Slicer(TiledMap slicerMap, double MOVEMENT_SCALAR) {
        this.slicerMap = slicerMap;
        this.MOVEMENT_SCALAR = MOVEMENT_SCALAR;

        position = slicerMap.getAllPolylines().get(0).get(0);
        dstNum = 1;
    }

    public void draw() {
        DrawOptions opt = new DrawOptions().setRotation(Math.atan2(vector.y, vector.x));
        slicerImage.draw(position.x, position.y, opt);
    }

    public void updateVector() {
        vector = getDestination().asVector().sub(position.asVector());
        vector = vector.normalised().mul(MOVEMENT_SCALAR);
    }

    public Point getDestination() {
        return slicerMap.getAllPolylines().get(0).get(dstNum);
    }

    public double checkDistanceToDst() {
        return position.distanceTo(getDestination());
    }

    // Returns false if slicer reaches end of path
    public boolean updateDestination() {
        dstNum++;
        if (dstNum >= slicerMap.getAllPolylines().get(0).size()) {
            return false;
        }
        updateVector();
        return true;
    }

    public void move() {
        this.position = new Point(position.x + vector.x, position.y + vector.y);
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public Image getSlicerImage() {
        return slicerImage;
    }

    public TiledMap getSlicerMap() {
        return slicerMap;
    }

    public Vector2 getVector() {
        return vector;
    }

    public void setVector(Vector2 vector) {
        this.vector = vector;
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
}
