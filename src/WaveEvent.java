import java.util.ArrayList;
import java.util.List;

import bagel.map.TiledMap;
import bagel.util.Point;

/**
 * Manages wave events within a wave, providing methods to start and query event statuses. Also provides methods to
 * control slicers spawned by wave events.
 */
public class WaveEvent {
    // Use to modify movement speeds to match video, as per Piazza pinned discussion
    private static final double MOVEMENT_SCALAR = 1;

    private final Player player;
    private final double delay;

    private TiledMap map;
    private double currentTimer;
    private List<Slicer> slicerList = new ArrayList<Slicer>();
    private int slicersToSpawnRemaining;
    private boolean waveEventComplete = false;
    private boolean isDelayEvent;
    private Class<?> slicerClass;


    /**
     * Creates a new slicer spawn event.
     * Slicer spawn events spawn a specified amount of a certain slicer class and ends when all slicers are spawned and
     * dead.
     * @param delay Time to wait between slicer spawns, in milliseconds.
     * @param map TiledMap containing polyline for slicer to travel along.
     * @param numSlicers Number of slicers to spawn.
     * @param slicerClass Type of slicer to spawn.
     * @param player Player object containing gold and health values.
     */
    public WaveEvent(double delay, TiledMap map, int numSlicers, Class<?> slicerClass, Player player) {
        this.delay = delay * 60 / 1000;
        this.currentTimer = 0;
        this.map = map;
        this.player = player;

        this.slicerList = new ArrayList<>();
        this.slicersToSpawnRemaining = numSlicers;
        this.slicerClass = slicerClass;
        Slicer.setMovementScalar(MOVEMENT_SCALAR);
    }

    /**
     * Creates a new delay event.
     * Delay events wait until the delay timer has finished, then completes.
     * @param delay Time to wait, in milliseconds.
     * @param player Player object containing gold value.
     */
    public WaveEvent(double delay, Player player) {
        this.delay = delay * 60 / 1000;
        this.currentTimer = this.delay;
        this.isDelayEvent = true;
        this.player = player;
    }

    /**
     * Ticks time for the wave event.
     * Moves all slicers forward, spawning new slicers when the appropriate delay has been reached.
     * Removes slicers that have reached the end of the polyline, penalising player.
     * Sets waveEventComplete to true when wave event is finished.
     * This should be called every frame.
     */
    public void moveAllSlicers() {
        if (isDelayEvent) {
            currentTimer = currentTimer - 1;
            if (currentTimer <= 0) {
                waveEventComplete = true;
            }
        } else {
            if (slicersToSpawnRemaining >= 1) {
                currentTimer = currentTimer - 1; // Counts frames
                if (currentTimer <= 0) {
                    spawnSlicer(slicerClass, 1);
                }
            }
            // Move all slicers
            // End wave when all slicers are done
            for (int i = 0; i < slicerList.size(); i++) {
                if (!updateSlicerPosition(slicerList.get(i))) {
                    i--;
                }
            }
            waveEventComplete = slicerList.isEmpty() && slicersToSpawnRemaining == 0;
        }

    }

    /**
     * Renders all slicers in wave event.
     */
    public void drawAllSlicers() {
        for (Slicer slicer : slicerList) {
            slicer.draw();
        }
    }

    /**
     * Moves a slicer towards it's next point
     * Updates it's destination if it reaches the next point.
     * Removes the slicer from the list if it reaches the end, penalising the player.
     * @param slicer slicer to move.
     * @return returns false if slicer reaches the final end, true otherwise.
     */
    public boolean updateSlicerPosition(Slicer slicer) {
        slicer.updateVector();
        // Close enough to next point
        if (slicer.checkDistanceToDst() < slicer.getFinalMovementSpeed()) {
            if (!slicer.updateDestination()) {
                slicerList.remove(slicer);
                player.loseHealth(slicer.getPenalty());
                return false;
            }
        }
        slicer.move();
        return true;
    }

    /**
     * Destroys the slicer when it gets killed by a tower, granting player gold.
     * Call this when slicer is killed by a projectile or explosive, not when it reaches the end.
     * @param slicer slicer to kill.
     */
    public void killSlicer(Slicer slicer) {
        spawnChildSlicers(slicer, slicer.getNumberOfChildren());
        slicerList.remove(slicer);
        player.gainGold(slicer.getReward());
    }

    /**
     * Triggers the child slicer spawns when slicer dies.
     * @param slicer slicer which just died.
     * @param n number of children to spawn.
     */
    public void spawnChildSlicers(Slicer slicer, int n) {
        for (int i = 0; i < n; i ++) {
            slicerList.add(slicer.onDeath());
        }
    }

    /**
     * Spawns slicers at the start of the polyline, as part of a wave event. Decrements slicersRemaining and resets
     * slicer spawn timer. All slicers are spawned immediately (unused functionality, left for future use).
     * @param slicerClass class of slicer to spawn.
     * @param n number of slicers to spawn.
     */
    public void spawnSlicer(Class<?> slicerClass, int n) {
        for (int i = 0; i < n; i ++) {
            if (slicerClass == RegularSlicer.class) {
                slicerList.add(new RegularSlicer(map));
            } else if (slicerClass == SuperSlicer.class) {
                slicerList.add(new SuperSlicer(map));
            } else if (slicerClass == MegaSlicer.class) {
                slicerList.add(new MegaSlicer(map));
            } else if (slicerClass == ApexSlicer.class) {
                slicerList.add(new ApexSlicer(map));
            } else {
                break;
            }
            slicersToSpawnRemaining--;
            currentTimer = delay;
        }
    }

    /**
     * Finds the nearest slicer to a specific point and range.
     * @param position point centred in search.
     * @param range radius to search around point.
     * @return nearest Slicer, otherwise null if none in range.
     */
    public Slicer getNearestSlicerInRange(Point position, int range) {
        if (slicerList.size() == 0) {
            return null;
        }
        double minDistance = Double.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < slicerList.size(); i++) {
            double distance = slicerList.get(i).getPosition().distanceTo(position);
            if (distance < minDistance && distance < range) {
                minDistance = distance;
                index = i;
            }
        }
        if (index != -1) {
            return slicerList.get(index);
        } else {
            return null;
        }
    }

    /**
     * Finds all slicers in range of a point.
     * @param position point centred in search.
     * @param range radius to search around point.
     * @return list of slicers. If no slicers are in range, a list of length 0 is returned.
     */
    public List<Slicer> getAllSlicersInRange(Point position, int range) {
        List<Slicer> nearbySlicerList = new ArrayList<>();
        if (slicerList.size() == 0) {
            return slicerList;
        }
        for (Slicer slicer : slicerList) {
            if (slicer.getPosition().distanceTo(position) < range) {
                nearbySlicerList.add(slicer);
            }
        }
        return nearbySlicerList;
    }

    /**
     * Checks if current event is finished and the next can be started.
     * @return true if next event can be started, false otherwise.
     */
    public boolean canStartNextEvent() {
        if (isDelayEvent) {
            return waveEventComplete;
        } else {
            return slicersToSpawnRemaining == 0;
        }
    }

    public List<Slicer> getSlicerList() {
        return slicerList;
    }

    public boolean isWaveEventComplete() {
        return waveEventComplete;
    }
}
