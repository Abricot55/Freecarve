package UI.Display2D.DrawCutWrapper;

import Common.DTO.CutDTO;
import Common.DTO.DimensionDTO;
import Common.DTO.RefCutDTO;
import Common.DTO.VertexDTO;
import Common.Units;
import Domain.CutType;
import UI.Display2D.Drawing;
import UI.Display2D.Rendering2DWindow;
import UI.Events.ChangeAttributeEvent;
import UI.MainWindow;
import UI.UIConfig;
import UI.UiUtil;
import UI.Widgets.CutBox;
import UI.Widgets.PersoPoint;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Rectangular line drawing that inherits from DrawCutWrapper
 *
 * @author Louis-Etienne Messier
 */
public class DrawCutRectangular extends DrawCutWrapper {

    public DrawCutRectangular(CutType type, Rendering2DWindow renderer, MainWindow mainWindow) {
        super(type, renderer, mainWindow);
    }

    @Override
    public void drawAnchor(Graphics2D graphics2D, Rendering2DWindow renderer) {
        VertexDTO p1 = cut.getRefsDTO().get(0).getAbsoluteFirstPoint(mainWindow.getController());
        VertexDTO p2 = cut.getRefsDTO().get(0).getAbsoluteSecondPoint(mainWindow.getController());
        VertexDTO p3 = cut.getRefsDTO().get(1).getAbsoluteFirstPoint(mainWindow.getController());
        VertexDTO p4 = cut.getRefsDTO().get(1).getAbsoluteSecondPoint(mainWindow.getController());

        Optional<VertexDTO> anchor = VertexDTO.isLineIntersectNoLimitation(p1, p2, p3, p4);

        if (anchor.isPresent()) {
            PersoPoint referenceAnchorPoint = new PersoPoint(anchor.get().getX(), anchor.get().getY(), cursorRadius, true);
            referenceAnchorPoint.setColor(strokeColor);
            referenceAnchorPoint.drawMMWithPersoPointColor(graphics2D, renderer);
        }

    }

    @Override
    public void draw(Graphics2D graphics2D, Rendering2DWindow renderer) {
        this.update(renderer);
        graphics2D.setStroke(stroke);
        graphics2D.setColor(this.strokeColor);

        //draw line
        Color c = Color.black;
        for (int i = 0; i < points.size() - 1; i++) {
            if (this.points.get(i).getColor() != HOVER_VIEW_COLOR) {
                c = this.points.get(i).getColor();
            }
        }
        for (int i = 0; i < points.size() - 2; i++) {
            Color c2 = c;
            if (renderer.getDrawing().getState() == Drawing.DrawingState.IDLE && hoveredView && mainWindow.getController().isRoundedCutDTOSegmentHoveredByCursor(cut, new VertexDTO(renderer.getMmMousePt().getX(), renderer.getMmMousePt().getY(), 0), i, i + 1)) {
                c = HOVER_VIEW_COLOR;
            }
            graphics2D.setColor(c);
            this.points.get(i).drawLineMM(graphics2D, renderer, this.points.get(i + 1));
            c = c2;
        }

        //draw points
        for (int i = 0; i < points.size() - 1; i++) {
            if (this.points.get(i).getColor() != HOVER_VIEW_COLOR) {
                c = this.points.get(i).getColor();
            }
        }
        for (VertexDTO point : renderer.getMainWindow().getController().getAbsolutePointsPosition(cut)) {
            graphics2D.setColor(c);
            Point2D temp = renderer.mmTopixel(new Point2D.Double(point.getX(), point.getY()));
            PersoPoint _point = new PersoPoint(point.getX(), point.getY(), this.pointsRadius / renderer.getZoom(), true);
            if (renderer.getDrawing().getState() == Drawing.DrawingState.IDLE && renderer.isPointonPanel() && mainWindow.getController().mouseOnTop(renderer.getMousePt().getX(), renderer.getMousePt().getY(), temp.getX(), temp.getY(), _point.getRadius() * renderer.getZoom())) {
                graphics2D.setColor(HOVER_VIEW_COLOR);
            }
            _point.drawMM(graphics2D, renderer);
        }
    }

