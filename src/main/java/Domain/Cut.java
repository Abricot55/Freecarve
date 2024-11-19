package Domain;

import Common.DTO.CutDTO;
import Common.DTO.RequestCutDTO;
import Common.DTO.VertexDTO;

import java.util.List;
import java.util.UUID;

/**
 * The {@code Cut} class encapsulates the basic attributes of a cuto
 *
 * @author Louis-Etienne Messier
 * @version 0.1
 * @since 2024-09-21
 */
class Cut {
    private VertexDTO startPoint;
    private CutType type;
    private List<VertexDTO> points;
    private int bitIndex;
    private double depth;
    private UUID id;
    private boolean valid;


    public Cut(CutDTO uiCut) {
        this.startPoint = uiCut.getPoints().getFirst();
        this.type = uiCut.getCutType();
        this.points = uiCut.getPoints();
        this.bitIndex = uiCut.getBitIndex();
        this.depth = uiCut.getDepth();
        this.id = uiCut.getId();
    }

    /**
     * Constructs a new {@code Cut} with all of it's attributes
     *
     * @param startPoint the initial {@code Point} of the cut
     * @param type       the type of the cut
     * @param points     all the other point that characterise the cut
     * @param bitIndex   the index of the bit that is used for the cut
     * @param depth      the depth of the cut
     */
    public Cut(VertexDTO startPoint, CutType type, List<VertexDTO> points, int bitIndex, double depth) {
        this.startPoint = startPoint;
        this.type = type;
        this.points = points;
        this.bitIndex = bitIndex;
        this.depth = depth;
        this.id = UUID.randomUUID();
    }

    public Cut(RequestCutDTO requestCutDTO) {
        //todo
    }

    public CutDTO getDTO() {
        return new CutDTO(id, depth, bitIndex, type, points.stream().toList());
    }

    public VertexDTO getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(VertexDTO startPoint) {
        this.startPoint = startPoint;
    }

    public List<VertexDTO> getPoints() {
        return points;
    }

    public void setPoints(List<VertexDTO> points) {
        this.points = points;
    }

    public int getBitIndex() {
        return bitIndex;
    }

    public void setBitIndex(int bitIndex) {
        this.bitIndex = bitIndex;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public UUID getId() {
        return this.id;
    }

    public CutType getType() {
        return this.type;
    }

    /**
     * Finds if a coordinate is in the current {@code Cut} zone.
     *
     * @param coordinatesmm The coordinate.
     * @return A boolean indicating if the point is in the Cut's zone.
     */
    boolean pointCollision(VertexDTO coordinatesmm) {
        //todo
        return false;
    }
}
