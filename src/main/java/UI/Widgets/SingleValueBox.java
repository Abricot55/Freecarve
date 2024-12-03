package UI.Widgets;


import Common.Interfaces.IUnitConverter;
import UI.MainWindow;
import UI.UIConfig;
import UI.UiUnits;

import java.awt.*;

/**
 * The {@code SingleValueBox} class is a UI class that encapsulate the Single Value field
 * of the editor
 *
 * @author Louis-Etienne Messier
 * @author Kamran Charles Nayebi
 * @since 2024-10-23
 */
public class SingleValueBox extends GenericAttributeBox {

    private MeasurementInputField theInput;

    public SingleValueBox(MainWindow mainWindow, boolean haveBackground, String name, String inputName, double value, UiUnits units) {
        super(haveBackground, name);
        this.init(mainWindow, inputName, value, units);
    }

    public SingleValueBox(MainWindow mainWindow, boolean haveBackground, String name, String inputName, double value, UiUnits units, double minValue, double maxValue) {
        super(haveBackground, name);
        this.init(mainWindow, inputName, value, units, minValue, maxValue);
    }

    private void init(MainWindow mainWindow, String name, double value, UiUnits units) {
        GridBagConstraints gc = new GridBagConstraints();
        this.theInput = new MeasurementInputField(mainWindow, name, value, -Double.MAX_VALUE, Double.MAX_VALUE, units);
        gc.gridx = 0;
        gc.gridy = 1;
        gc.fill = GridBagConstraints.NONE;
        gc.weightx = 1;
        this.add(theInput, gc);
    }

    private void init(MainWindow mainWindow, String name, double value, UiUnits units, double minValue, double maxValue) {
        GridBagConstraints gc = new GridBagConstraints();
        this.theInput = new MeasurementInputField(mainWindow, name, value, minValue, maxValue, units);
        gc.gridx = 0;
        gc.gridy = 1;
        gc.fill = GridBagConstraints.NONE;
        gc.weightx = 1;
        this.add(theInput, gc);
    }

    public MeasurementInputField getInput() {
        return this.theInput;
    }
}