    public DrawCutRectangular(CutDTO cut, Rendering2DWindow renderer, MainWindow mainWindow) {
        super(cut, renderer, mainWindow);
    }

    @Override
    public void moveUpdate(Point2D pixP, Rendering2DWindow renderer, MainWindow mainWindow) {
        List<VertexDTO> listPoints = mainWindow.getController().getAbsolutePointsPosition(getCutDTO());
        double threshold = getThresholdForMagnet(renderer);
        Point2D mmE = renderer.pixelTomm(pixP);
        this.cursorPoint = new PersoPoint(mmE.getX(), mmE.getY(), 1, true);
        PersoPoint p = new PersoPoint(mmE.getX(), mmE.getY(), 1, true);
        Optional<VertexDTO> closestPoint1 = mainWindow.getController().getGridPointNearBorder(new VertexDTO(p.getLocationX(), p.getLocationY(), 0), threshold);
        VertexDTO closestPoint = closestPoint1.orElse(new VertexDTO(p.getLocationX(), p.getLocationY(), 0));
        VertexDTO p1;
        VertexDTO p2;

        if (mainWindow.getController().isRoundedCutDTOSegmentHoveredByCursor(cut, new VertexDTO(renderer.getMmMousePt().getX(), renderer.getMmMousePt().getY(), 0), 0, 1)) {
            closestPoint = Optional.ofNullable(changeClosestLineMaybe(closestPoint1, threshold, false)).orElse(closestPoint);
            p1 = new VertexDTO(closestPoint.getX(), listPoints.get(1).getY(), 0);
            p2 = new VertexDTO(listPoints.get(2).getX(), listPoints.get(3).getY(), 0);
        } else if (mainWindow.getController().isRoundedCutDTOSegmentHoveredByCursor(cut, new VertexDTO(renderer.getMmMousePt().getX(), renderer.getMmMousePt().getY(), 0), 1, 2)) {
            closestPoint = Optional.ofNullable(changeClosestLineMaybe(closestPoint1, threshold, true)).orElse(closestPoint);
            p1 = new VertexDTO(listPoints.get(1).getX(), closestPoint.getY(), 0);
            p2 = new VertexDTO(listPoints.get(2).getX(), listPoints.get(3).getY(), 0);
        } else if (mainWindow.getController().isRoundedCutDTOSegmentHoveredByCursor(cut, new VertexDTO(renderer.getMmMousePt().getX(), renderer.getMmMousePt().getY(), 0), 2, 3)) {
            closestPoint = Optional.ofNullable(changeClosestLineMaybe(closestPoint1, threshold, false)).orElse(closestPoint);
            p1 = new VertexDTO(listPoints.get(1).getX(), listPoints.get(1).getY(), 0);
            p2 = new VertexDTO(closestPoint.getX(), listPoints.get(3).getY(), 0);
        } else {
            closestPoint = Optional.ofNullable(changeClosestLineMaybe(closestPoint1, threshold, true)).orElse(closestPoint);
            p1 = new VertexDTO(listPoints.get(1).getX(), listPoints.get(1).getY(), 0);
            p2 = new VertexDTO(listPoints.get(3).getX(), closestPoint.getY(), 0);
        }
        CutDTO c = getCutDTO();

        List<VertexDTO> relativePts = mainWindow.getController().generateRectanglePointsRelativeEdgeEdgeFromAbsolute(p1, p2, getCutDTO().getBitIndex(), getCutDTO().getRefsDTO());
        mainWindow.getController().modifyCut(new CutDTO(c.getId(), c.getDepth(), c.getBitIndex(), c.getCutType(), relativePts, c.getRefsDTO(), c.getState()), false);

        Optional<CutBox> cutBox = mainWindow.getMiddleContent().getCutWindow().getCutListPanel().getCutBoxWithId(getCutDTO().getId());
        mainWindow.getMiddleContent().getCutWindow().modifiedAttributeEventOccured(new ChangeAttributeEvent(cutBox, cutBox.get()));

    }

