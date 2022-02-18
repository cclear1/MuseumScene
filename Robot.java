import gmaths.*;
import com.jogamp.opengl.*;
import java.util.ArrayList;
import java.util.List;

public class Robot {

    private SGNode root;
    private TransformNode robotMoveTranslate, robotRotateTranslate, wheelRotate, bodyRotate,
            neckRotate, headRotate, comb1Rotate, comb2Rotate, comb3Rotate;
    private Model sphere, sphere2, sphere3, cube;

    private Vec3[] posePos;

    public Robot(GL3 gl, Camera camera, Light light, DirLight sun) {
        // define each pose position
        posePos = new Vec3[]{new Vec3(-8f, 0, -8f), new Vec3(8f, 0, -7f), new Vec3(6f, 0, 8f),
                new Vec3(0, 0, 10f), new Vec3(-12f, 0, 2f)};

        // load textures
        int[] robotTexture = TextureLibrary.loadTexture(gl, "textures/robot.jpg");
        int[] robotSpec = TextureLibrary.loadTexture(gl, "textures/robot_spec.jpg");
        int[] wheelTexture = TextureLibrary.loadTexture(gl, "textures/wheel.jpg");

        // define cubes
        Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
        Shader shader = new Shader(gl, "vertex/vs_main.txt", "fragment/fs_spec.txt");
        Material material = new Material(new Vec3(0.3f, 0, 0.3f), new Vec3(0.5f, 0.5f, 0.5f),
                new Vec3(0.5f, 0.5f, 0.5f), 10.0f);
        cube = new Model(gl, camera, light, sun, shader, material, new Mat4(1), mesh, robotTexture, robotSpec);

        // define main sphere
        shader = new Shader(gl, "vertex/vs_main.txt", "fragment/fs_spec.txt");
        mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        sphere = new Model(gl, camera, light, sun, shader, material, new Mat4(1), mesh, robotTexture, robotSpec);

        // define wheel sphere
        shader = new Shader(gl, "vertex/vs_main.txt", "fragment/fs_main.txt");
        material = new Material(new Vec3(0.3f, 0, 0.3f), new Vec3(0.5f, 0.5f, 0.5f),
                new Vec3(0.5f, 0.5f, 0.5f), 10.0f);
        sphere2 = new Model(gl, camera, light, sun, shader, material, new Mat4(1), mesh, wheelTexture);

        // define lens sphere
        shader = new Shader(gl, "vertex/vs_main.txt", "fragment/fs_base.txt");
        material = new Material(new Vec3(0, 0, 0), new Vec3(0, 0, 0), new Vec3(0.7f, 0.7f, 0.7f), 15.0f);
        sphere3 = new Model(gl, camera, light, sun, shader, material, new Mat4(1), mesh);

        // constants
        float wheelScale = 1.5f;
        float bodyLength = 5f;
        float bodyScale = 2f;
        float neckLength = 1.5f;
        float neckWidth = 0.25f;
        float headLength = 2.5f;
        float headWidth = 1f;
        float lensScale = 0.8f;
        float combLength = 1.5f;
        float combScale = 0.35f;

        // create root node
        root = new NameNode("root");
        robotMoveTranslate = new TransformNode("robot transform", Mat4Transform.translate(-8f, 0, -8f));
        TransformNode robotTranslate = new TransformNode("robot translate", Mat4Transform.translate(0, wheelScale / 2, 0));
        robotRotateTranslate = new TransformNode("robot rotate", Mat4Transform.rotateAroundY(25));

        // create wheel of robot
        NameNode wheel = new NameNode("wheel");
        Mat4 m = Mat4Transform.scale(wheelScale, wheelScale, wheelScale);
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0, 0));
        TransformNode wheelTransform = new TransformNode("wheel transform", m);
        ModelNode wheelShape = new ModelNode("Sphere(wheel)", sphere2);
        wheelRotate = new TransformNode("wheel rotate", Mat4Transform.rotateAroundX(0));

        // create body for robot
        NameNode body = new NameNode("body");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0, (wheelScale + bodyLength) / 2, 0));
        m = Mat4.multiply(m, Mat4Transform.scale(bodyScale, bodyLength, bodyScale));
        TransformNode bodyTransform = new TransformNode("body transform", m);
        bodyRotate = new TransformNode("body rotate", Mat4Transform.rotateAroundX(0));
        ModelNode bodyShape = new ModelNode("Sphere(body)", sphere);

        // calculate neck height based on length
        float neckHeight = neckLength * (float) Math.acos(Math.toRadians(45));

        // create neck for robot
        NameNode neck = new NameNode("neck");
        TransformNode neckTranslate = new TransformNode("neck translate", Mat4Transform.translate(0,
                bodyLength, 0));
        neckRotate = new TransformNode("neck rotate", Mat4Transform.rotateAroundX(45));
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(neckWidth, neckLength, neckWidth));
        m = Mat4.multiply(m, Mat4Transform.translate(0, neckLength / 2, neckHeight - 0.5f));
        TransformNode neckScale = new TransformNode("neck scale", m);
        ModelNode neckShape = new ModelNode("Cube(left arm)", cube);

        // create head for robot
        NameNode head = new NameNode("head");
        TransformNode headTranslate = new TransformNode("head translate",
                Mat4Transform.translate(0, headWidth + neckLength / 2, 0));
        headRotate = new TransformNode("head rotate", Mat4Transform.rotateAroundX(45));
        TransformNode headScale = new TransformNode("head scale", Mat4Transform.scale(headWidth,
                headLength, headWidth));
        ModelNode headShape = new ModelNode("Cube(right arm)", cube);

        // create lens for robot
        NameNode lens = new NameNode("lens");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0, headLength / 2, 0));
        m = Mat4.multiply(m, Mat4Transform.scale(lensScale, lensScale, lensScale));
        TransformNode lensTransform = new TransformNode("lens transform", m);
        ModelNode lensShape = new ModelNode("Sphere(lens)", sphere3);

        // create comb for head
        NameNode comb = new NameNode("comb");
        TransformNode combRotate = new TransformNode("comb1 rotate", Mat4Transform.rotateAroundX(-90));

        // create front comb finger
        NameNode comb1 = new NameNode("comb 1");
        comb1Rotate = new TransformNode("comb1 rotate", Mat4Transform.rotateAroundX(0));
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0, headWidth / 2 + 0.5f, headLength / 4));
        m = Mat4.multiply(m, Mat4Transform.scale(combScale, combLength, combScale));
        TransformNode comb1Transform = new TransformNode("comb1 transform", m);
        ModelNode comb1Shape = new ModelNode("Sphere(comb1)", sphere);

        // create middle comb finger
        NameNode comb2 = new NameNode("comb 2");
        m = new Mat4(1);
        comb2Rotate = new TransformNode("comb2 rotate", Mat4Transform.rotateAroundX(0));
        m = Mat4.multiply(m, Mat4Transform.translate(0, headWidth / 2 + 0.25f, headLength / 4 - 0.5f));
        m = Mat4.multiply(m, Mat4Transform.scale(combScale * 0.8f, combLength * 0.8f, combScale * 0.8f));
        TransformNode comb2Transform = new TransformNode("comb2 transform", m);
        ModelNode comb2Shape = new ModelNode("Sphere(comb2)", sphere);

        // create back comb finger
        NameNode comb3 = new NameNode("comb 3");
        comb3Rotate = new TransformNode("comb3 rotate", Mat4Transform.rotateAroundX(0));
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0, headWidth / 2 + 0.15f, headLength / 4 - 1f));
        m = Mat4.multiply(m, Mat4Transform.scale(combScale * 0.6f, combLength * 0.6f, combScale * 0.6f));
        TransformNode comb3Transform = new TransformNode("comb3 transform", m);
        ModelNode comb3Shape = new ModelNode("Sphere(comb3)", sphere);

        // define scene graph
        root.addChild(robotMoveTranslate);
          robotMoveTranslate.addChild(robotTranslate);
            robotTranslate.addChild(robotRotateTranslate);
              robotRotateTranslate.addChild(wheel);
                wheel.addChild(wheelTransform);
                  wheelTransform.addChild(wheelRotate);
                  wheelRotate.addChild(wheelShape);
                wheel.addChild(body);
                  body.addChild(bodyRotate);
                  bodyRotate.addChild(bodyTransform);
                    bodyTransform.addChild(bodyShape);
                  bodyRotate.addChild(neck);
                    neck.addChild(neckTranslate);
                      neckTranslate.addChild(neckRotate);
                      neckRotate.addChild(neckScale);
                      neckScale.addChild(neckShape);
                      neckRotate.addChild(head);
                      head.addChild(headTranslate);
                        headTranslate.addChild(headRotate);
                        headRotate.addChild(headScale);
                        headScale.addChild(headShape);
                      headRotate.addChild(lens);
                        lens.addChild(lensTransform);
                        lensTransform.addChild(lensShape);
                      headRotate.addChild(comb);
                      comb.addChild(combRotate);
                      combRotate.addChild(comb1);
                        comb1.addChild(comb1Rotate);
                        comb1Rotate.addChild(comb1Transform);
                        comb1Transform.addChild(comb1Shape);
                      combRotate.addChild(comb2);
                        comb2.addChild(comb2Rotate);
                        comb2Rotate.addChild(comb2Transform);
                        comb2Transform.addChild(comb2Shape);
                      combRotate.addChild(comb3);
                        comb3.addChild(comb3Rotate);
                        comb3Rotate.addChild(comb3Transform);
                        comb3Transform.addChild(comb3Shape);

        root.update();
    }

    public void render(GL3 gl) {
        root.draw(gl);
    }

    public void dispose(GL3 gl) {
        cube.dispose(gl);
        sphere.dispose(gl);
        sphere2.dispose(gl);
        sphere3.dispose(gl);
    }

    /* Get position of robot */
    public Vec3 getPos() {
        float[] transMat = robotMoveTranslate.getTransform().toFloatArrayForGLSL();
        return new Vec3(transMat[12], transMat[13], transMat[14]);
    }

    /* Change the pose of the robot */
    public void changePose(int pose) {
        switch(pose) {
            case 0:
                pose1();
                break;
            case 1:
                pose2();
                break;
            case 2:
                pose3();
                break;
            case 3:
                pose4();
                break;
            default:
                pose5();
        }

        root.update();
    }

    /* Animate the movement between poses */
    public boolean updatePos(Vec3 start, ArrayList<Integer> target, double startTime, double currentTime) {
        // compare current pos to target
        Vec3 difference = Vec3.subtract(posePos[target.get(0)], getPos());

        if (difference.magnitude() > 0.2f) {
            // rotate to target
            float angle = (float) Math.toDegrees(Math.atan2(difference.x, difference.z));
            robotRotateTranslate.setTransform(Mat4Transform.rotateAroundY(angle));

            double elapsedTime = (currentTime - startTime) / 2;

            // rotate wheel
            wheelRotate.setTransform(Mat4Transform.rotateAroundX((float) elapsedTime * 500));

            // wave comb
            float combAngle = (float) (20 *  Math.sin(elapsedTime * 5));
            comb1Rotate.setTransform(Mat4Transform.rotateAroundZ(combAngle));
            combAngle = (float) (20 *  Math.sin(elapsedTime * 4));
            comb2Rotate.setTransform(Mat4Transform.rotateAroundZ(combAngle));
            combAngle = (float) (20 *  Math.sin(elapsedTime * 3));
            comb3Rotate.setTransform(Mat4Transform.rotateAroundZ(combAngle));

            // translate to target
            difference = Vec3.subtract(posePos[target.get(0)], start);
            Vec3 translate = Vec3.add(start, Vec3.multiply(difference, (float) elapsedTime));
            robotMoveTranslate.setTransform(Mat4Transform.translate(translate));
            root.update();
            return true;
        }

        // remove pose from target
        int pose = target.remove(0);
        // check if target is empty
        if (target.size() == 0) {
            changePose(pose);
        }
        return false;
    }

    /* Robot pose 1 */
    public void pose1() {
        robotRotateTranslate.setTransform(Mat4Transform.rotateAroundY(25));
        bodyRotate.setTransform(Mat4Transform.rotateAroundX(0));
        neckRotate.setTransform(Mat4Transform.rotateAroundX(45));
        comb1Rotate.setTransform(Mat4Transform.rotateAroundX(0));
        comb2Rotate.setTransform(Mat4Transform.rotateAroundX(0));
        comb3Rotate.setTransform(Mat4Transform.rotateAroundX(0));
    }

    /* Robot pose 2 */
    private void pose2() {
        robotRotateTranslate.setTransform(Mat4Transform.rotateAroundY(135));
        bodyRotate.setTransform(Mat4Transform.rotateAroundX(25));
        neckRotate.setTransform(Mat4Transform.rotateAroundX(60));
        comb1Rotate.setTransform(Mat4Transform.rotateAroundZ(15));
        comb2Rotate.setTransform(Mat4Transform.rotateAroundZ(-15));
        comb3Rotate.setTransform(Mat4Transform.rotateAroundZ(15));
    }

    /* Robot pose 3 */
    private void pose3() {
        robotRotateTranslate.setTransform(Mat4Transform.rotateAroundY(90));
        bodyRotate.setTransform(Mat4Transform.rotateAroundX(-10));
        neckRotate.setTransform(Mat4Transform.rotateAroundX(0));
        comb1Rotate.setTransform(Mat4Transform.rotateAroundX(-10));
        comb2Rotate.setTransform(Mat4Transform.rotateAroundX(-20));
        comb3Rotate.setTransform(Mat4Transform.rotateAroundX(-30));
    }

    /* Robot pose 4 */
    private void pose4() {
        robotRotateTranslate.setTransform(Mat4Transform.rotateAroundY(180));
        bodyRotate.setTransform(Mat4Transform.rotateAroundZ(15));
        neckRotate.setTransform(Mat4Transform.rotateAroundX(60));
        comb1Rotate.setTransform(Mat4Transform.rotateAroundX(30));
        comb2Rotate.setTransform(Mat4Transform.rotateAroundX(10));
        comb3Rotate.setTransform(Mat4Transform.rotateAroundX(0));
    }

    /* Robot pose 5 */
    private void pose5() {
        robotRotateTranslate.setTransform(Mat4Transform.rotateAroundY(-90));
        bodyRotate.setTransform(Mat4Transform.rotateAroundZ(-20));
        neckRotate.setTransform(Mat4Transform.rotateAroundZ(45));
        comb1Rotate.setTransform(Mat4Transform.rotateAroundZ(30));
        comb2Rotate.setTransform(Mat4Transform.rotateAroundZ(20));
        comb3Rotate.setTransform(Mat4Transform.rotateAroundZ(10));
    }
}
