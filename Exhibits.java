import gmaths.*;
import com.jogamp.opengl.*;

public class Exhibits {

    private Model podium, egg, phone;
    private float SCENE_SCALE;

    public Exhibits(GL3 gl, Camera camera, Light light, DirLight sun, float scale) {
        SCENE_SCALE = scale;

        // load textures
        int[] podiumTexture = TextureLibrary.loadTexture(gl, "textures/marble.jpg");
        int[] podiumSpec = TextureLibrary.loadTexture(gl, "textures/marble_spec.jpg");
        int[] eggTexture = TextureLibrary.loadTexture(gl, "textures/egg.jpg");
        int[] eggSpec = TextureLibrary.loadTexture(gl, "textures/egg_spec.jpg");
        int[] phoneTexture = TextureLibrary.loadTexture(gl, "textures/phone.jpg", GL3.GL_CLAMP_TO_BORDER,
                GL3.GL_CLAMP_TO_BORDER, GL3.GL_CLAMP_TO_BORDER, GL3.GL_CLAMP_TO_BORDER);

        // create egg model
        Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        Shader shader = new Shader(gl, "vertex/vs_main.txt", "fragment/fs_spec.txt");
        Material material = new Material(new Vec3(0.3f, 0.3f, 0.3f), new Vec3(0.8f, 0.8f, 0.8f),
                new Vec3(0.8f, 0.8f, 0.8f), 20.0f);
        egg = new Model(gl, camera, light, sun, shader, material, getMforEgg(scale / 6), mesh,
                eggTexture, eggSpec);

        // create podium model
        material = new Material(new Vec3(0.2f, 0.2f, 0.2f), new Vec3(0.3f, 0.3f, 0.3f),
                new Vec3(0.1f, 0.1f, 0.1f), 1.0f);
        mesh = new Mesh(gl, Podium.vertices.clone(), Podium.indices.clone());
        shader = new Shader(gl, "vertex/vs_main.txt", "fragment/fs_spec.txt");
        podium = new Model(gl, camera, light, sun, shader, material, new Mat4(1), mesh, podiumTexture, podiumSpec);

        // create phone model
        mesh = new Mesh(gl, Phone.vertices.clone(), Phone.indices.clone());
        shader = new Shader(gl, "vertex/vs_main.txt", "fragment/fs_main.txt");
        material = new Material(new Vec3(0.3f, 0.3f, 0.3f), new Vec3(0.5f, 0.5f, 0.5f),
                new Vec3(0.5f, 0.5f, 0.5f), 16.0f);
        phone = new Model(gl, camera, light, sun, shader, material, getMforPhone(scale / 6), mesh, phoneTexture);
    }

    public void render(GL3 gl) {
        // render podiums
        podium.setModelMatrix(getMforPodium(SCENE_SCALE / 6, 0f, 2f, 0));
        podium.render(gl);
        podium.setModelMatrix(getMforPodium(SCENE_SCALE / 6, 12f, -12f, 45));
        podium.render(gl);

        // render egg and phone
        egg.render(gl);
        phone.render(gl);
    }

    public void dispose(GL3 gl) {
        podium.dispose(gl);
        egg.dispose(gl);
        phone.dispose(gl);
    }

    /* Matrix for podium */
    private Mat4 getMforPodium(float size, float x, float z, float rotate) {
        Mat4 modelMatrix = new Mat4(1);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(size, size / 3, size), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(rotate), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(x, size / 6, z), modelMatrix);
        return modelMatrix;
    }

    /* Matrix for egg */
    private Mat4 getMforEgg(float size) {
        Mat4 modelMatrix = new Mat4(1);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(size, size * 2, size), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(0f, 8 * size / 6, 2f), modelMatrix);
        return modelMatrix;
    }

    /* Matrix for phone */
    private Mat4 getMforPhone(float size) {
        Mat4 modelMatrix = new Mat4(1);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(size / 12, size * 2, size), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(45), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(12f, 8 * size / 6, -12f), modelMatrix);
        return modelMatrix;
    }
}
