import bagel.DrawOptions;
import bagel.Font;
import bagel.Image;
import bagel.Window;
import bagel.util.Colour;
import bagel.util.Point;

/**
 * This class implements and controls the buy panel at the top of the screen.
 * The buy panel displays the current amount of gold the player has, as well as some keybinds.
 * The buy panel also displays the 3 tower types, where players can left click to buy and place towers.
 * This class restricts purchase and placement of towers if they cannot be afforded, but does NOT complete the transaction.
 * This class should be initialised at the start of the game and {@link BuyPanel#render()} called every frame.
 */
public class BuyPanel {
    private static final Font priceFont = new Font("res/fonts/DejaVuSans-Bold.ttf", 18);
    private static final Font keyFont = new Font("res/fonts/DejaVuSans-Bold.ttf", 14);
    private static final Font moneyFont = new Font("res/fonts/DejaVuSans-Bold.ttf", 48);
    private static final Image buyPanel = new Image("res/images/buypanel.png");
    private static final Image tankImage = new Image("res/images/tank.png");
    private static final Image superTankImage = new Image("res/images/supertank.png");
    private static final Image airSupportImage = new Image("res/images/airsupport.png");
    private static final double towerPositions = buyPanel.getHeight() / 2 - 10;
    private static final double pricePositions = towerPositions + tankImage.getHeight() / 2 + 15;
    private static final Point tankPosition = new Point(64, towerPositions);
    private static final Point superTankPosition = new Point(184, towerPositions);
    private static final Point airSupportPosition = new Point(304, towerPositions);
    private static final int tankCost = 250;
    private static final int superTankCost = 600;
    private static final int airSupportCost = 500;

    private final Player player;

    private boolean tankAffordable;
    private boolean superTankAffordable;
    private boolean airSupportAffordable;

    /**
     * Initialises the buy panel
     * @param player player object to manipulate gold values
     */
    public BuyPanel(Player player) {
        this.player = player;
    }

    /**
     * Renders the buy panel at the top of the screen.
     * Displays tower prices in green if affordable, red if not.
     * Displays current gold value.
     */
    public void render() {
        buyPanel.drawFromTopLeft(0, 0);

        // Towers
        tankImage.draw(tankPosition.x, tankPosition.y);
        superTankImage.draw(superTankPosition.x, superTankPosition.y);
        airSupportImage.draw(airSupportPosition.x, airSupportPosition.y);

        DrawOptions optAffordable = new DrawOptions().setBlendColour(Colour.GREEN);
        DrawOptions optUnAffordable = new DrawOptions().setBlendColour(Colour.RED);

        this.tankAffordable = player.getGold() >= tankCost;
        this.superTankAffordable = player.getGold() >= superTankCost;
        this.airSupportAffordable = player.getGold() >= airSupportCost;

        priceFont.drawString(String.format("$%d", tankCost), 40, pricePositions, (tankAffordable) ? optAffordable : optUnAffordable);
        priceFont.drawString(String.format("$%d", superTankCost), 158, pricePositions, (superTankAffordable) ? optAffordable : optUnAffordable);
        priceFont.drawString(String.format("$%d", airSupportCost), 280, pricePositions, (airSupportAffordable) ? optAffordable : optUnAffordable);

        // Key binds
        keyFont.drawString("Key binds:", Window.getWidth()/2.3, 25);
        keyFont.drawString("S - Start Wave", Window.getWidth()/2.3, 50);
        keyFont.drawString("L - Increase Timescale", Window.getWidth()/2.3, 65);
        keyFont.drawString("K - Decrease Timescale", Window.getWidth()/2.3, 80);

        // Current money
        moneyFont.drawString("$" + player.getGold(), Window.getWidth() - 200, 65);
    }

    /**
     * Checks if the player's cursor is hovering over any affordable tower images.
     * @param position current cursor position.
     * @return The class of the tower being hovered if it is affordable, or {@code null} if none are.
     */
    public Class<?> checkCursorPosition(Point position) {
        if (tankImage.getBoundingBoxAt(tankPosition).intersects(position) && tankAffordable) {
            return Tank.class;
        } else if (superTankImage.getBoundingBoxAt(superTankPosition).intersects(position) && superTankAffordable) {
            return SuperTank.class;
        } else if (airSupportImage.getBoundingBoxAt(airSupportPosition).intersects(position) && airSupportAffordable) {
            return AirSupport.class;
        }
        return null;
    }

    /**
     * Renders a preview image of specified tower on cursor.
     * @param position current cursor position.
     * @param tower class of tower to preview.
     */
    public void renderPreview(Point position, Class<?> tower) {
        if (tower == Tank.class) {
            tankImage.draw(position.x, position.y);
        } else if (tower == SuperTank.class) {
            superTankImage.draw(position.x, position.y);
        } else if (tower == AirSupport.class)
            airSupportImage.draw(position.x, position.y);
    }

    public static Image getBuyPanel() {
        return buyPanel;
    }
}
