package UI.Display2D;

import Common.CutState;
import Common.DTO.CutDTO;
import Common.DTO.RefCutDTO;
import Common.DTO.VertexDTO;
import Domain.CutType;
import UI.Display2D.DrawCutWrapper.DrawCutFactory;
import UI.Display2D.DrawCutWrapper.DrawCutWrapper;
import UI.Events.ChangeAttributeEvent;
import UI.Events.ChangeCutEvent;
import UI.MainWindow;
import UI.SubWindows.CutListPanel;
import UI.UIConfig;
import UI.UiUtil;
import UI.Widgets.CutBox;
import UI.Widgets.PersoPoint;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Utility class to draw the cuts on the board
 *
 * @author Louis-Etienne Messier
 */
public class Drawing {
    private MouseMotionListener createCutMoveListener;
    private MouseListener createCutActionListener;
    private MouseMotionListener modifyAnchorMoveListener;
    private MouseListener modifyAnchorActionListener;
    private MouseMotionListener pointMoveListener;
    private MouseMotionListener cutMoveListener;
    private List<DrawCutWrapper> cutWrappers;
    private DrawCutWrapper currentDrawingCut;
    private DrawCutWrapper currentModifiedCut;
    private final Rendering2DWindow renderer;
    private final MainWindow mainWindow;
    private DrawingState state;
    private CutDTO prevCut;
    private int indexPoint = 0;
    private List<VertexDTO> prevPts;
    private FlatSVGIcon cursorIcon;

    public enum DrawingState {
        CREATE_CUT,
        IDLE,
        MODIFY_ANCHOR,
        MODIFY_POINT,
        MODIFY_CUT,
    }

    public void setCurrentDrawingCut(DrawCutWrapper currentDrawingCut) {
        this.currentDrawingCut = currentDrawingCut;
    }

    /**
     * Create the {@code Drawing} utility class
     *
     * @param renderer   reference to the renderer
     * @param mainWindow reference to the mainWindow
     */
    public Drawing(Rendering2DWindow renderer, MainWindow mainWindow) {
        this.renderer = renderer;
        this.mainWindow = mainWindow;
        currentDrawingCut = DrawCutFactory.createEmptyWrapper(CutType.LINE_VERTICAL, renderer, mainWindow);
        initCutMouseListener();
    }

    /**
     * Initiate a specific cut
     *
     * @param type type of the cut
     */
    public void initCut(CutType type) {
        deactivateCreateCutListener();
        setState(DrawingState.CREATE_CUT);
        currentDrawingCut = DrawCutFactory.createEmptyWrapper(type, renderer, mainWindow);
        activateCreateCutListener();
    }

    /**
     * Refresh all the cuts : i.e recreate the all the DrawCutWrappers and repaint
     */
    public void updateCuts() {
        this.cutWrappers = DrawCutFactory.createListDrawCutWrapper(mainWindow.getController().getCutListDTO(), renderer, mainWindow);
        this.renderer.repaint();
    }

    /**
     * @return all the DrawCutWrappers instances
     */
    /**
     * @return all the DrawCutWrappers instances
     */
    public List<DrawCutWrapper> getCutWrappers() {
        return this.cutWrappers;
    }

    /**
     * @return the cursor {@code PersoPoint}
     */
    public PersoPoint getCreateCursorPoint() {
        return this.currentDrawingCut.getCursorPoint();
    }

    public PersoPoint getModifyingAnchorCursorPoint() {
        return this.currentModifiedCut.getCursorPoint();
    }

    public DrawingState getState() {
        return this.state;
    }

    /**
     * @return the {@code DrawCutWrapper} that represents the cut being done, can be null
     */
    public DrawCutWrapper getCurrentDrawingCut() {
        return this.currentDrawingCut;
    }

    public DrawCutWrapper getCurrentModifiedCut() {
        return this.currentModifiedCut;
    }

    public FlatSVGIcon getCursorIcon(){
        return this.cursorIcon;
    }

