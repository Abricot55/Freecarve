package UI.Widgets;


import Common.Interfaces.IUnitConverter;

import java.awt.*;

/**
 * The {@code SingleValueBox} class is a UI class that encapsulate the Single Value field
 * of the editor
 *
 * @author Louis-Etienne Messier
 * @version 0.1
 * @since 2024-10-23
 */
public class SingleValueBox extends GenericAttributeBox {

    private CustomNumericInputField theInput;

    public SingleValueBox(IUnitConverter unitConverter, boolean haveBackground, String name, double value) {
        super(haveBackground, name);
        this.init(unitConverter, name, value);
    }

    private void init(IUnitConverter unitConverter, String name, double value) {
        GridBagConstraints gc = new GridBagConstraints();
        this.theInput = new CustomNumericInputField(unitConverter, name, value);

        gc.gridx = 0;
        gc.gridy = 1;
        gc.fill = GridBagConstraints.NONE;
        gc.weightx = 1;
        this.add(theInput, gc);
    }

    public CustomNumericInputField getInput() {
        return this.theInput;
    }
}