    @Override
    public void movePoint(Point2D pixP, Rendering2DWindow renderer, MainWindow mainWindow, int indexPoint) {
        List<VertexDTO> listPoints = mainWindow.getController().getAbsolutePointsPosition(getCutDTO());
        double threshold = getThresholdForMagnet(renderer);
        Point2D mmE = renderer.pixelTomm(pixP);
        this.cursorPoint = new PersoPoint(mmE.getX(), mmE.getY(), 1, true);

        PersoPoint p = new PersoPoint(mmE.getX(), mmE.getY(), 1, true);
        Optional<VertexDTO> closestPoint1 = mainWindow.getController().getGridPointNearBorder(new VertexDTO(p.getLocationX(), p.getLocationY(), 0), threshold);
        VertexDTO closestPoint = closestPoint1.orElse(new VertexDTO(p.getLocationX(), p.getLocationY(), 0));
        closestPoint = Optional.ofNullable(changeClosestLineMaybe(closestPoint1, threshold, false)).orElse(closestPoint);
        closestPoint = Optional.ofNullable(changeClosestLineMaybe(closestPoint1, threshold, true)).orElse(closestPoint);
        closestPoint = Optional.ofNullable(changeClosestPointMaybe(threshold, closestPoint1, true)).orElse(closestPoint);

        VertexDTO p1;
        VertexDTO p2;

        if (indexPoint == 0) {
            p1 = new VertexDTO(closestPoint.getX(), listPoints.get(2).getY(), 0);
            p2 = new VertexDTO(listPoints.get(2).getX(), closestPoint.getY(), 0);
        } else if (indexPoint == 1) {
            p1 = new VertexDTO(closestPoint.getX(), closestPoint.getY(), 0);
            p2 = new VertexDTO(listPoints.get(3).getX(), listPoints.get(3).getY(), 0);
        } else if (indexPoint == 2) {
            p1 = new VertexDTO(listPoints.get(0).getX(), closestPoint.getY(), 0);
            p2 = new VertexDTO(closestPoint.getX(), listPoints.get(0).getY(), 0);
        } else if (indexPoint == 3) {
            p1 = new VertexDTO(listPoints.get(1).getX(), listPoints.get(1).getY(), 0);
            p2 = new VertexDTO(closestPoint.getX(), closestPoint.getY(), 0);
        } else {
            p1 = new VertexDTO(renderer.getDrawing().getPrevPts().get(1).getX() + closestPoint.getX() - pointDepart.getX(), renderer.getDrawing().getPrevPts().get(1).getY() + closestPoint.getY() - pointDepart.getY(), 0);
            p2 = new VertexDTO(renderer.getDrawing().getPrevPts().get(3).getX() + closestPoint.getX() - pointDepart.getX(), renderer.getDrawing().getPrevPts().get(3).getY() + closestPoint.getY() - pointDepart.getY(), 0);
        }
        CutDTO c = getCutDTO();

        List<VertexDTO> relativePts = mainWindow.getController().generateRectanglePointsRelativeEdgeEdgeFromAbsolute(p1, p2, getCutDTO().getBitIndex(), getCutDTO().getRefsDTO());
        mainWindow.getController().modifyCut(new CutDTO(c.getId(), c.getDepth(), c.getBitIndex(), c.getCutType(), relativePts, c.getRefsDTO(), c.getState()), false);

        Optional<CutBox> cutBox = mainWindow.getMiddleContent().getCutWindow().getCutListPanel().getCutBoxWithId(getCutDTO().getId());
        mainWindow.getMiddleContent().getCutWindow().modifiedAttributeEventOccured(new ChangeAttributeEvent(cutBox, cutBox.get()));
    }


