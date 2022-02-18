import gmaths.*;
import com.jogamp.opengl.*;

public class Room {

    private Model floor, wall, sky;
    private Model[] windowWall;

    private float SCENE_SCALE;
    private int DAY_CYCLE;
    private float timeScale;
    private Light spotlight;

    public Room(GL3 gl, Camera camera, Light light, DirLight sun, Light spotlight, float scale) {
        SCENE_SCALE = scale;
        DAY_CYCLE = 10;
        this.spotlight = spotlight;

        int[] floorTexture = TextureLibrary.loadTexture(gl, "textures/floor.jpg");
        int[] wallTexture = TextureLibrary.loadTexture(gl, "textures/wall.jpg");
        int[] doorTexture = TextureLibrary.loadTexture(gl, "textures/door_wall.jpg");
        int[] skyTexture = TextureLibrary.loadTexture(gl, "textures/sky.jpg");
        int[] nightTexture = TextureLibrary.loadTexture(gl, "textures/night.jpg");

        // create floor pane
        Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        Shader shader = new Shader(gl, "vertex/vs_main.txt", "fragment/fs_spotlight.txt");
        Material material = new Material(new Vec3(0.3f, 0.3f, 0.3f), new Vec3(0.3f, 0.3f, 0.3f),
                new Vec3(0.1f, 0.1f, 0.1f), 6f);
        floor = new Model(gl, camera, light, sun, shader, material, getMforFloor(SCENE_SCALE), mesh, floorTexture);

        // create sky pane
        material = new Material(new Vec3(1f, 1f, 1f), new Vec3(0, 0, 0), new Vec3(0, 0, 0),  0);
        shader = new Shader(gl, "vertex/vs_sky.txt", "fragment/fs_sky.txt");
        sky = new Model(gl, camera, light, sun, shader, material, getMforSky(SCENE_SCALE * 2), mesh, skyTexture,
                nightTexture);

        // create door wall pane
        mesh = new Mesh(gl, Walls.verticesDoor.clone(), Walls.indices.clone());
        material = new Material(new Vec3(0.2f, 0.3f, 0.3f), new Vec3(0.4f, 0.4f, 0.4f), new Vec3(0.1f, 0.1f, 0.1f),  1f);
        shader = new Shader(gl, "vertex/vs_main.txt", "fragment/fs_main.txt");
        wall = new Model(gl, camera, light, sun, shader, material, getMforWall(SCENE_SCALE), mesh, doorTexture);

        // create panes for windowed wall
        windowWall = new Model[4];
        for (int i = 0; i < 4; i++) {
            mesh = new Mesh(gl, Walls.verticesWindow[i].clone(), Walls.indices.clone());
            windowWall[i] = new Model(gl, camera, light, sun, shader, material, getMforWindow(SCENE_SCALE, i),
                    mesh, wallTexture);
        }
    }

    public void render(GL3 gl) {
        floor.render(gl, spotlight);
        wall.render(gl);

        // loop through each pane for window wall
        for (int i = 0; i < 4; i++) {
          windowWall[i].render(gl);
        }

        sky.render(gl, timeScale);
    }

    /* Update the timescale for the day/night cycle */
    public float updateSky(double startTime, double currentTime) {
        double elapsedTime = currentTime - startTime;
        timeScale = (float) ((Math.sin(elapsedTime / DAY_CYCLE) + 1 ) / 2);
        return timeScale;
    }

    public void dispose(GL3 gl) {
        floor.dispose(gl);
        wall.dispose(gl);
        sky.dispose(gl);
    }

    /* Matrix for floor pane */
    private Mat4 getMforFloor(float size) {
        Mat4 modelMatrix = new Mat4(1);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(size * 1.2f, 1f, size * 1.2f), modelMatrix);
        return modelMatrix;
    }

    /* Matrix for back wall pane */
    private Mat4 getMforWall(float size) {
        Mat4 modelMatrix = new Mat4(1);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(size * 1.2f, 1f, size * 0.6f), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(0, size * 0.3f, -size * 0.6f), modelMatrix);
        return modelMatrix;
    }

    /* Matrix for window wall pane */
    private Mat4 getMforWindow(float size, int section) {
        // array for position of each pane
        float[][] section_pos = {{size * 0.3f, size * 0.4f}, {size * 0.3f, size * -0.4f}, {size * 0.5f, 0},
                {size * 0.1f, 0}};
        // array for size of each pane
        float[][] section_size = {{size * 0.4f, size * 0.6f}, {size * 0.4f, size * 0.6f}, {size * 0.4f, size * 0.2f},
                {size * 0.4f, size * 0.2f}};

        Mat4 modelMatrix = new Mat4(1);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(section_size[section][0], 1f, section_size[section][1]),
                modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(-size * 0.6f, section_pos[section][0],
                section_pos[section][1]), modelMatrix);
        return modelMatrix;
    }

    /* Matrix for sky pane */
    private Mat4 getMforSky(float size) {
        Mat4 modelMatrix = new Mat4(1);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(size * 2.5f, 1f, size), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(-size * 0.75f, 0, 0), modelMatrix);
        return modelMatrix;
    }
}
