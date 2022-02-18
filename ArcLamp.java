import gmaths.*;
import com.jogamp.opengl.*;

public class ArcLamp {

    public SGNode spotlightRoot;
    private TransformNode spotlightTranslate, translateX, spotlightMoveTranslate, archRotate, lampRotate, lampTranslate;
    private Model cube, bulb;
    private float lampAngle;
    private Vec3 lampPos;

    public ArcLamp(GL3 gl, Camera camera, Light light, DirLight sun, float scale) {
        // constants
        float baseHeight = 0.5f;
        float baseWidth = 2.5f;
        float poleScale = 0.5f;
        float poleLength = 16f;
        float archLength = 5f;
        float lampLength = 1.5f;
        float bulbScale = 0.75f;

        // create mesh, shader, and material for cubes
        Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
        Shader shader = new Shader(gl, "vertex/vs_main.txt", "fragment/fs_base.txt");
        Material material = new Material(new Vec3(0.1f, 0.1f, 0.1f), new Vec3(0.5f, 0.5f, 0.5f),
                new Vec3(0.2f, 0.2f, 0.2f), 2.0f);

        // create cube models
        cube = new Model(gl, camera, light, sun, shader, material, new Mat4(1), mesh);

        // define sphere for bulb
        mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        material = new Material(new Vec3(1f, 0.9f, 0.5f), new Vec3(1f, 1f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        bulb = new Model(gl, camera, light, sun, shader, material, new Mat4(1), mesh);

        // create root node
        spotlightRoot = new NameNode("spotlightRoot");
        spotlightTranslate = new TransformNode("spotlight transform", Mat4Transform.translate(16f, 0, 8f));

        // create base of lamp
        NameNode baseNode = new NameNode("base");
        Mat4 m = Mat4Transform.scale(baseWidth, baseHeight, baseWidth);
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        TransformNode baseTransform = new TransformNode("base transform", m);
        ModelNode baseShape = new ModelNode("Cube(base)", cube);

        // create pole of lamp
        NameNode poleNode = new NameNode("pole");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0, baseHeight, 0));
        m = Mat4.multiply(m, Mat4Transform.scale(poleScale, poleLength, poleScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        TransformNode poleTransform = new TransformNode("pole transform", m);
        ModelNode poleShape = new ModelNode("Cube(pole)", cube);

        // create arch of lamp
        NameNode archNode = new NameNode("arch");
        TransformNode archTranslate = new TransformNode("arch translate",
                Mat4Transform.translate((poleScale - archLength) / 2, baseHeight + poleLength + poleScale / 2, 0));
        archRotate = new TransformNode("arch rotate",Mat4Transform.rotateAroundZ(90));
        TransformNode archScale = new TransformNode("arch scale", Mat4Transform.scale(poleScale, archLength, poleScale));
        ModelNode archShape = new ModelNode("Cube(arch)", cube);

        // create lamp cube
        NameNode lampNode = new NameNode("lamp");
        lampTranslate = new TransformNode("lamp translate",
                Mat4Transform.translate(-archLength + poleScale / 2, baseHeight + poleLength, 0));
        lampRotate = new TransformNode("lamp rotate", Mat4Transform.rotateAroundX(180));
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(lampLength / 2, lampLength, lampLength / 2));
        m = Mat4.multiply(m, Mat4Transform.translate(0, lampLength * 0.25f, 0));
        TransformNode lampScale = new TransformNode("lamp scale", m);
        ModelNode lampShape = new ModelNode("Cube(lamp)", cube);

        // create bulb
        NameNode bulbNode = new NameNode("bulb");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0, lampLength - bulbScale / 2, 0));
        m = Mat4.multiply(m, Mat4Transform.scale(bulbScale, bulbScale, bulbScale));
        TransformNode bulbTransform = new TransformNode("bulb transform", m);
        ModelNode bulbShape = new ModelNode("Sphere(bulb)", bulb);

        // define scene graph
        spotlightRoot.addChild(spotlightTranslate);
            spotlightTranslate.addChild(baseNode);
                baseNode.addChild(baseTransform);
                    baseTransform.addChild(baseShape);
            spotlightTranslate.addChild(poleNode);
                poleNode.addChild(poleTransform);
                    poleTransform.addChild(poleShape);
            spotlightTranslate.addChild(archNode);
                archNode.addChild(archTranslate);
                    archTranslate.addChild(archRotate);
                    archRotate.addChild(archScale);
                    archScale.addChild(archShape);
                archNode.addChild(lampNode);
                    lampNode.addChild(lampTranslate);
                        lampTranslate.addChild(lampRotate);
                        lampRotate.addChild(lampScale);
                        lampRotate.addChild(bulbNode);
                            bulbNode.addChild(bulbTransform);
                            bulbTransform.addChild(bulbShape);
                        lampScale.addChild(lampShape);

        spotlightRoot.update();
    }

    public void render(GL3 gl) {
        spotlightRoot.draw(gl);
    }

    public void dispose(GL3 gl) {
        cube.dispose(gl);
        bulb.dispose(gl);
    }

    /* animate lamp movement */
    public void updateLamp(double startTime, double currentTime) {
        double elapsedTime = currentTime - startTime;
        lampAngle = (float) (180 + 20 *  Math.sin(elapsedTime));
        lampRotate.setTransform(Mat4Transform.rotateAroundX(lampAngle));
        lampRotate.update();
    }

    /* get position of moving lamp */
    public Vec3 getLampPosition() {
        Mat4 spotlightTransform = spotlightTranslate.getTransform();
        Mat4 lampTransform = lampTranslate.getTransform();
        float[] transMat = Mat4.multiply(spotlightTransform, lampTransform).toFloatArrayForGLSL();
        lampPos = new Vec3(transMat[12], transMat[13], transMat[14]);
        return lampPos;
    }

    /* get direction of moving lamp */
    public Vec3 getLampDirection() {
        // find tan of lamp angle
        double tanAngle = Math.tan(Math.toRadians(lampAngle - 20));
        // find target position of spotlight on floor
        Vec3 floorPos = new Vec3(lampPos.x, 0, (float) (-lampPos.y * tanAngle));
        // subtract target from lamp to get light direction
        Vec3 spotlightDir = Vec3.subtract(floorPos, lampPos);
        spotlightDir.normalize();
        return spotlightDir;
    }
}