    @Override
    public void drawWhileChanging(Graphics2D graphics2D, Rendering2DWindow renderer, PersoPoint cursor) {
        graphics2D.setStroke(stroke);
        graphics2D.setColor(cursor.getColor());
        if (!this.temporaryCreationPoints.isEmpty()) {
            this.points = new ArrayList<>();
            VertexDTO absPos = temporaryCreationPoints.getFirst();
            this.points.add(new PersoPoint(absPos.getX(), absPos.getY(), 10.0f, true, strokeColor));


            PersoPoint p1 = new PersoPoint(points.getFirst());
            PersoPoint p2 = new PersoPoint(points.getFirst());
            p1.movePoint(cursor.getLocationX(), this.points.getFirst().getLocationY());
            p2.movePoint(this.points.getFirst().getLocationX(), cursor.getLocationY());
            points.getFirst().drawLineMM(graphics2D, renderer, p1);
            points.getFirst().drawLineMM(graphics2D, renderer, p2);
            p1.drawLineMM(graphics2D, renderer, cursor);
            p2.drawLineMM(graphics2D, renderer, cursor);

            p1.drawMM(graphics2D, renderer); // drawing the points
            p2.drawMM(graphics2D, renderer); //drawing the points
        }

        for (PersoPoint point : this.points) { // drawing the points
            point.drawMM(graphics2D, renderer);

        }

        if (!refs.isEmpty()) { // drawing the first anchor point
            VertexDTO offset = refs.getFirst().getAbsoluteOffset(mainWindow.getController());
            PersoPoint referenceAnchorPoint = new PersoPoint(offset.getX(), offset.getY(), cursorRadius, true);
            referenceAnchorPoint.setColor(ANCHOR_COLOR);
            referenceAnchorPoint.drawMM(graphics2D, renderer);
        }

    }

