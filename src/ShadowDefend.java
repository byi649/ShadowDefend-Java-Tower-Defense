import bagel.*;
import bagel.map.TiledMap;

import java.util.ArrayList;
import java.util.List;

public class ShadowDefend extends AbstractGame {
    private final TiledMap map;
    private final Image mapImage;
    private Wave currentWave;
    private int timeScale = 1;

    /**
     * Entry point for Bagel game
     *
     * Explore the capabilities of Bagel: https://people.eng.unimelb.edu.au/mcmurtrye/bagel-doc/
     */
    public static void main(String[] args) {
        // Create new instance of game and run it
        new ShadowDefend().run();
    }

    /**
     * Setup the game
     */
    public ShadowDefend(){
        // Constructor
        map = new TiledMap("res/levels/1.tmx");
        mapImage = new Image("res/levels/sheet.png");

        map.draw(0, 0, 0, 0, Window.getWidth(), Window.getHeight());
        mapImage.draw(0, 0);

        // Bagel render bug - quick fix as per Piazza pinned discussion
        new Image("res/images/slicer.png").draw(0, 0);
    }

    /**
     * Updates the game state approximately 60 times a second, potentially reading from input.
     * @param input The input instance which provides access to keyboard/mouse state information.
     */
    @Override
    protected void update(Input input) {

        // Wave spawning hotkey
        if (input.wasReleased(Keys.S)) {
            if (currentWave == null || currentWave.isWaveComplete()) {
                currentWave = new Wave(5.0, map, 5);
            }
        }

        // Increase timescale
        if (input.wasReleased(Keys.L)) {
            timeScale++;
        }

        // Decrease timescale
        if (input.wasReleased(Keys.K)) {
            if (timeScale > 1) {
                timeScale--;
            }
        }

        map.draw(0, 0, 0, 0, Window.getWidth(), Window.getHeight());
        mapImage.draw(0, 0);

        if (currentWave != null) {
            currentWave.moveSlicers(timeScale);
        }

        if (currentWave != null && currentWave.isWaveComplete()) {
            Window.close();
        }

    }


}
