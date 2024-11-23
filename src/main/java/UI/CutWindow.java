package UI;

import Common.DTO.BitDTO;
import Common.DTO.CutDTO;
import UI.Events.ChangeAttributeEvent;
import UI.Events.ChangeAttributeListener;
import UI.Events.ChangeCutEvent;
import UI.Events.ChangeCutListener;
import UI.SubWindows.AttributePanel;
import UI.SubWindows.BasicWindow;
import UI.SubWindows.BitSelectionPanel;
import UI.SubWindows.CutListPanel;
import UI.Display2D.Rendering2DWindow;
import UI.Widgets.Attributable;
import UI.Widgets.CutBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
    private JSplitPane cutInformationSplitPane;
    private JPanel panel1;
    private JPanel panel2;
    private JPanel panel3;
    private JPanel bitPanel;
    private CutListPanel cutListPanel;
    private AttributePanel attributePanel;
    private Attributable selectedAttributable;
    private Rendering2DWindow rendering2DWindow;
    private MainWindow mainWindow;
    private BitSelectionPanel bitSelectionPanel;
    private Map<Integer, BitDTO> configuredBitsMap;

    /**
     * Constructs a {@code CutWindow} instance initializing all of it's sub-panels
     * and sub-components
     */
    public CutWindow(MainWindow mainWindow, Map<Integer, BitDTO> configuredBitsMap) {
        this.mainWindow = mainWindow;
        this.configuredBitsMap = configuredBitsMap;
        this.init(mainWindow);

        mainWindow.getController().addRefreshListener(()->this.rendering2DWindow.updateCuts());
    }

    public Rendering2DWindow getRenderer() {
        return ((Rendering2DWindow) this.panel1);
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

        // Change the cut that is selected
        if (event.getAttribute() instanceof  CutBox){
            CutBox eventCasted = (CutBox) event.getAttribute();
            UUID id = eventCasted.getCutUUID();

            if(eventCasted.getState() == CutBox.CutBoxState.SELECTED){
                this.rendering2DWindow.getDrawing().changeSelectedWrapperById(id);
                // Change attribute
                this.selectedAttributable = event.getAttribute();
                this.attributePanel.updateAttribute(this.selectedAttributable);
            }
            else if(eventCasted.getState() == CutBox.CutBoxState.HOVER){
                this.rendering2DWindow.getDrawing().changeHoverWrapperById(id);
            }
            else if(eventCasted.getState() == CutBox.CutBoxState.NOT_SELECTED){
                this.rendering2DWindow.getDrawing().changeNotSelectedWrapperById(id);
            }

        }
    }

    /**
     * One attribute got modified ::
     * needs to change the cutbox appropriately
     * needs to change the 2d renderer
     * @param event
     */
    @Override
    public void modifiedAttributeEventOccured(ChangeAttributeEvent event){
        this.rendering2DWindow.updateCuts();

        System.out.println(event);

    }

    @Override
    public void addCutEventOccured(ChangeCutEvent event){

        this.cutListPanel.update();

        Optional<CutBox> cutBox = this.cutListPanel.getCutBoxWithId(event.getCutId());
        if(cutBox.isPresent()){
            this.changeAttributeEventOccurred(new ChangeAttributeEvent(CutWindow.class, cutBox.get()));
            cutBox.get().select();
        }

        SwingUtilities.invokeLater(()->{ // Invoke later necessary to wait for the repaint
            JScrollBar scrollBar = this.cutListPanel.getScrollPane().getVerticalScrollBar();
            scrollBar.setValue(scrollBar.getMaximum());
        });


    }

    @Override
    public void deleteCutEventOccured(ChangeCutEvent event) {
        mainWindow.getController().removeCut(event.getCutId());
        if (event.getSource() == this.selectedAttributable) {// this means it's the same object, so the attributable will be deleted
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

        bitSelectionPanel = new BitSelectionPanel(true, this, mainWindow, configuredBitsMap);
        bitPanel = bitSelectionPanel;

        attributePanel = new AttributePanel(true);
        panel2 = attributePanel;

        cutListPanel = new CutListPanel(true, this, this, mainWindow);
        panel3 = cutListPanel;

        cutInformationSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, bitPanel, panel2);
        splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, cutInformationSplitPane, panel3);
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel1, splitPane1);

        cutInformationSplitPane.setDividerLocation(UIConfig.INSTANCE.getDefaultWindowHeight() / 8);
        splitPane1.setDividerLocation(UIConfig.INSTANCE.getDefaultWindowHeight() / 2);
        mainSplitPane.setResizeWeight(0.8);
    }

    /**
     * @return the {@code BitSelectionPanel} of the {@code CutWindow}
     */
    public BitSelectionPanel getBitSelectionPanel() {
        return bitSelectionPanel;
    }

}
