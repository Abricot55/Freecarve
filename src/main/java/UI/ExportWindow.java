package UI;

import UI.Listeners.ExportGcodeActionListener;
import UI.SubWindows.BasicWindow;
import UI.SubWindows.Rendering3DWindow;
import UI.Widgets.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Represents an export window that displays a 3D renderer for visualizing
 * the final board, and provides an export button to convert the board into GCode instructions.
 *
 * @author Adam Côté
 * @version 1.0
 * @since 2024-10-25
 */
public class ExportWindow {

    private JSplitPane mainSplitPane;
    private CNCCutSpecChooser cncCutSpecChooser;
    private final BigButton nextButton = new BigButton("Export");
    private Rendering3DWindow rendering3DWindow;
    private final MainWindow mainWindow;
    private final JScrollPane gcodeWindow = new JScrollPane();
    private final BasicWindow realGcodeContainer = new BasicWindow(true);
    private final JTextArea gcodeDisplay = new JTextArea();


    /**
     * Constructs an ExportWindow and initializes its layout and components.
     */
    public ExportWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        init();
        calculateGcode();
        setButtonAction();

    }

    public JSplitPane getMainSplitPane() {
        return mainSplitPane;
    }

    /**
     * Returns the renderer used in this export window.
     *
     * @return The Renderer instance responsible for displaying 3D shapes.
     */
    public Rendering3DWindow getRenderer() {
        return rendering3DWindow;
    }

    /**
     * Initializes the layout and adds components to the export window.
     * This method creates various 3D meshes and sets up the renderer with
     * those meshes.
     */
    public void init() {
        gcodeDisplay.setEditable(false);
        gcodeDisplay.setFont(new Font("Consolas", Font.PLAIN, 15));
        gcodeDisplay.setLineWrap(true);
        gcodeDisplay.setWrapStyleWord(true);
        gcodeDisplay.setMargin(new Insets(5, 5, 5, 5));
        gcodeDisplay.setBackground(null);
        gcodeDisplay.setBorder(new EmptyBorder(UIConfig.INSTANCE.getDefaultPadding(), UIConfig.INSTANCE.getDefaultPadding(), UIConfig.INSTANCE.getDefaultPadding(),
                UIConfig.INSTANCE.getDefaultPadding()));

        realGcodeContainer.setLayout(new BorderLayout());
        realGcodeContainer.add(gcodeDisplay, BorderLayout.CENTER);

        gcodeWindow.add(realGcodeContainer);
        gcodeWindow.setViewportView(realGcodeContainer);
        gcodeWindow.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        gcodeWindow.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        gcodeWindow.setBorder(null);
        gcodeWindow.getViewport().setViewPosition(new Point(0, 0));
        BasicWindow tempGCodeWindow = new BasicWindow(true);
        tempGCodeWindow.setupHeader("Aperçu du GCODE", gcodeWindow);


        rendering3DWindow = new Rendering3DWindow(mainWindow.getController().getCameraId(), mainWindow);
        BasicWindow tempRender3D = new BasicWindow(true);
        tempRender3D.setupHeader("Vue 3D", rendering3DWindow);

        cncCutSpecChooser = new CNCCutSpecChooser(mainWindow);
        BasicWindow tempSpecChooser = new BasicWindow(true);
        tempSpecChooser.setupHeader("Paramètres d'exportation", cncCutSpecChooser.getScrollPane());

        JSplitPane splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tempGCodeWindow, nextButton);
        JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tempSpecChooser, splitPane1);
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tempRender3D, splitPane2);

        splitPane2.setDividerLocation(UIConfig.INSTANCE.getDefaultWindowHeight() / 10 * 2);
        splitPane1.setDividerLocation(UIConfig.INSTANCE.getDefaultWindowHeight() / 2);
        mainSplitPane.setDividerLocation(UIConfig.INSTANCE.getDefaultWindowWidth() / 3 * 2);
        mainSplitPane.setResizeWeight(1);
        gcodeWindow.setFocusable(false);
        gcodeDisplay.setFocusable(false);
        rendering3DWindow.requestFocusInWindow();
    }

    public void calculateGcode() {
        String gcode = mainWindow.getController().convertToGCode();
        gcodeDisplay.setText(gcode);
        gcodeWindow.repaint();
        mainSplitPane.revalidate();
        mainSplitPane.repaint();
    }

    private void setButtonAction() {
        nextButton.getButton().addActionListener(new ExportGcodeActionListener(mainWindow));
    }

    public void refreshGcodeParam() {
        cncCutSpecChooser.refreshAttributes();
        calculateGcode();
    }

    private static class CNCCutSpecChooser extends GenericAttributeBox {

        private PixelNoUnitInputField rotationSpeed;
        private PixelNoUnitInputField feedRate;
        private JScrollPane scrollPane;
        private final MainWindow mainWindow;

        public CNCCutSpecChooser(MainWindow mainWindow) {
            super(false, "");
            this.mainWindow = mainWindow;
            init();
        }

        /**
         * Initializes the dimension setting UI with labels and input fields for width (x) and height (y),
         * with real-time resizing functionality.
         */
        private void init() {
            rotationSpeed = new PixelNoUnitInputField(mainWindow, "", mainWindow.getController().getCNCrotationSpeed(), "rotation/s");
            rotationSpeed.getNumericInput().addPropertyChangeListener("value", evt -> {
                mainWindow.getController().setCNCrotationSpeed(((Number) evt.getNewValue()).intValue());
                mainWindow.getMiddleContent().getExportWindow().calculateGcode();
            });
            feedRate = new PixelNoUnitInputField(mainWindow, "", mainWindow.getController().getCNCCuttingSpeed(), "m/min");
            feedRate.getNumericInput().addPropertyChangeListener("value", evt -> {
                mainWindow.getController().setCNCCuttingSpeed(((Number) evt.getNewValue()).intValue());
                mainWindow.getMiddleContent().getExportWindow().calculateGcode();

            });

            JPanel panel = new JPanel();
            GridBagLayout layout = new GridBagLayout();
            scrollPane = new JScrollPane(panel);
            this.setupHeader("Spécification CNC", scrollPane);
            panel.setLayout(layout);
            panel.setBorder(new EmptyBorder(UIConfig.INSTANCE.getDefaultPadding(), UIConfig.INSTANCE.getDefaultPadding(),
                    UIConfig.INSTANCE.getDefaultPadding(), UIConfig.INSTANCE.getDefaultPadding()));
            panel.setBackground(UIManager.getColor("SubWindow.background"));
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(UIConfig.INSTANCE.getScrollbarSpeed());
            panel.setAlignmentX(0);
            scrollPane.setAlignmentX(0);

            GridBagConstraints gbc = new GridBagConstraints();
            GenericAttributeBox genRotation = new GenericAttributeBox(true, "Vitesse de rotation");

            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 1;
            genRotation.add(rotationSpeed, gbc);

            GenericAttributeBox genFeedRate = new GenericAttributeBox(true, "Vitesse de coupe");
            gbc.gridx = 0;
            gbc.gridy = 2;
            genFeedRate.add(feedRate, gbc);

            gbc.anchor = GridBagConstraints.NORTH;
            gbc.insets = new Insets(0, 0, UIConfig.INSTANCE.getDefaultPadding() / 3, 0);
            gbc.weightx = 1.0;
            gbc.gridy = 0;
            panel.add(genRotation, gbc);
            gbc.gridy = 1;
            panel.add(genFeedRate, gbc);

        }

        public void refreshAttributes() {
            rotationSpeed.getNumericInput().setText("" + mainWindow.getController().getCNCrotationSpeed());
            feedRate.getNumericInput().setText("" + mainWindow.getController().getCNCCuttingSpeed());
        }

        public JScrollPane getScrollPane() {
            return scrollPane;
        }

    }
}
