package UI.Widgets.AttributeContainer;

import Common.DTO.CutDTO;
import Common.DTO.VertexDTO;
import UI.Events.ChangeAttributeEvent;
import UI.MainWindow;
import UI.SubWindows.CutListPanel;
import UI.UIConfig;
import UI.Widgets.*;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class AttributeContainerVertical extends AttributeContainer {
    SingleValueBox distanceFromEdgeToEdge;
    SingleValueBox absoluteDistanceFromEdgeToEdge;
    SingleValueBox distanceCenterToCenter;

    public AttributeContainerVertical(MainWindow mainWindow, CutListPanel cutListPanel, CutDTO cutDTO, CutBox cutBox) {
        super(mainWindow, cutListPanel, cutDTO, cutBox);
        init_attribute(mainWindow, cutDTO);
        init_layout();

    }

    private void init_layout(){
        setBackground(null);
        setOpaque(false);
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gc = new GridBagConstraints();
        setLayout(layout);
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 0, UIConfig.INSTANCE.getDefaultPadding() / 3, 0);
        add(distanceFromEdgeToEdge, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        gc.insets = new Insets(0, 0, UIConfig.INSTANCE.getDefaultPadding() / 3, 0);
        add(absoluteDistanceFromEdgeToEdge, gc);

        gc.gridx = 0;
        gc.gridy = 2;
        gc.insets = new Insets(0, 0, UIConfig.INSTANCE.getDefaultPadding() / 3, 0);
        add(distanceCenterToCenter, gc);

        gc.gridx = 0;
        gc.gridy = 3;
        gc.insets = new Insets(0, 0, UIConfig.INSTANCE.getDefaultPadding() / 3, 0);
        add(depthBox, gc);

        gc.gridx = 0;
        gc.gridy = 4;
        gc.insets = new Insets(0, 0, UIConfig.INSTANCE.getDefaultPadding() / 3, 0);
        add(bitChoiceBox, gc);

        gc.gridx = 0;
        gc.gridy = 5;
        gc.insets = new Insets(0, 0, UIConfig.INSTANCE.getDefaultPadding() / 3, 0);
        add(modifyAnchorBox, gc);

    }

    /**
     * Initialize variables relevant to the Attribute Panel
     */
    private void init_attribute(MainWindow mainWindow, CutDTO cutDTO) {
        super.init_attribute();
        distanceFromEdgeToEdge = new SingleValueBox(mainWindow, true, "Distance relative de la sous-pièce", "X", xEdgeEdge());
        absoluteDistanceFromEdgeToEdge = new SingleValueBoxNotEditable(mainWindow, true, "Taille de la sous-pièce", "X", Math.abs(xEdgeEdge()));
        distanceCenterToCenter = new SingleValueBoxNotEditable(mainWindow, true, "Position absolue (GCODE)", "X", xCenterCenter());
    }

    private double xEdgeEdge(){
        return cutDTO.getPoints().getFirst().getX();
    }
    private double xCenterCenter(){
        List<VertexDTO> absPoints = mainWindow.getController().getAbsolutePointsPosition(cutDTO);
        return absPoints.getFirst().getX();
    }

    @Override
    public void setupEventListeners() {
        addEventListenerToEdgeEdge(distanceFromEdgeToEdge);
        addEventListenerToDepth(depthBox);
        addEventListenerToBitChoiceBox(bitChoiceBox);
        addEventListenerModifyAnchor(modifyAnchorBox);
    }

    @Override
    public void updatePanel(CutDTO newCutDTO) {
        cutDTO = newCutDTO;
        distanceFromEdgeToEdge.getInput().setValueInMMWithoutTrigerringListeners(xEdgeEdge());
        absoluteDistanceFromEdgeToEdge.getInput().setValueInMMWithoutTrigerringListeners(Math.abs(xEdgeEdge()));
        distanceCenterToCenter.getInput().setValueInMMWithoutTrigerringListeners( xCenterCenter());
        depthBox.getInput().setValueInMMWithoutTrigerringListeners(cutDTO.getDepth());
        revalidate();
        repaint();
    }

    /**
     * Adding the custom event listeners to SingleValueBox objects. The goal is to make
     * the Value attribute react to change events
     *
     * Changes the X offset of the vertical cut
     *
     * @param sb {@code SingleValueBox object}
     */
    private void addEventListenerToEdgeEdge(SingleValueBox sb) {
        sb.getInput().getNumericInput().addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                CutDTO c = new CutDTO(cutDTO);
                double newEdgeEdge = sb.getInput().getMMValue();
                for(int i =0; i < c.getPoints().size(); i++){
                    VertexDTO oldVertex = c.getPoints().get(i);
                    VertexDTO newVertex = new VertexDTO(newEdgeEdge, oldVertex.getY(), oldVertex.getZ());
                    c.getPoints().set(i, newVertex);
                }
                mainWindow.getController().modifyCut(c, true);
                cutListPanel.modifiedAttributeEventOccured(new ChangeAttributeEvent(cutBox, cutBox));
            }
        });
    }

}
