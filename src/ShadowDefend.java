import bagel.*;
import bagel.map.TiledMap;

import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class ShadowDefend extends AbstractGame {
    private static final int GOLD_REWARD_FLAT = 100;
    private static final int GOLD_REWARD_PER_LEVEL = 150;

    private final List<Level> levelList = new ArrayList<>();

    private StatusPanel statusPanel;
    private BuyPanel buyPanel;
    private Level currentLevel = null;
    private Player currentPlayer = null;
    private List<Tower> towerList = new ArrayList<>();
    private List<Projectile> projectileList = new ArrayList<>();
    private List<Explosive> explosiveList = new ArrayList<>();
    private int timeScale = 1;
    private Class<?> currentlyBuying = null;
    private boolean goldAwarded = true;

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
        levelList.add(new Level(new TiledMap("res/levels/1.tmx"), new Player(), "res/levels/waves.txt"));
        levelList.add(new Level(new TiledMap("res/levels/2.tmx"), new Player(), "res/levels/waves.txt"));

        // Bagel render bug - quick fix as per Piazza pinned discussion
        new Image("res/images/slicer.png").draw(0, 0);
    }

    /**
     * Updates the game state approximately 60 times a second, potentially reading from input.
     * @param input The input instance which provides access to keyboard/mouse state information.
     */
    @Override
    protected void update(Input input) {

        // Level progression
        if (currentLevel == null || currentLevel.isAllWavesComplete()) {
            if (levelList.size() > 0) {
                currentLevel = levelList.remove(0);
                currentPlayer = currentLevel.getPlayer();
                statusPanel = new StatusPanel(currentPlayer, currentLevel);
                buyPanel = new BuyPanel(currentPlayer);
                towerList = new ArrayList<>();
                projectileList = new ArrayList<>();
                explosiveList = new ArrayList<>();
                timeScale = 1;
                goldAwarded = true;
            }
        }

        // Wave progression
        if (!currentLevel.waveInProgress() && !goldAwarded) {
            currentPlayer.gainGold(GOLD_REWARD_FLAT + GOLD_REWARD_PER_LEVEL * currentLevel.getCurrentWave());
            goldAwarded = true;
        }

        // Wave spawning hotkey
        if (input.wasReleased(Keys.S) && !currentLevel.waveInProgress() && !currentLevel.isAllWavesStarted()) {
            currentLevel.startNextWave();
            goldAwarded = false;
        }

        // Increase timescale
        if (input.wasReleased(Keys.L)) {
            if (timeScale < 5) {
                timeScale++;
            }
        }

        // Decrease timescale
        if (input.wasReleased(Keys.K)) {
            if (timeScale > 1) {
                timeScale--;
            }
        }

        // Stop buying tower
        if (input.wasReleased(MouseButtons.RIGHT)) {
            currentlyBuying = null;
        }

        // Clicking tower to buy
        if (input.wasReleased(MouseButtons.LEFT)) {
            Point currentMousePos = input.getMousePosition();
            if (currentlyBuying == null) {
                currentlyBuying = buyPanel.checkCursorPosition(currentMousePos);
            } else {
                // Check valid tower placement
                // Blocked tile
                boolean tileBool = currentLevel.getMap().getPropertyBoolean((int)currentMousePos.x, (int)currentMousePos.y, "blocked", false);
                // Existing tower
                boolean towerBool = false;
                for (Tower tower : towerList) {
                    if (tower.getImage().getBoundingBoxAt(tower.getPosition()).intersects(currentMousePos)) {
                        towerBool = true;
                    }
                }
                // Status and buy panels
                boolean statusBool = BuyPanel.getBuyPanel().getBoundingBox().intersects(currentMousePos) ;
                boolean buyBool = new Rectangle(statusPanel.getStatusPanelPosition(), statusPanel.getStatusPanel().getWidth(), statusPanel.getStatusPanel().getHeight()) .intersects(currentMousePos);
                if (!statusBool && !buyBool && (currentlyBuying == AirSupport.class || (!tileBool && !towerBool))) {
                    if (currentlyBuying == Tank.class) {
                        if (currentPlayer.spendGold(Tank.getCost())) {
                            towerList.add(new Tank(currentMousePos));
                        }
                    } else if (currentlyBuying == SuperTank.class) {
                        if (currentPlayer.spendGold(SuperTank.getCost())) {
                            towerList.add(new SuperTank(currentMousePos));
                        }
                    } else if (currentlyBuying == AirSupport.class) {
                        if (currentPlayer.spendGold(AirSupport.getCost())) {
                            towerList.add(new AirSupport(currentMousePos));
                        }
                    }
                    currentlyBuying = null;
                }
            }
        }

        // Fast-forward to simulate timescale
        // Draw only once per frame
        for (int j = 0; j < timeScale; j++) {

            // Move slicers
            currentLevel.moveAllWaveEvents();

            // Shoot at slicers
            for (int t = 0; t < towerList.size(); t++) {
                if (towerList.get(t) instanceof AirSupport) {
                    Explosive explosive = ((AirSupport) towerList.get(t)).attack();
                    if (!((AirSupport) towerList.get(t)).inWindow()) {
                        towerList.remove(t--);
                    }
                    if (explosive != null) {
                        explosiveList.add(explosive);
                    }
                } else {
                    Projectile projectile = ((StationaryTower) towerList.get(t)).attack(currentLevel);
                    if (projectile != null) {
                        projectileList.add(projectile);
                    }
                }
            }

            // Move projectiles
            for (int i = 0; i < projectileList.size(); i++) {
                Projectile projectile = projectileList.get(i);
                if (projectile.checkTargetIsAlive()) {
                    projectile.move();
                    if (projectile.checkIntersectionWithTarget()) {
                        // Projectile hits target
                        if (projectile.getTarget().takeDamage(projectile.getDamage())) {
                            // Target is dead
                            currentLevel.killSlicer(projectile.getTarget());
                        }
                        projectileList.remove(i--);
                    }
                } else {
                    // Target is already dead (from other projectiles' damage)
                    projectileList.remove(i--);
                }
            }

            // Explode explosives
            for (int i = 0; i < explosiveList.size(); i++) {
                if (explosiveList.get(i).tick()) {
                    List<Slicer> nearbySlicers = currentLevel.getAllSlicersInRange(explosiveList.get(i).getPosition(), Explosive.getRange());
                    for (Slicer nearbySlicer : nearbySlicers) {
                        if (nearbySlicer.takeDamage(explosiveList.get(i).getDamage())) {
                            // Target is dead
                            currentLevel.killSlicer(nearbySlicer);
                        }
                    }
                    explosiveList.remove(i--);
                }
            }

            // Check for player death
            if (currentPlayer.getHealth() <= 0) {
                Window.close();
            }
        }

        // Draw map and slicers
        currentLevel.drawMap();
        currentLevel.drawAllWaveEvents();

        // Draw towers, projectiles, explosives
        for (Tower tower : towerList) {
            tower.draw();
        }

        for (Projectile projectile : projectileList) {
            projectile.draw();
        }

        for (Explosive explosive : explosiveList) {
            explosive.draw();
        }

        // Draw panels
        statusPanel.render(currentlyBuying != null, timeScale);
        buyPanel.render();

        // Render buy preview
        if (currentlyBuying != null) {
            buyPanel.renderPreview(input.getMousePosition(), currentlyBuying);
        }

    }

}
