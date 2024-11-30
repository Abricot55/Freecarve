package Domain;

import Common.DTO.*;
import Common.Exceptions.InvalidBitException;
import Common.Interfaces.*;
import Common.Units;
import Domain.IO.GcodeGenerator;
import Domain.IO.ProjectFileManager;
import Domain.ThirdDimension.Camera;
import Domain.ThirdDimension.Mesh;
import Domain.ThirdDimension.Scene;

import java.awt.image.BufferedImage;
import java.security.InvalidKeyException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * The {@code Controller} class is a Larman's Controller which will be the entry point if an interface wants to interact with the Domain.
 *
 * @author Adam Côté
 * @version 1.0
 * @since 2024-10-20
 */
public class Controller implements IUnitConverter, IMemorizer {
    private final UndoRedoManager undoRedoManager;
    private final ProjectState currentProjectState;
    private Grid grid;
    private final int defaultGridPrecision = 5;
    private final int defaultMagnetPrecision = 5;
    private Scene scene;
    private final Camera camera;

    Controller(UndoRedoManager undoRedoManager, ProjectState projectState, Scene scene) {
        this.undoRedoManager = undoRedoManager;
        this.currentProjectState = projectState;
        this.scene = scene;
        this.camera = new Camera(scene);
        putGrid(defaultGridPrecision, defaultMagnetPrecision);
    }

    public static Controller initialize() {
        UndoRedoManager undoRedoManager = new UndoRedoManager();
        return new Controller(undoRedoManager, new ProjectState(undoRedoManager), new Scene());
    }

    public void setScene() {
        this.scene = new Scene(Mesh.PanelToMesh(this, getPanelDTO(), getBitsDTO()));
        this.camera.setScene(this.scene);
    }

    public List<UUID> getMeshesOfScene() {
        return this.scene.getMeshesID();
    }

    /**
     * Requests a cut to do on the panel of the current {@code ProjectState}
     *
     * @param cut A requestCutDTO with all the information about the cut.
     * @return The UUID of the Cut if the RequestCutDTO was valid.
     */
    public Optional<UUID> requestCut(RequestCutDTO cut) {
        return this.currentProjectState.getPanel().requestCut(cut);
    }

    /**
     * @return The current {@code ProjectState}
     */
    public ProjectStateDTO getProjectStateDTO() {
        return this.currentProjectState.getDTO();
    }

    /**
     * @return The board of the current {@code ProjectState}
     */
    public PanelDTO getPanelDTO() {
        return getProjectStateDTO().getPanelDTO();
    }

    /**
     * Finds a specific cut with id
     *
     * @param id id of the cut
     * @return Optional<CutDTO> : CutDTO if found, null if not found
     */
    public Optional<CutDTO> findSpecificCut(UUID id) {
        return this.currentProjectState.getPanel().findSpecificCut(id);
    }

    /**
     * Get the CutDTO list of the domain
     *
     * @return List<CutDTO> of the cuts in the domain
     */
    public List<CutDTO> getCutListDTO() {
        return getProjectStateDTO().getPanelDTO().getCutsDTO();
    }

    /**
     * Resizes the board to the desired size. The desired size must respect the CNC size constraints.
     *
     * @param width  The new width of the board.
     * @param height The new height of the board.
     */
    public void resizePanel(double width, double height) {
        this.currentProjectState.getPanel().resize(width, height, this.currentProjectState.getPanel().getDepth());
    }

    public void resizePanel(double width, double height, double depth) {
        this.currentProjectState.getPanel().resize(width, height, depth);
    }

    /**
     * Removes a cut from the current {@code ProjectState} board
     *
     * @param id The id of the {@code Cut} the needs to be removed
     * @return Boolean : true if cut is removed, false if it can't be removed
     */
    public boolean removeCut(UUID id) {
        return this.currentProjectState.getPanel().removeCut(id);
    }

