package UI;

import UI.Events.ChangeAttributeEvent;
import UI.Events.ChangeAttributeListener;
import UI.Events.ChangeCutEvent;
import UI.Events.ChangeCutListener;
import UI.SubWindows.AttributePanel;
import UI.SubWindows.BasicWindow;
import UI.SubWindows.CutListPanel;
import UI.Display2D.Rendering2DWindow;
import UI.Widgets.Attributable;
import UI.Widgets.ChooseDimension;
import UI.Widgets.CutBox;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * The {@code CutWindow} class encapsulates the sub-windows necessary for using the
 * CutEdition Menu
 *
 * @author Louis-Etienne Messier
 * @version 0.1
 * @since 2024-09-21
 */
public class CutWindow implements ChangeAttributeListener, ChangeCutListener {
    private JSplitPane mainSplitPane;
    private JSplitPane splitPane1;
    private JSplitPane splitPane2;
    private JPanel panel1;
    private JPanel panel2;
    private JPanel panel3;
    private CutListPanel cutListPanel;
    private AttributePanel attributePanel;
    private Attributable selectedAttributable;
    private Rendering2DWindow rendering2DWindow;
    private MainWindow mainWindow;

    /**
     * Constructs a {@code CutWindow} instance initializing all of it's sub-panels
     * and sub-components
     */
    public CutWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.init(mainWindow);
    }

    /**
     * @return the {@code JSplitPane} container of the {@code CutWindow}
     */
    public JSplitPane getCutWindow() {
        return mainSplitPane;
    }

    /**
     * @return the {@code Rendering2DWindow} present in the {@code CutWIndo}
     */
    public Rendering2DWindow getRendering2DWindow() {return rendering2DWindow;}

    public JPanel getScreen(int i) {
        switch (i) {
            case 1 -> {
                return panel1;
            }
        }
        return null;
    }

    /**
     * Set the selected element of the CutWindow and changed the AttributePanel accordingly
     * @param event ChangeAttributeEvent being called by a child class
     */
    @Override
    public void changeAttributeEventOccurred(ChangeAttributeEvent event) {
        this.selectedAttributable = event.getAttribute();
        this.attributePanel.updateAttribute(this.selectedAttributable);
    }

    @Override
    public void modifiedAttributeEventOccured(ChangeAttributeEvent event){
        this.rendering2DWindow.updateCuts();
    }

    @Override
    public void addCutEventOccured(ChangeCutEvent event){
        this.cutListPanel.update();
        Optional<CutBox> cutBox = this.cutListPanel.getCutBoxWithId(event.getCutId());
        if(cutBox.isPresent()){
            this.selectedAttributable = cutBox.get();
            this.attributePanel.updateAttribute(this.selectedAttributable);
        }

    }

    @Override
    public void deleteCutEventOccured(ChangeCutEvent event) {
        mainWindow.getController().removeCut(event.getCutId());
        if (event.getSource() == this.selectedAttributable) {// this means it's the same object, so the attributable will be delete
            this.attributePanel.updateAttribute(null);
        }
        this.cutListPanel.update();
        this.rendering2DWindow.updateCuts();
    }

    /**
     * Initiates all of the {@code CutWindow} components
     */
    private void init(MainWindow mainWindow) {
        panel1 = new Rendering2DWindow(mainWindow, this, this);
        rendering2DWindow = new Rendering2DWindow(mainWindow, this, this);
        panel1 = rendering2DWindow;

        attributePanel = new AttributePanel(true);
        panel2 = attributePanel;

        cutListPanel = new CutListPanel(true, this, this, mainWindow);
        panel3 = cutListPanel;

        splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panel2, panel3);
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel1, splitPane1);

        splitPane1.setDividerLocation(UIConfig.INSTANCE.getDefaultWindowHeight() / 2);
        mainSplitPane.setDividerLocation(UIConfig.INSTANCE.getDefaultWindowWidth() / 2);
    }
}
