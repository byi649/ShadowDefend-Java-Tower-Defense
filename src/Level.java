import bagel.Window;
import bagel.map.TiledMap;
import bagel.util.Point;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class provides control over wave and wave events within each level.
 * This includes the map, waves, wave events and slicers but NOT the player, towers and projectiles.
 * Level information is read through "waves.txt", but otherwise mostly acts as a wrapper for {@link WaveEvent}s.
 */
public class Level {
    private static final int MAX_EVENTS_PER_WAVE = 1000; // Needed for initialisation of wave array

    private final TiledMap map;
    private final Player player;
    private final List<List<WaveEvent>> eventList = new ArrayList<List<WaveEvent>>();

    private int numberOfWaves = 0;
    private int currentWave = 0;
    private boolean allWavesStarted = false;

    /**
     * Creates a new instance of Level, reading in information from a given wave text file.
     * This populates a wave events 2D arrayList, with {@code MAX_EVENTS_PER_WAVE = 1000}.
     * Increase this limit if needed, at a one-time level load performance cost.
     * @param map TiledMap to play wave events on.
     * @param player player specific to this level, containing gold and health information.
     * @param fileDir file path to waves.txt to load
     */
    public Level(TiledMap map, Player player, String fileDir) {
        for (int i = 0; i < MAX_EVENTS_PER_WAVE; i++) {
            eventList.add(new ArrayList<WaveEvent>());
        }
        this.map = map;
        this.player = player;
        readLevelFile(fileDir);
    }

    /**
     * Reads in information from waves.txt.
     * Stores it into a 2D arrayList, with outer index equal to wave number, and inner index corresponding to wave events.
     * The outer index will start from 1 - do NOT try to access element 0.
     * The inner index starts from 0 as usual.
     * Also initialises numberOfWaves.
     * @param fileDir file path to waves.txt to load
     */
    public void readLevelFile(String fileDir) {

        try {
            File textFile = new File(fileDir);
            Scanner waveReader = new Scanner(textFile);
            while (waveReader.hasNextLine()) {
                String waveEvent = waveReader.nextLine();
                String[] waveEventSplit = waveEvent.split(",");
                int waveNumber = Integer.parseInt(waveEventSplit[0]);
                double delay;
                if (waveEventSplit[1].equalsIgnoreCase("delay")) {
                    // Delay event
                    delay = Double.parseDouble(waveEventSplit[2]);
                    eventList.get(waveNumber).add(new WaveEvent(delay, player));
                } else {
                    // Slicer spawn event
                    delay = Double.parseDouble(waveEventSplit[4]);
                    int numberOfSlicers = Integer.parseInt(waveEventSplit[2]);
                    numberOfWaves = waveNumber; // Assume wave numbers don't go backwards
                    String slicerText = waveEventSplit[3];
                    Class<?> slicerClass;
                    switch (slicerText) {
                        case "superslicer":
                            slicerClass = SuperSlicer.class;
                            break;
                        case "megaslicer":
                            slicerClass = MegaSlicer.class;
                            break;
                        case "apexslicer":
                            slicerClass = ApexSlicer.class;
                            break;
                        case "slicer":
                        default:
                            slicerClass = RegularSlicer.class;
                    }
                    eventList.get(waveNumber).add(new WaveEvent(delay, map, numberOfSlicers, slicerClass, player));
                }
            }
            waveReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("waves.txt could not be opened");
            e.printStackTrace();
        }
    }

    /**
     * Renders map image. Call this every frame.
     */
    public void drawMap() {
        map.draw(0, 0, 0, 0, Window.getWidth(), Window.getHeight());
    }

    /**
     * Ticks time forward for slicers and wave events. Moves all slicers forward. Spawns and kills slicers at beginning
     * and end of polyline respectively. Starts wave events when the previous are finished.
     */
    public void moveAllWaveEvents() {
        for (int j = 0; j < eventList.get(currentWave).size(); j++) {
            if (!eventList.get(currentWave).get(j).isWaveEventComplete()) {
                if (j == 0 || eventList.get(currentWave).get(j-1).canStartNextEvent()) {
                    // Previous event is complete
                    eventList.get(currentWave).get(j).moveAllSlicers();
                }
            }
        }
    }

    /**
     * Renders all slicers currently alive.
     */
    public void drawAllWaveEvents() {
        for (int j = 0; j < eventList.get(currentWave).size(); j++) {
            if (!eventList.get(currentWave).get(j).isWaveEventComplete()) {
                if (j == 0 || eventList.get(currentWave).get(j-1).canStartNextEvent()) {
                    // Previous event is complete
                    eventList.get(currentWave).get(j).drawAllSlicers();
                }
            }
        }
    }

    /**
     * Starts the next wave. Sets allWavesStarted to true when all waves have been started, else increments it.
     * Call this when player presses wave hotkey.
     */
    public void startNextWave() {
        if (currentWave == numberOfWaves) {
            allWavesStarted = true;
        } else {
            currentWave++;
        }
    }

    /**
     * Returns the nearest slicer to a tower at given point with given range, or null if none are in range.
     * @param position position of tower.
     * @param range range of tower (radius).
     * @return nearest slicer or {@code null} if none in range.
     */
    public Slicer getNearestSlicerInRange(Point position, int range) {
        for (WaveEvent e : eventList.get(currentWave)) {
            if (!e.isWaveEventComplete()) {
                Slicer nearest = e.getNearestSlicerInRange(position, range);
                if (nearest != null) {
                    return nearest;
                }
            }
        }
        return null;
    }

    /**
     * Returns a list of all slicers near a tower/explosive at given position with given range.
     * @param position position of tower/explosive.
     * @param range range of tower/explosive (radius).
     * @return list of slicers in range. If no slicers are in range, returns a list with length 0.
     */
    public List<Slicer> getAllSlicersInRange(Point position, int range) {
        List<Slicer> slicerList = new ArrayList<>();
        for (WaveEvent e : eventList.get(currentWave)) {
            if (!e.isWaveEventComplete()) {
                slicerList.addAll(e.getAllSlicersInRange(position, range));
            }
        }
        return slicerList;
    }

    /**
     * Checks if all waves are complete (no more spawns, no more slicers alive).
     * @return true if all slicers are dead and no more slicers will spawn this level, false otherwise.
     */
    public boolean isAllWavesComplete() {
        for (int i = 1; i < numberOfWaves + 1; i++) {
            for (WaveEvent e : eventList.get(i)) {
                if (!e.isWaveEventComplete()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if any wave event is still in progress
     * @return true if any wave event is still spawning slicers or has slicers alive, false otherwise.
     */
    public boolean waveInProgress() {
        return eventList.get(currentWave).stream().anyMatch(e -> !e.isWaveEventComplete());
    }

    /**
     * Kills specified slicer, removing it from the slicer list.
     * @param slicer the (currently living) slicer
     */
    public void killSlicer(Slicer slicer) {
        for (WaveEvent e : eventList.get(currentWave)) {
            if (!e.isWaveEventComplete() && e.getSlicerList().contains(slicer)) {
                e.killSlicer(slicer);
                return;
            }
        }
    }

    public TiledMap getMap() {
        return map;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public boolean isAllWavesStarted() {
        return allWavesStarted;
    }

    public Player getPlayer() {
        return player;
    }
}