    /**
     * Modify a cut with the attributes of another one.
     *
     * @param cut The modified Cut.
     */
    public Optional<UUID> modifyCut(CutDTO cut) {
        return this.currentProjectState.getPanel().modifyCut(cut);
    }

    /**
     * @return The list of Bit of the CNC
     */
    public BitDTO[] getBitsDTO() {
        return getProjectStateDTO().getBitList();
    }

    /**
     * Removes a {@code Bit} from the bit list of the project.
     *
     * @param index The index of the bit that needs to be removed.
     */
    public void removeBit(int index) throws InvalidBitException {
        currentProjectState.removeBit(index);
    }

    /**
     * Modify a {@code Bit} in the bit list of the project.
     *
     * @param index The index of the {@code bit}
     * @param bit   A DTO representing the new {@code Bit}
     */
    public void modifyBit(int index, BitDTO bit) {
        this.currentProjectState.updateBit(index, bit);
    }

    /**
     * Does the Redo action on the project.
     */
    public void redo() {
        this.undoRedoManager.redo();
    }

    /**
     * Does the Undo action on the project.
     */
    public void undo() {
        this.undoRedoManager.undo();
    }

    /**
     * Calculates and return the list of {@code Vertex} that defines the grid of the board.
     *
     * @param precision The precision with which the grid should be calculated.
     * @return The list of intersections of the grid.
     */
    public List<VertexDTO> putGrid(int precision, int magnetPrecision) {
        if (this.grid == null) {
            this.grid = new Grid(precision, magnetPrecision);
        } else {
            this.grid.setMagnetPrecision(magnetPrecision);
            this.grid.setSize(precision);
        }
        return null;
    }


    /**
     * Function that returns the grid
     *
     * @return the grid
     */
    public GridDTO getGrid() {
        return new GridDTO(this.grid.getSize(), this.grid.getMagnetPrecision(), grid.isMagnetic(), grid.isActive());
    }

    /**
     * Saves the current state of the project.
     */
    public void saveProject() {
    }

    /**
     * Saves the Gcode of the project.
     */
    public void saveGcode(String path) {
        ProjectFileManager.saveString(path, convertToGCode());
    }

    /**
     * Opens a file and set the current project as the project saved in the file.
     */
    public void openProject() {
        //todo
    }

    /**
     * Modifies the {@code PanelCNC} of the current {@code ProjectState}
     *
     * @param panel The new PanelCNC as a DTO
     */
    public void modifyPanel(PanelDTO panel) {
        //todo
    }

    /**
     * Adds a {@code ClampZone} to the current {@code PanelCNC}
     *
     * @param points the list of points that define the {@code ClampZone}
     * @return The id of the clampZone if it could be created.
     */
    public Optional<UUID> addClampZone(VertexDTO[] points) {
        //todo
        return null;
    }

    /**
     * Modifies an existing clampZone
     *
     * @param id    The id of the ClampZone that is modified
     * @param clamp The new ClampZone
     */
    public void modifyClampZone(UUID id, ClampZoneDTO clamp) {
        //todo
    }

    /**
     * Removes a ClampZone form the current {@code PanelCNC}
     *
     * @param id The id of the {@code ClampZone} that needs to be removed.
     */
    public void removeClampZone(UUID id) {
        //todo
    }

    /**
     * Converts the current {@code ProjectState} as GCode instructions.
     *
     * @return The String that represent the GCode instructions.
     */
    public String convertToGCode() {
        return GcodeGenerator.convertToGCode(getProjectStateDTO());
    }


    /**
     * Gets the {@code Cut} or {@code ClampZone} at the specified mm position
     *
     * @param position The position which we want to know if there is an element.
     * @return The id of the element if an element as been found.
     */
    public Optional<UUID> getElementAtmm(VertexDTO position) {
        //todo
        return null;
    }

    public Optional<UUID> renderImage(BufferedImage image, VertexDTO position) {
        return camera.renderImage(image, position);
    }

    public UUID getCameraId() {
        return camera.getId();
    }

