package UI;

import UI.SubWindows.BasicWindow;
import UI.SubWindows.BitSelectionWindow;
import UI.SubWindows.Rendering2DWindow;
import UI.Widgets.BigButton;
import UI.Widgets.ChooseDimension;
import Util.UiUtil;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;

public class ConfigChoiceWindow extends JPanel {
    private Rendering2DWindow rend;
    private final BigButton nextButton = new BigButton("Suivant");
    private BitSelectionWindow bitWindow;
    private BasicWindow attributeWindow;

    public ConfigChoiceWindow() {
        this.setLayout(new GridBagLayout());
        rend = new Rendering2DWindow();
        bitWindow = new BitSelectionWindow();
        attributeWindow = new BasicWindow(true);
        setFocusable(true);
        requestFocusInWindow();
        init();
        setButtonEventHandler();
    }

    public void init() {
        GridBagConstraints gbc = new GridBagConstraints();

        ChooseDimension dimensions = new ChooseDimension();
        attributeWindow.setPreferredSize(new Dimension(0, 0));
        attributeWindow.add(dimensions);

        gbc.insets = new Insets(0, 0, 0, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.weightx = 2;
        gbc.weighty = 0.75;
        gbc.fill = GridBagConstraints.BOTH;
        add(rend, gbc);

        gbc.insets = new Insets(5, 0, 0, 10);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.weightx = 2;
        gbc.weighty = 0.25;
        gbc.fill = GridBagConstraints.BOTH;
        add(bitWindow, gbc);

        gbc.insets = new Insets(0, 0, 0, 5);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0.75;
        gbc.fill = GridBagConstraints.BOTH;
        add(attributeWindow, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0.25;
        gbc.fill = GridBagConstraints.BOTH;
        add(nextButton, gbc);
    }

    private void setButtonEventHandler() {
        nextButton.getButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.INSTANCE.getMiddleContent().nextWindow();
            }
        });
        for (int i = 0; i < bitWindow.getBitList().length; i++) {
            JToggleButton bit = bitWindow.getBitList()[i];
            int finalI = i;
            bit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.gridy = 1;
                    attributeWindow.add(new JLabel(bit.getText()), gbc);
                    for (int j = 0; j < bitWindow.getBitList().length; j++) {
                        bitWindow.getBitList()[j].setSelected(finalI ==j);
                    }
                    if (attributeWindow.getComponents().length > 1) {
                        attributeWindow.remove(attributeWindow.getComponents().length - 1);
                    }

                }
            });
        }
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        nextButton.revalidate();
    }
}
