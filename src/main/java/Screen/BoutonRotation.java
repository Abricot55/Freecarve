package Screen;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BoutonRotation implements KeyListener, MouseListener {

    private final Renderer renderer;
    private Vertex vertexX;
    private Vertex vertexY;
    private Vertex vertexZ;
    private Mesh selectedMesh;
    private int movementType = 0; //0-translation, 1-rotation

    private final Matrix RIGHT_ROTATION = new Matrix(new double[]{
            Math.cos(0.05), 0, -Math.sin(0.05),
            0, 1, 0,
            Math.sin(0.05), 0, Math.cos(0.05)
    });
    private final Matrix LEFT_ROTATION = new Matrix(new double[]{
            Math.cos(-0.05), 0, -Math.sin(-0.05),
            0, 1, 0,
            Math.sin(-0.05), 0, Math.cos(-0.05)
    });
    private final Matrix UP_ROTATION = new Matrix(new double[]{
            1, 0, 0,
            0, Math.cos(0.05), Math.sin(0.05),
            0, -Math.sin(0.05), Math.cos(0.05)
    });
    private final Matrix DOWN_ROTATION = new Matrix(new double[]{
            1, 0, 0,
            0, Math.cos(-0.05), Math.sin(-0.05),
            0, -Math.sin(-0.05), Math.cos(-0.05)
    });

    public BoutonRotation(Renderer renderer) {
        this.renderer = renderer;
        this.vertexX = new Vertex(1, 0, 0);
        this.vertexY = new Vertex(0, 1, 0);
        this.vertexZ = new Vertex(0, 0, 1);
        this.selectedMesh = null;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        ExecutorService executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        executors.submit(() -> {

            switch (e.getKeyCode()) {
                case KeyEvent.VK_RIGHT:
                    rotationCurrentShape(RIGHT_ROTATION);
                    break;
                case KeyEvent.VK_LEFT:
                    rotationCurrentShape(LEFT_ROTATION);
                    break;
                case KeyEvent.VK_UP:
                    rotationCurrentShape(UP_ROTATION);
                    break;
                case KeyEvent.VK_DOWN:
                    rotationCurrentShape(DOWN_ROTATION);
                    break;
                case KeyEvent.VK_W:
                    if (movementType == 0) {
                        translationMesh(selectedMesh, new Vertex(0, -3, 0));
                    } else {
                        rotationMesh(selectedMesh, UP_ROTATION);
                    }
                    break;
                case KeyEvent.VK_A:
                    if (movementType == 0) {
                        translationMesh(selectedMesh, new Vertex(-3, 0, 0));
                    } else {
                        rotationMesh(selectedMesh, LEFT_ROTATION);
                    }
                    break;
                case KeyEvent.VK_S:
                    if (movementType == 0) {
                        translationMesh(selectedMesh, new Vertex(0, 3, 0));
                    } else {
                        rotationMesh(selectedMesh, DOWN_ROTATION);
                    }
                    break;
                case KeyEvent.VK_D:
                    if (movementType == 0) {
                        translationMesh(selectedMesh, new Vertex(3, 0, 0));
                    } else {
                        rotationMesh(selectedMesh, RIGHT_ROTATION);
                    }
                    break;
                case KeyEvent.VK_SPACE:
                    if (movementType == 0) {
                        translationMesh(selectedMesh, new Vertex(0, 0, 3));
                    }
                    break;
                case KeyEvent.VK_SHIFT:
                    if (movementType == 0) {
                        translationMesh(selectedMesh, new Vertex(0, 0, -3));
                    }
                    break;
                case KeyEvent.VK_Q:
                    if (movementType == 0) {
                        movementType = 1;
                    } else {
                        movementType = 0;
                    }
                    break;
            }
        });
        executors.shutdown();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }


    public void translationMesh(Mesh mesh, Vertex translation) {
        Vertex translationModif = new Vertex(0, 0, 0);
        translationModif = translationModif.addition(vertexX.multiplication(translation.getX()));
        translationModif = translationModif.addition(vertexY.multiplication(translation.getY()));
        translationModif = translationModif.addition(vertexZ.multiplication(translation.getZ()));
        for (Triangle t : mesh.getTrianglesList()) {
            t.setVertex1(t.getVertex1().addition(translationModif));
            t.setVertex2(t.getVertex2().addition(translationModif));
            t.setVertex3(t.getVertex3().addition(translationModif));
        }
        mesh.setVerticesList();
        renderer.repaint();
    }

    public void rotationMesh(Mesh mesh, Matrix rotationMatrice){
        Vertex center = mesh.getCenter();
        for(Triangle t : mesh.getTrianglesList()){
            t.setVertex1(rotationMatrice.matriceXVertex3x3(t.getVertex1().substraction(center)).addition(center));
            t.setVertex2(rotationMatrice.matriceXVertex3x3(t.getVertex2().substraction(center)).addition(center));
            t.setVertex3(rotationMatrice.matriceXVertex3x3(t.getVertex3().substraction(center)).addition(center));
        }
        mesh.setVerticesList();
        renderer.repaint();
    }

    public void rotationCurrentShape(Matrix rotationMatrice) {
        for (Mesh m : renderer.getMeshes()) {
            for (Triangle t : m.getTrianglesList()) {
                t.setVertex1(rotationMatrice.matriceXVertex3x3(t.getVertex1()));
                t.setVertex2(rotationMatrice.matriceXVertex3x3(t.getVertex2()));
                t.setVertex3(rotationMatrice.matriceXVertex3x3(t.getVertex3()));
            }
            m.setVerticesList();
        }
        vertexX.setVertex(rotationMatrice.matriceXVertex3x3(vertexX));
        vertexY.setVertex(rotationMatrice.matriceXVertex3x3(vertexY));
        vertexZ.setVertex(rotationMatrice.matriceXVertex3x3(vertexZ));
        renderer.repaint();
    }

    public void setSelectedMesh(Mesh selectedMesh) {
        this.selectedMesh = selectedMesh;
        System.out.println(selectedMesh.getVerticesList());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        renderer.setMousePos(new Vertex(e.getX(), e.getY(), 1));
        renderer.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