    /**
     * Rotates the {@code Transform} around the transform's origin as if it were on a gimbal.
     * Mostly meant to be used with the camera, but it is generic to any transform
     *
     * @param transformId   The id of the {@code Transform} to move
     * @param XAxisRotation The amount of rotation in rad to apply around the X axis
     * @param YAxisRotation The amount of rotation in rad to apply around the Y axis
     * @throws InvalidKeyException if the given id does not correspond to a transform
     */
    public void panTransform(UUID transformId, double XAxisRotation, double YAxisRotation) throws InvalidKeyException {
        if (camera.getId() == transformId) {
            camera.pan(XAxisRotation, YAxisRotation);
        } else {
            scene.getMesh(transformId).pan(XAxisRotation, YAxisRotation);
        }
    }

    /**
     * Applies a position, rotation, and scale change to the {@code Transform}
     *
     * @param transformId    The id of the {@code Transform} to transform
     * @param positionChange The delta to apply to the position
     * @param rotationChange The euler angle vector in radians to apply to the rotation
     * @param scaleChange    The scale delta to apply to the scale
     * @throws InvalidKeyException if the given id does not correspond to a transform
     */
    public void applyTransform(UUID transformId, VertexDTO positionChange, VertexDTO rotationChange, double scaleChange) throws InvalidKeyException {
        scene.applyTransform(transformId, positionChange, rotationChange, scaleChange);
    }

    /**
     * Returns an optional closest line point to the board outlines + cuts based on a reference point (cursor)
     *
     * @param p1        initial point of the cut
     * @param cursor    current cursor position
     * @param threshold threshold of the distance
     * @return Optional<VertexDTO> : null if no line nearby, the closest Point if point nearby
     */
    public Optional<VertexDTO> getGridLineNearAllBorderAndCuts(VertexDTO p1, VertexDTO cursor, double threshold) {
        return this.grid.getLineNearAllBorderAndCuts(p1, cursor, this.currentProjectState.getPanel(), threshold);
    }


    /**
     * Returns an optionnal closest point to the board outlines + cuts based on a reference point
     *
     * @param point     reference point
     * @param threshold threshold of the distance
     * @return Optional<VertexDTO> : null if no line nearby, the closest Point if point nearby
     */
    public Optional<VertexDTO> getGridPointNearAllBorderAndCuts(VertexDTO point, double threshold) {
        return this.grid.getPointNearAllBorderAndCuts(point, this.currentProjectState.getPanel(), threshold);
    }

    /**
     * Returns an optionnal closest point to the board outlines based on a reference point
     *
     * @param point     reference point
     * @param threshold threshold of the distance
     * @return Optional<VertexDTO> : null if no line nearby, the closest Point if point nearby
     */
    public Optional<VertexDTO> getGridPointNearBorder(VertexDTO point, double threshold) {
        return this.grid.getPointNearAllBorder(point, this.currentProjectState.getPanel(), threshold, Optional.empty());
    }


    /**
     * Returns an optionnal closest point to all intersections on the board
     *
     * @param point     reference point
     * @param threshold threshold of the distance
     * @return Optional<VertexDTO> : null if no intersection nearby, the closest Point if point nearby
     */
    public Optional<VertexDTO> getPointNearIntersections(VertexDTO point, double threshold) {
        return this.grid.isPointNearIntersections(point, threshold);
    }

    /**
     * Computes all of the intersection points  on the board, stores them in the grid class
     */
    public void computeGridIntersections() {
        this.grid.computeIntersectionPointList(this.currentProjectState.getPanel());
    }

    /**
     * Return the list of the reference Cut that are touching the input point
     *
     * @param point point position to analyse
     * @return list of reference Cut touching the point
     */
    public List<RefCutDTO> getRefCutsAndBorderOnPoint(VertexDTO point) {
        return this.grid.getRefCutsAndBorderOnPoint(point, this.currentProjectState.getPanel());
    }