    @Override
    public void drawDimensions(Graphics2D graphics2D, Rendering2DWindow rendering2DWindow) {
        List<VertexDTO> absPoints = mainWindow.getController().getAbsolutePointsPosition(cut);
        VertexDTO relativeOffset = cut.getPoints().getFirst();
        VertexDTO relativeDimensions = cut.getPoints().get(1);

        int index1 = 0;
        int index2 = 2;

        if (relativeOffset.getX() < 0 && relativeOffset.getY() < 0) {
            index1 = 2;
            index2 = 0;
        } else if (relativeOffset.getX() < 0 && relativeOffset.getY() > 0) {
            index1 = 3;
            index2 = 1;
        } else if (relativeOffset.getX() > 0 && relativeOffset.getY() < 0) {
            index1 = 1;
            index2 = 3;
        }

        VertexDTO absoluteCentralPoint = absPoints.get(index2).add(absPoints.get(index1)).mul(0.5);
        double bitDiameter = mainWindow.getController().getBitDiameter(cut.getBitIndex());


        VertexDTO p1 = absoluteCentralPoint.sub(relativeOffset);
        VertexDTO p2 = absoluteCentralPoint;
        UiUtil.drawArrow(graphics2D, rendering2DWindow, p1, p2, ARROW_COLOR, ARROW_DIMENSION);

        VertexDTO dirOpposite = absPoints.get(index2).sub(absPoints.get(index1));
        double dirOppositeX = Math.signum(dirOpposite.getX());
        double dirOppositeY = Math.signum(dirOpposite.getY());
        VertexDTO dirOppositeTo1 = new VertexDTO(dirOppositeX, dirOppositeY, 0);

        VertexDTO p1Width = absPoints.get(index2);
        VertexDTO p2Width = absPoints.get(index2).sub(new VertexDTO(relativeDimensions.getX() * dirOppositeX, 0, 0));
        VertexDTO p1Height = absPoints.get(index2);
        VertexDTO p2Height = absPoints.get(index2).sub(new VertexDTO(0, relativeDimensions.getY() * dirOppositeY, 0));

        p1Width = p1Width.sub(dirOppositeTo1.mul(bitDiameter / 2));
        p2Width = p2Width.sub(dirOppositeTo1.mul(bitDiameter / 2));

        p1Height = p1Height.sub(dirOppositeTo1.mul(bitDiameter / 2));
        p2Height = p2Height.sub(dirOppositeTo1.mul(bitDiameter / 2));


        UiUtil.drawArrow(graphics2D, rendering2DWindow, p1Width, p2Width, ARROW_COLOR, ARROW_DIMENSION);
        UiUtil.drawArrow(graphics2D, rendering2DWindow, p1Height, p2Height, ARROW_COLOR, ARROW_DIMENSION);

        DimensionDTO xOffsetUnit = mainWindow.getController().convertUnit(new DimensionDTO(relativeOffset.getX(), Units.MM), UIConfig.INSTANCE.getDefaultUnit().getUnit());
        DimensionDTO yOffsetUnit = mainWindow.getController().convertUnit(new DimensionDTO(relativeOffset.getY(), Units.MM), UIConfig.INSTANCE.getDefaultUnit().getUnit());
        DimensionDTO xDimensionUnit = mainWindow.getController().convertUnit(new DimensionDTO(relativeDimensions.getX(), Units.MM), UIConfig.INSTANCE.getDefaultUnit().getUnit());
        DimensionDTO yDimensionUnit = mainWindow.getController().convertUnit(new DimensionDTO(relativeDimensions.getY(), Units.MM), UIConfig.INSTANCE.getDefaultUnit().getUnit());

        UiUtil.drawNumberXY(graphics2D, rendering2DWindow, p1, p2, xOffsetUnit.value(), yOffsetUnit.value(), ARROW_COLOR, DIMENSION_COLOR);
        UiUtil.drawNumber(graphics2D, rendering2DWindow, p1Width, p2Width, xDimensionUnit.value(), ARROW_COLOR, DIMENSION_COLOR);
        UiUtil.drawNumber(graphics2D, rendering2DWindow, p1Height, p2Height, yDimensionUnit.value(), ARROW_COLOR, DIMENSION_COLOR);
    }

    @Override
    public boolean addPoint(Drawing drawing, Rendering2DWindow renderer, PersoPoint pointInMM) {

        if (refs.isEmpty()) {
            VertexDTO p1 = new VertexDTO(pointInMM.getLocationX(), pointInMM.getLocationY(), 0.0f);
            refs = mainWindow.getController().getRefCutsAndBorderOnPoint(p1);
            return false;
        } else {
            if (temporaryCreationPoints.isEmpty()) { // premier point a ajouter
                VertexDTO newPoint = new VertexDTO(pointInMM.getLocationX(), pointInMM.getLocationY(), 0.0f);
                temporaryCreationPoints.add(newPoint);
                return false;
            } else {

                // Dans le cas contraire, c'est le dernier point, donc ajoute les 4 points finaux:
                //  3 - 2*
                //  |   |
                //  4 - 1
                VertexDTO newPoint = new VertexDTO(pointInMM.getLocationX(), pointInMM.getLocationY(), 0.0f);
                temporaryCreationPoints.add(newPoint);
                return true;

            }
        }
    }

    @Override
    public boolean areRefsValid() {
        return refs.size() >= 2 && areRefsNotCircular();
    }

    @Override
    public Optional<UUID> end() {
        VertexDTO p1 = temporaryCreationPoints.getFirst();
        VertexDTO p3 = temporaryCreationPoints.get(1);
        List<VertexDTO> relativeEdgeEdgePoints = mainWindow.getController().generateRectanglePointsRelativeEdgeEdgeFromAbsolute(p1, p3, this.cut.getBitIndex(), refs);
        this.cut = new CutDTO(this.cut.getId(), this.cut.getDepth(), this.cut.getBitIndex(), this.cut.getCutType(), relativeEdgeEdgePoints, refs, this.cut.getState());
        return createCut();
    }