    /**
     * Initialize all of the mouse listeners : both MouseAdapter(s)
     */
    private void initCutMouseListener() {
        createCutMoveListener = new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                if (currentDrawingCut.getCursorPoint() != null) {
                    currentDrawingCut.cursorUpdate(renderer, Drawing.this);
                    renderer.repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
            }
        };

        createCutActionListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentDrawingCut.getCursorPoint() != null && getState() == DrawingState.CREATE_CUT) {
                    if(e.getButton() == MouseEvent.BUTTON3){
                        deactivateCreateCutListener(); // if right click, deactivate drawing
                    }
                    else if (currentDrawingCut.getCursorPoint().getValid() == PersoPoint.Valid.NOT_VALID) {  // Cut invalid
                        deactivateCreateCutListener();
                    }
                    else // Cut valid
                    {
                        boolean isOver = currentDrawingCut.addPoint(Drawing.this, renderer, new PersoPoint(currentDrawingCut.getCursorPoint()));
                        if (isOver) {
                            Optional<UUID> id = currentDrawingCut.end();
                            if (id.isPresent()) {
                                updateCuts();
                                mainWindow.getMiddleContent().getCutWindow().notifyObservers();
                                renderer.getChangeCutListener().addCutEventOccured(new ChangeCutEvent(renderer, id.get()));
                                againCreateAnotherCut();
                            }
                        }
                    }
                }
            }
        };

        modifyAnchorMoveListener = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                if (currentModifiedCut.getCursorPoint() != null) {
                    currentModifiedCut.cursorUpdate(renderer, Drawing.this);
                    renderer.repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
            }
        };

        modifyAnchorActionListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentModifiedCut.getCursorPoint() != null) {
                    if (currentModifiedCut.getCursorPoint().getValid() == PersoPoint.Valid.NOT_VALID)  // Cut invalid
                    {
                        deactivateModifyAnchorCutListener();
                    } else // Cut valid
                    {
                        currentModifiedCut.addPoint(Drawing.this, renderer, new PersoPoint(currentModifiedCut.getCursorPoint()));
                        if (currentModifiedCut.areRefsValid()) {
                            CutDTO c = currentModifiedCut.getCutDTO();
                            List<RefCutDTO> newRefs = currentModifiedCut.getRefs();
                            c = new CutDTO(c.getId(), c.getDepth(), c.getBitIndex(), c.getCutType(), c.getPoints(), newRefs, c.getState());
                            CutListPanel cutListPanel = mainWindow.getMiddleContent().getCutWindow().getCutListPanel();
                            mainWindow.getController().modifyCut(c, true);

                            Optional<CutBox> cutBox = cutListPanel.getCutBoxWithId(c.getId());
                            if (cutBox.isPresent()) {
                                cutListPanel.modifiedAttributeEventOccured(new ChangeAttributeEvent(c, cutBox.get()));
                            }

                            deactivateModifyAnchorCutListener();
                        } else {
                            deactivateModifyAnchorCutListener();
                        }
                    }
                }
            }
        };

        pointMoveListener = new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                currentModifiedCut.movePoint(e.getPoint(), renderer, mainWindow, indexPoint);
            }
        };

        cutMoveListener = new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                currentModifiedCut.moveUpdate(e.getPoint(), renderer, mainWindow);
            }
        };

    }

    public void initModifyAnchor(CutDTO cutToChangeAnchor) {
        Optional<DrawCutWrapper> modifiedCut = getWrapperById(cutToChangeAnchor.getId());

        if (modifiedCut.isPresent()) {
            currentModifiedCut = modifiedCut.get();
            deactivateModifyAnchorCutListener();
            ;
            currentModifiedCut.emptyRefs();
            setState(DrawingState.MODIFY_ANCHOR);
            activateModifyAnchorCutListener();
        }

    }

    public void initModifyPoint(DrawCutWrapper cutToChangePoint, PersoPoint pointToChange) {
        currentModifiedCut = cutToChangePoint;
        currentModifiedCut.setState(DrawCutWrapper.DrawCutState.SELECTED,renderer);
        Optional<CutBox> cutBox = mainWindow.getMiddleContent().getCutWindow().getCutListPanel().getCutBoxWithId(currentModifiedCut.getCutDTO().getId());
        cutBox.get().select();
        currentModifiedCut.setPointDepart(new Point2D.Double(pointToChange.getLocationX(), pointToChange.getLocationY()));
        prevCut = cutToChangePoint.getCutDTO();
        prevPts = mainWindow.getController().getAbsolutePointsPosition(prevCut);
        for (int i = 0; i<currentModifiedCut.getPersoPoints().size(); i++) {
            if (currentModifiedCut.getPersoPoints().get(i) == pointToChange) {
                indexPoint = i;
            }
        }
        setState(Drawing.DrawingState.MODIFY_POINT);
        activateModifyPointCutListener();
    }


    public void closeModifyPoint() {
        setState(DrawingState.IDLE);
        CutDTO copyUndo = new CutDTO(prevCut);
        CutDTO copyRedo = new CutDTO(mainWindow.getController().getCutDTOById(currentModifiedCut.getCutDTO().getId()));
        mainWindow.getController().executeAndMemorize(()->mainWindow.getController().modifyCut(copyRedo, false), ()->mainWindow.getController().modifyCut(copyUndo, false));
        currentModifiedCut.emptyRefs();
        deactivateModifyPointCutListener();
    }

    public void initModifyCut(DrawCutWrapper cutToChangePoint) {
        currentModifiedCut = cutToChangePoint;
        Optional<CutBox> cutBox = mainWindow.getMiddleContent().getCutWindow().getCutListPanel().getCutBoxWithId(currentModifiedCut.getCutDTO().getId());
        cutBox.get().select();
        currentModifiedCut.setPointDepart(renderer.getMmMousePt());
        setState(DrawingState.MODIFY_CUT);
        activateModifyCutListener();
        prevCut = cutToChangePoint.getCutDTO();
        prevPts = mainWindow.getController().getAbsolutePointsPosition(prevCut);
    }

    public void closeModifyCut() {
        setState(DrawingState.IDLE);
        CutDTO copyUndo = new CutDTO(prevCut);
        CutDTO copyRedo = new CutDTO(mainWindow.getController().getCutDTOById(currentModifiedCut.getCutDTO().getId()));
        mainWindow.getController().executeAndMemorize(()->mainWindow.getController().modifyCut(copyRedo, false), ()->mainWindow.getController().modifyCut(copyUndo, false));
        currentModifiedCut.emptyRefs();
        deactivateModifyCutListener();
    }

    private void activateModifyPointCutListener() {
        renderer.addMouseMotionListener(pointMoveListener);
    }

    private void deactivateModifyPointCutListener() {
        renderer.removeMouseMotionListener(pointMoveListener);
    }

    private void activateModifyCutListener() {
        renderer.addMouseMotionListener(cutMoveListener);
    }

    private void deactivateModifyCutListener() {
        renderer.removeMouseMotionListener(cutMoveListener);
    }

    public void setState(DrawingState state) {
        if (state != DrawingState.CREATE_CUT) {
            mainWindow.getLeftBar().deactivateAllCuts();
        }
        this.state = state;
    }


    /**
     * Activates the cutListener so that the board reacts when a cut is being made
     */
    private void activateCreateCutListener() {
        renderer.addMouseMotionListener(createCutMoveListener);
        renderer.addMouseListener(createCutActionListener);
        currentDrawingCut.createCursorPoint(this.renderer);
        String iconName = UiUtil.getIconFileName(currentDrawingCut.getCutType());
        cursorIcon = new FlatSVGIcon(UiUtil.getIcon(iconName, UIConfig.INSTANCE.getCREATE_CUT_ICON_SIZE()));
    }

    private void activateModifyAnchorCutListener() {
        renderer.addMouseMotionListener(modifyAnchorMoveListener);
        renderer.addMouseListener(modifyAnchorActionListener);
        currentModifiedCut.createCursorPoint(this.renderer);
    }

    /**
     * Deactivate the cutListener
     */
    public void deactivateCreateCutListener() {
        renderer.removeMouseMotionListener(createCutMoveListener);
        renderer.removeMouseListener(createCutActionListener);
        currentDrawingCut.destroyCursorPoint();
        setState(DrawingState.IDLE);
        renderer.repaint();
    }

    /**
     * Cleanup the necessary components to draw another cut of the same type
     */
    public void againCreateAnotherCut(){
        if(currentDrawingCut == null) {return;}
        renderer.removeMouseMotionListener(createCutMoveListener);
        renderer.removeMouseListener(createCutActionListener);
        currentDrawingCut.destroyCursorPoint();
        currentDrawingCut = DrawCutFactory.createEmptyWrapper(currentDrawingCut.getCutType(), renderer, mainWindow);
        activateCreateCutListener();

    }

    private void deactivateModifyAnchorCutListener() {
        renderer.removeMouseMotionListener(modifyAnchorMoveListener);
        renderer.removeMouseListener(modifyAnchorActionListener);
        currentModifiedCut.destroyCursorPoint();
        setState(DrawingState.IDLE);
        renderer.repaint();
    }

    public void changeSelectedWrapperById(UUID id) {

        for (DrawCutWrapper wrapper : cutWrappers) {
            if (wrapper.getState() == DrawCutWrapper.DrawCutState.SELECTED) {
                wrapper.setState(DrawCutWrapper.DrawCutState.NOT_SELECTED, renderer);
            }
        }

        for (DrawCutWrapper wrapper : cutWrappers) {
            if (wrapper.getCutDTO().getId() == id) {
                wrapper.setState(DrawCutWrapper.DrawCutState.SELECTED, renderer);
            }
        }
    }

    public void changeHoverWrapperById(UUID id) {
        for (DrawCutWrapper wrapper : cutWrappers) {
            if (wrapper.getCutDTO().getId() == id) {
                wrapper.setState(DrawCutWrapper.DrawCutState.HOVER, renderer);
            }
        }
    }

    public void changeNotSelectedWrapperById(UUID id) {
        for (DrawCutWrapper wrapper : cutWrappers) {
            if (wrapper.getCutDTO().getId() == id) {
                wrapper.setState(DrawCutWrapper.DrawCutState.NOT_SELECTED, renderer);
            }
        }
    }

    public void changeRefWrapperById(UUID id) {
        for (DrawCutWrapper wrapper : cutWrappers) {
            if (wrapper.getCutDTO().getId() == id) {
                wrapper.setState(DrawCutWrapper.DrawCutState.REF, renderer);
            }
        }
    }

    public void changeInvalidWrapperById(UUID id) {
        for (DrawCutWrapper wrapper : cutWrappers) {
            if (wrapper.getCutDTO().getId() == id) {
                wrapper.setState(DrawCutWrapper.DrawCutState.INVALID, renderer);
            }
        }
    }

    public void changeAllInvalid() {
        for (DrawCutWrapper wrapper : cutWrappers) {
            if (wrapper.getCutDTO().getState() == CutState.NOT_VALID) {
                wrapper.setState(DrawCutWrapper.DrawCutState.INVALID, renderer);
            }
        }
    }

    public void changeGoBackWrapperById(UUID id) {
        for (DrawCutWrapper wrapper : cutWrappers) {
            if (wrapper.getCutDTO().getId() == id) {
                wrapper.goBackState(renderer);
            }
        }
    }

    public Optional<DrawCutWrapper> getWrapperById(UUID id) {
        for (DrawCutWrapper wrapper : cutWrappers) {
            if (wrapper.getCutDTO().getId() == id) {
                return Optional.of(wrapper);
            }
        }
        return Optional.empty();
    }

    public List<VertexDTO> getPrevPts() {
        return prevPts;
    }
}