    public void setGridMagnetism(boolean magnetism) {
        grid.setMagnetic(magnetism);
    }

    public void setGridAvtive(boolean active) {
        grid.setActive(active);

    }

    /**
     * Executes the doAction and memorizes it for the undoRedo system
     *
     * @param doAction   lambda of method to execute
     * @param undoAction lambda of method undoing the first one
     */
    public void executeAndMemorize(IDoAction doAction, IUndoAction undoAction) {
        undoRedoManager.executeAndMemorize(doAction, undoAction);
    }

    /**
     * Register a method to be called when an undo or redo is done
     *
     * @param refreshable class that implements a refresh method
     */
    public void addRefreshListener(IRefreshable refreshable) {
        undoRedoManager.addRefreshListener(refreshable);
    }

    /**
     * @return True if the point is on the board.
     */
    public boolean isPointOnPanel(VertexDTO point) {
        return this.currentProjectState.getPanel().isPointOnPanel(point);
    }

    /**
     * Returns a Map of the Bits as (Position of the bit, BitDTO) to know what bit has a value
     *
     * @return Map containing Position of the bit, BitDTO
     */
    public Map<Integer, BitDTO> refreshConfiguredBitMaps() {
        return currentProjectState.getConfiguredBits();
    }

    public DimensionDTO convertUnit(DimensionDTO toConvert, Units targetUnit) {
        return new DimensionDTO((toConvert.value() * toConvert.unit().getRatio()) / targetUnit.getRatio(), targetUnit);
    }

    public void resetPanelCNC() {
        currentProjectState.resetPanelCNC();
    }


    /**
     * Returns the bit diameter if index is valid. If index not valid, returns 0
     *
     * @param bitIndex index
     * @return diameter of the bit
     */
    public double getBitDiameter(int bitIndex) {
        return this.currentProjectState.getBitDiameter(bitIndex);
    }

    /**
     * Transform a edge-edge distance of a cut into a center-center distance
     *
     * @param edge      edge-edge distance
     * @param bitIndex1 bit index of the first point
     * @param bitIndex2 bit index of the reference point
     * @return the converted center-center distance
     */
    public double edgeEdgeToCenterCenter(double edge, int bitIndex1, int bitIndex2) {
        return this.currentProjectState.edgeEdgeToCenterCenter(edge, bitIndex1, bitIndex2);
    }

    /**
     * Transform a center-center distance of a cut into a edge-edge distance
     *
     * @param center    center-center distance
     * @param bitIndex1 bit index of the first point
     * @param bitIndex2 bit index of the reference point
     * @return the converted edge-edge distance
     */
    public double centerCenterToEdgeEdge(double center, int bitIndex1, int bitIndex2) {
        return this.currentProjectState.centerCenterToEdgeEdge(center, bitIndex1, bitIndex2);
    }

    /**
     * Generate a fixed list of points used by the rectangle cut according to an anchor point, a width and a height
     *
     * @param anchor
     * @param width
     * @param height
     * @return
     */
    public List<VertexDTO> generateRectanglePoints(VertexDTO anchor, double width, double height) {
        return Cut.generateRectanglePoints(anchor, width, height);
    }

    /**
     * From a cut DTO queries the corresponding Cut Object to get it's absolute position
     *
     * @param cutDTO cutDto to query
     * @return the list of absolute points
     */
    public List<VertexDTO> getAbsolutePointsPosition(CutDTO cutDTO) {
        return Cut.getAbsolutePointsPositionOfCutDTO(cutDTO, this.currentProjectState.getPanel());
    }

    public VertexDTO getBorderPointCut(double margin) {
        return Cut.getBorderPointCut(margin);
    }

    public VertexDTO getDefaultBorderPointCut() {
        return Cut.getBorderPointCutDefaultMargins();
    }

    public boolean isRefCircular(RefCutDTO refCutDTO, CutDTO cutToTest) {
        return RefCut.isRefCircular(refCutDTO, cutToTest);
    }
}