    @Override
    public void cursorUpdate(Rendering2DWindow renderer, Drawing drawing) {
        PersoPoint p = this.cursorPoint;
        mainWindow.getController().setIntersectionMagnetic();
        double threshold = getThresholdForMagnet(renderer);
        p.movePoint(renderer.getMmMousePt().getX(), renderer.getMmMousePt().getY());
        VertexDTO p1 = new VertexDTO(p.getLocationX(), p.getLocationY(), 0.0f);

        if (refs.isEmpty()) {
            Optional<VertexDTO> closestPoint = mainWindow.getController().getPointNearIntersections(p1, threshold);
            Optional<VertexDTO> finalClosestPoint = closestPoint;
            closestPoint = Optional.ofNullable(changeClosestLineMaybe(closestPoint, threshold, false, false))
                    .or(() -> finalClosestPoint);
            closestPoint = Optional.ofNullable(changeClosestLineMaybe(closestPoint, threshold, true, false))
                    .or(() -> finalClosestPoint);
            closestPoint = Optional.ofNullable(changeClosestPointMaybe(threshold, closestPoint, false))
                    .or(() -> finalClosestPoint);
            if (closestPoint.isPresent() && renderer.isPointonPanel()) {
                p.movePoint(closestPoint.get().getX(), closestPoint.get().getY());
                p1 = new VertexDTO(p.getLocationX(), p.getLocationY(), 0.0f);
                List<RefCutDTO> refsDTO = mainWindow.getController().getRefCutsAndBorderOnPoint(p1);

                if (refsDTO.size() >= 2) {
                    p.setColor(ANCHOR_COLOR);
                    p.setValid(PersoPoint.Valid.VALID);
                } else {
                    p.setColor(INVALID_COLOR);
                    p.setValid(PersoPoint.Valid.NOT_VALID);
                }
            } else {
                p.setColor(INVALID_COLOR);
                p.setValid(PersoPoint.Valid.NOT_VALID);
            }
        } else {
            Optional<VertexDTO> closestPoint = mainWindow.getController().getGridPointNearAllBorderAndCuts(p1, threshold);
            Optional<VertexDTO> finalClosestPoint = closestPoint;

            closestPoint = Optional.ofNullable(changeClosestLineMaybe(closestPoint, threshold, false))
                    .or(() -> finalClosestPoint);
            closestPoint = Optional.ofNullable(changeClosestLineMaybe(closestPoint, threshold, true))
                    .or(() -> finalClosestPoint);
            closestPoint = Optional.ofNullable(changeClosestPointMaybe(threshold, closestPoint, true))
                    .or(() -> finalClosestPoint);

            if (this.points.isEmpty()) { // First point after anchor
                if (closestPoint.isPresent() && renderer.isPointonPanel()) {
                    p.movePoint(closestPoint.get().getX(), closestPoint.get().getY());
                    p.setColor(VALID_COLOR);
                    p.setValid(PersoPoint.Valid.VALID);
                }
            } else { // Rest of the rectangle points
                // For the snap area
                // Get the possible closest point

                // Snap
                if (closestPoint.isPresent() && renderer.isPointonPanel()) {
                    p.movePoint(closestPoint.get().getX(), closestPoint.get().getY());
                    p.setColor(SNAP_COLOR);
                    p.setValid(PersoPoint.Valid.VALID);
                    return;
                }

                // Test if on board
                VertexDTO pointDTO = new VertexDTO(p.getLocationX(), p.getLocationY(), 0.0f);
                if (mainWindow.getController().isPointOnPanel(pointDTO)) {
                    // Inside of the board
                    p.setColor(VALID_COLOR);
                    p.setValid(PersoPoint.Valid.VALID);
                } else {
                    // Outside of the board
                    p.setColor(INVALID_COLOR);
                    p.setValid(PersoPoint.Valid.NOT_VALID);
                }
            }
        }
    }
}
