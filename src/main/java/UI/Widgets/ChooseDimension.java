package UI.Widgets;

import UI.Display2D.Rendering2DWindow;
import UI.SubWindows.BasicWindow;

import javax.swing.*;
import java.awt.*;

/**
 * ChooseDimension provides a UI window for setting and adjusting the dimensions of a 2D rendering panel.
 * It displays input fields for width (x) and height (y), allowing the user to modify dimensions dynamically.
 *
 * @author Adam Côté
 * @version 1.1
 * @since 2024-11-02
 */
public class ChooseDimension extends BasicWindow implements Attributable {

    private final Rendering2DWindow rend;
    private final boolean gridDisaplyed;
    private NumberTextField xTextField;
    private NumberTextField yTextField;
    private NumberTextField gridPrecision;
    private NumberTextField magnetPrecision;

    /**
     * Constructs a ChooseDimension panel for modifying the width and height of the specified Rendering2DWindow.
     *
     * @param rend The Rendering2DWindow instance to be resized
     */
    public ChooseDimension(Rendering2DWindow rend, boolean gridDisplayed) {
        super(false);
        this.rend = rend;
        this.gridDisaplyed = gridDisplayed;
        init();
    }

    /**
     * @return The NumberTextfield for the width input
     */
    public NumberTextField getxTextField() {
        return xTextField;
    }

    /**
     * @return The NumberTextfield for the height input
     */
    public NumberTextField getyTextField() {
        return yTextField;
    }

    /**
     * Initializes the dimension setting UI with labels and input fields for width (x) and height (y),
     * with real-time resizing functionality.
     */
    private void init() {
        JLabel dimensionLabel = new JLabel("Dimensions");
        dimensionLabel.setFont(dimensionLabel.getFont().deriveFont(20f));

        xTextField = new NumberTextField("" + rend.getBoard().getWidth(), width -> rend.resizePanneau(width, rend.getBoard().getHeight()));
        yTextField = new NumberTextField("" + rend.getBoard().getHeight(), height -> rend.resizePanneau(rend.getBoard().getWidth(), height));

        JLabel xLabel = new JLabel("x");
        JLabel yLabel = new JLabel("y");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 15, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridx = 4;
        gbc.gridy = 0;
        add(dimensionLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(xLabel, gbc);

        gbc.gridx = 4;
        add(xTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(yLabel, gbc);

        gbc.gridx = 4;
        add(yTextField, gbc);

        if (gridDisaplyed) {
            gridPrecision = new NumberTextField("" + rend.getMainWindow().getController().getGrid().getSize(), size -> {
                rend.getMainWindow().getController().putGrid(size.intValue(), rend.getMainWindow().getController().getGrid().getMagnetPrecision());
                rend.repaint();
            });
            magnetPrecision = new NumberTextField("" + rend.getMainWindow().getController().getGrid().getMagnetPrecision(), magnet -> {
                rend.resizePanneau(rend.getMainWindow().getController().getGrid().getSize(), magnet.intValue());
                rend.repaint();
            });

            JLabel gridLabel = new JLabel("Grid Size");
            JLabel magnetLabel = new JLabel("Magnet Precision");

            gbc.gridx = 0;
            gbc.gridy = 3;
            add(gridLabel, gbc);

            gbc.gridx = 4;
            add(gridPrecision, gbc);

            gbc.gridx = 0;
            gbc.gridy = 4;
            add(magnetLabel, gbc);
            
            gbc.gridx = 4;
            add(magnetPrecision, gbc);

        }
    }

    /**
     * @return JLabel representing the name of this component.
     */
    @Override
    public JLabel showName() {
        return new JLabel("Dimensions du panneau en mm");
    }

    /**
     * @return JPanel representation of this component's attribute view.
     */
    @Override
    public JPanel showAttribute() {
        return this;
    }
}
