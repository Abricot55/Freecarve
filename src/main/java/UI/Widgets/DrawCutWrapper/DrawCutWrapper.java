package UI.Widgets.DrawCutWrapper;

import Domain.CutDTO;
import Domain.CutType;
import Domain.ThirdDimension.Vertex;
import Domain.ThirdDimension.VertexDTO;
import UI.SubWindows.Rendering2DWindow;
import UI.Widgets.PersoPoint;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Polymorphic drawing class for cuts
 * @author Louis-Etienne Messier
 */
public abstract class DrawCutWrapper {

    protected ArrayList<PersoPoint> points; // Stores the PersoPoint in MM - necessary to keep precision

    public ArrayList<PersoPoint> getPersoPoints(){
        return this.points;
    }
    public abstract void draw(Graphics2D graphics2D, Rendering2DWindow renderer);
    public abstract void beingDrawned(Graphics2D graphics2D, Rendering2DWindow renderer, PersoPoint cursor);
    public abstract boolean addPoint(Rendering2DWindow renderer, PersoPoint point);
    public abstract Optional<UUID> end();
    public abstract  CutType getCutType();

    public static DrawCutWrapper createCutWrapper(CutDTO cut, Rendering2DWindow renderer){
        if (cut.getCutType() == CutType.LINE_VERTICAL || cut.getCutType() == CutType.LINE_HORIZONTAL){
            return new DrawParrallelCut(cut, renderer);
        }

        return new DrawParrallelCut(cut, renderer);
    }

    public static DrawCutWrapper createEmptyWrapper(CutType type, Rendering2DWindow renderer){
        if (type == CutType.LINE_VERTICAL || type == CutType.LINE_HORIZONTAL){
            return new DrawParrallelCut(type, renderer);
        }

        return new DrawParrallelCut(type, renderer);
    }

    public static List<DrawCutWrapper> createListDrawCutWrapper(List<CutDTO> cutDTOList, Rendering2DWindow renderer){
        ArrayList<DrawCutWrapper> outputList = new ArrayList<DrawCutWrapper>();
        for (CutDTO cut : cutDTOList){
            outputList.add(DrawCutWrapper.createCutWrapper(cut, renderer));
        }
        return outputList;
    }
}
