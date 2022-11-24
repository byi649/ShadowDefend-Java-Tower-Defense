import java.util.ArrayList;
import java.util.List;

import bagel.map.TiledMap;

public class Wave {
    // Use to modify movement speeds to match video, as per Piazza pinned discussion
    private static final double MOVEMENT_SCALAR = 1;

    private final TiledMap map;
    private double delay;
    private double currentTimer;
    private List<Slicer> slicerList;
    private int slicersRemaining;
    private boolean waveComplete = false;

    // Initialise a wave and spawn the first slicer
    // Inputs:
    // double delay: Delay (in seconds) between slicer spawns
    // TiledMap map: Map for wave to spawn on
    // int numSlicers: Number of slicers to spawn in total
    public Wave(double delay, TiledMap map, int numSlicers) {
        this.delay = delay * 60;
        this.map = map;

        this.slicerList = new ArrayList<>();
        this.slicersRemaining = numSlicers;
        spawnSlicer();
    }

    // Move all slicers one MOVEMENT_SCALAR step forward
    // Spawns a slicer if appropriate delay has been reached
    // Must be called every frame to maintain delay timer
    // Inputs:
    // int timeScale: Scales speed of time by integer.
    public void moveSlicers(int timeScale) {
        // Spawn slicers every (delay * 60)/timeScale frames
        if (slicersRemaining >= 1) {
            currentTimer = currentTimer - timeScale;
            if (currentTimer <= 0) {
                spawnSlicer();
            }
        }

        // Move all slicers
        // End wave when all slicers are done
        for (int i = 0; i < slicerList.size(); i++) {
            if (updateSlicerPosition(slicerList.get(i), timeScale)) {
                slicerList.get(i).draw();
            } else {
                i--;
            }
            if (slicerList.isEmpty() && slicersRemaining == 0) {
                waveComplete = true;
                break;
            }
        }
    }

    // Move a slicer towards next point
    // Update destination if it reaches the next point
    // Remove slicer when they reach the end
    public boolean updateSlicerPosition(Slicer slicer, int timeScale) {
        for (int j = 0; j < timeScale; j++) {
            slicer.updateVector();
            // Close enough to next point
            if (slicer.checkDistanceToDst() < timeScale * MOVEMENT_SCALAR) {
                if (!slicer.updateDestination()) {
                    slicerList.remove(slicer);
                    return false;
                }
            }
            slicer.move();
        }
        return true;
    }

    public void spawnSlicer() {
        slicerList.add(new Slicer(map, MOVEMENT_SCALAR));
        slicersRemaining--;
        currentTimer = delay;
    }

    public TiledMap getMap() {
        return map;
    }

    public double getDelay() {
        return delay;
    }

    public void setDelay(double delay) {
        if (delay < 0) {
            throw new IllegalArgumentException();
        }
        this.delay = delay;
    }

    public double getCurrentTimer() {
        return currentTimer;
    }

    public void setCurrentTimer(double currentTimer) {
        if (currentTimer < 0) {
            throw new IllegalArgumentException();
        }
        this.currentTimer = currentTimer;
    }

    public List<Slicer> getSlicerList() {
        return slicerList;
    }

    public void setSlicerList(List<Slicer> slicerList) {
        this.slicerList = slicerList;
    }

    public int getSlicersRemaining() {
        return slicersRemaining;
    }

    public void setSlicersRemaining(int slicersRemaining) {
        if (slicersRemaining < 0) {
            throw new IllegalArgumentException();
        }
        this.slicersRemaining = slicersRemaining;
    }

    public boolean isWaveComplete() {
        return waveComplete;
    }

    public void setWaveComplete(boolean waveComplete) {
        this.waveComplete = waveComplete;
    }
}
