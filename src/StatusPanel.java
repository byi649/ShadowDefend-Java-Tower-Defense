import bagel.DrawOptions;
import bagel.Font;
import bagel.Image;
import bagel.Window;
import bagel.util.Colour;
import bagel.util.Point;

/**
 * This class implements and controls the status panel at the bottom of the screen.
 * The buy panel displays the current wave number, timescale, status and player health.
 * This class should be initialised at the start of the game and {@link StatusPanel#render(boolean, int)} called every frame.
 */
public class StatusPanel {
    private static final Font font = new Font("res/fonts/DejaVuSans-Bold.ttf", 18);
    private static final Image statusPanel = new Image("res/images/statuspanel.png");
    private static Point statusPanelPosition;
    private static double statusTextHeight;

    private final Player player;
    private final Level level;

    /**
     * Initialises the status panel.
     * @param player player object to get life value.
     * @param level level object to get wave information from.
     */
    public StatusPanel(Player player, Level level) {
        statusTextHeight = Window.getHeight() - statusPanel.getHeight()/2 + 5;
        statusPanelPosition = new Point(0, Window.getHeight() - statusPanel.getHeight());
        this.player = player;
        this.level = level;
    }

    /**
     * Renders the status panel at the bottom of the screen.
     * Should be called every frame.
     * Statuses in order of priority:
     * <ul>
     *     <li>Winner!: All levels and waves are complete.</li>
     *     <li>Placing: Currently buying a tower, rendering tower over cursor.</li>
     *     <li>Wave in Progress: Wave in Progress.</li>
     *     <li>Awaiting Start: No waves in progress, waiting for player to start next wave.</li>
     * </ul>
     * Timescale is rendered in green if above 1, white otherwise.
     * @param currentlyBuying true if currently placing tower.
     * @param timeScale current timescale.
     */
    public void render(boolean currentlyBuying, int timeScale) {

        String statusString;
        if (level.isAllWavesComplete()) {
            statusString = "Winner!";
        } else if (currentlyBuying) {
            statusString = "Placing";
        } else if (level.waveInProgress()) {
            statusString = "Wave in Progress";
        } else {
            statusString = "Awaiting Start";
        }

        DrawOptions opt = new DrawOptions();
        if (timeScale > 1) {
            opt.setBlendColour(Colour.GREEN);
        } else {
            opt.setBlendColour(Colour.WHITE);
        }

        int waveCounter = level.getCurrentWave();
        if (!level.waveInProgress()) {
            waveCounter++;
        }

        statusPanel.drawFromTopLeft(statusPanelPosition.x, statusPanelPosition.y);
        font.drawString("Wave: " + waveCounter, 5, statusTextHeight);
        font.drawString(String.format("Time scale: %.2f", (double) timeScale), Window.getWidth()/4., statusTextHeight, opt);
        font.drawString("Status: " + statusString, Window.getWidth()/2., statusTextHeight);
        font.drawString("Lives: " + player.getHealth(), Window.getWidth()-100, statusTextHeight);
    }

    public Image getStatusPanel() {
        return statusPanel;
    }

    public Point getStatusPanelPosition() {
        return statusPanelPosition;
    }
}
